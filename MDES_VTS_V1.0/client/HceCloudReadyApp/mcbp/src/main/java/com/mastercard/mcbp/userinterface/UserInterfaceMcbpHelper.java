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
package com.mastercard.mcbp.userinterface;

import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLog;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.currency.Iso4217CurrencyUtils;

import java.util.Currency;

/**
 * Utility class to process transaction information before passing them to the UI layer
 */
public enum UserInterfaceMcbpHelper {
    INSTANCE;

    public static DisplayTransactionInfo getLogInfo(final TransactionLog log) {

        return new DisplayTransactionInfo() {
            @Override
            public String getDisplayableAmount() {
                return getDisplayableAmountAndCurrency(log.getAmount(), log.getCurrencyCode());
            }

            @Override
            public DisplayStatus getStatus() {
                return null;
            }

            @Override
            public String getTransactionIdentifier() {
                return "";
            }
        };
    }

    public static DisplayTransactionInfo getDisplayableTransactionInformation(
            final ContactlessLog contactlessLog,
            final ByteArray transactionId) {

        return new DisplayTransactionInfo() {
            @Override
            public String getDisplayableAmount() {
                return getDisplayableAmountAndCurrency(contactlessLog.getAmount(),
                                                       contactlessLog.getCurrencyCode());
            }

            @Override
            public DisplayStatus getStatus() {
                return getDisplayStatus(contactlessLog);
            }

            @Override
            public String getTransactionIdentifier() {
                return transactionId == null ? "" : transactionId.toHexString();
            }
        };
    }

    private static DisplayStatus getDisplayStatus(final ContactlessLog contactlessLog) {
        if (contactlessLog.getResult() == null) {
            return DisplayStatus.FAILED;
        }
        switch (contactlessLog.getResult()) {
            case ERROR_CONTEXT_CONFLICT:
            case DECLINE:
            case ABORT_UNKNOWN_CONTEXT:
                return DisplayStatus.DECLINED;
            case ABORT_PERSISTENT_CONTEXT:
                return DisplayStatus.FIRST_TAP;
            case AUTHENTICATE_ONLINE:
            case AUTHORIZE_ONLINE:
                return DisplayStatus.COMPLETED;
            default:
                return DisplayStatus.FAILED;
        }
    }

    public static String getDisplayableAmountAndCurrency(final ByteArray amount,
                                                         final ByteArray currencyCode) {
        // String transactionAmount = Utils.bcdAmountArrayToString(log.getAmount());
        final Currency currency =
                Iso4217CurrencyUtils.getCurrencyByCode(currencyCode.getBytes());
        final double amountAsDouble = Iso4217CurrencyUtils
                .convertBcdAmountToDouble(amount.getBytes(), currency);

        String currencySymbol = null;
        int noDecimalDigits = 0;
        if (currency != null) {
            currencySymbol = currency.getSymbol();
            noDecimalDigits = currency.getDefaultFractionDigits();
        }

        final String format = "%." + noDecimalDigits + "f";
        final String transactionAmount = String.format(format, amountAsDouble);

        if (currencySymbol == null) {
            return transactionAmount;
        }

        return currencySymbol + " " + transactionAmount;
    }
}
