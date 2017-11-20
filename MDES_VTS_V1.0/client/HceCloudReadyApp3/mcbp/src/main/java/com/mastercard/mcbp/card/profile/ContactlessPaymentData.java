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

package com.mastercard.mcbp.card.profile;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import flexjson.JSON;

public final class ContactlessPaymentData {

    @JSON(name = "issuerApplicationData")
    private ByteArray issuerApplicationData;

    @JSON(name = "iccPrivateKeyCrtComponents")
    private IccPrivateKeyCrtComponents iccPrivateKeyCrtComponents;

    @JSON(name = "gpoResponse")
    private ByteArray gpoResponse;

    @JSON(name = "cdol1RelatedDataLength")
    private int cdol1RelatedDataLength;

    @JSON(name = "ciacDecline")
    private ByteArray ciacDecline;

    @JSON(name = "ciacDeclineOnPpms")
    private ByteArray ciacDeclineOnPpms;

    @JSON(name = "alternateContactlessPaymentData")
    private AlternateContactlessPaymentData alternateContactlessPaymentData;

    @JSON(name = "paymentFci")
    private ByteArray paymentFci;

    @JSON(name = "ppseFci")
    private ByteArray ppseFci;

    @JSON(name = "cvrMaskAnd")
    private ByteArray cvrMaskAnd;

    @JSON(name = "aid")
    private ByteArray aid;

    @JSON(name = "pinIvCvc3Track2")
    private ByteArray pinIvCvc3Track2;

    @JSON(name = "records")
    private Record[] records;

    public ByteArray getIssuerApplicationData() {
        return issuerApplicationData;
    }

    public void setIssuerApplicationData(ByteArray issuerApplicationData) {
        this.issuerApplicationData = issuerApplicationData;
    }

    public ByteArray getGpoResponse() {
        return gpoResponse;
    }

    public void setGpoResponse(ByteArray gpoResponse) {
        this.gpoResponse = gpoResponse;
    }

    // This function is used by C++ library although it is reported as not used
    public int getCdol1RelatedDataLength() {
        return cdol1RelatedDataLength;
    }

    public void setCdol1RelatedDataLength(int cdol1RelatedDataLength) {
        this.cdol1RelatedDataLength = cdol1RelatedDataLength;
    }

    public ByteArray getCiacDecline() {
        return ciacDecline;
    }

    public void setCiacDecline(ByteArray ciacDecline) {
        this.ciacDecline = ciacDecline;
    }

    public ByteArray getCiacDeclineOnPpms() {
        return ciacDeclineOnPpms;
    }

    public void setCiacDeclineOnPpms(ByteArray ciacDeclineOnPpms) {
        this.ciacDeclineOnPpms = ciacDeclineOnPpms;
    }

    public AlternateContactlessPaymentData getAlternateContactlessPaymentData() {
        return alternateContactlessPaymentData;
    }

    public void setAlternateContactlessPaymentData(
            AlternateContactlessPaymentData alternateContactlessPaymentData) {
        this.alternateContactlessPaymentData = alternateContactlessPaymentData;
    }

    public ByteArray getPaymentFci() {
        return paymentFci;
    }

    public void setPaymentFci(ByteArray paymentFci) {
        this.paymentFci = paymentFci;
    }

    public ByteArray getPpseFci() {
        return ppseFci;
    }

    public void setPpseFci(ByteArray ppseFci) {
        this.ppseFci = ppseFci;
    }

    public ByteArray getCvrMaskAnd() {
        return cvrMaskAnd;
    }

    public void setCvrMaskAnd(ByteArray cvrMaskAnd) {
        this.cvrMaskAnd = cvrMaskAnd;
    }

    public ByteArray getAid() {
        return aid;
    }

    public void setAid(ByteArray aid) {
        this.aid = aid;
    }

    public ByteArray getPinIvCvc3Track2() {
        return pinIvCvc3Track2;
    }

    public void setPinIvCvc3Track2(ByteArray pinIvCvc3Track2) {
        this.pinIvCvc3Track2 = pinIvCvc3Track2;
    }
    public Record[] getRecords() {
        return records;
    }

    public void setRecords(Record[] records) {
        this.records = records;
    }

    public IccPrivateKeyCrtComponents getIccPrivateKeyCrtComponents() {
        return iccPrivateKeyCrtComponents;
    }

    public void setIccPrivateKeyCrtComponents(
            final IccPrivateKeyCrtComponents iccPrivateKeyCrtComponents) {
        this.iccPrivateKeyCrtComponents = iccPrivateKeyCrtComponents;
    }

    public final boolean isMagstripeDataValid() {
        return pinIvCvc3Track2 != null &&
               pinIvCvc3Track2.getLength() == 2 &&
               ciacDeclineOnPpms != null &&
               ciacDeclineOnPpms.getLength() == 2;
    }

    public boolean isPrimaryAidMchipDataValid() {
        return ciacDecline != null &&
               ciacDecline.getLength() == 3 &&
               cvrMaskAnd != null &&
               cvrMaskAnd.getLength() == 6 &&
               cdol1RelatedDataLength >= 45 &&
               issuerApplicationData != null &&
               issuerApplicationData.getLength() == 18 &&
               iccPrivateKeyCrtComponents.getP() != null &&
               iccPrivateKeyCrtComponents.getQ() != null &&
               iccPrivateKeyCrtComponents.getDp() != null &&
               iccPrivateKeyCrtComponents.getDq() != null &&
               iccPrivateKeyCrtComponents.getU() != null;
    }

    public boolean isAlternateAidMchipDataValid() {
        return alternateContactlessPaymentData != null &&
               alternateContactlessPaymentData.getCiacDecline() != null &&
               alternateContactlessPaymentData.getCiacDecline().getLength() == 3 &&
               alternateContactlessPaymentData.getCvrMaskAnd() != null &&
               alternateContactlessPaymentData.getCvrMaskAnd().getLength() == 6 &&
               isPrimaryAidMchipDataValid();
    }

    public void wipe() {
        cdol1RelatedDataLength = 0;
        alternateContactlessPaymentData.wipe();
        Utils.clearByteArray(aid);
        Utils.clearByteArray(ciacDecline);
        Utils.clearByteArray(ciacDeclineOnPpms);
        Utils.clearByteArray(cvrMaskAnd);
        Utils.clearByteArray(gpoResponse);
        Utils.clearByteArray(issuerApplicationData);
        Utils.clearByteArray(paymentFci);
        Utils.clearByteArray(pinIvCvc3Track2);
        Utils.clearByteArray(ppseFci);
        if (records != null && records.length > 0) {
            for (Record record : records) {
                record.wipe();
            }
        }
    }

}