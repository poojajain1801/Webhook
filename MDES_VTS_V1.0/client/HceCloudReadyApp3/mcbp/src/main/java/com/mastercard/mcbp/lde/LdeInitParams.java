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
import com.mastercard.mobile_api.utils.Utils;

/**
 * Encapsulate the parameter which are either required or optional for LDE initialization.
 */
public class LdeInitParams {
    /**
     * CMS MPA identifier.
     */
    private ByteArray mCmsMpaId;
    /**
     * RNS MPA identifier.
     */
    private ByteArray mRnsMpaId;
    /**
     * Remote management url.
     */
    private String mUrlRemoteManagement;
    /**
     * Mobile device finger print.
     */
    private ByteArray mpaFingerPrint;
    /**
     * Application life cycle.
     */
    private String mApplicationLifeCycle;

    /**
     * Mobile network operator.
     */
    private String mMno;
    /**
     * Geo Location.
     */
    private GeoLocation mGeoLocation;
    /**
     * WSP name.
     */
    private String mWspName;

    /**
     * Default constructor.
     */
    public LdeInitParams(ByteArray cmsMpaId, ByteArray mpaFingerPrint) {
        this.setCmsMpaId(cmsMpaId);
        this.setMpaFingerPrint(mpaFingerPrint);
    }

    public boolean isValid() {
        return !(getCmsMpaId() == null || getCmsMpaId()
                .isEmpty()) && !(getMpaFingerPrint() == null || getMpaFingerPrint().isEmpty());
    }

    public ByteArray getCmsMpaId() {
        return mCmsMpaId;
    }

    private void setCmsMpaId(ByteArray cmsMpaId) {
        this.mCmsMpaId = cmsMpaId;
    }

    public String getUrlRemoteManagement() {
        return mUrlRemoteManagement;
    }

    public void setUrlRemoteManagement(String urlRemoteManagement) {
        this.mUrlRemoteManagement = urlRemoteManagement;
    }

    public ByteArray getMpaFingerPrint() {
        return mpaFingerPrint;
    }

    private void setMpaFingerPrint(ByteArray mpaFingerPrint) {
        this.mpaFingerPrint = mpaFingerPrint;
    }

    public String getApplicationLifeCycle() {
        return mApplicationLifeCycle;
    }

    public String getMno() {
        return mMno;
    }

    public GeoLocation getGeolocation() {
        return mGeoLocation;
    }

    public String getWspName() {
        return mWspName;
    }

    public ByteArray getRnsMpaId() {
        return mRnsMpaId;
    }

    public void setRnsMpaId(ByteArray rnsMpaId) {
        this.mRnsMpaId = rnsMpaId;
    }

    /**
     * Wipe all the sensitive data.
     */
    public void wipe() {
        mApplicationLifeCycle = "";
        Utils.clearByteArray(mCmsMpaId);
        Utils.clearByteArray(mRnsMpaId);
        Utils.clearByteArray(mpaFingerPrint);
        if (mGeoLocation != null) {
            mGeoLocation.setLatitude(0);
            mGeoLocation.setLongitude(0);
        }
        mMno = "";
        mWspName = "";
        mUrlRemoteManagement = null;
    }

}
