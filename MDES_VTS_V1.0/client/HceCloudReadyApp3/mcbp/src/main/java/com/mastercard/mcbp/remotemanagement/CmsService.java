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

package com.mastercard.mcbp.remotemanagement;

import com.mastercard.mcbp.businesslogic.MobileDeviceInfo;
import com.mastercard.mcbp.remotemanagement.mcbpV1.CmsRegisterResult;
import com.mastercard.mcbp.userinterface.UserInterfaceListener;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * This module is responsible for the communication with the Credentials
 * Management System in the context of the Remote Management of the Mobile
 * Payment Application. The Remote Management encompasses the delivery of assets
 * to the Mobile Payment Application such as Card Profile(s) and the Set(s) of
 * Keys (including Single Use Keys) necessary to support transactions as well as
 * the remote management functions. Both can be triggered anytime by the
 * Credentials Management System.
 */
public interface CmsService {

    /**
     * Registers the MPA and the user to the CMS.
     */
    CmsRegisterResult registerToCms(String userId, String activationCode);

    /**
     * Sends RNS and device information data to CMS.
     */
    CmsRegisterResult sendInformation(String rnsId, String userId);

    /**
     * Creates a session with the CMS. This method must be called when a RNS message is received.
     */
    void openRemoteSession(ByteArray rnsMessageId);

    /**
     * Registers a ui listener if needed
     */
    void registerUiListener(UserInterfaceListener listener);

    void goOnlineForSync();

    void setMobileDeviceInfo(MobileDeviceInfo mobileDeviceInfo);

    /**
     * Insert dummy mobile key set id to use while insertion of a mobile key after successful
     * registration of MPA.
     * Used for backward architecture compatibility
     */
    void insertDummyMobileKeySetId();
}
