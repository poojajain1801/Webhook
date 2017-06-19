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

import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolValues;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionSummary;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

public final class ContactlessTransactionContext {

    /**
     * Application transaction counter.
     */
    private ByteArray mAtc;
    /**
     * Transaction amount.
     */
    private ByteArray mAmount;
    /**
     * Currency code.
     */
    private ByteArray mCurrencyCode;
    /**
     * Transaction date.
     */
    private ByteArray mTrxDate;
    /**
     * Transaction Type.
     */
    private ByteArray mTrxType;
    /**
     * Unpredictable Number
     */
    private ByteArray mUnpredictableNumber;
    /**
     * Cryptogram value.
     */
    private ByteArray mCryptogram;
    /**
     * Context Type
     */
    private TransactionSummary mResult;

    /**
     * Alternate Aid flag
     */
    private boolean mIsAlternateAid;

    /**
     * Application Interchange Profile
     */
    private ByteArray mAip;

    /**
     * Processing Options Data Object Values
     */
    private byte[] mPdolData;

    /**
     * Processing Options Object List
     */
    private DolRequestList mPdolList;

    /**
     * UDOL Object List as requested in the Read Record Response
     */
    private DolRequestList mUdolList;

    /**
     * UDOL Data as received in the Compute CC C-APDU
     */
    private byte[] mUdolData;

    /**
     * The CDOL Object list (As returned in the Read Record Response)
     */
    private DolRequestList mCdolList;

    /**
     * The CDOL data as received in the Generate AC Command APDU
     */
    private byte[] mCdolData;

    /**
     * Constructor.
     */
    public ContactlessTransactionContext(final ByteArray atc,
                                         final ByteArray amount,
                                         final ByteArray currencyCode,
                                         final ByteArray trxDate,
                                         final ByteArray trxType,
                                         final ByteArray unpredictableNumber,
                                         final ByteArray cryptogram,
                                         final TransactionSummary result) {
        this.mAtc = atc;
        this.mAmount = amount;
        this.mCurrencyCode = currencyCode;
        this.mTrxDate = trxDate;
        this.mTrxType = trxType;
        this.mUnpredictableNumber = unpredictableNumber;
        this.mCryptogram = cryptogram;
        this.mResult = result;
    }

    /**
     * Constructor.
     */
    public ContactlessTransactionContext() {
        this.mAtc = null;
        this.mAmount = null;
        this.mCurrencyCode = null;
        this.mTrxDate = null;
        this.mTrxType = null;
        this.mUnpredictableNumber = null;
        this.mCryptogram = null;
        this.mResult = null;
    }

    public final ByteArray getCryptogram() {
        return mCryptogram;
    }

    public final void setCryptogram(final ByteArray cryptogram) {
        this.mCryptogram = cryptogram;
    }

    public final TransactionSummary getResult() {
        return mResult;
    }

    public final void setResult(final TransactionSummary result) {
        this.mResult = result;
    }

    public final ByteArray getAtc() {
        return mAtc;
    }

    public final void setAtc(final ByteArray atc) {
        this.mAtc = atc;
    }

    public final ByteArray getAmount() {
        return mAmount;
    }

    public final void setAlternateAid(final boolean alternateAid) {
        this.mIsAlternateAid = alternateAid;
    }

    public final void setAmount(final ByteArray amount) {
        this.mAmount = amount;
    }

    public final ByteArray getCurrencyCode() {
        return mCurrencyCode;
    }

    public final void setCurrencyCode(final ByteArray currencyCode) {
        this.mCurrencyCode = currencyCode;
    }

    public final ByteArray getTrxDate() {
        return mTrxDate;
    }

    public final void setTrxDate(final ByteArray trxDate) {
        this.mTrxDate = trxDate;
    }

    public final void setTrxType(final ByteArray trxType) {
        this.mTrxType = trxType;
    }

    public final ByteArray getUnpredictableNumber() {
        return mUnpredictableNumber;
    }

    public final void setUnpredictableNumber(final ByteArray unpredictableNumber) {
        this.mUnpredictableNumber = unpredictableNumber;
    }

    public final boolean isAlternateAid() {
        return mIsAlternateAid;
    }

    public final void setPdolData(final byte[] pdolData) {
        this.mPdolData = pdolData;
    }

    public final void setUdolData(final byte[] udolData) {
        this.mUdolData = udolData;
    }

    public final void setCdolData(final byte[] cdolData) {
        this.mCdolData = cdolData;
    }

    public final void setPdolList(final DolRequestList pdolList) {
        this.mPdolList = pdolList;
    }

    public final void setUdolList(final DolRequestList udolList) {
        this.mUdolList = udolList;
    }

    public final void setCdolList(final DolRequestList cdolList) {
        this.mCdolList = cdolList;
    }

    public final ByteArray getAip() {
        return mAip;
    }

    public final void setAip(ByteArray aip) {
        this.mAip = ByteArray.of(aip);
    }

    /**
     * Get the PDOL as easy accessible list of tags - values
     */
    public final DolValues getPdolValues() {
        return DolValues.of(mPdolList, mPdolData);
    }

    /**
     * Get the UDOL as easy accessible list of tags - values
     */
    public final DolValues getUdolValues() {
        return DolValues.of(mUdolList, mUdolData);
    }

    /**
     * Get the CDOL as easy accessible list of tags - values
     */
    public final DolValues getCdolValues() {
        return DolValues.of(mCdolList, mCdolData);
    }

    /**
     * Get the PDOL as byte[]
     */
    public final byte[] getPdolData() {
        return mPdolData;
    }

    /**
     * Get the list of tags requested in the PDOL
     */
    public final DolRequestList getPdolList() {
        return mPdolList;
    }

    /**
     * Get the list of tags requested in the UDOL
     */
    public final DolRequestList getUdolList() {
        return mUdolList;
    }

    /**
     * Wipe all sensitive data.
     */
    public final void wipe() {
        Utils.clearByteArray(mAtc);
        Utils.clearByteArray(mAmount);
        Utils.clearByteArray(mCurrencyCode);
        Utils.clearByteArray(mTrxDate);
        Utils.clearByteArray(mTrxType);
        Utils.clearByteArray(mUnpredictableNumber);
        Utils.clearByteArray(mCryptogram);
        Utils.clearByteArray(mAip);
        Utils.clearByteArray(mPdolData);
    }
}
