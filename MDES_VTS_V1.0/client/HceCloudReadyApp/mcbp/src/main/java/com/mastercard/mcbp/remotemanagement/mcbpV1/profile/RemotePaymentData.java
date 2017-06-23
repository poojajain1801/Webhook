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
class RemotePaymentData {
    @JSON(name = "PAN")
    public String pan;

    @JSON(name = "issuerApplicationData")
    public String issuerApplicationData;

    @JSON(name = "CVR_MaskAnd")
    public String cvrMaskAnd;

    @JSON(name = "applicationExpiryDate")
    public String applicationExpiryDate;

    @JSON(name = "track2_equivalentData")
    public String track2EquivalentData;

    @JSON(name = "PAN_SequenceNumber")
    public String panSequenceNumber;

    @JSON(name = "CIAC_Decline")
    public String ciacDecline;

    @JSON(name = "AIP")
    public String aip;
}
