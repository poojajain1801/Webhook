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

package com.mastercard.mcbp.transactiondecisionmanager.transaction;

import com.mastercard.mobile_api.utils.Utils;

/**
 * Transaction Type
 */
public enum TransactionType {
    /**
     * Purchase
     * */
    PURCHASE,

    /**
     * Refund
     * */
    REFUND,

    /**
     * Cash or Cash Back transaction
     * */
    CASH_OR_CASH_BACK,

    /**
     * Transit
     * */
    TRANSIT,

    /**
     * Not determined or not known
     * */
    UNKNOWN;

    /**
     * Merchant category code for Transit
     * */
    public static final char MERCHANT_CATEGORY_CODE_TRANSIT_4111 = 0x4111;

    /**
     * Merchant category code for Transit
     * */
    public static final char MERCHANT_CATEGORY_CODE_TRANSIT_4131 = 0x4131;

    /**
     * Merchant category code for Transit
     * */
    public static final char MERCHANT_CATEGORY_CODE_TRANSIT_4784 = 0x4784;

    /**
     * Merchant category code for Transit
     * */
    public static final char MERCHANT_CATEGORY_CODE_TRANSIT_7523 = 0x7523;

    /**
     * Static factory method to initialize the transaction type from a set of input values
     * @param transactionType The Transaction Type as communicated by the POS
     * @param merchantCategoryCode The Merchant Category Code
     * @param authorizedAmount The authorized amount
     * @return The Type of the transaction
     */
    public static TransactionType of(final byte transactionType,
                                     final byte[] merchantCategoryCode,
                                     final byte[] authorizedAmount) {
        // Let's first check if it is transit
        if (authorizedAmount != null && Utils.isZero(authorizedAmount)
            && merchantCategoryCode != null) {
            short categoryCode = Utils.readShort(merchantCategoryCode, 0);
            if (categoryCode == MERCHANT_CATEGORY_CODE_TRANSIT_4111
                || categoryCode == MERCHANT_CATEGORY_CODE_TRANSIT_4131
                || categoryCode == MERCHANT_CATEGORY_CODE_TRANSIT_4784
                || categoryCode == MERCHANT_CATEGORY_CODE_TRANSIT_7523) {
                return TRANSIT;
            }
        }

        switch (transactionType) {
            case 0x00:
                return PURCHASE;
            case 0x20:
                return REFUND;
            case 0x01:
            case 0x09:
                return CASH_OR_CASH_BACK;
            default:
                return UNKNOWN;
        }
    }
}
