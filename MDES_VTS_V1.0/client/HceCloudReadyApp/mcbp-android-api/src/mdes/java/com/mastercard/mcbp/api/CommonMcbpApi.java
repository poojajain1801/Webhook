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

import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.userinterface.UserInterfaceMcbpHelper;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * API class with methods commons to both product flavors
 */
class CommonMcbpApi {

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

}
