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
 * Represents the business logic module in card profile of mdes.
 */
public class BusinessLogicModule {
    @JSON(name = "cvmResetTimeout")
    private int cvmResetTimeout;

    @JSON(name = "dualTapResetTimeout")
    private int dualTapResetTimeout;

    @JSON(name = "applicationLifeCycleData")
    private String applicationLifeCycleData;

    @JSON(name = "cardLayoutDescription")
    private String cardLayoutDescription;

    @JSON(name = "securityWord")
    private String securityWord;

    @JSON(name = "cardholderValidators")
    private String[] cardholderValidators;

    @JSON(name = "mChipCvmIssuerOptions")
    private MChipCvmIssuerOptions chipCvmIssuerOptions;

    @JSON(name = "magstripeCvmIssuerOptions")
    private MagstripeCvmIssuerOptions magstripeCvmIssuerOptions;

    public int getCvmResetTimeout() {
        return cvmResetTimeout;
    }

    public void setCvmResetTimeout(final int cvmResetTimeout) {
        this.cvmResetTimeout = cvmResetTimeout;
    }

    public int getDualTapResetTimeout() {
        return dualTapResetTimeout;
    }

    public void setDualTapResetTimeout(final int dualTapResetTimeout) {
        this.dualTapResetTimeout = dualTapResetTimeout;
    }

    public String getApplicationLifeCycleData() {
        return applicationLifeCycleData;
    }

    public void setApplicationLifeCycleData(final String applicationLifeCycleData) {
        this.applicationLifeCycleData = applicationLifeCycleData;
    }

    public String getCardLayoutDescription() {
        return cardLayoutDescription;
    }

    public void setCardLayoutDescription(final String cardLayoutDescription) {
        this.cardLayoutDescription = cardLayoutDescription;
    }

    public String getSecurityWord() {
        return securityWord;
    }

    public void setSecurityWord(final String securityWord) {
        this.securityWord = securityWord;
    }

    public String[] getCardholderValidators() {
        return cardholderValidators;
    }

    public void setCardholderValidators(final String[] cardholderValidators) {
        this.cardholderValidators = cardholderValidators;
    }
    // getMChipCvmIssuerOptions
    public MChipCvmIssuerOptions getMChipCvmIssuerOptions() {
        return chipCvmIssuerOptions;
    }

    public void setChipCvmIssuerOptions(final MChipCvmIssuerOptions chipCvmIssuerOptions) {
        this.chipCvmIssuerOptions = chipCvmIssuerOptions;
    }

    public MagstripeCvmIssuerOptions getMagstripeCvmIssuerOptions() {
        return magstripeCvmIssuerOptions;
    }

    public void setMagstripeCvmIssuerOptions(final MagstripeCvmIssuerOptions magstripeCvmIssuerOptions) {
        this.magstripeCvmIssuerOptions = magstripeCvmIssuerOptions;
    }
}
