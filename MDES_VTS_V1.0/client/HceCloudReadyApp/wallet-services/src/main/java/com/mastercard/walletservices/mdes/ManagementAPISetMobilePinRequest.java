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

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
  * Encapsulate the data which will be needed to process set PIN request.
  */
public class ManagementAPISetMobilePinRequest {

    String requestId;
    String responseHost;
    String paymentAppInstanceId;
    String newMobilePin;
    String currentMobilePin;

    public String getCurrentMobilePin() {
        return currentMobilePin;
    }

    public void setCurrentMobilePin(final String currentMobilePin) {
        this.currentMobilePin = currentMobilePin;
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

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getNewMobilePin() {
        return newMobilePin;
    }

    public void setNewMobilePin(String newMobilePin) {
        this.newMobilePin = newMobilePin;
    }

    @Override
    public String toString() {
        return "ManagementAPISetMobilePin{" +
               "requestId='" + requestId + '\'' +
               ", responseHost='" + responseHost + '\'' +
               ", paymentAppInstanceId='" + paymentAppInstanceId + '\'' +
               ", newMobilePin='" + newMobilePin + '\'' +
               '}';
    }

    public static ManagementAPISetMobilePinRequest deserialize(String json) {
        return new JSONDeserializer<ManagementAPISetMobilePinRequest>()
                .deserialize(json, ManagementAPISetMobilePinRequest.class);
    }

    public String toJSONString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        return serializer.serialize(this);
    }
}
