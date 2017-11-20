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

package com.mastercard.mcbp.lde.data.mobilecheck;

import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import java.io.Serializable;

import flexjson.JSON;

/**
 * This class encompass following type of information:<br>
 * <li>Information about Mobile Device</li>
 * <li>Information about Mobile Payment Application</li>
 * <li>Information about each card profile loaded in to Mobile Payment Application and Transaction</li>
 */
public class MobileCheckResponse implements Serializable {

    private static final long serialVersionUID = -5443241991391637914L;

    @JSON(name = "CMSMPA_ID")
    private String cmsMpaId;

    @JSON(name = "RFU")
    private String rfu;

    @JSON(name = "MPA_Data")
    private MpaData mpaData;

    private String errorCode;

    public String getCmsMpaId() {
        return cmsMpaId;
    }

    public void setCmsMpaId(String cmsMpaId) {
        this.cmsMpaId = cmsMpaId;
    }

    public MpaData getMpaData() {
        return mpaData;
    }

    public void setMpaData(MpaData mpaData) {
        this.mpaData = mpaData;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getRfu() {
        return rfu;
    }

    public void setRfu(String rfu) {
        this.rfu = rfu;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return Returns debug information for the class in debug mode.
     * In release mode it returns only the class name, so that sensitive information is never
     * returned by this method.
     */
    @Override
    public String toString() {

        if (BuildInfo.isDebugEnabled()) {
            return "MobileCheckResponse [cmsMpaId=" + cmsMpaId + ", rfu=" + rfu + ", mpaData="
                   + mpaData + ", errorCode=" + errorCode + "]";
        } else {
            return "MobileCheckResponse";
        }
    }

    public String toJsonString() {
        return new JsonUtils<MobileCheckResponse>(MobileCheckResponse.class).toJsonString(this);
    }

}
