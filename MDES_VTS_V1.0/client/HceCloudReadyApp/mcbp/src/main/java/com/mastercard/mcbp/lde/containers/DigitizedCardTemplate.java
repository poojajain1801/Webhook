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

package com.mastercard.mcbp.lde.containers;

import com.mastercard.mcbp.card.profile.DigitizedCardProfile;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mobile_api.payment.cld.Cld;


public class DigitizedCardTemplate {

    /**
     * Digitized Card identifier.
     */
    private String mDigitizedCardId;

    /**
     * Mobile Payment container.
     */
    private MobilePaymentContainer mMobilePaymentContainer;
    /**
     * Monitoring container
     */
    private MonitoringContainer mMonitoringContainer;
    /**
     * User Interaction Container
     */
    private UserInteractionContainer mUserInteractionContainer;

    public DigitizedCardTemplate(String digitizedCardId, DigitizedCardProfile profile) {
        this.setDigitizedCardId(digitizedCardId);

        this.mUserInteractionContainer = new UserInteractionContainer(profile);

        this.mMobilePaymentContainer = new MobilePaymentContainer(profile.getMppLiteModule());
    }

    public void wipeDigitalizedData() {
        if(mMonitoringContainer != null){
            mMonitoringContainer.wipeData();
        }

        if(mMobilePaymentContainer != null){
            mMobilePaymentContainer.wipeData();
        }

        if(mUserInteractionContainer != null){
            mUserInteractionContainer.wipeData();
        }
    }

    public String getDigitizedCardId() {
        return mDigitizedCardId;
    }

    public void setDigitizedCardId(String digitizedCardId) {
        this.mDigitizedCardId = digitizedCardId;
    }

    public boolean isClSupported() {
        return getUserInteractionContainer().isClSupported();
    }

    public boolean isRpSupported() {
        return getUserInteractionContainer().isRpSupported();
    }

    public Cld getCld() {
        return getUserInteractionContainer().getCld();
    }

    public String getCardMetadata() { return mUserInteractionContainer.getCardMetadata();}

    public void setCardMetadata(String metadata) {
        mUserInteractionContainer.setCardMetadata(metadata);
    }

    public int getMaximumPinTry() { return mUserInteractionContainer.getMaximumPinTry();}

    public int getCvmResetTimeout() {
        return getUserInteractionContainer().getCvmResetTimeout();
    }

    public int getDualTapResetTimeout() {
        return getUserInteractionContainer().getDualTapResetTimeout();
    }

    public String getCvm() {
        return getUserInteractionContainer().getCvm();
    }

    public MppLiteModule getDcCpMpp() {
        return getMobilePaymentContainer().getMppLiteModule();
    }

    private UserInteractionContainer getUserInteractionContainer() {
        return mUserInteractionContainer;
    }

    private void setUserInteractionContainer(UserInteractionContainer userInteractionContainer) {
        this.mUserInteractionContainer = userInteractionContainer;
    }

    private MobilePaymentContainer getMobilePaymentContainer() {
        return mMobilePaymentContainer;
    }

    private void setMobilePaymentContainer(MobilePaymentContainer mobilePaymentContainer) {
        this.mMobilePaymentContainer = mobilePaymentContainer;
    }

    private MonitoringContainer getMonitoringContainer() {
        return mMonitoringContainer;
    }

    private void setMonitoringContainer(MonitoringContainer monitoringContainer) {
        this.mMonitoringContainer = monitoringContainer;
    }

}
