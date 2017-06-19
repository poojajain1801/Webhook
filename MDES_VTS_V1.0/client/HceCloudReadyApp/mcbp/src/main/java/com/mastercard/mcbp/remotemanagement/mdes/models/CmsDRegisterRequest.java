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
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.ByteArrayObjectFactory;
import com.mastercard.mobile_api.utils.json.ByteArrayTransformer;
import com.mastercard.mobile_api.utils.json.SuppressNullTransformer;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class CmsDRegisterRequest {

    @JSON(name = "paymentAppProviderId")
    String paymentAppProviderId;

    @JSON(name = "paymentAppInstanceId")
    String paymentAppInstanceId;

    @JSON(name = "registrationCode")
    String registrationCode;

    @JSON(name = "rgk")
    ByteArray rgk;

    @JSON(name = "deviceFingerprint")
    ByteArray deviceFingerprint;

    public CmsDRegisterRequest() {
        super();
    }

    public CmsDRegisterRequest(String paymentAppProviderId,
                               String paymentAppInstanceId, String registrationCode, ByteArray rgk,
                               ByteArray deviceFingerprint) {
        super();
        this.paymentAppProviderId = paymentAppProviderId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.registrationCode = registrationCode;
        this.rgk = rgk;
        this.deviceFingerprint = deviceFingerprint;
    }

    /**
     * Returns equivalent
     * {@link com.mastercard.mcbp.remotemanagement.mdes.models.CmsDRegisterRequest} object
     * from given Json string
     *
     * @param jsonString Json string.
     * @return CmsDRegisterRequest object.
     */
    public static CmsDRegisterRequest valueOf(String jsonString) {
        return new JSONDeserializer<CmsDRegisterRequest>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(jsonString, CmsDRegisterRequest.class);
    }

    public String getPaymentAppProviderId() {
        return paymentAppProviderId;
    }

    public void setPaymentAppProviderId(String paymentAppProviderId) {
        this.paymentAppProviderId = paymentAppProviderId;
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public ByteArray getRgk() {
        return rgk;
    }

    public void setRgk(ByteArray rgk) {
        this.rgk = rgk;
    }

    public ByteArray getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(ByteArray deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
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
            return "CmsDRegisterRequest [paymentAppProviderId="
                   + paymentAppProviderId + ", paymentAppInstanceId="
                   + paymentAppInstanceId + ", registrationCode="
                   + registrationCode + ", rgk=" + rgk + ", deviceFingerprint="
                   + deviceFingerprint + "]";
        } else {
            return "CmsDRegisterRequest";
        }
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
        serializer.transform(new ByteArrayTransformer(), ByteArray.class);
        // Skip null values
        serializer.transform(new SuppressNullTransformer(), void.class);
        return serializer.serialize(this);
    }
}
