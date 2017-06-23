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
 * Represents the remote payment data in card profile of mdes.
 */
public class RemotePaymentData {

    @JSON(name = "track2Equivalent")
    private String track2Equivalent;

    @JSON(name = "pan")
    private String pan;

    @JSON(name = "panSequenceNumber")
    private String panSequenceNumber;

    @JSON(name = "applicationExpiryDate")
    private String applicationExpiryDate;

    @JSON(name = "aip")
    private String aip;

    @JSON(name = "ciacDecline")
    private String ciacDecline;

    @JSON(name = "cvrMaskAnd")
    private String cvrMaskAnd;

    @JSON(name = "issuerApplicationData")
    private String issuerApplicationData;

    public String getTrack2Equivalent() {
        return track2Equivalent;
    }

    public void setTrack2Equivalent(final String track2Equivalent) {
        this.track2Equivalent = track2Equivalent;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(final String pan) {
        this.pan = pan;
    }

    public String getPanSequenceNumber() {
        return panSequenceNumber;
    }

    public void setPanSequenceNumber(final String panSequenceNumber) {
        this.panSequenceNumber = panSequenceNumber;
    }

    public String getApplicationExpiryDate() {
        return applicationExpiryDate;
    }

    public void setApplicationExpiryDate(final String applicationExpiryDate) {
        this.applicationExpiryDate = applicationExpiryDate;
    }

    public String getAip() {
        return aip;
    }

    public void setAip(final String aip) {
        this.aip = aip;
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
}