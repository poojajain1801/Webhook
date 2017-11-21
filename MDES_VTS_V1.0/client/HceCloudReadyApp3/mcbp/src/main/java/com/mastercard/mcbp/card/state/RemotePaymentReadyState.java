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

package com.mastercard.mcbp.card.state;

import com.mastercard.mcbp.card.BusinessLogicTransactionInformation;
import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpOutputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpResult;
import com.mastercard.mcbp.card.mobilekernel.MobileKernel;
import com.mastercard.mcbp.card.mobilekernel.RemotePaymentResultCode;
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import com.mastercard.mcbp.card.transactionlogging.TransactionIdentifier;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mcbp.lde.services.LdeMcbpCardService;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.datamanagement.UnexpectedData;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionLoggingError;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionStorageLimitReach;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.nio.charset.Charset;

/**
 * In this state the MPP Lite has been initialize to handle a contactless transaction
 */
class RemotePaymentReadyState extends GenericState {
    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    /***
     * Default constructor for the state. The context must be provided
     *
     * @param cardContext The MCBP Card State context
     */
    public RemotePaymentReadyState(final CardContext cardContext,
                                   final MppLite mppLite) {
        super(cardContext, mppLite);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startContactlessPayment(final BusinessLogicTransactionInformation
                                                businessLogicTransactionInformation)
            throws InvalidCardStateException, McbpCryptoException, InvalidInput, LdeNotInitialized {
        throw new InvalidCardStateException("Invalid state for startContactlessPayment");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopContactLess() throws InvalidCardStateException {
        throw new InvalidCardStateException("stopContactLess");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRemotePayment() throws InvalidCardStateException, DsrpIncompatibleProfile {
        throw new InvalidCardStateException("Invalid State for startRemotePayment");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DsrpResult getTransactionRecord(final DsrpInputData dsrpInputData)
            throws InvalidCardStateException {
        final LdeMcbpCardService ldeMcbpCardService = getCardContext().getLdeMcbpCardService();

        if (ldeMcbpCardService == null) {
            toInitializedState(false);
            return new DsrpResult((RemotePaymentResultCode.INTERNAL_ERROR), null);
        }

        final DsrpResult dsrpResult;
        try {
            // Online is always set to allowed (hardcoded True)
            dsrpResult = MobileKernel.generateDsrpData(dsrpInputData, getMppLite());
        } catch (final UnexpectedData e) {
            toInitializedState(false);
            return new DsrpResult(RemotePaymentResultCode.ERROR_UNEXPECTED_DATA, null);
        } catch (final McbpCryptoException | InvalidInput e) {
            toInitializedState(false);
            return new DsrpResult(RemotePaymentResultCode.INTERNAL_ERROR, null);
        }

        if (dsrpResult.getCode() != RemotePaymentResultCode.OK) {
            toInitializedState(false);
            return new DsrpResult(RemotePaymentResultCode.INTERNAL_ERROR, null);
        }

        // add transaction record to log
        final ByteArray atc = ByteArray.of((char) dsrpResult.getData().getAtc());

        final String digitizedCardId = getCardContext().getDigitizedCardId();

        try {
            ldeMcbpCardService.insertOrUpdateTransactionCredentialStatus
                    (digitizedCardId, atc, TransactionCredentialStatus.Status.USED_FOR_DSRP);
        } catch (InvalidInput invalidInput) {
            // If I cannot update the usage of the Credentials in the log we return an error
            mLogger.d(invalidInput.getMessage());
        }

        final TransactionLog log = TransactionLog.forRemotePayment(digitizedCardId,
                                                                   dsrpInputData,
                                                                   dsrpResult.getData(),
                                                                   getTransactionId(
                                                                           dsrpResult.getData()),
                                                                   false, false);

        try {
            ldeMcbpCardService.addToLog(log);
        } catch (final LdeNotInitialized | TransactionStorageLimitReach |
                TransactionLoggingError e) {
            toInitializedState(false);
            return new DsrpResult(RemotePaymentResultCode.INTERNAL_ERROR, null);
        }

        toInitializedState(false);
        return dsrpResult;
    }


    /**
     * generate unique identifier for a transaction based on transaction data
     *
     * @param dsrpOutputData transaction data
     * @return unique identifier generated using the transaction data
     */
    private ByteArray getTransactionId(final DsrpOutputData dsrpOutputData) {
        ByteArray transactionId;
        switch (dsrpOutputData.getCryptogramType()) {
            case UCAF:

                try {
                    transactionId = ByteArray.of(
                            TransactionIdentifier.generateDsrpWithUcafTransactionIdentifier(
                                    ByteArray.of(dsrpOutputData.getPan()
                                                               .getBytes(Charset.defaultCharset())),
                                    dsrpOutputData.getTransactionCryptogramData()));
                } catch (McbpCryptoException | InvalidInput e) {
                    mLogger.d(e.getMessage());
                    // Something went wrong we do add any transaction id
                    transactionId = ByteArray.of("");
                }
                break;
            case DE55:
            default:
                transactionId = ByteArray.get(0);
                break;
        }
        return transactionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArray processApdu(final ByteArray apdu) throws InvalidCardStateException {
        // If we tap while waiting for a remote payment we respond directly that the MPP Lite is
        // busy
        return ByteArray.of(ResponseApduFactory.conditionsOfUseNotSatisfied());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processOnDeactivated() {
        // We ignore this message if it happens as we are not interested in Contactless events
    }
}
