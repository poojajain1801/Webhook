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

import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionOutcomeBuilder;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionOutcomeBuilderListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement
        .PosCardholderInteractionInformation;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentials;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLog;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLogImpl;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionSummary;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ComputeCcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import com.mastercard.mcbp.card.mpplite.mcbpv1.state.ContactlessContext;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement.CardRiskManagement;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement.CardRiskManagementFactory;
import com.mastercard.mcbp.card.profile.CardRiskManagementData;
import com.mastercard.mcbp.card.profile.ContactlessPaymentData;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Advice;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.CdCvmSupport;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.PersistentTransactionContext;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalInformation;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionInformation;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.ConditionsOfUseNotSatisfied;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Tlv;
import com.mastercard.mobile_api.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Utility class to build a Compute Cryptographic Checksum Response APDU
 */
public class ComputeCryptographicChecksum {

    public static final ByteArray ATC_TAG = ByteArray.of((char) 0x9F36);

    /**
     * Working variable - Reference to the Crypto Service utility
     */
    private final static CryptoService sCryptoService
            = CryptoServiceFactory.getDefaultCryptoService();

    /**
     * Working variable - Store the current APDU being processed
     */
    private final ComputeCcCommandApdu mApdu;

    /**
     * Store the current Contactless Context
     */
    private final ContactlessContext mContext;

    /**
     * The POS Cardholder Interaction Information
     */
    private PosCardholderInteractionInformation mPoscii;

    /**
     * The Cryptogram Information Data
     */
    private final CryptogramInformationData mCid;

    /**
     * The Transaction Information as extracted from the C-APDU
     */
    private final TransactionInformation mTransactionInformation;

    /**
     * The Terminal Information as extracted from the C-APDU and PDOL data list received in the
     * GPO C-APDU
     */
    private final TerminalInformation mTerminalInformation;

    /**
     * Flag indicating whether the reader supports Persistent Transaction Context
     */
    private boolean mIsPersistentTransactionContextSupported;

    /**
     * Working variable - Store the current Contactless Profile
     */
    private final ContactlessPaymentData mContactlessPaymentData;

    /**
     * Profile Card Risk Management Data
     */
    private final CardRiskManagementData mCardRiskManagementData;

    /**
     * Working variable - The Contactless Transaction Listener
     */
    private final ContactlessTransactionListener mListener;

    /**
     * Contactless Transaction Context
     */
    private final ContactlessTransactionContext mTransactionContext;

    /**
     * Reference to the Transaction Credentials Manager
     */
    private final TransactionCredentialsManager mTransactionCredentialsManager;

    /**
     * Static factory method to build the object to handle the Compute Cryptographic Checksum
     *
     * @param apdu               The Compute CC Command APDU
     * @param contactlessContext The Contactless Context
     * @return An object of Compute Cryptographic Checksum
     */
    public static ComputeCryptographicChecksum of(final ComputeCcCommandApdu apdu,
                                                  final ContactlessContext contactlessContext) {
        if (apdu == null || contactlessContext == null) {
            throw new MppLiteException("Invalid data to initialize the ComputeCC function");
        }
        return new ComputeCryptographicChecksum(apdu, contactlessContext);
    }

    /**
     * Build the Cryptographic Checksum and set all the internal variables. Please note that the
     * constructor is not directly accessible. Please use the static factory instead.
     *
     * @param apdu    The Command APDU
     * @param context The Contactless Context.
     */
    private ComputeCryptographicChecksum(final ComputeCcCommandApdu apdu,
                                         final ContactlessContext context) {
        mApdu = apdu;
        mContext = context;
        mCid = new CryptogramInformationData();

        // We cache profile, listener, and transaction context to
        // simplify most of the methods and to make them more readable
        final MppLiteModule profile = context.getCardProfile();
        if (profile == null) {
            throw new MppLiteException("Invalid Card Profile object");
        }
        mContactlessPaymentData = profile.getContactlessPaymentData();
        mCardRiskManagementData = profile.getCardRiskManagementData();
        if (mContactlessPaymentData == null || mCardRiskManagementData == null) {
            throw new MppLiteException("Invalid Card Profile data");
        }

        mListener = context.getTransactionListener();
        mTransactionContext = context.getTransactionContext();

        mTransactionCredentialsManager = context.getTransactionCredentialsManager();

        // Set the UDOL in the context, otherwise we may not be able to get the right data
        // when building the Terminal Information
        mContext.getTransactionContext().setUdolData(apdu.getUdol());

        // Let's get all the data about this transaction
        mTerminalInformation = TerminalInformation.forMagstripe(
                mApdu, mTransactionContext.getPdolValues(), mTransactionContext.getUdolValues());

        mTransactionInformation = TransactionInformation.forMagstripe(
                readApplicationLabel(), mApdu, mTransactionContext.getUdolValues());

        mIsPersistentTransactionContextSupported =
                mTerminalInformation
                        .getPersistentTransactionContext() == PersistentTransactionContext.YES;
    }

    /**
     * Build the Compute CC Response
     *
     * @return The response APDU as byte[]
     * @throws com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException In case something
     *                                                                       goes wrong
     */
    public byte[] response() {
        // CCC.1.1 is taken care by the architecture design - I call this very method only if true
        // CCC.1.2
        if (!mContactlessPaymentData.isMagstripeDataValid()) {
            // CCC.1.3
            // cancelPayment();
            throw new ConditionsOfUseNotSatisfied("Magstripe data not found in profile");
        }

        // CCC.1.4 / CCC.1.5 / CCC.1.6 / CCC.1.7 - Taken care by the C-APDU initialization

        // CCC.1.8
        if (Utils.isTerminalOffline(mApdu.getTerminalType())) {
            // CCC.1.9
            // cancelPayment();
            throw new ConditionsOfUseNotSatisfied("The Terminal is offline");
        }

        // CCC.1.10
        initializeComputeCryptographicChecksumContext();

        // CCC.1.11 - We do not initialize the POSCII anymore

        return computeCcCrm();
    }


    /**
     * Initialize the Transaction Context for the Compute Cryptographic Checksum CCC.1.10
     */
    private void initializeComputeCryptographicChecksumContext() {
        mTransactionContext.setAmount(ByteArray.of(mApdu.getAuthorizedAmount()));
        mTransactionContext.setCurrencyCode(ByteArray.of(mApdu.getTransactionCurrencyCode()));
        mTransactionContext.setTrxDate(ByteArray.of(mApdu.getTransactionDate()));
        mTransactionContext.setTrxType(ByteArray.of(mApdu.getTransactionType()));
        mTransactionContext.setUnpredictableNumber(ByteArray.of(mApdu.getUnpredictableNumber()));
    }

    /**
     * Perform the Card Risk Management detailed in MCBP MPA Functional Description 1.0 (CCC.3.x)
     *
     * @return the response APDU
     */
    private byte[] computeCcCrm() {
        final byte[] ciacDeclineOnPpms = mContactlessPaymentData.getCiacDeclineOnPpms().getBytes();
        final byte[] countryCode = mCardRiskManagementData.getCrmCountryCode().getBytes();

        // Get the MasterCard advice
        final CardRiskManagement cardRiskManagement =
                CardRiskManagementFactory.forMagstripe(mApdu,
                                                       mContext,
                                                       ciacDeclineOnPpms,
                                                       countryCode,
                                                       mTransactionCredentialsManager);
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
                                                magstripeCardRiskActionListener());

        return transactionOutcomeBuilder.buildResponse();
    }

    /**
     * Define the Magstripe behaviour for Card Risk actions
     *
     * @return The MCHIP Card Risk Action Listener
     */
    private TransactionOutcomeBuilderListener<byte[]> magstripeCardRiskActionListener() {
        return new TransactionOutcomeBuilderListener<byte[]>() {
            @Override
            public byte[] approveOnline(final List<Reason> walletReasons,
                                        final TransactionCredentials credentials) {
                mContext.getTransactionContext().setResult(
                        TransactionSummary.AUTHORIZE_ONLINE);
                mPoscii = PosCardholderInteractionInformation
                        .forApproveMagstripe(mContext.isCvmEntered(), walletReasons);
                return online(credentials);
            }

            @Override
            public byte[] abort(final List<Reason> walletReasons) {
                mContext.getTransactionContext().setResult(
                        TransactionSummary.ABORT_PERSISTENT_CONTEXT);
                mPoscii = PosCardholderInteractionInformation
                        .forAbortMagstripe(mContext.isCvmEntered(), walletReasons);
                return ComputeCryptographicChecksum.this.decline(getRandomCredentials());
            }

            @Override
            public byte[] decline(final List<Reason> walletReasons) {
                if (walletReasons.contains(Reason.CONTEXT_NOT_MATCHING)) {
                    return error(walletReasons);
                }
                mContext.getTransactionContext().setResult(TransactionSummary.DECLINE);
                mPoscii = PosCardholderInteractionInformation
                        .forDeclineMagstripe(mContext.isCvmEntered(), walletReasons);
                return ComputeCryptographicChecksum.this.decline(getRandomCredentials());
            }

            @Override
            public byte[] error(final List<Reason> walletReasons) {
                setTransactionSummaryForError(walletReasons);
                mPoscii = PosCardholderInteractionInformation
                        .forErrorMagstripe(mContext.isCvmEntered(), walletReasons);
                return ComputeCryptographicChecksum.this.decline(getRandomCredentials());
            }

            @Override
            public byte[] authenticate(final List<Reason> walletReasons,
                                       final TransactionCredentials credentials) {
                throw new MppLiteException("Authentication is not supported for Magstripe!");
            }
        };
    }

    /**
     * Utility function to set the Transaction summary in case of error conditions
     * @param walletReasons The list of Wallet reasons that is used to determine the error type
     *                      to be reported to the UI
     */
    private void setTransactionSummaryForError(final List<Reason> walletReasons) {
        if (walletReasons.contains(Reason.CONTEXT_NOT_MATCHING)) {
            // CCC.2.7
            mContext.getTransactionContext().setResult(TransactionSummary.ERROR_CONTEXT_CONFLICT);
        } else {
            mContext.getTransactionContext().setResult(TransactionSummary.ERROR);
        }
    }

    /**
     * Prepare a response for online approval to be sent back to the terminal according to CCC.6.x
     *
     * @return The Response APDU
     * @throws MppLiteException if something goes wrong
     */
    private byte[] online(final TransactionCredentials transactionCredentials) {
        // Get the Credentials
        final byte[] atc = transactionCredentials.getAtc().getBytes();
        // We need a copy of the ATC as the original value will be cleared when erasing credentials
        mTransactionContext.setAtc(ByteArray.of(atc));

        // CCC.4.1 - indicate online decision
        mCid.indicateOnlineDecision();

        // CCC4.2 build input to message authentication code
        final byte[] input = buildInputForDes3Generation(atc, mApdu.getUnpredictableNumber());

        // CCC.4.3
        final byte[] umdSessionKey = transactionCredentials.getUmdSessionKey().getBytes();
        final byte[] mdSessionKey = transactionCredentials.getMdSessionKey().getBytes();

        final CryptoService.TransactionCryptograms cryptograms;
        try {
            cryptograms =
                    sCryptoService.buildComputeCcCryptograms(input, umdSessionKey, mdSessionKey);
        } catch (McbpCryptoException e) {
            throw new MppLiteException(e.getMessage());
        }

        final byte[] desUmd = cryptograms.getUmdCryptogram();
        final byte[] desMd = cryptograms.getMdCryptogram();

        //Clear the session keys as we have generated the cryptograms
        Utils.clearByteArray(umdSessionKey);
        Utils.clearByteArray(mdSessionKey);

        // CCC.4.4 Calculate CVC3 and CryptoATC
        final byte[] cryptogram = buildCvc3ApplicationCryptogram(desUmd, desMd, atc);

        //Clearing sensitive data
        transactionCredentials.wipe();
        Utils.clearByteArray(desUmd);
        Utils.clearByteArray(desMd);

        final ByteArray applicationCryptogram = ByteArray.of(cryptogram);

        final byte[] cvc3 = applicationCryptogram.copyOfRange(0, 2).getBytes();
        final byte[] cryptoAtc = applicationCryptogram.copyOfRange(2, 4).getBytes();

        // CCC.4.5 - Generate response
        mTransactionContext.setCryptogram(applicationCryptogram);
        final byte[] response = buildCvc3Response(cvc3, cryptoAtc);

        final byte[] responseApdu;

        try {
            responseApdu = ResponseApduFactory.successfulProcessing(response);
        } catch (final InvalidInput e) {
            Utils.clearByteArray(response);
            throw new MppLiteException(e.getMessage());
        }

        // CCC.4.6
        final ContactlessLog contactlessLog = getContactlessLog(ByteArray.of(cvc3),
                                                                ByteArray.of(cryptoAtc));
        mListener.onContactlessTransactionCompleted(contactlessLog);

        // CCC.4.7 - cancelPayment() It is handled when the APDU is returned
        Utils.clearByteArray(cvc3);
        Utils.clearByteArray(cryptoAtc);

        return responseApdu;
    }

    /**
     * Utility function to build the input for the Compute CC DES 3 Generation (CCC.4.2)
     *
     * @param atc The Application Transaction Counter of the credentials being used
     * @param un  The unpredictable number
     * @return the input byte[]
     */
    private byte[] buildInputForDes3Generation(final byte[] atc, final byte[] un) {

        final byte[] pinIvCvc3 = mContactlessPaymentData.getPinIvCvc3Track2().getBytes();
        final byte[] input = new byte[8];

        System.arraycopy(pinIvCvc3, 0, input, 0, pinIvCvc3.length);
        System.arraycopy(un, 0, input, pinIvCvc3.length, un.length);
        System.arraycopy(atc, 0, input, pinIvCvc3.length + un.length, atc.length);

        return input;
    }

    /**
     * Build the CVC3 Application Cryptogram using CVC3 and Crypto ATC
     *
     * @param desUmd The UMD DES 3 data
     * @param desMd  The MD DES 3 data
     * @return The Application Cryptogram
     */
    private byte[] buildCvc3ApplicationCryptogram(final byte[] desUmd,
                                                  final byte[] desMd,
                                                  final byte[] atc) {
        // CCC4.4 Calculate CVC3 and CryptoATC
        final byte[] applicationCryptogram;

        // Step-1 and Step-2
        final int a = Utils.wordToChar(desMd[0], desMd[1]) % 1000;
        final int b = Utils.wordToChar(desUmd[6], desUmd[7]) % 1000;
        final int atcMod = Utils.wordToChar(atc[0], atc[1]) % 100;

        // Step-3
        final int cryptoAtcComponent = a + 1000 * (atcMod / 10);
        final int cvc3Component = b + 1000 * (atcMod % 10);

        // Step-4
        final int allocationSize = Integer.SIZE / Byte.SIZE;
        final byte[] cryptoAtcBuffer = ByteBuffer
                .allocate(allocationSize).order(ByteOrder.BIG_ENDIAN)
                .putInt(cryptoAtcComponent).array();

        final byte[] cryptoAtc = new byte[2];
        cryptoAtc[0] = cryptoAtcBuffer[2];
        cryptoAtc[1] = cryptoAtcBuffer[3];

        // Step-5
        final byte[] cvc3Buffer = ByteBuffer
                .allocate(allocationSize).order(ByteOrder.BIG_ENDIAN)
                .putInt(cvc3Component).array();

        final byte[] cvc3 = new byte[2];
        cvc3[0] = cvc3Buffer[2];
        cvc3[1] = cvc3Buffer[3];

        // Step-6
        applicationCryptogram =
                new byte[]{cvc3[0], cvc3[1], cryptoAtc[0], cryptoAtc[1], 0x00, 0x00, 0x00, 0x00};

        // Clear temporary data
        Utils.clearByteArray(cryptoAtc);
        Utils.clearByteArray(cvc3);

        return applicationCryptogram;
    }

    /**
     * Build the CVC3 Response for the Compute Cryptographic Checksum (CCC.5.5)
     *
     * @param cvc3      The CVC3 value as calculated in CCC.5.4
     * @param cryptoAtc The Crypto ATC value as calculated in CCC.5.4
     * @return The Compute Cryptographic Checksum response
     */
    private byte[] buildCvc3Response(final byte[] cvc3, final byte[] cryptoAtc) {
        final byte[] cvc3Track2Tag = new byte[]{(byte) 0x9F, (byte) 0x61};
        final byte[] cvc3Track1Tag = new byte[]{(byte) 0x9F, (byte) 0x60};
        final byte[] atcTag = new byte[]{(byte) 0x9F, (byte) 0x36};

        final byte[] cvc3Track2Tlv = Tlv.create(cvc3Track2Tag, cvc3);
        final byte[] cvc3Track1Tlv = Tlv.create(cvc3Track1Tag, cvc3);
        final byte[] atcTlv = Tlv.create(atcTag, cryptoAtc);

        final byte[] posciiTlv;

        if (mIsPersistentTransactionContextSupported) {
            // Is mobile supported
            posciiTlv = mPoscii.getTlv();
        } else {
            posciiTlv = new byte[0];
        }

        final int responseDataLength = cvc3Track2Tlv.length + cvc3Track1Tlv.length + atcTlv.length +
                                       posciiTlv.length;

        final byte[] responseData = new byte[responseDataLength];

        int start = 0;
        int noBytes = cvc3Track2Tlv.length;
        System.arraycopy(cvc3Track2Tlv, 0, responseData, start, noBytes);

        start += noBytes;
        noBytes = cvc3Track1Tlv.length;
        System.arraycopy(cvc3Track1Tlv, 0, responseData, start, noBytes);

        start += noBytes;
        noBytes = atcTlv.length;
        System.arraycopy(atcTlv, 0, responseData, start, noBytes);

        if (posciiTlv.length > 0) {
            start += noBytes;
            noBytes = posciiTlv.length;
            System.arraycopy(posciiTlv, 0, responseData, start, noBytes);
        }

        final byte[] response = Tlv.create(new byte[]{0x77}, responseData);

        Utils.clearByteArray(cvc3Track2Tlv);
        Utils.clearByteArray(cvc3Track1Tlv);
        Utils.clearByteArray(atcTlv);
        Utils.clearByteArray(posciiTlv);
        Utils.clearByteArray(responseData);

        return response;
    }

    /**
     * Prepare a decline response to be sent back to the terminal according to CCC.5.x
     *
     * @return The Response APDU
     * @throws MppLiteException if something goes wrong
     */
    private byte[] decline(final TransactionCredentials transactionCredentials) {
        // CCC.5.1
        // Cid is set to 0x00 when the object is created
        // We need a copy of the ATC as the original value will be cleared when erasing credentials
        mTransactionContext.setAtc(ByteArray.of(transactionCredentials.getAtc()));

        // CCC.5.2
        final byte[] response;

        // decline
        if (!mIsPersistentTransactionContextSupported) {
            // CCC.5.3
            mContext.getTransactionContext().setResult(TransactionSummary.ABORT_UNKNOWN_CONTEXT);
            response = ResponseApduFactory.securityStatusNotSatisfied();
        } else {
            // CCC.5.4
            final byte[] atcTlv = Tlv.create(ATC_TAG, transactionCredentials.getAtc()).getBytes();

            final byte[] posciiTlv = mPoscii.getTlv();
            final byte[] responseData = new byte[atcTlv.length + posciiTlv.length];
            System.arraycopy(atcTlv, 0, responseData, 0, atcTlv.length);
            System.arraycopy(posciiTlv, 0, responseData, atcTlv.length, posciiTlv.length);

            final byte[] responseTlv = Tlv.create(new byte[]{(byte) 0x77}, responseData);

            try {
                response = ResponseApduFactory.successfulProcessing(responseTlv);
            } catch (InvalidInput e) {
                throw new MppLiteException(e.getMessage());
            } finally {
                Utils.clearByteArray(atcTlv);
                Utils.clearByteArray(posciiTlv);
                Utils.clearByteArray(responseData);
                Utils.clearByteArray(responseTlv);
            }
        }
        // CCC. 5.5
        final ContactlessLog contactlessLog = getContactlessLog(null, null);
        mListener.onContactlessTransactionCompleted(contactlessLog);

        // CCC.5.6
        // cancelPayment();

        return response;
    }

    /**
     * Get Random Transaction Credentials
     *
     * @return A set of randomly generated transaction credentials
     */
    private TransactionCredentials getRandomCredentials() {
        return mTransactionCredentialsManager.getRandomCredentials();
    }

    /**
     * Utility function to generate the Contactless Log
     *
     * @return The contactless log
     */
    private ContactlessLog getContactlessLog(final ByteArray cvc3,
                                             final ByteArray cryptoAtc) {
        return ContactlessLogImpl.forMagstripe
                (mTransactionContext,
                 mContactlessPaymentData,
                 cvc3,
                 cryptoAtc,
                 mTerminalInformation.getCdCvmSupport() == CdCvmSupport.YES);
    }

    /**
     * Utility function to read the Application Label from the SELECT R-APDU
     */
    private byte[] readApplicationLabel() {
        final byte[] selectResponse;
        if (mTransactionContext.isAlternateAid()) {
            selectResponse = mContactlessPaymentData.getAlternateContactlessPaymentData()
                                                    .getPaymentFci().getBytes();
        } else {
            selectResponse = mContactlessPaymentData.getPaymentFci().getBytes();
        }
        return ContactlessUtils.readApplicationLabel(selectResponse);
    }
}
