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

package com.mastercard.mcbp.remotemanagement.mdes.profile;

import flexjson.JSON;

/**
 * Represents the contactless payment data in card profile of mdes.
 */
public class ContactlessPaymentData {

    @JSON(name = "aid")
    private String aid;

    @JSON(name = "ppseFci")
    private String ppseFci;

    @JSON(name = "paymentFci")
    private String paymentFci;

    @JSON(name = "gpoResponse")
    private String gpoResponse;

    @JSON(name = "cdol1RelatedDataLength")
    private String cdol1RelatedDataLength;

    @JSON(name = "ciacDecline")
    private String ciacDecline;

    @JSON(name = "cvrMaskAnd")
    private String cvrMaskAnd;

    @JSON(name = "issuerApplicationData")
    private String issuerApplicationData;

    @JSON(name = "iccPrivateKeyCrtComponents")
    private IccPrivateKeyCrtComponents iccPrivateKeyCrtComponents;

    @JSON(name = "pinIvCvc3Track2")
    private String pinIvCvc3Track2;

    @JSON(name = "ciacDeclineOnPpms")
    private String ciacDeclineOnPpms;

    @JSON(name = "alternateContactlessPaymentData")
    private AlternateContactlessPaymentData alternateContactlessPaymentData;

    @JSON(name = "records")
    private Records[] records;

    public String getAid() {
        return aid;
    }

    public void setAid(final String aid) {
        this.aid = aid;
    }

    public String getPpseFci() {
        return ppseFci;
    }

    public void setPpseFci(final String ppseFci) {
        this.ppseFci = ppseFci;
    }

    public String getPaymentFci() {
        return paymentFci;
    }

    public void setPaymentFci(final String paymentFci) {
        this.paymentFci = paymentFci;
    }

    public String getGpoResponse() {
        return gpoResponse;
    }

    public void setGpoResponse(final String gpoResponse) {
        this.gpoResponse = gpoResponse;
    }

    public String getCdol1RelatedDataLength() {
        return cdol1RelatedDataLength;
    }

    public void setCdol1RelatedDataLength(final String cdol1RelatedDataLength) {
        this.cdol1RelatedDataLength = cdol1RelatedDataLength;
    }

    public String getCiacDecline() {
        return ciacDecline;
    }

    public void setCiacDecline(final String ciacDecline) {
        this.ciacDecline = ciacDecline;
    }

    public String getCvrMaskAnd() {
        return cvrMaskAnd;
    }

    public void setCvrMaskAnd(final String cvrMaskAnd) {
        this.cvrMaskAnd = cvrMaskAnd;
    }

    public String getIssuerApplicationData() {
        return issuerApplicationData;
    }

    public void setIssuerApplicationData(final String issuerApplicationData) {
        this.issuerApplicationData = issuerApplicationData;
    }

    public IccPrivateKeyCrtComponents getIccPrivateKeyCrtComponents() {
        return iccPrivateKeyCrtComponents;
    }

    public void setIccPrivateKeyCrtComponents(final IccPrivateKeyCrtComponents iccPrivateKeyCrtComponents) {
        this.iccPrivateKeyCrtComponents = iccPrivateKeyCrtComponents;
    }

    public String getPinIvCvc3Track2() {
        return pinIvCvc3Track2;
    }

    public void setPinIvCvc3Track2(final String pinIvCvc3Track2) {
        this.pinIvCvc3Track2 = pinIvCvc3Track2;
    }

    public String getCiacDeclineOnPpms() {
        return ciacDeclineOnPpms;
    }

    public void setCiacDeclineOnPpms(final String ciacDeclineOnPpms) {
        this.ciacDeclineOnPpms = ciacDeclineOnPpms;
    }

    public AlternateContactlessPaymentData getAlternateContactlessPaymentData() {
        return alternateContactlessPaymentData;
    }

    public void setAlternateContactlessPaymentData(final AlternateContactlessPaymentData alternateContactlessPaymentData) {
        this.alternateContactlessPaymentData = alternateContactlessPaymentData;
    }

    public Records[] getRecords() {
        return records;
    }

    public void setRecords(final Records[] records) {
        this.records = records;
    }
}