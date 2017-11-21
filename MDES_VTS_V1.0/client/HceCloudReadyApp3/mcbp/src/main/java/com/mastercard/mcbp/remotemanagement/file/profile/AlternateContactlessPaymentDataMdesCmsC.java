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

package com.mastercard.mcbp.remotemanagement.file.profile;

import flexjson.JSON;

class AlternateContactlessPaymentDataMdesCmsC {

    public AlternateContactlessPaymentDataMdesCmsC() {
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getPaymentFci() {
        return paymentFci;
    }

    public void setPaymentFci(String paymentFci) {
        this.paymentFci = paymentFci;
    }

    public String getGpoResponse() {
        return gpoResponse;
    }

    public void setGpoResponse(String gpoResponse) {
        this.gpoResponse = gpoResponse;
    }

    public String getCiacDecline() {
        return ciacDecline;
    }

    public void setCiacDecline(String ciacDecline) {
        this.ciacDecline = ciacDecline;
    }

    public String getCvrMaskAnd() {
        return cvrMaskAnd;
    }

    public void setCvrMaskAnd(String cvrMaskAnd) {
        this.cvrMaskAnd = cvrMaskAnd;
    }

    @JSON(name = "aid")
    private String aid;

    @JSON(name = "paymentFci")
    private String paymentFci;

    @JSON(name = "gpoResponse")
    private String gpoResponse;

    @JSON(name = "ciacDecline")
    private String ciacDecline;

    @JSON(name = "cvrMaskAnd")
    private String cvrMaskAnd;
}