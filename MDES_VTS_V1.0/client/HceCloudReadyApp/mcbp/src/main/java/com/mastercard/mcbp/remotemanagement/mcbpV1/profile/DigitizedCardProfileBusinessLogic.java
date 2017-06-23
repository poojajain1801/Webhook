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
class DigitizedCardProfileBusinessLogic {
    @JSON(name = "securityWord")
    public String securityWord;

    @JSON(name = "dualTapResetTimeout")
    public int dualTapResetTimeout;

    @JSON(name = "mChipCVM_IssuerOptions")
    public CvmIssuerOptions mChipCVM_IssuerOptions;

    @JSON(name = "applicationLifeCycleData")
    public String applicationLifeCycleData;

    @JSON(name = "cardholderValidators")
    public CardholderValidators cardholderValidators;

    @JSON(name = "CVM_ResetTimeout")
    public int cvmResetTimeout;

    @JSON(name = "cardLayoutDescription")
    public String cardLayoutDescription;

    @JSON(name = "magstripeCVM_IssuerOptions")
    public CvmIssuerOptions magstripeCvmIssuerOptions;
}
