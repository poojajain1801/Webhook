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

package com.mastercard.mcbp.lde;

import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * Template for previous sdk version transaction log.
 * TransactionLog structure is changed since version 1.0.6a
 *
 * @see TransactionLog
 */

class TransactionLogWithApplicationCryptogram {

    public static final byte FORMAT_MCHIP = 1;
    public static final byte FORMAT_PPMC_DSRP = 3;
    private static final int CRYPTOGRAM_LENGTH = 8;
    /**
     * Dc Id
     */
    private final String mDigitizedCardId;
    /**
     * Unpredictable Number
     */
    private final ByteArray mUnpredictableNumber;
    /**
     * Application transaction counter.
     */
    private final ByteArray mAtc;
    /**
     * Cryptogram Format.
     */
    private final byte mCryptogramFormat;
    /**
     * Application Cryptogram
     */
    private final ByteArray mApplicationCryptogram;
    /**
     * Jail Broken  flag.
     */
    private final boolean mHostingMeJailbroken;
    /**
     * Recent Attack flag.
     */
    private final boolean mRecentAttack;
    /**
     * Data
     */
    private final ByteArray mDate;
    /**
     * Amount value.
     */
    private final ByteArray mAmount;
    /**
     * Currency Code
     */
    private final ByteArray mCurrencyCode;

    public String getTimestamp() {
        return timestamp;
    }

    private final String timestamp;

    /**
     * Default Constructor.
     */
    public TransactionLogWithApplicationCryptogram(String digitizedCardId,
                                                   ByteArray unpredictableNumber, ByteArray atc,
                                                   byte cryptogramFormat,
                                                   ByteArray applicationCryptogram,
                                                   boolean hostingMeJailbroken,
                                                   boolean recentAttack, ByteArray date,
                                                   ByteArray amount, ByteArray currencyCode,
                                                   String timestamp) {
        this.mDigitizedCardId = digitizedCardId;
        this.mUnpredictableNumber = unpredictableNumber;
        this.mAtc = atc;
        this.mCryptogramFormat = cryptogramFormat;
        if (cryptogramFormat == FORMAT_MCHIP || cryptogramFormat == FORMAT_PPMC_DSRP) {
            this.mApplicationCryptogram = applicationCryptogram;
        } else {
            this.mApplicationCryptogram = ByteArray.get(CRYPTOGRAM_LENGTH);
        }
        this.mHostingMeJailbroken = hostingMeJailbroken;
        this.mRecentAttack = recentAttack;
        this.mDate = date;
        this.mAmount = amount;
        this.mCurrencyCode = currencyCode;
        this.timestamp = timestamp;
    }

    public byte getCryptogramFormat() {
        return mCryptogramFormat;
    }

    public ByteArray getDate() {
        return mDate;
    }

    public ByteArray getAmount() {
        return mAmount;
    }

    public ByteArray getCurrencyCode() {
        return mCurrencyCode;
    }

    public String getDigitizedCardId() {
        return mDigitizedCardId;
    }

    public ByteArray getAtc() {
        return mAtc;
    }

    public ByteArray getUnpredictableNumber() {
        return mUnpredictableNumber;
    }

}
