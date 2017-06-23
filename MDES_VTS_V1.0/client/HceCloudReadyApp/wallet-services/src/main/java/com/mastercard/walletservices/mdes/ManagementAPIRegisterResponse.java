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

package com.mastercard.walletservices.mdes;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.ByteArrayObjectFactory;
import com.mastercard.mobile_api.utils.json.ByteObjectFactory;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * The output data of a CMS-D Register request
 */
public class ManagementAPIRegisterResponse {
    /**
     * The request id
     */
    @JSON(name = "requestId")
    private String requestId;
    /**
     * The response host
     */
    @JSON(name = "responseHost")
    private String responseHost;
    /**
     * The Keyset ID
     */
    @JSON(name = "mobileKeysetId")
    private String mobileKeysetId;
    /**
     * The Mobile Keys
     */
    @JSON(name = "mobileKeys")
    private MobileKeys mobileKeys;
    /**
     * The Remote Management URL
     */
    @JSON(name = "remoteManagementUrl")
    private String remoteManagementUrl;
    /**
     * The error code
     */
    @JSON(name = "errorCode")
    private String errorCode;
    /**
     * The error description
     */
    @JSON(name = "errorDescription")
    private String errorDescription;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public ManagementAPIRegisterResponse() {
        super();
    }

    public String getMobileKeysetId() {
        return mobileKeysetId;
    }

    public void setMobileKeysetId(String mobileKeysetId) {
        this.mobileKeysetId = mobileKeysetId;
    }

    public MobileKeys getMobileKeys() {
        return mobileKeys;
    }

    public void setMobileKeys(MobileKeys mobileKeys) {
        this.mobileKeys = mobileKeys;
    }

    public String getRemoteManagementUrl() {
        return remoteManagementUrl;
    }

    public void setRemoteManagementUrl(String remoteManagementUrl) {
        this.remoteManagementUrl = remoteManagementUrl;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResponseHost() {
        return responseHost;
    }

    public void setResponseHost(String responseHost) {
        this.responseHost = responseHost;
    }

    public static String serialize(ManagementAPIRegisterResponse registerResponse) {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        return serializer.serialize(registerResponse);
    }

    public static ManagementAPIRegisterResponse deserialize(String json) {
        return new JSONDeserializer<ManagementAPIRegisterResponse>()
                .deserialize(json, ManagementAPIRegisterResponse.class);
    }

    @JSON(include = false)
    public boolean isSuccess() {
        return (errorCode == null);
    }

    @Override
    public String toString() {
        return "ManagementAPIRegisterResponse{" +
               "requestId='" + requestId + '\'' +
               ", responseHost='" + responseHost + '\'' +
               ", mobileKeysetId='" + mobileKeysetId + '\'' +
               ", mobileKeys=" + mobileKeys +
               ", remoteManagementUrl='" + remoteManagementUrl + '\'' +
               ", errorCode='" + errorCode + '\'' +
               ", errorDescription='" + errorDescription + '\'' +
               '}';
    }

    public static ManagementAPIRegisterResponse valueOf(final String registerResponse) {
        return new JSONDeserializer<ManagementAPIRegisterResponse>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .use(byte.class, new ByteObjectFactory())
                .deserialize(registerResponse, ManagementAPIRegisterResponse.class);
    }
}
