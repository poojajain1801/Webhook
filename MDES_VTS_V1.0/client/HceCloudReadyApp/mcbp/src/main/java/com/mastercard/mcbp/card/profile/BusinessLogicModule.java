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

/**
 * Business Logic Module related Card Profile information
 * */
public final class BusinessLogicModule {
    /**
     * The Dual Tap Reset timeout. Time between first and second tap in which the Cardholder can
     * be authenticated
     * */
    @JSON(name = "dualTapResetTimeout")
    private int mDualTapResetTimeout;

    /**
     * The Cardholder verification method timeout. When the timeout expires the card holder has to
     * be verified again
     * */
    @JSON(name = "cvmResetTimeout")
    private int mCvmResetTimeout;

    /**
     * Issuers related options for Magstripe Cardholder verification
     * */
    @JSON(name = "magstripeCvmIssuerOptions")
    private CvmIssuerOptions mMagstripeCvmIssuerOptions;

    /**
     * Issuers related options for M-Chip Cardholder verification
     * */
    @JSON(name = "mChipCvmIssuerOptions")
    private CvmIssuerOptions mChipCvmIssuerOptions;

    /**
     * The security word. Provided for legacy reasons, but not currently used
     * */
    @JSON(name = "securityWord")
    private ByteArray mSecurityWord;

    /**
     * Application Life Cycle data. Provided for legacy reasons, but not currently used
     * */
    @JSON(name = "applicationLifeCycleData")
    private ByteArray mApplicationLifeCycleData;

    /**
     * Cardholder validators
     * */
    @JSON(name = "cardholderValidators")
    private CardholderValidators mCardholderValidators;

    /**
     * Card Layout Description. The field is supported for legacy reasons but it will be deprecated
     * in future releases. Please use card metadata information instead
     * */
    @Deprecated
    @JSON(name = "cardLayoutDescription")
    private ByteArray mCardLayoutDescription;

    /**
     * Get the Security Word
     *
     * @return The Security Word
     *
     * */
    @SuppressWarnings("unused")
    public ByteArray getSecurityWord() {
        return mSecurityWord;
    }

    /**
     * Set the Security Word
     *
     * @param securityWord The Security Word
     *
     * */
    public void setSecurityWord(ByteArray securityWord) {
        this.mSecurityWord = securityWord;
    }

    /**
     * Get the Dual Tap Reset Timeout
     *
     * @return The Dual Tap Reset Timeout (in seconds)
     *
     * */
    public int getDualTapResetTimeout() {
        return mDualTapResetTimeout;
    }

    /**
     * Set the Dual Tap Reset Timeout
     *
     * @param dualTapResetTimeout The value of the Dual Tap Reset Timeout (in seconds)
     *
     * */
    public void setDualTapResetTimeout(int dualTapResetTimeout) {
        this.mDualTapResetTimeout = dualTapResetTimeout;
    }

    /**
     * Get the Issuer related Magstripe Cardholder Verification Options
     *
     * @return The Issuer related Magstripe Cardholder Verification Options
     * */
    @SuppressWarnings("unused")
    public CvmIssuerOptions getMagstripeCvmIssuerOptions() {
        return mMagstripeCvmIssuerOptions;
    }

    /**
     * Set the Issuer related Magstripe Cardholder Verification Options
     *
     * @param magstripeCvmIssuerOptions The Issuer related Magstripe Cardholder Verification Options
     * */
    public void setMagstripeCvmIssuerOptions(CvmIssuerOptions magstripeCvmIssuerOptions) {
        this.mMagstripeCvmIssuerOptions = magstripeCvmIssuerOptions;
    }

    /**
     * Get the Issuer related M-Chip Cardholder Verification Options
     *
     * @return The Issuer related M-Chip Cardholder Verification Options
     * */
    @SuppressWarnings("unused")
    public CvmIssuerOptions getMChipCvmIssuerOptions() {
        return mChipCvmIssuerOptions;
    }

    /**
     * Set the Issuer related M-Chip Cardholder Verification Options
     *
     * @param mChipCvmIssuerOptions The Issuer related Magstripe Cardholder Verification Options
     * */
    public void setMChipCvmIssuerOptions(CvmIssuerOptions mChipCvmIssuerOptions) {
        this.mChipCvmIssuerOptions = mChipCvmIssuerOptions;
    }

    /**
     * Get the Application Life Cycle Data
     *
     * @return The Application Life Cycle Data
     *
     * */
    @SuppressWarnings("unused")
    public ByteArray getApplicationLifeCycleData() {
        return mApplicationLifeCycleData;
    }

    /**
     * Set the Application Life Cycle Data
     *
     * @param applicationLifeCycleData The value of the application Life Cycle data
     * */
    public void setApplicationLifeCycleData(ByteArray applicationLifeCycleData) {
        this.mApplicationLifeCycleData = applicationLifeCycleData;
    }

    /**
     * Get the Cardholder validator for this card
     *
     * @return The Cardholder validator object associated with the card profile
     *
     * */
    public CardholderValidators getCardholderValidators() {
        return mCardholderValidators;
    }

    /**
     * Set the Cardholder validator for this card
     *
     * @param cardholderValidators The Cardholder validator object associated withthe card profile
     *
     * */
    public void setCardholderValidators(CardholderValidators cardholderValidators) {
        this.mCardholderValidators = cardholderValidators;
    }

    /**
     * Get the Cvm Reset Timeout
     *
     * @return The Cardholder Verification Method reset timeout (in seconds)
     *
     * */
    public int getCvmResetTimeout() {
        return mCvmResetTimeout;
    }

    /**
     * Set the Cvm Reset Timeout
     *
     * @param cvmResetTimeout The value of Cardholder Verification Method reset timeout (in seconds)
     *
     * */
    public void setCvmResetTimeout(int cvmResetTimeout) {
        this.mCvmResetTimeout = cvmResetTimeout;
    }

    /**
     * Get The Card Layout Description
     *
     * @return A byte array containing the Card Layout Description
     *
     * */
    @Deprecated
    public ByteArray getCardLayoutDescription() {
        return mCardLayoutDescription;
    }

    /**
     * Set The Card Layout Description
     *
     * @param cardLayoutDescription A byte array containing the Card Layout Description
     *
     * */
    @Deprecated
    public void setCardLayoutDescription(ByteArray cardLayoutDescription) {
        this.mCardLayoutDescription = cardLayoutDescription;
    }

    /**
     * Securely wipe the content of the business logic module profile information
     * */
    public void wipe() {
        Utils.clearByteArray(mApplicationLifeCycleData);
        Utils.clearByteArray(mCardLayoutDescription);
        Utils.clearByteArray(mSecurityWord);
    }
}
