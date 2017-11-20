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

package com.mastercard.mcbp.api;

import android.os.Build;
import android.util.Log;

import com.mastercard.mcbp.card.CardListener;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.card.cvm.PinListener;
import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpResult;
import com.mastercard.mcbp.card.mobilekernel.RemotePaymentResultCode;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.RemotePaymentListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLog;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.exceptions.InsufficientPaymentTokensException;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.keymanagement.KeyManagementPolicy;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.listeners.FirstTapListener;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedWalletEventListener;
import com.mastercard.mcbp.listeners.ProcessContactlessListener;
import com.mastercard.mcbp.listeners.ProcessDsrpListener;
import com.mastercard.mcbp.userinterface.DisplayTransactionInfo;
import com.mastercard.mcbp.userinterface.MakeDefaultListener;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Class contains methods for mcbp card features and functionality
 */
class CommonMcbpCardApi {

    /**
     * maximum allowed numeric digits for Mobile PIN
     */
    protected static final int MAXIMUM_MOBILE_PIN_LENGTH = 8;

    /**
     * minimum allowed numeric digits for Mobile PIN
     */
    protected static final int MINIMUM_MOBILE_PIN_LENGTH = 4;

    /**
     * Logger
     */
    private static final McbpLogger sLogger =
            McbpLoggerFactory.getInstance().getLogger(CommonMcbpCardApi.class);
    /**
     * Reference for the current listener for a contactless transaction.<br>
     * Used so that an outside entity can inject events into the listener.
     */
    private static ProcessContactlessListener sProcessContactlessListener;

    /**
     * Determine if card is the default one to use for contactless payments.
     *
     * @param card Instance of {@link McbpCard} to check if its
     *             the default card to use for contactless payments.
     * @return true if card is the default payment card for contactless payments.
     * @deprecated since 1.0.4
     */
    public static boolean isDefaultCardForContactlessPayment(McbpCard card) {
        // If the device is not using an OS version below KitKat (4.4) then no card can be the
        // default card for contactless payment
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
               && McbpInitializer.getInstance().getBusinessService()
                                 .getDefaultCardsManager()
                                 .isDefaultCardForContactlessPayment(card);
    }

    /**
     * Sets card as the default payment card for contactless payments.
     *
     * @param card     Instance of {@link McbpCard} to set as the
     *                 default card for contactless payments.
     * @param listener Callback for success/failure handling, see
     *                 {@link MakeDefaultListener}.
     * @deprecated since 1.0.4
     */
    public static void setAsDefaultCardForContactlessPayment(McbpCard card,
                                                             MakeDefaultListener listener) {
        McbpInitializer.getInstance().getBusinessService()
                       .getDefaultCardsManager()
                       .setAsDefaultCardForContactlessPayment(card, listener);
    }

    /**
     * Sets card as the default payment card for contactless payments.
     *
     * @param card               Instance of {@link McbpCard} to
     *                           set as the default card for contactless payments.
     * @param applicationDefault Whether this card should be permanently set as the default.
     * @param listener           Callback for success/failure handling, see
     *                           {@link MakeDefaultListener}.
     * @deprecated since 1.0.4
     */
    @SuppressWarnings("unused")
    public static void setAsDefaultCardForContactlessPayment(McbpCard card,
                                                             boolean applicationDefault,
                                                             MakeDefaultListener listener) {
        McbpInitializer.getInstance().getBusinessService()
                       .getDefaultCardsManager()
                       .setAsDefaultCardForContactlessPayment(card, applicationDefault, listener);
    }


    /**
     * Determine if card is the default one to use for remote payments.
     *
     * @param card Instance of {@link McbpCard} to check if its
     *             the default card to use for remote payments.
     * @return true if card is the default payment card for remote payments.
     * @deprecated since 1.0.4
     */
    public static boolean isDefaultCardForRemotePayment(McbpCard card) {
        return McbpInitializer.getInstance().getBusinessService()
                              .getDefaultCardsManager()
                              .isDefaultCardForRemotePayment(card);
    }


    /**
     * Sets card as the default payment card for remote payments.
     *
     * @param card Instance of {@link McbpCard} to set as the
     *             default card for remote payments.
     * @return true if the card was successfully set as the default card for remote
     * payments.
     * @deprecated since 1.0.4
     */
    public static boolean setAsDefaultCardForRemotePayment(McbpCard card) {
        return McbpInitializer.getInstance().getBusinessService()
                              .getDefaultCardsManager()
                              .setAsDefaultCardForRemotePayment(card);
    }


    /**
     * Wipe all the SUKs on the CMS linked to card.
     *
     * @param card Instance of {@link McbpCard} to wipe all the
     *             SUKs for.
     * @return true if the wipe was successful, false otherwise.
     * @deprecated since 1.0.4
     */
    public static boolean remoteWipeSuksForCard(McbpCard card) {
        return remoteWipeSuksForCardWithId(card.getDigitizedCardId());
    }

    /**
     * Wipe all the SUKs on the CMS linked to the card identified by digitizedCardId.
     *
     * @param digitizedCardId Id of the digitized card to remove the SUKs for.
     * @return true if the wipe was successful, false otherwise.
     * of the wipe.
     * @deprecated since 1.0.4
     */
    private static boolean remoteWipeSuksForCardWithId(String digitizedCardId) {
        try {
            McbpInitializer.getInstance()
                           .getLdeRemoteManagementService()
                           .wipeDcSuk(ByteArray.of(digitizedCardId));

        } catch (LdeNotInitialized | InvalidInput e) {
            sLogger.d(Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    /**
     * Wipe all the data on the CMS linked to card.
     *
     * @param card Instance of {@link McbpCard} to wipe all the
     *             data for.
     * @return true if the wipe was successful, false otherwise.
     * @deprecated since 1.0.4
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public static boolean remoteWipeCard(McbpCard card) {
        return remoteWipeCardWithId(card.getDigitizedCardId());
    }

    /**
     * Wipe all the data on the CMS linked to the card identified by digitizedCardId.
     *
     * @param digitizedCardId Id of the digitized card to remove all data for.
     * @return true if the wipe was successful, false otherwise.
     * @deprecated since 1.0.4
     */
    @SuppressWarnings("WeakerAccess")
    private static boolean remoteWipeCardWithId(String digitizedCardId) {
        try {
            McbpInitializer.getInstance()
                           .getLdeRemoteManagementService()
                           .wipeDigitizedCard(ByteArray.of(digitizedCardId));
        } catch (InvalidInput | LdeNotInitialized e) {
            sLogger.d(Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    /**
     * Remove card from the local database.
     *
     * @param card Instance of {@link McbpCard} to remove from
     *             the local database.
     * @return true if the wipe was successful, false otherwise.
     * @deprecated since 1.0.4
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public static boolean wipeCard(McbpCard card) {
        return wipeCard(card.getDigitizedCardId());
    }

    /**
     * Remove the card identified by digitizedCardId from the local database.
     *
     * @param digitizedCardId Id of the digitized card to remove from the local database.
     * @return true if the wipe was successful, false otherwise.
     * @deprecated since 1.0.4
     */
    @SuppressWarnings("WeakerAccess")
    private static boolean wipeCard(String digitizedCardId) {
        try {
            final LdeRemoteManagementService ldeRemoteManagementService =
                    McbpInitializer.getInstance().getSdkContext().getLdeRemoteManagementService();
            ldeRemoteManagementService.wipeDigitizedCard(ByteArray.of(digitizedCardId));
        } catch (LdeNotInitialized | InvalidInput e) {
            sLogger.d(Log.getStackTraceString(e));
            return false;
        } catch (NullPointerException e) {
            // Intentional no-op - it is most likely because it has already been removed
            // but we log it in any case as it should not happen
            sLogger.d(Log.getStackTraceString(e));
        }
        return true;
    }

    /**
     * Determines if card is the default payment card for contactless or remote payments and un-sets
     * if (normally before being deleted).
     *
     * @param card Instance of {@link McbpCard} that is to be deleted.
     * @deprecated since 1.0.4
     */
    public static void unsetIfDefaultCard(McbpCard card) {
        // If this card is the default card for contactless payments, then we need to unset it
        if (isDefaultCardForContactlessPayment(card)) {
            unsetDefaultContactlessCard();
        }

        // If this card is the default card for remote payments, then we need to unset it
        if (isDefaultCardForRemotePayment(card)) {
            unsetDefaultRemoteCard();
        }
    }

    /**
     * Un-sets the default card for contactless payments.
     *
     * @since 1.0.2
     */
    public static void unsetDefaultContactlessCard() {
        McbpInitializer.getInstance().getBusinessService()
                       .getDefaultCardsManager()
                       .unsetAsDefaultCardForContactlessPayment(null, new MakeDefaultListener() {
                           @Override
                           public void onSuccess() {
                               // Intentional no-op
                           }

                           @Override
                           public void onAbort() {
                               // Intentional no-op
                           }
                       });
    }

    /**
     * Un-sets the default card for remote payments.
     *
     * @return true if the default card for remote payments was successfully unset.
     * @since 1.0.2
     */
    public static boolean unsetDefaultRemoteCard() {
        return McbpInitializer.getInstance().getBusinessService()
                              .getDefaultCardsManager()
                              .unsetAsDefaultCardForRemotePayment(null);
    }

    /**
     * Returns all the transaction logs associated with card.
     *
     * @param card Instance of {@link McbpCard} to get the
     *             transaction logs for.
     * @return List of {@link TransactionLog} for card.
     * @deprecated since 1.0.4
     */
    public static List<TransactionLog> getTransactionLogsForCard(McbpCard card) {
        return getTransactionLogsForCardWithId(card.getDigitizedCardId());
    }

    /**
     * Returns all the transaction logs associated with the card identified by digitizedCardId.
     *
     * @param digitizedCardId Id of the digitized card to get transaction logs for.
     * @return List of {@link TransactionLog} for the card identified
     * by digitizedCardId.
     * @throws IllegalStateException    If the LDE has not been initialized
     * @throws IllegalArgumentException If the input parameters are not valid
     * @since 1.0.4
     */
    private static List<TransactionLog> getTransactionLogsForCardWithId(String digitizedCardId) {
        final List<TransactionLog> transactionLogs;
        try {
            transactionLogs = McbpInitializer.getInstance().getBusinessService()
                                             .getTransactionLogs(digitizedCardId);
        } catch (LdeNotInitialized e) {
            throw new IllegalStateException("The LDE Database has not been initialized");
        } catch (InvalidInput e) {
            throw new IllegalArgumentException("Invalid Input: " + e);
        }
        return transactionLogs;
    }

    /**
     * Prepare a card for contactless payment.
     *
     * @param card     Instance of {@link McbpCard} to use for
     *                 the contactless payment.
     * @param listener Callback for handling events, see
     *                 {@link ProcessContactlessListener}
     * @throws NullPointerException               If a card is not provided
     * @throws IllegalArgumentException           If the input parameters are not valid
     * @throws InsufficientPaymentTokensException If there are no payments token left to complete
     *                                            the transaction
     * @since 1.0.2
     */
    public static void prepareContactless(final McbpCard card,
                                          final ProcessContactlessListener listener) {
        // Check we have been given a card
        if (card == null) {
            throw new NullPointerException("No card provided");
        }

        // Check this card has at least 1 payment token remaining
        if (card.numberPaymentsLeft() <= 0) {
            throw new InsufficientPaymentTokensException();
        }

        // Keep a reference to it so we can inject events if we choose to
        sProcessContactlessListener = listener;

        // Activate the card for contactless payment, wrap in our own listener that allows us to
        // apply a key management policy after a transaction has been completed
        try {
            card.activateContactless(new CardListener() {
                @Override
                public void onContactlessReady() {
                    // Just pass it through to our listener
                    listener.onContactlessReady();
                }

                @Override
                public void onTransactionCompleted(DisplayTransactionInfo info) {
                    // Pass it through to our listener
                    listener.onContactlessPaymentCompleted(info);

                    // Remove the listener
                    sProcessContactlessListener = null;

                    // Check if new keys should be requested
                    //we try for auto replenish once, do not handle the case when any other
                    // request in process
                    try {
                        enforceKeyManagementPolicy(card);
                    } catch (AlreadyInProcessException | InvalidCardStateException e) {
                        sLogger.d(Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onTransactionAbort(DisplayTransactionInfo info) {
                    // Pass it through to our listener
                    listener.onContactlessPaymentAborted(info);

                    // Remove the listener
                    sProcessContactlessListener = null;

                    // Check if new keys should be requested
                    //we try for auto replenish once, do not handle the case when any other
                    // request in process
                    try {
                        enforceKeyManagementPolicy(card);
                    } catch (AlreadyInProcessException | InvalidCardStateException e) {
                        sLogger.d(Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onPinRequired(PinListener pinListener) {
                    // Just pass it through to our listener
                    listener.onPinRequired(pinListener);
                }
            });
        } catch (InvalidInput e) {
            throw new IllegalArgumentException("Invalid Input: " + e);
        }
    }

    /**
     * Process a remote payment.
     *
     * @param card      Instance of {@link McbpCard} to use for
     *                  the remote payment.
     * @param inputData Data required to process a DSRP payment, it is suggested you use
     *                  {@link com.mastercard.mcbp.data.DsrpInputDataBuilder} to build the
     *                  necessary data.
     * @param listener  Callback for handling events, see
     *                  {@link ProcessDsrpListener}
     * @throws NullPointerException               If a card is not provided
     * @throws IllegalArgumentException           If the input parameters are not valid
     * @throws InsufficientPaymentTokensException If there are no payments token left to complete
     *                                            the transaction
     * @since 1.0.2
     */
    public static boolean processDsrp(final McbpCard card,
                                      final DsrpInputData inputData,
                                      final ProcessDsrpListener listener) {
        // Check we have been given a card
        if (card == null) {
            throw new NullPointerException("No card provided");
        }

        // Check this card has at least 1 payment token remaining
        if (card.numberPaymentsLeft() <= 0) {
            throw new InsufficientPaymentTokensException();
        }

        // Activate the card for remote payment, listening to the events
        try {
            card.activateRemotePayment(new RemotePaymentListener() {
                @Override
                public void onRPReady() {
                    DsrpResult res = null;
                    try {
                        res = card.getTransactionRecord(inputData);
                    } catch (InvalidCardStateException e) {
                        listener.onRemotePaymentError();
                    }
                    if (res != null) {
                        // Handle response
                        if (res.getCode() == RemotePaymentResultCode.OK) {
                            // Send back the response
                            listener.onRemotePaymentComplete(res.getData());
                        } else {
                            listener.onRemotePaymentError();
                        }
                    }

                    // Check if new keys should be requested
                    //we try for auto replenish once, do not handle the case when any other
                    // request in process
                    try {
                        enforceKeyManagementPolicy(card);
                    } catch (AlreadyInProcessException | InvalidCardStateException e) {
                        sLogger.d(Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onPinRequired(PinListener pinListener) {
                    listener.onPinRequired(pinListener);
                }
            }, null);
        } catch (Exception exception) {
            sLogger.d(Log.getStackTraceString(exception));
            listener.onRemotePaymentError();
            return false;
        }

        return true;
    }

    /**
     * Initialise a card so that it is available for contactless.  Success/error will be reported
     * via a callback.
     *
     * @param card     The {@link McbpCard} to prepare for a
     *                 contactless transaction.
     * @param listener Callback for events, see
     *                 {@link FirstTapListener}.
     * @return true if preparing the card was successful; otherwise false.
     * @ since 1.0.2
     */
    public static boolean prepareCardForFirstTap(final McbpCard card,
                                                 final FirstTapListener listener) {

        ContactlessTransactionListener contactlessTransactionListener =
                new ContactlessTransactionListener() {
                    @Override
                    public void onContactlessReady() {
                        // Intentional no-op
                    }

                    @Override
                    public void onContactlessTransactionCompleted(ContactlessLog contactlessLog) {
                        switch (contactlessLog.getResult()) {
                            case ABORT_PERSISTENT_CONTEXT:
                                // Pull out the information from the transaction
                                String amount = contactlessLog.getAmount().toHexString();
                                String currencyCode =
                                        contactlessLog.getCurrencyCode().toHexString();
                                String digitizedCardId = card.getDigitizedCardId();

                                // Inform our listener
                                listener.onFirstTap(amount, currencyCode, digitizedCardId);
                                break;
                            case ABORT_UNKNOWN_CONTEXT:
                                // TODO: Discuss more where/how to handle this
                                // A reason could be lack of keys
                            default:
                                // Intentional no-op
                                break;
                        }
                    }

                    @Override
                    public void onContactlessTransactionAbort(ContactlessLog contactlessLog) {
                        // Intentional no-op
                    }
                };

        card.setFirstTapListener(contactlessTransactionListener);
        return true;
    }

    /**
     * Get the current listener for contactless transactions.
     *
     * @return The current listener for contactless transactions.
     * @since 1.0.2
     */
    public static ProcessContactlessListener getProcessContactlessListener() {
        return sProcessContactlessListener;
    }

    /**
     * Un-set any listener for contactless transactions.
     */
    public static void unsetProcessContactlessListener() {
        sProcessContactlessListener = null;
    }

    /**
     * Use the cards key management policy to determine if new keys are required.
     *
     * @param card Instance of {@link McbpCard} to enforce the
     *             key management policy for.
     * @since 1.0.2
     */
    private static void enforceKeyManagementPolicy(
            McbpCard card) throws AlreadyInProcessException, InvalidCardStateException {
        // Get the key management policy for ths card
        KeyManagementPolicy keyManagementPolicy = McbpWalletApi.getKeyManagementPolicyForCard(card);

        // Check whether new keys should be requested
        if (keyManagementPolicy.shouldRequestNewKeys(card)) {
            // Use the key acquirer to acquire new keys, if required inform any wallet event
            // listener that new keys have been added
            if (McbpInitializer.getInstance().getKeyAcquirer().acquireKeysForCard(card)) {
                ArrayList<MdesCmsDedicatedWalletEventListener> walletEventListeners =
                        McbpWalletApi.getWalletEventListeners();
                for (MdesCmsDedicatedWalletEventListener listener : walletEventListeners) {
                    if (listener.onPaymentTokensReceived(card.getDigitizedCardId(), -1)) {
                        break;
                    }
                }
            }
        }
    }
}
