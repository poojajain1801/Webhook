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

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
  * Represents the set mobile PIN response for CMS-D.
  */
public class ManagementAPISetMobilePinResponse {
    String requestId;
    String responseHost;
    String errorCode;
    String errorDescription;

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

    @JSON(include = false)
    public boolean isSuccess() {
        return (errorCode == null);
    }

    @Override
    public String toString() {
        return "ManagementAPISetMobilePinResponse{" +
               "requestId='" + requestId + '\'' +
               ", responseHost='" + responseHost + '\'' +
               '}';
    }

    public static String serialize(ManagementAPISetMobilePinResponse managementAPISetMobilePinResponse) {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        return serializer.serialize(managementAPISetMobilePinResponse);
    }

    public static ManagementAPISetMobilePinResponse valueOf(final String registerResponse) {
        return new JSONDeserializer<ManagementAPISetMobilePinResponse>()
                .deserialize(registerResponse, ManagementAPISetMobilePinResponse.class);
    }
}
