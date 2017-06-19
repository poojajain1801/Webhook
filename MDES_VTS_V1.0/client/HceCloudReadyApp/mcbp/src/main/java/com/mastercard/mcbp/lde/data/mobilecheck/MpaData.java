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

package com.mastercard.mcbp.lde.data.mobilecheck;

import com.mastercard.mcbp.businesslogic.MobileDeviceInfo;
import com.mastercard.mcbp.utils.BuildInfo;

import java.io.Serializable;
import java.util.Arrays;

import flexjson.JSON;

/**
 * This class encompass all the attributes that are required of Mobile Payment Application in Mobile
 * Check response.
 */
public class MpaData implements Serializable {

    private static final long serialVersionUID = 2244276923956395875L;
    @JSON(name = "mobileDeviceData")
    private MobileDeviceInfo mobileDeviceData;

    @JSON(name = "MPA_SpecificData")
    private MpaSpecificData mpaSpecificData;

    @JSON(name = "cardProfiles")
    private com.mastercard.mcbp.lde.data.mobilecheck.DigitizeCardProfileLogs[] cardProfiles;

    public MpaSpecificData getMpaSpecificData() {
        return mpaSpecificData;
    }

    public void setMpaSpecificData(MpaSpecificData mpaSpecificData) {
        this.mpaSpecificData = mpaSpecificData;
    }

    public com.mastercard.mcbp.lde.data.mobilecheck.DigitizeCardProfileLogs[] getCardProfiles() {
        return cardProfiles;
    }

    public void setCardProfiles(com.mastercard.mcbp.lde.data.mobilecheck.DigitizeCardProfileLogs[] cardProfiles) {
        this.cardProfiles = cardProfiles;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return Returns debug information for the class in debug mode.
     * In release mode it returns only the class name, so that sensitive information is never
     * returned by this method.
     */
    @Override
    public String toString() {
        if (BuildInfo.isDebugEnabled()) {
            return "MpaData [mobileDeviceData=" + getMobileDeviceData() + ", mpaSpecificData="
                   + mpaSpecificData + ", cardProfiles="
                   + Arrays.toString(cardProfiles) + "]";
        } else {
            return "MpaData";
        }
    }

    public MobileDeviceInfo getMobileDeviceData() {
        return mobileDeviceData;
    }

    public void setMobileDeviceData(MobileDeviceInfo mobileDeviceData) {
        this.mobileDeviceData = mobileDeviceData;
    }

}
