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

package com.mastercard.mcbp.card.profile;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import flexjson.JSON;

public final class RemotePaymentData {

    @JSON(name = "pan")
    private ByteArray mPan;

    @JSON(name = "issuerApplicationData")
    private ByteArray mIssuerApplicationData;

    @JSON(name = "cvrMaskAnd")
    private ByteArray mCvrMaskAnd;

    @JSON(name = "applicationExpiryDate")
    private ByteArray mApplicationExpiryDate;

    @JSON(name = "track2Equivalent")
    private ByteArray mTrack2EquivalentData;

    @JSON(name = "panSequenceNumber")
    private ByteArray mPanSequenceNumber;

    @JSON(name = "ciacDecline")
    private ByteArray mCiacDecline;

    @JSON(name = "aip")
    private ByteArray mAip;

    public ByteArray getPan() {
        return mPan;
    }

    public void setPan(ByteArray pan) {
        this.mPan = pan;
    }

    public ByteArray getIssuerApplicationData() {
        return mIssuerApplicationData;
    }

    public void setIssuerApplicationData(ByteArray issuerApplicationData) {
        this.mIssuerApplicationData = issuerApplicationData;
    }

    public ByteArray getCvrMaskAnd() {
        return mCvrMaskAnd;
    }

    public void setCvrMaskAnd(ByteArray cvrMaskAnd) {
        this.mCvrMaskAnd = cvrMaskAnd;
    }

    public ByteArray getApplicationExpiryDate() {
        return mApplicationExpiryDate;
    }

    public void setApplicationExpiryDate(ByteArray applicationExpiryDate) {
        this.mApplicationExpiryDate = applicationExpiryDate;
    }

    public ByteArray getTrack2EquivalentData() {
        return mTrack2EquivalentData;
    }

    public void setTrack2EquivalentData(ByteArray track2EquivalentData) {
        this.mTrack2EquivalentData = track2EquivalentData;
    }

    public ByteArray getPanSequenceNumber() {
        return mPanSequenceNumber;
    }

    public void setPanSequenceNumber(ByteArray panSequenceNumber) {
        this.mPanSequenceNumber = panSequenceNumber;
    }

    public ByteArray getCiacDecline() {
        return mCiacDecline;
    }

    public void setCiacDecline(ByteArray ciacDecline) {
        this.mCiacDecline = ciacDecline;
    }

    public ByteArray getAip() {
        return mAip;
    }

    public void setAip(ByteArray aip) {
        this.mAip = aip;
    }

    public void wipe() {
        Utils.clearByteArray(mAip);
        Utils.clearByteArray(mApplicationExpiryDate);
        Utils.clearByteArray(mCiacDecline);
        Utils.clearByteArray(mCvrMaskAnd);
        Utils.clearByteArray(mIssuerApplicationData);
        Utils.clearByteArray(mPan);
        Utils.clearByteArray(mPanSequenceNumber);
        Utils.clearByteArray(mTrack2EquivalentData);
    }

}