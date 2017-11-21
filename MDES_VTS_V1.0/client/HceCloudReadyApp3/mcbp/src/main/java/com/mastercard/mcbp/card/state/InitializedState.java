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
import com.mastercard.mcbp.card.CardListener;
import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpResult;
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLog;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionSummary;
import com.mastercard.mcbp.card.transactionlogging.TransactionIdentifier;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mcbp.lde.services.LdeMcbpCardService;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.userinterface.DisplayStatus;
import com.mastercard.mcbp.userinterface.DisplayTransactionInfo;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionLoggingError;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionStorageLimitReach;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mcbp.utils.lde.Utils;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mcbp.utils.task.McbpAsyncTask;
import com.mastercard.mcbp.utils.task.McbpTaskFactory;
import com.mastercard.mcbp.utils.task.McbpTaskListener;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.nio.charset.Charset;

import static com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus.Status
        .UNUSED_DISCARDED;
import static com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus.Status
        .USED_FOR_CONTACTLESS;
import static com.mastercard.mcbp.userinterface.DisplayStatus.CANCELED;

/**
 * In this state the MCBP Card is ready to accept both contactless and remote payment requests
 */
class InitializedState extends GenericState {
    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    /***
     * Default constructor for the state. The context must be provided
     *
     * @param cardContext The MCBP Card State context
     * @param mppLite     The MPP Lite
     */
    public InitializedState(final CardContext cardContext,
                            final MppLite mppLite) {
        super(cardContext, mppLite);
    }

    /**
     * {@inheritDoc}
     *
     * @param businessLogicTransactionInformation
     */
    @Override
    public void startContactlessPayment(final BusinessLogicTransactionInformation
                                                businessLogicTransactionInformation)
            throws InvalidCardStateException {
        toContactlessReadyState(createContactlessListener(), businessLogicTransactionInformation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopContactLess() {
        // Do nothing, we stay in this state
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRemotePayment()
            throws InvalidCardStateException, DsrpIncompatibleProfile {
        getMppLite().startRemotePayment();
        toRemotePaymentReadyState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DsrpResult getTransactionRecord(final DsrpInputData dsrpInputData)
            throws InvalidCardStateException {
        throw new InvalidCardStateException("Invalid State for getTransactionRecord");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArray processApdu(final ByteArray apdu) throws InvalidCardStateException {
        // If we are in this state, it means that this is the very first APDU of a new transaction.
        // We need to prepare the MPP for Contactless and let that state handle the APDU.
        toContactlessReadyState();
        // We pass the APDU to newly created state as we need to delegate the management of the
        // incoming command APDU
        return getCardContext().getCurrentState().processApdu(apdu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processOnDeactivated() {
        // Do nothing. We stay in initialized state
    }

    /**
     * Create a Contactless Listener object for the next contactless transaction
     *
     * @return a contactless listener object to be used during the next contactless transaction
     * @since 1.0.6a
     */
    private ContactlessTransactionListener createContactlessListener() {
        final McbpAsyncTask mcbpAsyncTask = McbpTaskFactory.getMcbpAsyncTask();

        final CardListener cardListener = getCardContext().getCardListener();

        return new ContactlessTransactionListener() {
            @Override
            public void onContactlessReady() {
                cardListener.onContactlessReady();
            }

            @Override
            public void onContactlessTransactionCompleted(final ContactlessLog contactlessLog) {
                // Notify the cardholder validator that the transaction has been completed
                getCardContext().getChValidator().notifyTransactionCompleted();
                final ByteArray transactionId = getTransactionId(contactlessLog);
                toContactlessTransactionCompleted();
                mcbpAsyncTask.execute(new McbpTaskListener() {
                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onRun() {
                        if (!addToLog(contactlessLog, transactionId, USED_FOR_CONTACTLESS)) {
                            // Something went wrong. We could potentially notify it to some listener
                            // For the moment we simply ignore it.
                            // TODO: In future releases we may need a listener to notify to the
                        }
                    }

                    @Override
                    public void onPostExecute() {
                        // Notify card listener
                        notifyTransactionCompleted(contactlessLog, transactionId, cardListener);
                        // Clean the log content from memory
                        contactlessLog.wipe();
                    }
                });
            }

            @Override
            public void onContactlessTransactionAbort(final ContactlessLog contactlessLog) {
                // Notify the cardholder validator that the transaction has been completed
                getCardContext().getChValidator().notifyTransactionCompleted();
                final ByteArray transactionId = getTransactionId(contactlessLog);
                addToLog(contactlessLog, transactionId, UNUSED_DISCARDED);

                // Notify card listener
                cardListener.onTransactionAbort(
                        new DisplayTransactionInfo() {

                            @Override
                            public String getDisplayableAmount() {
                                return "";
                            }

                            @Override
                            public DisplayStatus getStatus() {
                                return CANCELED;
                            }

                            @Override
                            public String getTransactionIdentifier() {
                                return "";
                            }
                        });
            }
        };
    }

    /**
     * Add the transaction data to the log
     *
     * @param contactlessLog Contactless Log information
     * @param status         The Status of how the key has been used
     *                       (e.g. Contactless, Remote Payment)
     * @return A boolean indicating whether or not the transaction was added to log
     * @since 1.0.6a
     */
    private boolean addToLog(final ContactlessLog contactlessLog,
                             final ByteArray transactionId,
                             final TransactionCredentialStatus.Status status) {
        final LdeMcbpCardService ldeMcbpCardService = getCardContext().getLdeMcbpCardService();

        final ByteArray atc = contactlessLog.getAtc();
        final String digitizedCardId = getCardContext().getDigitizedCardId();
        try {
            ldeMcbpCardService.insertOrUpdateTransactionCredentialStatus(digitizedCardId,
                                                                         atc,
                                                                         status);
        } catch (final InvalidInput | TransactionStorageLimitReach | LdeNotInitialized e) {
            // Something went wrong, we could not add the card as the input was not valid
            mLogger.d(e.getMessage());
        }

        try {
            ldeMcbpCardService.addToLog(TransactionLog.forContactless(digitizedCardId,
                                                                      contactlessLog,
                                                                      transactionId,
                                                                      false, false));
        } catch (final TransactionLoggingError e) {
            mLogger.d(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Utility function to get the Transaction Id
     */
    private ByteArray getTransactionId(ContactlessLog contactlessLog) {
        if (contactlessLog == null) {
            return ByteArray.of("");
        }
        final TransactionSummary transactionSummary = contactlessLog.getResult();
        if (transactionSummary == null) {
            return ByteArray.of("");
        }
        final String digitizedCardId = getCardContext().getDigitizedCardId();
        if (contactlessLog.getTerminalTechnology() == null) {
            return ByteArray.of("");
        }
        switch (contactlessLog.getTerminalTechnology()) {
            case CONTACTLESS_EMV:
                return getTransactionIdForMchip(digitizedCardId, contactlessLog);
            case CONTACTLESS_MAGSTRIPE:
                return getTransactionIdForMagstripe(contactlessLog);

            default:
                return ByteArray.of("");
        }
    }

    /**
     * Utility function to get the Transaction Id for MCHIP
     */
    private ByteArray getTransactionIdForMchip(final String digitizedCardId,
                                               final ContactlessLog contactlessLog) {
        try {
            // FIXME: Why there is not an API in the LDE to retrieve the PAN without padding?
            final String paddedPan = Utils.retrievePanFromDigitizedCardId(digitizedCardId);
            final String pan = Utils.removePaddingFromPAN(paddedPan);
            final ByteArray panByteArray = ByteArray.of(pan.getBytes(Charset.defaultCharset()));

            return TransactionIdentifier.getMChip(
                    panByteArray, contactlessLog.getAtc(), contactlessLog.getCryptogram());
        } catch (McbpCryptoException | InvalidInput e) {
            mLogger.d(e.getMessage());
            return ByteArray.of("");
        }
    }

    /**
     * Utility function to get the Transaction Id for Magstripe
     */
    private ByteArray getTransactionIdForMagstripe(final ContactlessLog contactlessLog) {
        try {
            return TransactionIdentifier.getMagstripe(
                    contactlessLog.getMagstripeDynamicTrack1Data(),
                    contactlessLog.getMagstripeDynamicTrack2Data());
        } catch (final McbpCryptoException | InvalidInput e) {
            mLogger.d(e.getMessage());
            // Something went wrong we do add any transaction id
            return ByteArray.of("");
        }
    }
}
