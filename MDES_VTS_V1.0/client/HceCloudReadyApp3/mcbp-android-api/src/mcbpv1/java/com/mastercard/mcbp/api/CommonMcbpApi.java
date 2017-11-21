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

import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.listeners.WalletEventListener;
import com.mastercard.mcbp.remotemanagement.mcbpV1.ServiceRequestUtils;
import com.mastercard.mcbp.userinterface.UserInterfaceListener;
import com.mastercard.mcbp.userinterface.UserInterfaceMcbpHelper;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.ArrayList;

/**
 * API class with methods commons to any product flavors
 *
 * @deprecated Use MDES build flavour instead
 */
@Deprecated
class CommonMcbpApi {

    /**
     * The list of listeners currently waiting for wallet events.
     */
    private static ArrayList<WalletEventListener> sWalletListeners = new ArrayList<>();
    /**
     * Logger
     */
    private static final McbpLogger sLogger =
            McbpLoggerFactory.getInstance().getLogger(CommonMcbpApi.class);

    /**
     * Determine if the application is currently running.
     *
     * @return true if the application is running; otherwise false.
     * @deprecated since 1.0.6a
     */
    public static boolean isAppRunning() {
        McbpInitializer mcbpInitializer = McbpInitializer.getInstance();
        if (mcbpInitializer != null) {
            return mcbpInitializer.getMcbpActivityLifecycleCallback().isAppRunning();
        }
        return false;
    }

    /**
     * Add an additional listener for wallet events.
     *
     * @param listener The callback for wallet events to add, see {@link WalletEventListener}.
     */
    public static void addWalletEventListener(WalletEventListener listener) {
        // Add this listener to the beginning of our list
        sWalletListeners.add(0, listener);

        // If this is the first listener, then we also need to register with the lower levels
        // of the SDK
        if (sWalletListeners.size() == 1) {
            McbpInitializer.getInstance().getRemoteManagementService()
                           .registerListener(sUserInterfaceListener);
        }
    }

    /**
     * Remove a listener for wallet events.
     *
     * @param listener The callback for wallet events to remove, see {@link WalletEventListener}.
     */
    public static void removeWalletEventListener(WalletEventListener listener) {
        sWalletListeners.remove(listener);

        // If we no longer have any listeners then remove ourselves as the listener for the lower
        // levels of the SDK
        if (sWalletListeners.size() == 0) {
            McbpInitializer.getInstance().getRemoteManagementService().unRegisterUiListener();
        }
    }

    /**
     * Get the current wallet event listener.
     *
     * @return List of instances of {@link WalletEventListener} if a listener is set; otherwise
     * null.
     */
    public static ArrayList<WalletEventListener> getWalletEventListeners() {
        return sWalletListeners;
    }

    /**
     * Converts amount and currency code into user friendly readable string
     *
     * @param amount   Amount in lowest denomination
     * @param currency Currency code
     * @return The currency in user friendly readable string
     */
    public static String getDisplayableAmountAndCurrency(long amount, int currency) {
        String sAmount = String.valueOf(amount);
        String sCurrency = String.valueOf(currency);

        return getDisplayableAmountAndCurrency(sAmount, sCurrency);
    }

    /**
     * Convert the data retrieved via HCE into a human readable format.
     *
     * @param amount   The amount of the transaction.
     * @param currency The currency code of the transaction.
     * @return A human readable string of the amount to be paid.
     */
    public static String getDisplayableAmountAndCurrency(String amount, String currency) {
        amount = leftPadToLength(amount, '0', 12);
        currency = leftPadTillEven(currency, '0');


        return UserInterfaceMcbpHelper.getDisplayableAmountAndCurrency(
                ByteArray.of(amount),
                ByteArray.of(currency));
    }

    /**
     * Left pads {@code input} with {@code c} until its an even length.
     *
     * @param input     The string to be padded.
     * @param character The character to pad with.
     * @return The padded string.
     */
    @SuppressWarnings("SameParameterValue")
    private static String leftPadTillEven(String input, char character) {
        if (input.length() % 2 != 0) {
            input = character + input;
        }
        return input;
    }

    /**
     * Lefts pads {@code input} with {@code c} until {@code length}.
     *
     * @param input     The string to be padded.
     * @param character The character to pad with.
     * @param length    The length to pad to.
     * @return The padding string.
     */
    @SuppressWarnings("SameParameterValue")
    private static String leftPadToLength(String input, char character, int length) {
        String result = input;
        for (int i = input.length(); i < length; i++) {
            result = character + result;
        }
        return result;
    }

    /**
     * Helper method for converting the digitized card Id represented as an {@link Object}
     * to a {@link String} with casting safety.
     *
     * @param additionalData The digitized card Id represented as an {@link Object}.
     * @return The digitized card Id if additional data contains a {@link String};
     * otherwise null.
     */
    private static String convertDigitizedCardId(Object additionalData) {
        String digitizedCardId = null;

        try {
            digitizedCardId = (String) additionalData;
        } catch (ClassCastException e) {
            // Intentional no-op
        }

        return digitizedCardId;
    }

    private static UserInterfaceListener sUserInterfaceListener = new UserInterfaceListener() {
        @Override
        public void onCardUpdated(ServiceRequestUtils.ServiceRequestEnum
                                          remoteManagementInfo,
                                  Object additionalData) {
            String digitizedCardId = convertDigitizedCardId(additionalData);

            switch (remoteManagementInfo) {
                // The application has been completely reset
                case RESETMPA:
                    for (WalletEventListener listener : sWalletListeners) {
                        if (listener.applicationReset()) {
                            break;
                        }
                    }
                    break;

                // A card has been deleted
                case DELETE:
                    for (WalletEventListener listener : sWalletListeners) {
                        if (listener.cardDeleted(digitizedCardId)) {
                            break;
                        }
                    }
                    break;

                // A new card has been provision
                case PROVISIONCP:
                    for (WalletEventListener listener : sWalletListeners) {
                        if (listener.cardAdded(digitizedCardId)) {
                            break;
                        }
                    }
                    break;

                // New payments tokens have been provisioned
                case PROVISIONSUK:
                    for (WalletEventListener listener : sWalletListeners) {
                        if (listener.paymentTokensAdded(digitizedCardId)) {
                            break;
                        }
                    }
                    break;

                // Card has been suspended
                case SUSPEND:
                    // Unset this card as the default if it is the default
                    if (McbpCardApi
                            .isDefaultCardForContactlessPayment(
                                    digitizedCardId)) {
                        McbpCardApi.unsetDefaultContactlessCard();
                    }
                    if (McbpCardApi
                            .isDefaultCardForRemotePayment(digitizedCardId)) {
                        McbpCardApi.unsetDefaultRemoteCard();
                    }

                    for (WalletEventListener listener : sWalletListeners) {
                        if (listener.cardSuspended(digitizedCardId)) {
                            break;
                        }
                    }
                    break;

                // Card has been resumed
                case RESUME:
                    for (WalletEventListener listener : sWalletListeners) {
                        if (listener.cardResumed(digitizedCardId)) {
                            break;
                        }
                    }
                    break;

                // PIN number has been changed
                case CHANGEMOBILEPIN:
                    // Ensure all the old SUKs for this card have been removed
                    McbpCardApi.remoteWipeSuksForCardWithId(digitizedCardId);

                    // Inform our listener if we have one
                    for (WalletEventListener listener : sWalletListeners) {
                        if (listener.pinChanged(digitizedCardId)) {
                            break;
                        }
                    }

                    // Now the PIN has been changed we need to request some more tokens
                    try {
                        McbpCardApi.replenishForCardWithId(digitizedCardId);
                    } catch (AlreadyInProcessException e) {
                        sLogger.d(Log.getStackTraceString(e));
                    }
                    break;

                // Device information has been requested
                case GETDEVICEINFORMATION:
                    // Intentional no-op
                    break;

                // All cards have been deleted
                case REMOTEWIPE:
                    for (WalletEventListener listener : sWalletListeners) {
                        if (listener.remoteWipe()) {
                            break;
                        }
                    }
                    break;
            }
        }
    };
}
