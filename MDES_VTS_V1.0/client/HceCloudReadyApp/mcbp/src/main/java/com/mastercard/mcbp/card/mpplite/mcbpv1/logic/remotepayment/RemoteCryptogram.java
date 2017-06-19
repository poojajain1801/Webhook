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

package com.mastercard.mcbp.card.mpplite.mcbpv1.logic.remotepayment;

import com.mastercard.mcbp.card.mobilekernel.CryptogramInput;
import com.mastercard.mcbp.card.mobilekernel.TransactionOutput;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionOutcomeBuilder;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionOutcomeBuilderListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement.CardVerificationResults;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.AdditionalCheckTable;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentials;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless.CryptogramInformationData;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.CryptogramOutput;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement.CardRiskManagement;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement.CardRiskManagementFactory;
import com.mastercard.mcbp.card.mpplite.mcbpv1.state.RemotePaymentContext;
import com.mastercard.mcbp.card.profile.CardRiskManagementData;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.card.profile.RemotePaymentData;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Advice;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalInformation;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionInformation;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidDigitizedCardProfile;
import com.mastercard.mcbp.utils.exceptions.generic.InternalError;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * Single Tone utility object to generate Remote Cryptograms
 */
public final class RemoteCryptogram {
    static final int IAD_CVR_OFFSET = 2;
    static final int IAD_DAC_IDN_OFFSET = 8;
    static final int IAD_MD_AC_LENGTH = 5;
    static final int IAD_MD_AC_OFFSET = 11;

    /***
     * Instance of the default crypto service factory
     */
    private final CryptoService mCryptoService = CryptoServiceFactory.getDefaultCryptoService();

    /***
     * The MPP Lite Profile for Remote Payment
     */
    private final RemotePaymentData mProfile;

    /**
     * The MPP Lite Profile related to Card Risk Management Data
     */
    private final CardRiskManagementData mCardRiskManagementData;

    /**
     * Transaction Credentials
     */
    private final TransactionCredentialsManager mTransactionCredentialsManager;

    /**
     * The transaction cryptogram input
     */
    private final CryptogramInput mCryptogramInput;

    /**
     * The Card Verification Results
     */
    private final CardVerificationResults mCvr;

    /**
     * Transaction Credentials
     */
    private final RemotePaymentContext mContext;

    /**
     * Transaction Information
     */
    private final TransactionInformation mTransactionInformation;

    /**
     * Terminal Information
     */
    private final TerminalInformation mTerminalInformation;

    /**
     * Cryptogram Information Data
     */
    private final CryptogramInformationData mCid;

    /**
     * @param profile The MPP Lite Profile module
     * @param input   The Cryptogram Input
     * @param context The Remote Payment Transaction Context
     * @throws InvalidDigitizedCardProfile In case the Card Profile does not contain Remote Payment
     *                                     information
     */
    public RemoteCryptogram(final MppLiteModule profile,
                            final CryptogramInput input,
                            final RemotePaymentContext context) throws InvalidDigitizedCardProfile {
        this.mProfile = profile.getRemotePaymentData();
        if (mProfile == null) {
            throw new InvalidDigitizedCardProfile("Remote payment data not found in profile");
        }
        this.mCardRiskManagementData = profile.getCardRiskManagementData();
        if (mCardRiskManagementData == null) {
            throw new InvalidDigitizedCardProfile("Card Risk Management data not found in profile");
        }
        this.mCryptogramInput = input;
        this.mContext = context;
        this.mTransactionCredentialsManager = context.getTransactionCredentialsManager();

        this.mCvr = CardVerificationResults.withIssuerApplicationData(
                mProfile.getIssuerApplicationData().getBytes());
        this.mTransactionInformation = TransactionInformation.forRemotePayment(mCryptogramInput);
        this.mCid = new CryptogramInformationData();
        this.mTerminalInformation = TerminalInformation.forRemotePayment(mCryptogramInput);
    }

    /**
     * Build the Remote Cryptogram
     *
     * @return The Transaction output object
     */
    public final TransactionOutput build() {
        // REM.1.6, REM.1.7, REM.1.8
        evaluateTerminalCountryCode(mCryptogramInput);

        // REM.1.9 and REM.1.10
        additionalCheckTable(buildCdol(mCryptogramInput));

        // REM.2.1
        if (mContext.isCvmEntered()) {
            mCvr.indicateCdCvmPerformed();
        } else {
            mCvr.indicateCdCvmNotPerformed();
        }

        // REM.2.2
        final byte[] ciacDecline = mProfile.getCiacDecline().getBytes();
        final CardRiskManagement cardRiskManagement =
                CardRiskManagementFactory.forRemotePayment(mCvr,
                                                           ciacDecline,
                                                           mTransactionCredentialsManager);

        final Advice masterCardAdvice =
                cardRiskManagement.getMasterCardAdvice(mTransactionInformation,
                                                       mContext.isConsentGiven(),
                                                       mContext.isCvmEntered());

        final Advice walletAdvice = mContext.getWalletAdvice(masterCardAdvice,
                                                             mTransactionInformation,
                                                             mTerminalInformation);

        // Take actions based on the final advice
        final TransactionOutcomeBuilder<TransactionOutput> transactionOutcomeBuilder =
                new TransactionOutcomeBuilder<>(
                        walletAdvice,
                        mTransactionInformation,
                        mTerminalInformation,
                        mTransactionCredentialsManager,
                        remotePaymentCardRiskActionListener());

        return transactionOutcomeBuilder.buildResponse();
    }

    /**
     * Define the Remote Payment behaviour for Card Risk actions
     *
     * @return The Remote Payment Card Risk Action Listener
     */
    private TransactionOutcomeBuilderListener<TransactionOutput>
    remotePaymentCardRiskActionListener() {
        return new TransactionOutcomeBuilderListener<TransactionOutput>() {
            @Override
            public TransactionOutput approveOnline(final List<Reason> walletReasons,
                                                   final TransactionCredentials credentials) {
                mCid.indicateOnlineDecision();
                mCvr.indicateArqcReturnedInFirstAndAcNotRequestedInSecondGenerateAc();
                return buildOutput(mCryptogramInput, mCid.getValue(), credentials);
            }

            @Override
            public TransactionOutput abort(final List<Reason> walletReasons) {
                if (walletReasons.contains(Reason.MISSING_CONSENT) || walletReasons
                        .contains(Reason.MISSING_CD_CVM)) {
                    mCvr.indicateCvmRequiredNotSatisfied();
                }
                return decline(walletReasons);
            }

            @Override
            public TransactionOutput decline(final List<Reason> walletReasons) {
                mCvr.indicateAacReturnedInFirstAndAcNotRequestedInSecondGenerateAc();
                mCid.indicateDecline();
                return buildOutput(mCryptogramInput, mCid.getValue(), getRandomCredentials());
            }

            @Override
            public TransactionOutput error(final List<Reason> walletReasons) {
                return decline(walletReasons);
            }

            @Override
            public TransactionOutput authenticate(final List<Reason> walletReasons,
                                                  final TransactionCredentials credentials) {
                return decline(walletReasons);
            }
        };
    }

    /***
     * Build the Transaction Output
     *
     * @param input                  The Cryptogram Input
     * @param cid                    The Cid has computed during REM.2.5 or REM.2.7
     * @param transactionCredentials The Transaction Credentials to be used for the cryptogram
     *                               generation
     */
    private TransactionOutput buildOutput(final CryptogramInput input,
                                          final byte cid,
                                          final TransactionCredentials transactionCredentials) {

        // REM.3.1 and REM.3.2
        // We need a copy of the ATC as the original value will be cleared when erasing credentials
        final ByteArray atc = ByteArray.of(transactionCredentials.getAtc());
        final ByteArray acInput = buildApplicationCryptogramInput(input, atc);

        // REM.3.3 compute cryptogram
        final ByteArray umdSessionKey = transactionCredentials.getUmdSessionKey();
        final ByteArray mdSessionKey = transactionCredentials.getMdSessionKey();

        final CryptoService.TransactionCryptograms cryptograms =
                buildApplicationCryptogram(acInput, umdSessionKey, mdSessionKey);

        final ByteArray umdAc = ByteArray.of(cryptograms.getUmdCryptogram());
        final ByteArray mdAc = ByteArray.of(cryptograms.getMdCryptogram());

        // Clear the data as the cryptogram have been copied into a new Byte Array
        Utils.clearByteArray(cryptograms.getUmdCryptogram());
        Utils.clearByteArray(cryptograms.getMdCryptogram());

        // REM.3.4
        final ByteArray issuerApplicationData = buildIssuerApplicationData(mdAc);

        final CryptogramOutput cryptogramOutput =
                new CryptogramOutput(atc, issuerApplicationData, umdAc, cid);

        // This does also part of the REM.1.5 tasks as well as finalizing the output
        return new TransactionOutput(mProfile.getTrack2EquivalentData(),
                                     mProfile.getPan(),
                                     mProfile.getPanSequenceNumber(),
                                     mProfile.getAip(),
                                     mProfile.getApplicationExpiryDate(),
                                     mContext.isCvmEntered(),
                                     cryptogramOutput);
    }

    /***
     * Build the input for the Application Cryptogram generation
     *
     * @param input The Cryptogram Input data structure
     * @param atc   The ATC of the current credentials
     * @return The input for the Application Cryptogram
     */
    private ByteArray buildApplicationCryptogramInput(final CryptogramInput input,
                                                      final ByteArray atc) {
        // REM.3.1
        final byte[] cvrMaskAnd = mProfile.getCvrMaskAnd().getBytes();

        mCvr.applyMaskAnd(cvrMaskAnd);

        // REM.3.2
        final ByteArray applicationCryptogramInput = buildCdol(input);

        // adding aip
        applicationCryptogramInput.append(mProfile.getAip());
        applicationCryptogramInput.append(atc);
        applicationCryptogramInput.append(ByteArray.of(mCvr.getBytes()));

        return applicationCryptogramInput;
    }

    /***
     * Utility function to build the Issuer Application Data
     *
     * @param mdAc The MD Application Cryptogram
     * @return The Issuer Application Data formatted as specified in MCBP specifications
     */
    private ByteArray buildIssuerApplicationData(final ByteArray mdAc) {
        // REM.3.4
        final byte[] iadProfile = mProfile.getIssuerApplicationData().getBytes();
        final byte[] issuerApplicationData = new byte[iadProfile.length];
        final byte[] cvr = mCvr.getBytes();

        // Populate the Issuer Application Data
        System.arraycopy(iadProfile, 0, issuerApplicationData, 0, iadProfile.length);
        System.arraycopy(cvr, 0, issuerApplicationData, IAD_CVR_OFFSET, cvr.length);

        // output.cryptoOutput.issuerApplicationData[9 : 10] := '0000'
        issuerApplicationData[IAD_DAC_IDN_OFFSET] = 0x00;
        issuerApplicationData[IAD_DAC_IDN_OFFSET + 1] = 0x00;

        // store bytes 1:5 of the 8-byte application cryptogram
        System.arraycopy(mdAc.getBytes(), 0, issuerApplicationData,
                         IAD_MD_AC_OFFSET, IAD_MD_AC_LENGTH);

        final ByteArray iad = ByteArray.of(issuerApplicationData);

        // Clear the temporary Issuer Application Data as we have copied it
        Utils.clearByteArray(issuerApplicationData);

        return iad;
    }

    /**
     * Build Application Cryptograms
     *
     * @param input         The Application Cryptogram input
     * @param umdSessionKey The UMD session key to be used for this cryptogram
     * @param mdSessionKey  The MD session key to be used for this cryptogram
     * @return The generated application cryptogram
     */
    private CryptoService.TransactionCryptograms buildApplicationCryptogram(
            final ByteArray input,
            final ByteArray umdSessionKey,
            final ByteArray mdSessionKey) {
        try {
            return mCryptoService.buildGenerateAcCryptograms(input.getBytes(),
                                                             umdSessionKey.getBytes(),
                                                             mdSessionKey.getBytes());
        } catch (final McbpCryptoException e) {
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Perform the Additional Check Table if needed (REM.1.9 and REM.1.10)
     */
    private void additionalCheckTable(final ByteArray cdol) {
        final byte[] cvrMaskAnd = mProfile.getCvrMaskAnd().getBytes();
        final byte[] ciacDecline = mProfile.getCiacDecline().getBytes();

        final byte[] additionalCheckTable =
                mCardRiskManagementData.getAdditionalCheckTable().getBytes();
        AdditionalCheckTable.Result result = AdditionalCheckTable.process(cdol.getBytes(),
                                                                          cvrMaskAnd,
                                                                          ciacDecline,
                                                                          additionalCheckTable);

        // Set the CVR accordingly
        if (result == AdditionalCheckTable.Result.MATCH_FOUND) {
            mCvr.indicateMatchFoundInAdditionalCheckTable();
        } else if (result == AdditionalCheckTable.Result.MATCH_NOT_FOUND) {
            mCvr.indicateMatchNotFoundInAdditionalCheckTable();
        }
    }

    /***
     * Evaluate whether it is a domestic or international transaction
     *
     * @param input The Cryptogram Input
     */
    private void evaluateTerminalCountryCode(final CryptogramInput input) {

        final ByteArray terminalCountryCode = input.getTerminalCountryCode();
        final ByteArray cardCountryCode = mCardRiskManagementData.getCrmCountryCode();

        if (terminalCountryCode == null ||
            !Arrays.equals(terminalCountryCode.getBytes(), cardCountryCode.getBytes())) {
            // REM.1.8 - indicate "International transaction in cvr:
            mCvr.indicatedInternationalTransaction();
        } else {
            // REM.1.8 - indicate domestic transaction
            mCvr.indicateDomesticTransaction();
        }
    }

    /**
     * Utility function to retrieve random credentials
     */
    private TransactionCredentials getRandomCredentials() {
        return mTransactionCredentialsManager.getRandomCredentials();
    }

    /***
     * Build the cdol data that is used during additional check table and application cryptogram
     * generation
     *
     * @param input The Cryptogram Input data
     * @return The CDOL equivalent data to be used for additional check table and application
     * cryptogram
     */
    private static ByteArray buildCdol(final CryptogramInput input) {
        ByteArray cdol;
        if (input.getAmountAuthorized() == null) {
            cdol = ByteArray.of(new byte[6]);
        } else {
            cdol = ByteArray.of(input.getAmountAuthorized());
        }
        if (input.getAmountOther() == null) {
            cdol.append(ByteArray.of(new byte[6]));
        } else {
            cdol.append(input.getAmountOther());
        }
        if (input.getTerminalCountryCode() == null) {
            cdol.append(ByteArray.of(new byte[2]));
        } else {
            cdol.append(input.getTerminalCountryCode());
        }
        if (input.getTvr() == null) {
            cdol.append(ByteArray.of(new byte[5]));
        } else {
            cdol.append(input.getTvr());
        }
        if (input.getTransactionCurrencyCode() == null) {
            cdol.append(ByteArray.of(new byte[2]));
        } else {
            cdol.append(input.getTransactionCurrencyCode());
        }
        if (input.getTransactionDate() == null) {
            cdol.append(ByteArray.of(new byte[3]));
        } else {
            cdol.append(input.getTransactionDate());
        }
        if (input.getTransactionType() == null) {
            cdol.append(ByteArray.of(new byte[1]));
        } else {
            cdol.append(input.getTransactionType());
        }
        if (input.getUnpredictableNumber() == null) {
            cdol.append(ByteArray.of(new byte[4]));
        } else {
            cdol.append(input.getUnpredictableNumber());
        }
        return cdol;
    }
}
