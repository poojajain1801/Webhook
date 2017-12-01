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

import com.mastercard.mcbp.card.mpplite.mcbpv1.output.CryptogramOutput;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

public class TransactionOutput {
    /**
     * Track2 Equivalent Data
     */
    private final ByteArray mTrack2EquivalentData;
    /**
     * PAN number
     */
    private final ByteArray mPan;
    /**
     * PAN Sequence Number
     */
    private final ByteArray mPanSequenceNumber;
    /**
     * AIP
     */
    private final ByteArray mAip;
    /**
     * Expiry date
     */
    private final ByteArray mExpiryDate;
    /**
     * Cvm Entered.
     */
    private final boolean mCvmEntered;
    /**
     * Cryptogram Output
     */
    private final CryptogramOutput mCryptogramOutput;

    public TransactionOutput(final ByteArray track2EquivalentData,
                             final ByteArray pan,
                             final ByteArray panSequenceNumber,
                             final ByteArray aip,
                             final ByteArray expiryDate,
                             final boolean cvmEntered,
                             final CryptogramOutput cryptogramOutput) {
        this.mTrack2EquivalentData = track2EquivalentData;
        this.mPan = pan;
        this.mPanSequenceNumber = panSequenceNumber;
        this.mAip = aip;
        this.mExpiryDate = expiryDate;
        this.mCvmEntered = cvmEntered;
        this.mCryptogramOutput = cryptogramOutput;
}

    public ByteArray getTrack2EquivalentData() {
        return mTrack2EquivalentData;
    }


    public ByteArray getPan() {
        return mPan;
    }

    public ByteArray getPanSequenceNumber() {
        return mPanSequenceNumber;
    }

    public ByteArray getAip() {
        return mAip;
    }

    public ByteArray getExpiryDate() {
        return mExpiryDate;
    }

    public boolean isCvmEntered() {
        return mCvmEntered;
    }

    public CryptogramOutput getCryptogramOutput() {
        return mCryptogramOutput;
    }

    public void wipe() {
        Utils.clearByteArray(mAip);
        Utils.clearByteArray(mExpiryDate);
        Utils.clearByteArray(mPan);
        Utils.clearByteArray(mPanSequenceNumber);
        Utils.clearByteArray(mTrack2EquivalentData);
        mCryptogramOutput.wipe();
    }

}
