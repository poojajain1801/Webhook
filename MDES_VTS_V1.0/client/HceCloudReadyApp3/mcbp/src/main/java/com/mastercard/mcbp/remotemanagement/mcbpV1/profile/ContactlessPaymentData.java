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

package com.mastercard.mcbp.remotemanagement.mcbpV1.profile;

import flexjson.JSON;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
class ContactlessPaymentData {
    @JSON(name = "issuerApplicationData")
    public String issuerApplicationData;

    @JSON(name = "ICC_privateKey_a")
    public String iccPrivateKeyA;

    @JSON(name = "GPO_Response")
    public String gpoResponse;

    @JSON(name = "CDOL1_RelatedDataLength")
    public int cdol1RelatedDataLength;

    @JSON(name = "CIAC_Decline")
    public String ciacDecline;

    @JSON(name = "CIAC_DeclineOnPPMS")
    public String ciacDeclineOnPpms;

    @JSON(name = "alternateContactlessPaymentData")
    public AlternateContactlessPaymentData alternateContactlessPaymentData;

    @JSON(name = "ICC_privateKey_q")
    public String iccPrivateKeyQ;

    @JSON(name = "paymentFCI")
    public String paymentFci;

    @JSON(name = "PPSE_FCI")
    public String ppseFci;

    @JSON(name = "CVR_MaskAnd")
    public String cvrMaskAnd;

    @JSON(name = "ICC_privateKey_p")
    public String iccPrivateKeyP;

    @JSON(name = "ICC_privateKey_dq")
    public String iccPrivateKeyDq;

    @JSON(name = "AID")
    public String aid;

    @JSON(name = "PIN_IV_CVC3_Track2")
    public String pinIvCvc3Track2;

    @JSON(name = "ICC_privateKey_dp")
    public String iccPrivateKeyDp;

    @JSON(name = "records")
    public Records[] records;
}
