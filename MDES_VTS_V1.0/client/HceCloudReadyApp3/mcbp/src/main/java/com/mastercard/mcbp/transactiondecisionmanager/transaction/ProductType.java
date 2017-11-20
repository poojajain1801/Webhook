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

/**
 * Card Product Type
 */
public enum ProductType {
    /**
     * Credit Card
     * */
    CREDIT,

    /**
     * Debit Card
     * */
    DEBIT,

    /**
     * Product Type not known or not recognized
     * */
    UNKNOWN;

    /**
     * Static factory method to determine the Product Type from the Select R-APDU
     * @param appLabel The Application Label as in the SELECT R-APDU
     * @return The Product Type. Unknown if DEBIT or CREDIT cannot be identified
     */
    static ProductType of(final byte[] appLabel) {
        if (appLabel == null) {
            return UNKNOWN;
        }
        final String applicationLabel = new String(appLabel).toLowerCase();

        if (applicationLabel.equalsIgnoreCase("mastercard")) {
            return CREDIT;
        }

        if (applicationLabel.contains("maestro") ||
            applicationLabel.contains("debit")) {
            return DEBIT;
        }

        // Otherwise, return unknown
        return UNKNOWN;
    }
}
