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

import android.util.Log;

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.keymanagement.KeyManagementPolicy;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedPinChangeResult;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedTaskStatus;
import com.mastercard.mcbp.listeners.MdesCmsDedicatedWalletEventListener;
import com.mastercard.mcbp.listeners.WalletEventListener;
import com.mastercard.mcbp.userinterface.MdesRemoteManagementEventListener;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mcbp.utils.task.McbpTaskFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * API class used for interacting with the wallet features and functionality.
 */
public class McbpWalletApi {
    /**
     * Map for setting different key management policies per card.
     */
    private static HashMap<String, KeyManagementPolicy>
            sCardKeyManagementPolicies = new HashMap<>();
    /**
     * Logger
     */
    private static final McbpLogger sLogger =
            McbpLoggerFactory.getInstance().getLogger(McbpWalletApi.class);
    /**
     * The list of listeners currently waiting for wallet events.
     */
    private static ArrayList<MdesCmsDedicatedWalletEventListener> sWalletListeners =
            new ArrayList<>();

    /**
     * Add an additional listener for wallet events.
     *
     * @param listener The callback for wallet events to add, see {@link WalletEventListener}.
     */
    public static void addWalletEventListener(MdesCmsDedicatedWalletEventListener listener) {
        // Add this listener to the beginning of our list
        sWalletListeners.add(0, listener);

        // If this is the first listener, then we also need to register with the lower levels
        // of the SDK
        if (sWalletListeners.size() == 1) {
            RemoteManagementServices
                    .registerMdesRemoteManagementEventListener(sRemoteManagementEventListener);
        }
    }

    /**
     * Remove a listener for wallet events.
     *
     * @param listener The callback for wallet events to remove, see {@link WalletEventListener}.
     */
    public static void removeWalletEventListener(MdesCmsDedicatedWalletEventListener listener) {
        sWalletListeners.remove(listener);

        // If we no longer have any listeners then remove ourselves as the listener for the lower
        // levels of the SDK
        if (sWalletListeners.size() == 0) {
            RemoteManagementServices.unRegisterUiListener();
        }
    }

    /**
     * Get the current wallet event listener.
     *
     * @return List of instances of {@link WalletEventListener} if a listener is set; otherwise
     * null.
     */
    public static ArrayList<MdesCmsDedicatedWalletEventListener> getWalletEventListeners() {
        return sWalletListeners;
    }

    /**
     * Get the card that is set as the currently active one.
     *
     * @return Instance of {@link McbpCard} representing the
     * currently active card.
     */
    public static McbpCard getCurrentCard() {
        return McbpInitializer.getInstance().getBusinessService()
                              .getCurrentCard();
    }

    /**
     * Set card as the currently active one.
     *
     * @param card Instance of {@link McbpCard} to set as the
     *             currently active card.
     */
    public static void setCurrentCard(McbpCard card) {
        McbpInitializer.getInstance().getBusinessService()
                       .setCurrentCard(card);
    }

    /**
     * Get the card that is currently set as the default one to use for contactless payments.
     *
     * @return Instance of {@link McbpCard} representing the
     * card currently set as the default one to use for contactless payments.
     */
    public static McbpCard getDefaultCardForContactlessPayment() {
        return McbpInitializer.getInstance().getBusinessService()
                              .getDefaultCardsManager()
                              .getDefaultCardForContactlessPayment();
    }

    /**
     * Get the card that is currently set as the default one to use for remote payments.
     *
     * @return Instance of {@link McbpCard} representing the
     * card currently set as the default one to use for remote payments.
     */
    public static McbpCard getDefaultCardForRemotePayment() {
        return McbpInitializer.getInstance().getBusinessService()
                              .getDefaultCardsManager()
                              .getDefaultCardForRemotePayment();
    }

    /**
     * Get all the cards currently within the wallet.
     *
     * @return List of {@link McbpCard} instances currently
     * within the wallet.
     */
    public static ArrayList<McbpCard> getCards() {
        return getCards(false);
    }

    /**
     * Get all the cards currently within the wallet.
     *
     * @param refresh true to retrieve the cards from the database; false to retrieve a cached
     *                version.
     * @return List of {@link McbpCard} instances currently
     * within the wallet.
     */
    public static ArrayList<McbpCard> getCards(boolean refresh) {
        try {
            return McbpInitializer.getInstance().getBusinessService().getAllCards(refresh);
        } catch (final LdeNotInitialized e) {
            throw new IllegalStateException("LDE has not been initialized: " + e);
        }
    }

    /**
     * Get all the cards within the wallet that are eligible for remote payments.
     *
     * @return List of {@link McbpCard} instances currently
     * within the wallet that are eligible for remote payments.
     */
    public static ArrayList<McbpCard> getCardsEligibleForRemotePayment() {
        return getCardsEligibleForRemotePayment(false);
    }

    /**
     * Get all the cards within the wallet that are eligible for remote payments.
     *
     * @param refresh true to retrieve the cards from the database; false to retrieve a cached
     *                version.
     * @return List of {@link McbpCard} instances currently
     * within the wallet that are eligible for remote payments.
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public static ArrayList<McbpCard> getCardsEligibleForRemotePayment(boolean refresh) {
        // Get the entire list of cards in the wallet
        ArrayList<McbpCard> cards = getCards(refresh);

        // Don't try to process them if we don't have any at all
        if (cards == null) {
            return null;
        }

        // Build up a new list of eligible cards
        final ArrayList<McbpCard> eligibleCards = new ArrayList();

        // Loop through each card in the wallet and test whether remote payments are supported
        for (McbpCard card : cards) {
            if (card.isRpSupported()) {
                eligibleCards.add(card);
            }
        }

        return eligibleCards;
    }

    /**
     * Get all the cards within the wallet that are eligible for contactless payments.
     *
     * @return List of {@link McbpCard} instances currently
     * within the wallet that are eligible for contactless payments.
     */
    public static ArrayList<McbpCard> getCardsEligibleForContactlessPayment() {
        return getCardsEligibleForContactlessPayment(false);
    }

    /**
     * Get all the cards within the wallet that are eligible for contactless payments.
     *
     * @param refresh true to retrieve the cards from the database; false to retrieve a cached
     *                version.
     * @return List of {@link McbpCard} instances currently
     * within the wallet that are eligible for contactless payments.
     */
    public static ArrayList<McbpCard> getCardsEligibleForContactlessPayment(boolean refresh) {
        // Get the entire list of cards in the wallet
        ArrayList<McbpCard> cards = getCards(refresh);

        // Don't try to process them if we don't have any at all
        if (cards == null) {
            return null;
        }

        // Build up a new list of eligible cards
        ArrayList<McbpCard> eligibleCards = new ArrayList<>();

        // Loop through each card in the wallet and test whether contactless payments are supported
        for (McbpCard card : cards) {
            if (card.isClSupported()) {
                eligibleCards.add(card);
            }
        }

        return eligibleCards;
    }

    /**
     * Delete all cards within the wallet.
     *
     * @return true if deleting all the cards was successful; otherwise false.
     * @deprecated since 1.0.6
     */
    public static boolean wipeWallet() {
        try {
            LdeRemoteManagementService ldeRemoteManagementService =
                    McbpInitializer.getInstance().getSdkContext().getLdeRemoteManagementService();

            ldeRemoteManagementService.remoteWipeWallet();
            ldeRemoteManagementService.resetMpaToInstalledState();

        } catch (LdeNotInitialized ldeNotInitialized) {
            // This should never happen, but we can safely ignore it as there is nothing to wipe
            // if the LDE had not been initialized
            sLogger.d(Log.getStackTraceString(ldeNotInitialized));
        }
        return true;
    }

    /**
     * Resets existing MPA instance and  all the data. This operation will revert SDK to it's
     * original unintialized state.
     * <p/>
     * This function should be called only in case the MPA wants to reset the application.
     * The SDK needs to be initialized again, if this function is called from MPA.
     */
    public static void resetMpaToInstalledState() {
        try {
            LdeRemoteManagementService ldeRemoteManagementService =
                    McbpInitializer.getInstance().getSdkContext().getLdeRemoteManagementService();

            RemoteManagementServices.cancelPendingRequest();
            McbpTaskFactory.getMcbpAsyncTask().cancel();
            ldeRemoteManagementService.resetMpaToInstalledState();
        } catch (LdeNotInitialized ldeNotInitialized) {
            // This should never happen, but we can safely ignore it as there is nothing to wipe
            // if the LDE had not been initialized
            sLogger.d(Log.getStackTraceString(ldeNotInitialized));
        }
    }

    /**
     * Get the list of supported AIDs.<br>
     * NB: This is currently a hard coded list of AIDs, it is to be updated to calculate this based
     * on the updates provided by Android Lollipop.
     *
     * @return List of AIDs supported by the application.
     */
    public static List<String> getSupportedAids() {
        final ArrayList<String> aids = new ArrayList<>();

        aids.add("A0000000041010");
        aids.add("A0000000042203");

        return aids;
    }

    /**
     * Set a card specific {@link KeyManagementPolicy}.
     *
     * @param card                Instance of {@link McbpCard}
     *                            to set the key management policy for.
     * @param keyManagementPolicy The implementation of
     *                            {@link KeyManagementPolicy} to
     *                            set for this card.
     */
    public static void setKeyManagementPolicyForCard(McbpCard card,
                                                     KeyManagementPolicy keyManagementPolicy) {
        setKeyManagementPolicyForCardWithId(card.getDigitizedCardId(), keyManagementPolicy);
    }

    /**
     * Set a card specific {@link KeyManagementPolicy}.
     *
     * @param digitizedCardId     Identifier for the card to set the key management policy for.
     * @param keyManagementPolicy The implementation of
     *                            {@link KeyManagementPolicy} to
     *                            set for the card with this Id.
     */
    public static void setKeyManagementPolicyForCardWithId(String digitizedCardId,
                                                           KeyManagementPolicy
                                                                   keyManagementPolicy) {
        sCardKeyManagementPolicies.put(digitizedCardId, keyManagementPolicy);
    }

    /**
     * Reset this cards key management policy to the default.
     *
     * @param card Instance of {@link McbpCard} to reset the key
     *             management policy for.
     */
    public static void unsetKeyManagementPolicyForCard(McbpCard card) {
        unsetKeyManagementPolicyForCardWithId(card.getDigitizedCardId());
    }

    /**
     * Reset the cards key management policy to the default.
     *
     * @param digitizedCardId Identifier for the card to reset the key management policy for.
     */
    public static void unsetKeyManagementPolicyForCardWithId(String digitizedCardId) {
        sCardKeyManagementPolicies.remove(digitizedCardId);
    }

    /**
     * Get the key management policy to use for this card.
     *
     * @param card Instance of {@link McbpCard} to get the key
     *             management policy for.
     * @return The {@link KeyManagementPolicy} to use for this
     * card.
     */
    public static KeyManagementPolicy getKeyManagementPolicyForCard(McbpCard card) {
        return getKeyManagementPolicyForCardWithId(card.getDigitizedCardId());
    }

    /**
     * Get the key management policy to use for this card.
     *
     * @param digitizedCardId Identifier for the card to get the key management policy for.
     * @return The {@link KeyManagementPolicy} to use for this
     * card.
     */
    public static KeyManagementPolicy getKeyManagementPolicyForCardWithId(String digitizedCardId) {
        KeyManagementPolicy keyManagementPolicy = McbpInitializer
                .getInstance().getDefaultKeyManagementPolicy();
        if (sCardKeyManagementPolicies.containsKey(digitizedCardId)) {
            keyManagementPolicy = sCardKeyManagementPolicies.get(digitizedCardId);
        }
        return keyManagementPolicy;
    }

    private static MdesRemoteManagementEventListener sRemoteManagementEventListener =
            new MdesRemoteManagementEventListener() {
                @Override
                public void onCardAdded(final String tokenUniqueReference) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onCardAdded(tokenUniqueReference)) {
                            break;
                        }
                    }
                }

                @Override
                public void onCardAddedFailure(final String tokenUniqueReference,
                                               final int retriesRemaining,
                                               final int errorCode) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onCardAddedFailure(tokenUniqueReference, retriesRemaining,
                                                        errorCode)) {
                            break;
                        }
                    }
                }

                @Override
                public void onCardDelete(final String tokenUniqueReference) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onCardDelete(tokenUniqueReference)) {
                            break;
                        }
                    }
                }

                @Override
                public void onCardDeleteFailure(final String tokenUniqueReference,
                                                final int retriesRemaining,
                                                final int errorCode) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onCardDeleteFailure(tokenUniqueReference, retriesRemaining,
                                                         errorCode)) {
                            break;
                        }
                    }
                }

                @Override
                public void onCardPinChanged(final String tokenUniqueReference,
                                             final String result,
                                             final int pinTriesRemaining) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onCardPinChanged(tokenUniqueReference, getPinStatus(result),
                                                      pinTriesRemaining)) {
                            break;
                        }
                    }
                }

                @Override
                public void onCardPinChangedFailure(final String tokenUniqueReference,
                                                    final int retriesRemaining,
                                                    final int errorCode) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onCardPinChangedFailure(tokenUniqueReference, retriesRemaining,
                                                             errorCode)) {
                            break;
                        }
                    }
                }

                @Override
                public void onCardPinReset(final String tokenUniqueReference) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onCardPinReset(tokenUniqueReference)) {
                            break;
                        }
                    }
                }

                @Override
                public void onCardPinResetFailure(final String tokenUniqueReference,
                                                  final int retriesRemaining,
                                                  final int errorCode) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onCardPinResetFailure(tokenUniqueReference, retriesRemaining,
                                                           errorCode)) {
                            break;
                        }
                    }
                }

                @Override
                public void onPaymentTokensReceived(final String tokenUniqueReference,
                                                    final int numberOfCredentialReceived) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onPaymentTokensReceived(tokenUniqueReference,
                                                             numberOfCredentialReceived)) {
                            break;
                        }
                    }
                }

                @Override
                public void onPaymentTokensReceivedFailure(final String tokenUniqueReference,
                                                           final int retriesRemaining,
                                                           final int errorCode) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onPaymentTokensReceivedFailure(tokenUniqueReference,
                                                                    retriesRemaining,
                                                                    errorCode)) {
                            break;
                        }
                    }
                }

                @Override
                public void onRegistrationCompleted() {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onRegistrationCompleted()) {
                            break;
                        }
                    }
                }

                @Override
                public void onRegistrationFailure(final int retriesRemaining, final int errorCode) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onRegistrationFailure(retriesRemaining, errorCode)) {
                            break;
                        }
                    }
                }

                @Override
                public void onTaskStatusReceived(final String status) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onTaskStatusReceived(getTaskStatus(status))) {
                            break;
                        }
                    }
                }

                @Override
                public void onTaskStatusReceivedFailure(final int retriesRemaining,
                                                        final int errorCode) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onTaskStatusReceivedFailure(retriesRemaining, errorCode)) {
                            break;
                        }
                    }
                }

                @Override
                public void onWalletPinChange(final String result, final int pinTriesRemaining) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onWalletPinChange(getPinStatus(result), pinTriesRemaining)) {
                            break;
                        }
                    }
                }

                @Override
                public void onWalletPinChangeFailure(final int retriesRemaining,
                                                     final int errorCode) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onWalletPinChangeFailure(retriesRemaining, errorCode)) {
                            break;
                        }
                    }
                }

                @Override
                public void onWalletPinReset() {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onWalletPinReset()) {
                            break;
                        }
                    }
                }

                @Override
                public void onWalletPinResetFailure(final int retriesRemaining,
                                                    final int errorCode) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onWalletPinResetFailure(retriesRemaining, errorCode)) {
                            break;
                        }
                    }
                }

                @Override
                public void onSystemHealthCompleted() {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onSystemHealthCompleted()) {
                            break;
                        }
                    }
                }

                @Override
                public void onSystemHealthFailure(final int errorCode) {
                    for (MdesCmsDedicatedWalletEventListener listener : sWalletListeners) {
                        if (listener.onSystemHealthFailure(errorCode)) {
                            break;
                        }
                    }
                }
            };

    private static MdesCmsDedicatedTaskStatus getTaskStatus(final String status) {
        return MdesCmsDedicatedTaskStatusImpl.valueOf(status);

    }

    private static MdesCmsDedicatedPinChangeResult getPinStatus(final String result) {
        return MdesCmsDedicatedPinChangeResultImpl.valueOf(result);
    }

    /**
     * Check the general status of a Digitization API host.
     */
    public static void getSystemHealth() throws AlreadyInProcessException {
        RemoteManagementServices.getSystemHealth();
    }

}

