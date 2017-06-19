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

import com.mastercard.mobile_api.utils.Date;

public class DsrpInputData {
    /**
     * Transaction Amount
     */
    private long mTransactionAmount;
    /**
     * Other Amount
     */
    private long mOtherAmount;
    /**
     * Currency Code
     */
    private char mCurrencyCode;
    /**
     * Transaction Type
     */
    private byte mTransactionType;
    /**
     * Unpredictable Number
     */
    private long mUnpredictableNumber;
    /**
     * Cryptogram Type
     */
    private CryptogramType mCryptogramType;
    /**
     * Transaction Date
     */
    private Date mTransactionDate;
    /**
     * Country Code
     */
    private char mCountryCode;

    public long getTransactionAmount() {
        return mTransactionAmount;
    }

    public void setTransactionAmount(long transactionAmount) {
        this.mTransactionAmount = transactionAmount;
    }

    public long getOtherAmount() {
        return mOtherAmount;
    }

    public void setOtherAmount(long otherAmount) {
        this.mOtherAmount = otherAmount;
    }

    public char getCurrencyCode() {
        return mCurrencyCode;
    }

    public void setCurrencyCode(char currencyCode) {
        this.mCurrencyCode = currencyCode;
    }

    public byte getTransactionType() {
        return mTransactionType;
    }

    public void setTransactionType(byte transactionType) {
        this.mTransactionType = transactionType;
    }

    public long getUnpredictableNumber() {
        return mUnpredictableNumber;
    }

    public void setUnpredictableNumber(long unpredictableNumber) {
        this.mUnpredictableNumber = unpredictableNumber;
    }

    public CryptogramType getCryptogramType() {
        return mCryptogramType;
    }

    public void setCryptogramType(CryptogramType cryptogramType) {
        this.mCryptogramType = cryptogramType;
    }

    public Date getTransactionDate() {
        if (mTransactionDate == null) {
            // We do a lazy initialization in case the Transaction Date has not been provided
            mTransactionDate = new Date(0, 0, 0);
        }
        return mTransactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.mTransactionDate = transactionDate;
    }

    public char getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(char countryCode) {
        this.mCountryCode = countryCode;
    }
}
