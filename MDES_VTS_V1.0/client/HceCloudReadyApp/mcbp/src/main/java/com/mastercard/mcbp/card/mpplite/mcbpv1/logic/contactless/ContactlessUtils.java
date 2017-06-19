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

import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionType;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.TlvEditor;

import java.util.Arrays;

/**
 * Single tone object to provide MPP Lite related utility functions
 */
public enum ContactlessUtils {
    INSTANCE;

    public final static byte[] FCI_TEMPLATE_TAG = new byte[]{0x6F};
    public final static byte[] FCI_PROPRIETARY_TAG = new byte[]{(byte) 0xA5};
    public final static byte[] APPLICATION_LABEL_TAG = new byte[]{(byte) 0x50};

    /***
     * Check whether there is a match between the transaction currency code and the
     * Business Logic Currency Code
     *
     * @param businessLogicCurrencyCode The Business Logic Currency Code
     * @param transactionCurrencyCode   The Transaction Currency Code
     * @return True if the transaction currency code is the same as business logic currency code,
     * false otherwise.
     */
    public static boolean validateCurrency(final byte[] businessLogicCurrencyCode,
                                           final byte[] transactionCurrencyCode) {
        return !(businessLogicCurrencyCode == null || transactionCurrencyCode == null) && Arrays
                .equals(transactionCurrencyCode, businessLogicCurrencyCode);
    }

    /***
     * Check whether the amount of the current transaction is acceptable
     *
     * @param businessLogicAmount        The Amount received from the Business Logic
     * @param transactionAmount          The Transaction Amount
     * @param isBusinessLogicExactAmount Flag specifying whether the Business Logic has specified an
     *                                   exact amount for the transaction
     * @return true if the amount is within the accepted limits, false otherwise
     */
    public static boolean validateAmount(final byte[] businessLogicAmount,
                                         final byte[] transactionAmount,
                                         final boolean isBusinessLogicExactAmount) {
        if (businessLogicAmount == null || transactionAmount == null) {
            return false;
        }

        final Long amount = Long.valueOf(ByteArray.of(transactionAmount).toHexString(), 10);
        final Long blAmount = Long.valueOf(ByteArray.of(businessLogicAmount).toHexString(), 10);

        return (!isBusinessLogicExactAmount && blAmount.compareTo(amount) >= 1)
               || blAmount.compareTo(amount) == 0;
    }

    /**
     * Parse the Select Response APDU (as in the profile) and extract the application label
     *
     * @param selectResponse The SELECT R-APDU as in the Card Profile
     * @return The Application Label
     */
    public static byte[] readApplicationLabel(final byte[] selectResponse) {
        final int length = selectResponse.length;

        // We assume the response is always present
        final byte[] data = new byte[length];
        // We have our response in TLV format without the 9000 at the end
        System.arraycopy(selectResponse, 0, data, 0, length);

        final TlvEditor selectResponseTlv = TlvEditor.of(data);
        if (selectResponseTlv == null) return null;

        final TlvEditor fciTemplate = TlvEditor.of(selectResponseTlv.getValue(FCI_TEMPLATE_TAG));
        if (fciTemplate == null) return null;

        final TlvEditor fciProprietary =
                TlvEditor.of(fciTemplate.getValue(FCI_PROPRIETARY_TAG));
        if (fciProprietary == null) return null;

        return fciProprietary.getValue(APPLICATION_LABEL_TAG);
    }

    /**
     * Utility function to check whether there is a context match
     *
     * @param transactionAmount          Transaction Amount as in the C-APDU
     * @param transactionCurrencyCode    Transaction Currency Code as in the C-APDU
     * @param businessLogicAmount        Transaction Amount as specified by the business logic
     * @param currencyCode               Transaction Currency Code as specified by the business
     *                                   logic
     * @param isBusinessLogicExactAmount Business Logic flag specifying whether the exact amount
     *                                   should be enforced
     * @return True if the context is matching or if the context should not be enforced. False in
     * case of mismatch.
     */
    public static boolean isContextMatching(final byte[] transactionAmount,
                                            final byte[] transactionCurrencyCode,
                                            final byte[] businessLogicAmount,
                                            final byte[] currencyCode,
                                            final boolean isBusinessLogicExactAmount) {
        // CCC.2.4 and GAC.3.4
        if (businessLogicAmount == null || !isBusinessLogicExactAmount) {
            return true;
        }

        // CCC.2.5 and GAC.3.5
        if (!ContactlessUtils.validateCurrency(currencyCode, transactionCurrencyCode)) {
            // CCC.2.7 and GAC.3.7
            return false;
        }

        // CCC.2.5 and GAC.3.5
        return ContactlessUtils.validateAmount(
                businessLogicAmount, transactionAmount, isBusinessLogicExactAmount);
    }


    /**
     * Utility function to check if the transaction is transit
     *
     * @param transactionType      The Transaction Type as communicated by the POS
     * @param merchantCategoryCode The Merchant Category Code
     * @param authorizedAmount     Transaction Amount as in the C-APDU
     * @return True if the transaction is transit, false otherwise.
     */
    public static boolean isTransitTransaction(final byte transactionType,
                                               final byte[] merchantCategoryCode,
                                               final byte[] authorizedAmount) {
        return TransactionType.of(transactionType, merchantCategoryCode,
                                  authorizedAmount) == TransactionType.TRANSIT;
    }
}
