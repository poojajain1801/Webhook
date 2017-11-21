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

/**
 * Represents the request session request for CMS-D.
 */
public class CmsDRequestSession {

    @JSON(name = "paymentAppProviderId")
    private String paymentAppProviderId;

    @JSON(name = "paymentAppInstanceId")
    private String paymentAppInstanceId;

    @JSON(name = "mobileKeysetId")
    private String mobileKeysetId;

    public CmsDRequestSession() {
        super();
    }

    public CmsDRequestSession(String paymentAppProviderId, String paymentAppInstanceId,
                              String mobileKeysetId) {
        this.paymentAppProviderId = paymentAppProviderId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.mobileKeysetId = mobileKeysetId;
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

    public String getMobileKeysetId() {
        return mobileKeysetId;
    }

    public void setMobileKeysetId(String mobileKeysetId) {
        this.mobileKeysetId = mobileKeysetId;
    }

    /**
     * Returns equivalent
     * {@link CmsDRequestSession} object
     * from given Json string
     *
     * @param jsonString Json string.
     * @return RequestSession object.
     */
    public static CmsDRequestSession valueOf(String jsonString) {
        return new JSONDeserializer<CmsDRequestSession>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(jsonString, CmsDRequestSession.class);
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
            return "CmsDRequestSession{" +
                   "paymentAppProviderId='" + paymentAppProviderId + '\'' +
                   ", paymentAppInstanceId='" + paymentAppInstanceId + '\'' +
                   ", mobileKeysetId='" + mobileKeysetId + '\'' +
                   '}';
        } else {
            return "CmsDRequestSession";
        }
    }
}
