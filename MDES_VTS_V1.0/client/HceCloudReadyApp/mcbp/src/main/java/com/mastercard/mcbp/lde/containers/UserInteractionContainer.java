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
import com.mastercard.mobile_api.payment.cld.Cld;

/**
 * Contains information needed for Business Logic.
 */
public class UserInteractionContainer {
    /**
     * Default Constructor
     */
    public UserInteractionContainer(DigitizedCardProfile profile) {
        this.mDigitizedCardProfile = profile;
        this.mCld = new Cld(profile.getBusinessLogicModule().getCardLayoutDescription());
    }

    public int getDualTapResetTimeout() {
        return mDigitizedCardProfile.getBusinessLogicModule().getDualTapResetTimeout();
    }

    public int getMaximumPinTry() {
        return mDigitizedCardProfile.getMaximumPinTry();
    }

    public String getCardMetadata() {return mDigitizedCardProfile.getCardMetadata();}

    public void setCardMetadata(String cardMetadata) {
        mDigitizedCardProfile.setCardMetadata(cardMetadata);
    }

    public int getCvmResetTimeout() {
        return mDigitizedCardProfile.getBusinessLogicModule().getCvmResetTimeout();
    }

    public String getCvm() {
        return mDigitizedCardProfile.getBusinessLogicModule().
                getCardholderValidators().getCardholderValidators();
    }

    public Cld getCld() {
        return mCld;
    }

    public boolean isClSupported() {
        return mDigitizedCardProfile.getContactlessSupported();
    }

    public boolean isRpSupported() {
        return mDigitizedCardProfile.getRemotePaymentSupported();
    }

    /**
     * card layout
     */
    private Cld mCld;

    /**
     * The Digitized Card Profile
     * */
    private DigitizedCardProfile mDigitizedCardProfile;

    public void wipeData() {
     if(mCld != null){
         mCld.clear();
     }
     if(mDigitizedCardProfile != null){
         mDigitizedCardProfile.wipe();
     }
    }
}
