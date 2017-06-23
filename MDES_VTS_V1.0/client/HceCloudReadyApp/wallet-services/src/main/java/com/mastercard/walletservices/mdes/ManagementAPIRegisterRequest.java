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
  * Encapsulate the data which will be needed to process register request .
  */
public class ManagementAPIRegisterRequest {

    String requestId;
    String responseHost;
    String paymentAppId;
    String paymentAppInstanceId;
    String deviceFingerprint;
    RnsInfo rnsInfo;
    String rgk;
    String newMobilePin;
    String certificateFingerprint;

    public ManagementAPIRegisterRequest() {
        super();
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public String getRgk() {
        return rgk;
    }

    public void setRgk(String rgk) {
        this.rgk = rgk;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public void setDeviceFingerprint(String deviceFingerprint) {
        this.deviceFingerprint = deviceFingerprint;
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

    public static ManagementAPIRegisterRequest deserialize(String json) {
        return new JSONDeserializer<ManagementAPIRegisterRequest>()
                .deserialize(json, ManagementAPIRegisterRequest.class);
    }

    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        return serializer.serialize(this);
    }

    public RnsInfo getRnsInfo() {
        return rnsInfo;
    }

    public void setRnsInfo(RnsInfo rnsInfo) {
        this.rnsInfo = rnsInfo;
    }

    public String getNewMobilePin() {
        return newMobilePin;
    }

    public void setNewMobilePin(String newMobilePin) {
        this.newMobilePin = newMobilePin;
    }

    public String getPaymentAppId() {
        return paymentAppId;
    }

    public void setPaymentAppId(String paymentAppId) {
        this.paymentAppId = paymentAppId;
    }

    public String getCertificateFingerprint() {
        return certificateFingerprint;
    }

    public void setCertificateFingerprint(String certificateFingerprint) {
        this.certificateFingerprint = certificateFingerprint;
    }

    @Override
    public String toString() {
        return "ManagementAPIRegisterRequest{" +
               "requestId='" + requestId + '\'' +
               ", responseHost='" + responseHost + '\'' +
               ", paymentAppId='" + paymentAppId + '\'' +
               ", paymentAppInstanceId='" + paymentAppInstanceId + '\'' +
               ", deviceFingerprint='" + deviceFingerprint + '\'' +
               ", rnsInfo=" + rnsInfo +
               ", newMobilePin='" + newMobilePin + '\'' +
               ", certificateFingerprint='" + certificateFingerprint + '\'' +
               '}';
    }
}
