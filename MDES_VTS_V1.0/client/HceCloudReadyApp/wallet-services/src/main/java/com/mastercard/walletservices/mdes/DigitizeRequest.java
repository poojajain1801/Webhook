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
  * Encapsulate the data which will be needed to process request to digitize a card.
  */
public class DigitizeRequest {

    @JSON(name = "pan")
    private String pan;

    @JSON(name = "paymentAppInstanceId")
    private String paymentAppInstanceId;

    @JSON(name = "expiryMonth")
    private String expiryMonth;

    @JSON(name = "expiryYear")
    private String expiryYear;

    @JSON(name = "cvc")
    private String cvc;

    @JSON(name = "cardholderName")
    private String cardholderName;

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(final String cvc) {
        this.cvc = cvc;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(final String cardholderName) {
        this.cardholderName = cardholderName;
    }

    /**
     * DigitizeResponse
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
     * {@link com.mastercard.walletservices.mdes.SignupRequest}
     * object from given json string.
     *
     * @param jsonContent The Content of the Request as JSON Object
     * @return The SignupRequest object
     */
    public static DigitizeRequest valueOf(String jsonContent) {
        return new JSONDeserializer<DigitizeRequest>()
                .deserialize(jsonContent, DigitizeRequest.class);
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
            return "DigitizeRequest{" +
                   "pan='" + pan + '\'' +
                   ", paymentAppInstanceId='" + paymentAppInstanceId + '\'' +
                   ", expiryMonth='" + expiryMonth + '\'' +
                   ", expiryYear='" + expiryYear + '\'' +
                   '}';
        } else {
            return "DigitizeRequest";
        }
    }
}
