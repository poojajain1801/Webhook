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

package com.mastercard.mcbp.remotemanagement.mdes.models;

import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.utils.json.SuppressNullTransformer;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class CmsDNotifyProvisioningRequest extends GenericCmsDRemoteManagementRequest {

    @JSON(name = "tokenUniqueReference")
    String tokenUniqueReference;

    @JSON(name = "result")
    String result;

    @JSON(name = "errorCode")
    String errorCode;

    @JSON(name = "errorDescription")
    String errorDescription;

    public CmsDNotifyProvisioningRequest() {
        super();
    }

    public CmsDNotifyProvisioningRequest(String requestId, String tokenUniqueReference,
                                         String result, String errorCode,
                                         String errorDescription) {
        this.setRequestId(requestId);
        this.tokenUniqueReference = tokenUniqueReference;
        this.result = result;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    public void setTokenUniqueReference(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

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
            return "CmsDNotifyProvisioningRequest{" +
                   "tokenUniqueReference='" + tokenUniqueReference + '\'' +
                   ", result='" + result + '\'' +
                   ", errorCode='" + errorCode + '\'' +
                   ", errorDescription='" + errorDescription + '\'' +
                   ", requestId='" + getRequestId() + '\'' +
                   '}';

        } else {
            return "CmsDNotifyProvisioningRequest";
        }
    }

    /**
     * Returns equivalent
     * {@link com.mastercard.mcbp.remotemanagement.mdes.models.CmsDNotifyProvisioningRequest}
     * object from given json string
     *
     * @param jsonString Json string
     * @return CmsDNotifyProvisioningRequest object
     */
    public static CmsDNotifyProvisioningRequest valueOf(final String jsonString) {
        return new JSONDeserializer<CmsDNotifyProvisioningRequest>()
                .deserialize(jsonString, CmsDNotifyProvisioningRequest.class);
    }

    /**
     * Returns equivalent json string.
     *
     * @return Json string
     */
    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        // Skip null values
        serializer.transform(new SuppressNullTransformer(), void.class);
        return serializer.serialize(this);
    }
}
