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
 * Represents the alternate contactless payment data in card profile of mdes.
 */
public class AlternateContactlessPaymentData {

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

    public String getCvrMaskAnd() {
        return cvrMaskAnd;
    }

    public void setCvrMaskAnd(final String cvrMaskAnd) {
        this.cvrMaskAnd = cvrMaskAnd;
    }

    public String getCiacDecline() {
        return ciacDecline;
    }

    public void setCiacDecline(final String ciacDecline) {
        this.ciacDecline = ciacDecline;
    }

    public String getGpoResponse() {
        return gpoResponse;
    }

    public void setGpoResponse(final String gpoResponse) {
        this.gpoResponse = gpoResponse;
    }

    public String getPaymentFci() {
        return paymentFci;
    }

    public void setPaymentFci(final String paymentFci) {
        this.paymentFci = paymentFci;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(final String aid) {
        this.aid = aid;
    }
}