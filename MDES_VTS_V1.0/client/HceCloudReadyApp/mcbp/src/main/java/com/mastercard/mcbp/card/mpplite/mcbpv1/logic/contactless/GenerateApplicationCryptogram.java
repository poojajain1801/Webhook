/*
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 *
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 *
 * Please refer to the file LICENSE.TXT for full details.
 *
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless;

import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcResponseApdu;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionOutcomeBuilder;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionOutcomeBuilderListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement.CardVerificationResults;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement
        .PosCardholderInteractionInformation;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentials;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.AdditionalCheckTable;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLog;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLogImpl;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.CryptogramOutput;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.mcbpv1.state.ContactlessContext;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement.CardRiskManagement;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement.CardRiskManagementFactory;
import com.mastercard.mcbp.card.profile.AlternateContactlessPaymentData;
import com.mastercard.mcbp.card.profile.CardRiskManagementData;
import com.mastercard.mcbp.card.profile.ContactlessPaymentData;
import com.mastercard.mcbp.card.profile.IccPrivateKeyCrtComponents;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionSummary;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Advice;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalInformation;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionInformation;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcCommandApdu;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.ConditionsOfUseNotSatisfied;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidLc;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * Utility Class to build a Generate Application Cryptogram response
 */
public final class GenerateApplicationCryptogram {
    /**
     * Crypto Factory Object
     */
    private static final CryptoService sCryptoService =
            CryptoServiceFactory.getDefaultCryptoService();

    /**
     * The Command APDU
     */
    private final GenerateAcCommandApdu mApdu;

    /**
     * The contactless context for this transaction
     */
    private final ContactlessContext mContext;

    /**
     * Contactless Payment Data part of the profile. Fields subject to Primary or Alternate AID
     * values are cached as class variables
     */
    private final ContactlessPaymentData mContactlessPaymentData;

    /**
     * The Card Risk Management Data of the card profile
     */
    private final CardRiskManagementData mCardRiskManagementData;

    /**
     * The Cardholder Verification Result data
     */
    private final CardVerificationResults mCvr;

    /**
     * The POS Cardholder Interaction Information
     */
    private PosCardholderInteractionInformation mPoscii;

    /**
     * The Cryptogram Information Data
     */
    private final CryptogramInformationData mCid;

    /**
     * The CIAC Decline as in the contactless profile. It is automatically determined based on the
     * type of transaction, i.e. alternate or primary AID
     */
    private final byte[] mCiacDecline;

    /**
     * The CVR AND Mask
     * It is automatically determined based on the type of transaction, alternate or primary AID
     */
    private final byte[] mCvrMaskAnd;

    /**
     * The Transaction Credential Manager
     */
    private final TransactionCredentialsManager mTransactionCredentialsManager;

    /**
     * Contactless Transaction Context
     */
    private final ContactlessTransactionContext mTransactionContext;

    /**
     * The Contactless Transaction Listener
     */
    private final ContactlessTransactionListener mListener;

    /**
     * Transaction Information
     */
    private final TransactionInformation mTransactionInformation;

    /**
     * Terminal Information
     */
    private final TerminalInformation mTerminalInformation;

    /**
     * Build an object to perform the Generate AC command
     *
     * @param apdu               The Generate AC Command APDU
     * @param contactlessContext The Contactless Context
     * @return A Generate AC object
     */
    public static GenerateApplicationCryptogram of(final GenerateAcCommandApdu apdu,
                                                   final ContactlessContext contactlessContext) {
        if (apdu == null || contactlessContext == null) {
            throw new MppLiteException("Invalid Parameters to process the Generate AC");
        }
        return new GenerateApplicationCryptogram(apdu, contactlessContext);
    }

    /**
     * Constructor is not available. Use the static factory method instead.
     */
    private GenerateApplicationCryptogram(final GenerateAcCommandApdu apdu,
                                          final ContactlessContext contactlessContext) {

        mApdu = apdu;
        mContext = contactlessContext;

        MppLiteModule profile = contactlessContext.getCardProfile();
        if (profile == null) {
            throw new MppLiteException("Invalid Card Profile for Generate AC");
        }

        mContactlessPaymentData = profile.getContactlessPaymentData();
        mCardRiskManagementData = profile.getCardRiskManagementData();

        if (mContactlessPaymentData == null || mCardRiskManagementData == null) {
            throw new MppLiteException("Invalid Card Profile for Generate AC");
        }

        mTransactionContext = mContext.getTransactionContext();

        final byte[] selectResponse;
        if (mTransactionContext.isAlternateAid()) {
            final AlternateContactlessPaymentData alternateContactlessPaymentData =
                    mContactlessPaymentData.getAlternateContactlessPaymentData();
            mCiacDecline = alternateContactlessPaymentData.getCiacDecline().getBytes();
            mCvrMaskAnd = alternateContactlessPaymentData.getCvrMaskAnd().getBytes();
            selectResponse = alternateContactlessPaymentData.getPaymentFci().getBytes();
        } else {
            mCiacDecline = mContactlessPaymentData.getCiacDecline().getBytes();
            mCvrMaskAnd = mContactlessPaymentData.getCvrMaskAnd().getBytes();
            selectResponse = mContactlessPaymentData.getPaymentFci().getBytes();
        }
        final byte[] applicationLabel = ContactlessUtils.readApplicationLabel(selectResponse);

        // Initializing cvr - Note this is done here instead of 'startContactlessPayment' as per
        // MCBP v1 functional description
        mCvr = CardVerificationResults.withIssuerApplicationData(
                mContactlessPaymentData.getIssuerApplicationData().getBytes());
        mCid = new CryptogramInformationData();

        mTransactionCredentialsManager = mContext.getTransactionCredentialsManager();
        mListener = mContext.getTransactionListener();
        mTransactionInformation = TransactionInformation.forMchip(
                applicationLabel, mApdu, mTransactionContext.getPdolValues());

        // Set the UDOL in the context, otherwise we may not be able to get the right data
        // when building the Terminal Information
        mContext.getTransactionContext().setCdolData(apdu.getCdol());

        mTerminalInformation = TerminalInformation.forMchip(
                mApdu, mTransactionContext.getPdolValues(), mTransactionContext.getCdolValues());
    }

    /**
     * Build the Generate Application Cryptogram Response APDU
     *
     * @return The response APDU as byte[]
     * @throws com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException In case something
     *                                                                       goes wrong
     */
    public byte[] response() {
        // GAC.1.1 is validated by the MPP Lite architecture (a class for each state)
        // GAC.1.4, GAC.1.5 are validated via the C-APDU constructor

        // GAC.1.2, GAC.1.3, GAC.1.6, GAC.1.7, GAC.1.8, GAC.1.9
        validateGenerateAcApdu();

        // GAC 1.10: initialize trxContext
        initializeGenerateAcTransactionContext();

        // GAC.2.1
        // Removed as the Crypto Output is generated at the end to keep the object immutable

        // GAC.2.2, GAC.2.3, GAC.2.4
        setTransactionTypeInCvr();

        // GAC.2.5 and GAC.2.6
        processAdditionalCheckTable();

        // GAC.2.7
        setPinInformation();

        // GAC.2.8 POSCII is set to null by default

        // GAC.3.
        return performCardRiskManagement();
    }

    /**
     * Validate the Command APDU against the terminal type and the availability of MCHIP data info
     * in the profile.
     *
     * @throws ConditionsOfUseNotSatisfied In case the MCHIP Profile is not supported or the
     *                                     terminal indicates that it is offline only capable.
     */
    private void validateGenerateAcApdu() {
        // GAC.1.2
        if (!checkMChipParameters()) {
            // GAC.1.3
            // cancelPayment();
            throw new ConditionsOfUseNotSatisfied("MCHIP profile data is not available");
        }

        // GAC.1.4 and GAC.1.5 are handled within the C-APDU constructor

        // GAC.1.6
        if (mContactlessPaymentData.getCdol1RelatedDataLength() != mApdu.getCdol().length) {
            // GAC.1.7
            // cancelPayment();
            throw new InvalidLc("CDOL and CDOL1 Related Data Length do not match");
        }

        // GAC.1.8 checking terminal type
        if (Utils.isTerminalOffline(mApdu.getTerminalType())) {
            // GAC.1.9
            // cancelPayment();
            throw new ConditionsOfUseNotSatisfied("Terminal is offline!");
        }
    }

    /**
     * Check whether the relevant MCHIP Parameters are present
     *
     * @return true if all the MCHIP parameters are available, false otherwise
     */
    private boolean checkMChipParameters() {
        return mTransactionContext.isAlternateAid() ?
               mContactlessPaymentData.isAlternateAidMchipDataValid() :
               mContactlessPaymentData.isPrimaryAidMchipDataValid();
    }

    /**
     * Initialize the contactless transaction context based on the content of the Generate AC
     * Command APDU
     */
    private void initializeGenerateAcTransactionContext() {
        mTransactionContext.setAmount(ByteArray.of(mApdu.getAuthorizedAmount()));
        mTransactionContext.setCurrencyCode(ByteArray.of(mApdu.getTransactionCurrencyCode()));
        mTransactionContext.setTrxDate(ByteArray.of(mApdu.getTransactionDate()));
        mTransactionContext.setTrxType(ByteArray.of(mApdu.getTransactionType()));
        mTransactionContext.setUnpredictableNumber(ByteArray.of(mApdu.getUnpredictableNumber()));
    }

    /**
     * Check whether the transaction is domestic or international and set the CVR values
     * accordingly
     */
    private void setTransactionTypeInCvr() {
        final byte[] countryCode = mCardRiskManagementData.getCrmCountryCode().getBytes();

        // Check whether it is domestic or international transaction
        if (Arrays.equals(countryCode, mApdu.getTerminalCountryCode())) {
            mCvr.indicateDomesticTransaction();
        } else {
            mCvr.indicatedInternationalTransaction();
        }
    }

    /**
     * Process the Additional Check Table
     */
    private void processAdditionalCheckTable() {
        final byte[] cdol = mApdu.getCdol();

        final byte[] additionalCheckTable =
                mCardRiskManagementData.getAdditionalCheckTable().getBytes();

        final AdditionalCheckTable.Result result =
                AdditionalCheckTable.process(cdol, mCvrMaskAnd, mCiacDecline, additionalCheckTable);

        if (result == AdditionalCheckTable.Result.MATCH_FOUND) {
            mCvr.indicateMatchFoundInAdditionalCheckTable();
        } else if (result == AdditionalCheckTable.Result.MATCH_NOT_FOUND) {
            mCvr.indicateMatchNotFoundInAdditionalCheckTable();
        }
    }

    /***
     * Set PIN Information based on whether a Mobile PIN has been entered or not.
     */
    private void setPinInformation() {
        if (mContext.isCvmEntered()) {
            mCvr.indicateCdCvmPerformed();
        } else {
            mCvr.indicateCdCvmNotPerformed();
        }
    }

    /**
     * Perform the Card Risk Management as detailed in MCBP MPA Functional Description 1.0 (GAC.4.x)
     *
     * @return the response APDU
     */
    private byte[] performCardRiskManagement() {
        final CardRiskManagement cardRiskManagement =
                CardRiskManagementFactory.forMchip(mApdu,
                                                   mContext,
                                                   mCvr,
                                                   mCiacDecline,
                                                   mContext.getTransactionCredentialsManager());

        final Advice masterCardAdvice =
                cardRiskManagement.getMasterCardAdvice(mTransactionInformation,
                                                       mContext.isConsentGiven(),
                                                       mContext.isCvmEntered());

        // Send the advice to the Wallet
        final Advice walletAdvice = mContext.getWalletAdvice(masterCardAdvice,
                                                             mTransactionInformation,
                                                             mTerminalInformation);

        // Take actions based on the final advice
        final TransactionOutcomeBuilder<byte[]> transactionOutcomeBuilder =
                new TransactionOutcomeBuilder<>(walletAdvice,
                                                mTransactionInformation,
                                                mTerminalInformation,
                                                mTransactionCredentialsManager,
                                                mchipCardRiskActionListener());

        return transactionOutcomeBuilder.buildResponse();
    }

    /**
     * Define the MCHIP behaviour for Card Risk actions
     *
     * @return The MCHIP Card Risk Action Listener
     */
    private TransactionOutcomeBuilderListener<byte[]> mchipCardRiskActionListener() {
        return new TransactionOutcomeBuilderListener<byte[]>() {
            @Override
            public byte[] approveOnline(final List<Reason> walletReasons,
                                        final TransactionCredentials credentials) {
                mContext.getTransactionContext().setResult(
                        TransactionSummary.AUTHORIZE_ONLINE);
                mPoscii = PosCardholderInteractionInformation.forApproveMchip();
                // Due to the current MDES implementation we need to overrule potential
                // indications we set during the MasterCard advice
                // FIXME: In the future we may be able to remove this.
                mCvr.setFlagsForApproval();
                return arqc(credentials);
            }

            @Override
            public byte[] abort(final List<Reason> walletReasons) {
                mContext.getTransactionContext().setResult(
                        TransactionSummary.ABORT_PERSISTENT_CONTEXT);
                mCvr.indicateCvmRequiredNotSatisfied();
                mPoscii = PosCardholderInteractionInformation.forAbortMchip(walletReasons);
                return aac(getRandomCredentials());
            }

            @Override
            public byte[] decline(final List<Reason> walletReasons) {
                if (walletReasons.contains(Reason.CONTEXT_NOT_MATCHING)) {
                    return error(walletReasons);
                }
                mContext.getTransactionContext().setResult(TransactionSummary.DECLINE);
                mPoscii = PosCardholderInteractionInformation.forDeclineMchip();
                return aac(getRandomCredentials());
            }

            @Override
            public byte[] error(final List<Reason> walletReasons) {
                if (walletReasons.contains(Reason.CONTEXT_NOT_MATCHING)) {
                    // GAC.3.7
                    mContext.getTransactionContext().setResult(
                            TransactionSummary.ERROR_CONTEXT_CONFLICT);
                    // We set the POSCII only in case of the context conflict.
                    mPoscii = PosCardholderInteractionInformation.forErrorMchip(walletReasons);
                } else {
                    mContext.getTransactionContext().setResult(TransactionSummary.ERROR);
                }
                return aac(getRandomCredentials());
            }

            @Override
            public byte[] authenticate(final List<Reason> walletReasons,
                                       final TransactionCredentials credentials) {
                // An AAC was requested, let's return it
                mContext.getTransactionContext().setResult(
                        TransactionSummary.AUTHENTICATE_ONLINE);
                mPoscii = PosCardholderInteractionInformation.forAuthenticateMchip();
                // Due to the current MDES implementation we need to overrule potential
                // indications we set during the MasterCard advice
                // FIXME: In the future we may be able to remove this.
                mCvr.setFlagsForApproval();
                return aac(credentials);
            }
        };
    }

    /**
     * Set the relevant parameters related to AAC, if it was requested
     *
     * @return the Response APDU
     */
    private byte[] aac(final TransactionCredentials transactionCredentials) {
        // We need a copy of the ATC as the original value will be cleared when erasing credentials
        mTransactionContext.setAtc(ByteArray.of(transactionCredentials.getAtc()));
        // GAC.6.1
        mCvr.indicateAacReturnedInFirstAndAcNotRequestedInSecondGenerateAc();

        // GAC 6.2
        mCid.indicateDecline();

        // GAC 6.3
        if (mApdu.isAacRequested() && mApdu.isCombinedDdaAcGenerationRequested()) {
            // GAC 6.4
            final boolean cvmEntered = mContext.isCvmEntered();

            if (cvmEntered) {
                // GAC.6.5
                mCvr.indicateCombinedDdaAcGenerationReturnedInFirstGenerateAc();
            }
        }
        return ac(transactionCredentials);
    }

    /**
     * Set the relevant fields related to the ARQC, if it was requested
     *
     * @return the Response APDU
     */
    private byte[] arqc(final TransactionCredentials transactionCredentials) {
        // We need a copy of the ATC as the original value will be cleared when erasing credentials
        mTransactionContext.setAtc(ByteArray.of(transactionCredentials.getAtc()));
        // GAC.5.1
        mCvr.indicateArqcReturnedInFirstAndAcNotRequestedInSecondGenerateAc();

        // GAC 5.2
        mCid.indicateOnlineDecision();

        // GAC.5.3
        if (mApdu.isCombinedDdaAcGenerationRequested()) {
            // GAC.5.4
            mCvr.indicateCombinedDdaAcGenerationReturnedInFirstGenerateAc();
        }

        return ac(transactionCredentials);
    }

    /**
     * Calculate the Application Cryptogram as defined in the MPA Functional Description v1.0
     *
     * @param transactionCredentials The Transaction Credentials to be used for the Application
     *                               Cryptogram
     * @return the Response APDU
     */
    private byte[] ac(final TransactionCredentials transactionCredentials) {
        // GAC.7.1
        mCvr.applyMaskAnd(mCvrMaskAnd);

        // GAC.7.2 and GAC.7.3 Compute Application (Cryptogram MD,UMD)
        final CryptoService.TransactionCryptograms cryptograms =
                getTransactionCryptograms(transactionCredentials);

        final byte[] umdCryptogram = cryptograms.getUmdCryptogram();
        final byte[] mdCryptogram = cryptograms.getMdCryptogram();

        mContext.getTransactionContext().setCryptogram(ByteArray.of(umdCryptogram));

        // GAC.7.4
        final byte[] issuerApplicationData = buildIssuerApplicationData(mdCryptogram);

        final CryptogramOutput cryptogramOutput =
                new CryptogramOutput(transactionCredentials.getAtc(),
                                     ByteArray.of(issuerApplicationData),
                                     ByteArray.of(umdCryptogram),
                                     mCid.getValue());

        // Clear these vectors as we have created copies already
        Utils.clearByteArray(issuerApplicationData);
        Utils.clearByteArray(umdCryptogram);
        Utils.clearByteArray(mdCryptogram);

        // GAC.7.5
        if (mCvr.isCombinedDdaAcGenerationReturnedInFirstGenerateAc()) {

            byte[] responseWithCda = cda(transactionCredentials.getIdn(), cryptogramOutput);
            //Clear cryptogram output as we are done with the usage.
            cryptogramOutput.wipe();
            return responseWithCda;
        }

        // GAC.7.6
        final byte[] response = buildGenerateAcResponseWithoutCda(cryptogramOutput);

        //Clear cryptogram output as we are done with the usage.
        cryptogramOutput.wipe();

        // GAC.7.7
        mListener.onContactlessTransactionCompleted(getContactlessLog());

        // GAC.7.8
        // cancelPayment();

        return response;
    }

    /**
     * Utility function to build the transaction cryptograms
     *
     * @param transactionCredentials The Transaction Credentials
     * @return The Transaction Cryptograms
     */
    private CryptoService.TransactionCryptograms getTransactionCryptograms(
            final TransactionCredentials transactionCredentials) {
        // GAC.7.2
        final byte[] cryptogramInput =
                buildCryptogramInput(transactionCredentials.getAtc().getBytes());
        // GAC.7.3
        final byte[] umdSessionKey = transactionCredentials.getUmdSessionKey().getBytes();
        final byte[] mdSessionKey = transactionCredentials.getMdSessionKey().getBytes();

        final CryptoService.TransactionCryptograms cryptograms;

        try {
            cryptograms = sCryptoService.buildGenerateAcCryptograms(cryptogramInput,
                                                                    umdSessionKey,
                                                                    mdSessionKey);
        } catch (McbpCryptoException e) {
            throw new MppLiteException(e.getMessage());
        } finally {
            // In any case we need to make sure credentials are zeroed
            Utils.clearByteArray(umdSessionKey);
            Utils.clearByteArray(mdSessionKey);
        }
        return cryptograms;
    }

    /**
     * Prepare the crypto input for the GAC.7.x. The actual content is specified in GAC.7.2
     *
     * @return The input for the cryptogram generation
     */
    private byte[] buildCryptogramInput(final byte[] atc) {
        final byte[] cryptoInput = new byte[39];  // 29 bytes of CDOL data, 2 AIP, 2 ATC, and 6 CVR
        final byte[] cdol = mApdu.getCdol();
        final byte[] aip = mTransactionContext.getAip().getBytes();

        System.arraycopy(cdol, 0, cryptoInput, 0, 29);
        System.arraycopy(aip, 0, cryptoInput, 29, 2);
        System.arraycopy(atc, 0, cryptoInput, 31, 2);
        System.arraycopy(mCvr.getBytes(), 0, cryptoInput, 33, 6);

        return cryptoInput;
    }

    /**
     * Utility function to build the Issuer Application Data
     *
     * @param mdCryptogram The MD Cryptogram
     * @return The Issuer Application Data to be included in the Generate AC response
     */
    private byte[] buildIssuerApplicationData(final byte[] mdCryptogram) {
        final byte[] iadProfile = mContactlessPaymentData.getIssuerApplicationData().getBytes();
        final byte[] issuerApplicationData = new byte[iadProfile.length];
        System.arraycopy(iadProfile, 0, issuerApplicationData, 0, iadProfile.length);
        System.arraycopy(mCvr.getBytes(), 0, issuerApplicationData, 2, 6);

        if (Utils.isZero(mApdu.getIccDynamicNumber())) {
            System.arraycopy(mApdu.getDataAuthenticationCode(), 0, issuerApplicationData, 8, 2);
        } else {
            System.arraycopy(mApdu.getIccDynamicNumber(), 0, issuerApplicationData, 8, 2);
        }
        System.arraycopy(mdCryptogram, 0, issuerApplicationData, 11, 5);
        return issuerApplicationData;
    }

    /**
     * If a CDA was requested, this function is responsible to generate the corresponding signature.
     * GAC.8.x
     *
     * @return the response
     */
    private byte[] cda(final ByteArray idn, final CryptogramOutput cryptogramOutput) {
        // GAC.8.1, GAC.8.2, GAC.8.3, and GAC.8.4 are done by the Generate AC Response APDU object
        final IccPrivateKeyCrtComponents privateKey =
                mContactlessPaymentData.getIccPrivateKeyCrtComponents();

        final GenerateAcResponseApdu generateAcResponseApdu =
                GenerateAcResponseApdu.withCda(mApdu,
                                               mTransactionContext,
                                               mPoscii,
                                               cryptogramOutput,
                                               idn,
                                               privateKey);

        // GAC.8.5
        mListener.onContactlessTransactionCompleted(getContactlessLog());

        // GAC.8.6
        // cancelPayment();

        return generateAcResponseApdu.getBytes();
    }

    /***
     * Utility function to prepare the contactless log to be returned via the listener
     *
     * @return The Contactless Log
     */
    private ContactlessLog getContactlessLog() {
        return ContactlessLogImpl.forMchip(mTransactionContext);
    }

    /**
     * Build the Generate AC Response
     *
     * @return The Generate AC Response APDU without Cda
     */
    private byte[] buildGenerateAcResponseWithoutCda(final CryptogramOutput output) {
        return GenerateAcResponseApdu.withoutCda(mApdu, mTransactionContext, mPoscii, output)
                                     .getBytes();
    }

    /**
     * Get Random Transaction Credentials
     *
     * @return A set of Random Transaction Credentials
     */
    private TransactionCredentials getRandomCredentials() {
        return mTransactionCredentialsManager.getRandomCredentials();
    }


}
