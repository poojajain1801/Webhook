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
  * Encapsulate the data which will be needed to process signup request.
  */
public class SignupRequest {

    @JSON(name = "userId")
    private String userId;

    @JSON(name = "activationCode")
    private String activationCode;

    @JSON(name = "rnsRegistrationId")
    private String rnsRegistrationId;

    @JSON(name = "deviceInfoJson")
    private String deviceInfoJson;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getRnsRegistrationId() {
        return rnsRegistrationId;
    }

    public void setRnsRegistrationId(String rnsRegistrationId) {
        this.rnsRegistrationId = rnsRegistrationId;
    }

    public String getDeviceInfoJson() {
        return deviceInfoJson;
    }

    public void setDeviceInfoJson(String deviceInfoJson) {
        this.deviceInfoJson = deviceInfoJson;
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
     * {@link com.mastercard.walletservices.mdes.DigitizeRequest}
     * object from given json string.
     *
     * @param jsonContent The Content of the Request as JSON Object
     * @return The SignupRequest  object
     */
    public static SignupRequest valueOf(String jsonContent) {
        return new JSONDeserializer<SignupRequest>()
                .deserialize(jsonContent, SignupRequest.class);
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
            return "SignupRequest{" +
                   "userId='" + userId + '\'' +
                   ", activationCode='" + activationCode + '\'' +
                   ", rnsRegistrationId='" + rnsRegistrationId + '\'' +
                   ", deviceInfoJson='" + deviceInfoJson + '\'' +
                   '}';
        } else {
            return "SignupRequest";
        }
    }
}
