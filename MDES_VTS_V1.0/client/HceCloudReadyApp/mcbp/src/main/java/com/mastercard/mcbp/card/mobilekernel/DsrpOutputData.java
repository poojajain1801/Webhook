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
import com.mastercard.mobile_api.utils.Date;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import flexjson.JSON;

public class DsrpOutputData {

    /**
     * PAN number
     */
    private final String mPan;

    /**
     * Pan Sequence Number
     */
    private final int mPanSequenceNumber;

    /**
     * Expiry Date
     */
    private final Date mExpiryDate;

    /**
     * Application cryptogram: 8 bytes
     */
    private final ByteArray mCryptogram;

    /**
     * Formatted ucaf or de55 data
     */
    private final ByteArray mTransactionCryptogramData;

    /**
     * Cryptogram Type
     */
    private final CryptogramType mCryptogramType;

    /**
     * UCAF version
     */
    private final int mUcafVersion;

    /**
     * Transaction amount
     */
    private final long mTransactionAmount;

    /**
     * Currency code
     */
    private final int mCurrencyCode;

    /**
     * Application Transaction Counter
     */
    private final int mAtc;

    /**
     * Unpredictable number
     */
    private final long mUnpredictableNumber;

    /**
     * Track 2 Data
     * */
    private final String mTrack2Data;

    /**
     * Default constructor.
     */
    public DsrpOutputData(final String pan,
                          final int panSequenceNumber,
                          final Date expiryDate,
                          final ByteArray cryptogram,
                          final ByteArray cryptogramData,
                          final int ucafVersion,
                          final long transactionAmount,
                          final int currencyCode,
                          final int atc,
                          final long unpredictableNumber,
                          CryptogramType cryptogramType,
                          final String track2Data) {
        this.mPan = pan;
        this.mPanSequenceNumber = panSequenceNumber;
        this.mExpiryDate = expiryDate;
        this.mCryptogram = cryptogram;
        this.mTransactionCryptogramData = cryptogramData;
        this.mUcafVersion = ucafVersion;
        this.mTransactionAmount = transactionAmount;
        this.mCurrencyCode = currencyCode;
        this.mAtc = atc;
        this.mUnpredictableNumber = unpredictableNumber;
        this.mCryptogramType = cryptogramType;
        this.mTrack2Data = track2Data;
    }

    /***
     * The Cryptogram is returned only for logging purposes
     * @return The Transaction Cryptogram (i.e. the Application Cryptogram)
     */
    @JSON(include = false)
    public ByteArray getCryptogram() {
        return mCryptogram;
    }


    public ByteArray getTransactionCryptogramData() {
        return mTransactionCryptogramData;
    }


    public String getPan() {
        return mPan;
    }

    public int getPanSequenceNumber() {
        return mPanSequenceNumber;
    }

    public Date getExpiryDate() {
        return mExpiryDate;
    }

    public int getUcafVersion() {
        return mUcafVersion;
    }

    public long getTransactionAmount() {
        return mTransactionAmount;
    }

    public int getCurrencyCode() {
        return mCurrencyCode;
    }

    public int getAtc() {
        return mAtc;
    }

    public long getUnpredictableNumber() {
        return mUnpredictableNumber;
    }

    public CryptogramType getCryptogramType() {
        return mCryptogramType;
    }

    public String getTrack2Data() { return mTrack2Data; }

    /**
     * Wipe all sensitive data.
     */
    public void wipe() {
        Utils.clearByteArray(mCryptogram);
        Utils.clearByteArray(mTransactionCryptogramData);
    }

    public String toJsonString() {
        return new JsonUtils<DsrpOutputData>(DsrpOutputData.class).toJsonString(this);
    }

}
