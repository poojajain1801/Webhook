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

package com.mastercard.mcbp.listeners;

import com.mastercard.mcbp.remotemanagement.mdes.ChangePinStatus;

/**
 * Interface wrapping {@link com.mastercard.mcbp.userinterface.UserInterfaceListener} to be more
 * explicit in the events that have occurred in the background after receiving a remote
 * notification.
 */
public interface WalletEventListener {
    /**
     * Event raised when the application has been completely reset from the CMS system.
     */
    boolean applicationReset();

    /**
     * Event raised when a card has been deleted remotely from the CMS system.
     *
     * @param digitizedCardId Identifier of the card affected.
     */
    boolean cardDeleted(String digitizedCardId);

    /**
     * Event raised when a card has been added remotely from the CMS system.
     *
     * @param digitizedCardId Identifier of the card affected.
     */
    boolean cardAdded(String digitizedCardId);

    /**
     * Event raised when new payment tokens have been downloaded to the device.
     *
     * @param digitizedCardId Identifier of the card affected.
     */
    boolean paymentTokensAdded(String digitizedCardId);

    /**
     * Event raised when a card has been suspended remotely from the CMS system.
     *
     * @param digitizedCardId Identifier of the card affected.
     */
    boolean cardSuspended(String digitizedCardId);

    /**
     * Event raised when a card has been resumed remotely from the CMS system.
     *
     * @param digitizedCardId Identifier of the card affected.
     */
    boolean cardResumed(String digitizedCardId);

    /**
     * Event raised when a cards PIN number has been changed remotely from the CMS system.
     *
     * @param digitizedCardId Identifier of the card affected.
     */
    boolean pinChanged(String digitizedCardId);

    /**
     * Event raised when all cards have been deleted remotely from the CMS system.
     */
    boolean remoteWipe();

    /**
     * Event raised when a change pin status report has been received from the CMS-D
     *
     * Note: This listener is applicable only to MDES mode
     *
     * @param changePinStatus The current status of the PIN change request
     * */
    boolean changePinStatusReceived(final ChangePinStatus changePinStatus);

}
