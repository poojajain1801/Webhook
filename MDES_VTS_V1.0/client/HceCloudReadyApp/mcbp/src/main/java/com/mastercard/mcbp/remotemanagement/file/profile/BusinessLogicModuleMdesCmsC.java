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

class BusinessLogicModuleMdesCmsC {

    public int getCvmResetTimeout() {
        return cvmResetTimeout;
    }

    public void setCvmResetTimeout(int cvmResetTimeout) {
        this.cvmResetTimeout = cvmResetTimeout;
    }

    public int getDualTapResetTimeout() {
        return dualTapResetTimeout;
    }

    public void setDualTapResetTimeout(int dualTapResetTimeout) {
        this.dualTapResetTimeout = dualTapResetTimeout;
    }

    public String getApplicationLifeCycleData() {
        return applicationLifeCycleData;
    }

    public void setApplicationLifeCycleData(String applicationLifeCycleData) {
        this.applicationLifeCycleData = applicationLifeCycleData;
    }

    public String getCardLayoutDescription() {
        return cardLayoutDescription;
    }

    public void setCardLayoutDescription(String cardLayoutDescription) {
        this.cardLayoutDescription = cardLayoutDescription;
    }

    public String getSecurityWord() {
        return securityWord;
    }

    public void setSecurityWord(String securityWord) {
        this.securityWord = securityWord;
    }

    public String[] getCardholderValidators() {
        return cardholderValidators;
    }

    public void setCardholderValidators(String[] cardholderValidators) {
        this.cardholderValidators = cardholderValidators;
    }

    public CvmIssuerOptionsMdesCmsC getmChipCvmIssuerOptions() {
        return mChipCvmIssuerOptions;
    }

    public void setmChipCvmIssuerOptions(CvmIssuerOptionsMdesCmsC mChipCvmIssuerOptions) {
        this.mChipCvmIssuerOptions = mChipCvmIssuerOptions;
    }

    public CvmIssuerOptionsMdesCmsC getMagstripeCvmIssuerOptions() {
        return magstripeCvmIssuerOptions;
    }

    public void setMagstripeCvmIssuerOptions(CvmIssuerOptionsMdesCmsC magstripeCvmIssuerOptions) {
        this.magstripeCvmIssuerOptions = magstripeCvmIssuerOptions;
    }

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
    private CvmIssuerOptionsMdesCmsC mChipCvmIssuerOptions;

    @JSON(name = "magstripeCvmIssuerOptions")
    private CvmIssuerOptionsMdesCmsC magstripeCvmIssuerOptions;

}
