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

public final class AlternateContactlessPaymentData {

    @JSON(name = "paymentFci")
    private ByteArray mPaymentFci;

    @JSON(name = "gpoResponse")
    private ByteArray mGpoResponse;

    @JSON(name = "cvrMaskAnd")
    private ByteArray mCvrMaskAnd;

    @JSON(name = "aid")
    private ByteArray mAid;

    @JSON(name = "ciacDecline")
    private ByteArray mCiacDecline;

    public ByteArray getPaymentFci() {
        return mPaymentFci;
    }

    public void setPaymentFci(ByteArray paymentFci) {
        this.mPaymentFci = paymentFci;
    }

    public ByteArray getGpoResponse() {
        return mGpoResponse;
    }

    public void setGpoResponse(ByteArray gpoResponse) {
        this.mGpoResponse = gpoResponse;
    }

    public ByteArray getCvrMaskAnd() {
        return mCvrMaskAnd;
    }

    public void setCvrMaskAnd(ByteArray cvrMaskAnd) {
        this.mCvrMaskAnd = cvrMaskAnd;
    }

    public ByteArray getAid() {
        return mAid;
    }

    public void setAid(ByteArray aid) {
        this.mAid = aid;
    }

    public ByteArray getCiacDecline() {
        return mCiacDecline;
    }

    public void setCiacDecline(ByteArray ciacDecline) {
        this.mCiacDecline = ciacDecline;
    }

    public void wipe() {
        Utils.clearByteArray(mAid);
        Utils.clearByteArray(mCiacDecline);
        Utils.clearByteArray(mCvrMaskAnd);
        Utils.clearByteArray(mGpoResponse);
        Utils.clearByteArray(mPaymentFci);
    }
}