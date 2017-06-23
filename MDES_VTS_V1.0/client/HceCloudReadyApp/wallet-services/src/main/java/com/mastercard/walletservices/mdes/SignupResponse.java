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

import com.mastercard.walletservices.BuildConfig;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
  * Represents the signup response for Payment app server.
  */
public class SignupResponse {

    @JSON(name = "isError")
    private boolean isError;

    @JSON(name = "errorCause")
    private String errorCause;

    @JSON(name = "paymentAppInstanceId")
    private String paymentAppInstanceId;

    @JSON(name = "paymentAppProviderId")
    private String paymentAppProviderId;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }

    public String getErrorCause() {
        return errorCause;
    }

    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getPaymentAppProviderId() {
        return paymentAppProviderId;
    }

    public void setPaymentAppProviderId(String paymentAppProviderId) {
        this.paymentAppProviderId = paymentAppProviderId;
    }

    /**
     * Returns json string.
     *
     * @return Json string.
     */
    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        // ByteArray serialization
        return serializer.serialize(this);
    }

    /**
     * Returns equivalent
     * {@link com.mastercard.walletservices.mdes.SignupResponse}
     * object from given json string.
     *
     * @param jsonContent The Content of the Response as JSON Object
     * @return The SignupResponse object
     */
    public static SignupResponse valueOf(String jsonContent) {
        return new JSONDeserializer<SignupResponse>()
                .deserialize(jsonContent, SignupResponse.class);
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
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            return "SignupResponse{" +
                   "getErrorDescription=" + isError +
                   ", errorCause='" + errorCause + '\'' +
                   ", paymentAppInstanceId='" + paymentAppInstanceId + '\'' +
                   ", paymentAppProviderId='" + paymentAppProviderId + '\'' +
                   '}';
        } else {
            return "SignupResponse";
        }
    }
}
