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

package com.mastercard.mcbp.card.mobilekernel;

import com.mastercard.mobile_api.bytes.ByteArray;

public class CryptogramInput {
    /**
     * Amount Authorized
     */
    private final ByteArray mAmountAuthorized;

    /**
     * Amount Other
     */
    private final ByteArray mAmountOther;

    /**
     * Terminal Country Code
     */
    private final ByteArray mTerminalCountryCode;

    /**
     * Terminal Verification Results
     */
    private final ByteArray mTvr;

    /**
     * Transaction Currency Code
     */
    private final ByteArray mTransactionCurrencyCode;

    /**
     * Transaction Date
     */
    private final ByteArray mTransactionDate;

    /**
     * Transaction Type
     */
    private final ByteArray mTransactionType;

    /**
     * Unpredictable Number
     */
    private final ByteArray mUnpredictableNumber;

    /**
     * Cryptogram Type
     */
    private final CryptogramType mCryptogramType;

    /**
     * Build the Cryptogram Input type for a UCAF Remote Payment Transaction
     * @param unpredictableNumber The Unpredictable Number
     * @return The Cryptogram Input for a UCAF Remote Payment Transaction
     */
    public static CryptogramInput forUcaf(final ByteArray unpredictableNumber) {
        final ByteArray amountAuthorized = ByteArray.of(new byte[6]);
        final ByteArray amountOther = ByteArray.of(new byte[6]);
        final ByteArray terminalCountryCode = ByteArray.of(new byte[2]);
        final ByteArray tvr = ByteArray.of(new byte[5]);
        final ByteArray transactionCurrencyCode = ByteArray.of(new byte[2]);
        final ByteArray transactionDate = ByteArray.of(new byte[3]);
        final byte transactionType = 0x00;

        return new CryptogramInput(amountAuthorized,
                                   amountOther,
                                   terminalCountryCode,
                                   tvr,
                                   transactionCurrencyCode,
                                   transactionDate,
                                   getTransactionType(transactionType),
                                   unpredictableNumber,
                                   CryptogramType.UCAF);
    }

    /**
     * Build the Cryptogram Input type for a DE55 Remote Payment Transaction
     * @param amountAuthorized The Authorized Amount as BCD value
     * @param amountOther The Amount Other as BCD value
     * @param terminalCountryCode The Terminal Country Code
     * @param transactionCurrencyCode The Transaction Currency Code
     * @param transactionDate The Transaction Date
     * @param unpredictableNumber The Unpredictable Number
     * @param transactionType The Transaction Type
     * @return The Cryptogram Input for a DE55 Remote Payment Transaction
     */
    public static CryptogramInput forDe55(final ByteArray amountAuthorized,
                                          final ByteArray amountOther,
                                          final ByteArray terminalCountryCode,
                                          final ByteArray transactionCurrencyCode,
                                          final ByteArray transactionDate,
                                          final ByteArray unpredictableNumber,
                                          final byte transactionType) {

        final ByteArray tvr = ByteArray.of(new byte[5]);
        return new CryptogramInput(amountAuthorized,
                                   amountOther,
                                   terminalCountryCode,
                                   tvr,
                                   transactionCurrencyCode,
                                   transactionDate,
                                   getTransactionType(transactionType),
                                   unpredictableNumber,
                                   CryptogramType.DE55);
    }

    /**
     * Build the Cryptogram Input for the next Remote Payment Transaction
     * @param amountAuthorized The Authorized Amount
     * @param amountOther The Other Amount
     * @param terminalCountryCode The Terminal Country Code
     * @param tvr The TVR
     * @param transactionCurrencyCode The Transaction Currency Code
     * @param transactionDate The Transaction Date
     * @param transactionType The Transaction Type
     * @param unpredictableNumber The Unpredictable Number
     * @param cryptogramType The Cryptogram Type
     */
    private CryptogramInput(final ByteArray amountAuthorized,
                            final ByteArray amountOther,
                            final ByteArray terminalCountryCode,
                            final ByteArray tvr,
                            final ByteArray transactionCurrencyCode,
                            final ByteArray transactionDate,
                            final ByteArray transactionType,
                            final ByteArray unpredictableNumber,
                            final CryptogramType cryptogramType) {
        mAmountAuthorized = amountAuthorized;
        mAmountOther = amountOther;
        mTerminalCountryCode = terminalCountryCode;
        mTvr = tvr;
        mTransactionCurrencyCode = transactionCurrencyCode;
        mTransactionDate = transactionDate;
        mTransactionType = transactionType;
        mUnpredictableNumber = unpredictableNumber;
        mCryptogramType = cryptogramType;
    }

    public CryptogramType getCryptogramType() {
        return mCryptogramType;
    }

    public ByteArray getAmountAuthorized() {
        return mAmountAuthorized;
    }

    public ByteArray getAmountOther() {
        return mAmountOther;
    }

    public ByteArray getTerminalCountryCode() {
        return mTerminalCountryCode;
    }

    public ByteArray getTvr() {
        return mTvr;
    }

    public ByteArray getTransactionCurrencyCode() {
        return mTransactionCurrencyCode;
    }

    public ByteArray getTransactionDate() {
        return mTransactionDate;
    }

    public ByteArray getTransactionType() {
        return mTransactionType;
    }

    public ByteArray getUnpredictableNumber() {
        return mUnpredictableNumber;
    }

    /**
     * Utility function to get the transaction type as Byte Array
     * @param transactionType The transaction type as byte value
     * @return The Transaction Type as ByteArray
     */
    private static ByteArray getTransactionType(final byte transactionType) {
        return ByteArray.of(new byte[]{transactionType});
    }
}
