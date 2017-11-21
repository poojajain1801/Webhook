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

import com.mastercard.mcbp.remotemanagement.mdes.PendingRetryRequest;
import com.mastercard.mcbp.remotemanagement.mdes.RemoteManagementRequestType;
import com.mastercard.mcbp.userinterface.MdesRemoteManagementEventListener;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * Interface for CMS-Dedicated Remote Management Services
 */
public interface CmsDService {

    /**
     * Request for Replenish. Card must be in active state for replenish.
     *
     * @param digitizeCardId The Digitized Card Id
     */
    void requestForPaymentTokens(final String digitizeCardId)
            throws InvalidInput, InvalidCardStateException;

    /**
     * Request for Delete
     *
     * @param digitizeCardId The Digitized Card Id
     */
    void requestForDeleteToken(final String digitizeCardId)
            throws InvalidInput;

    /**
     * Rns message handling. This message is called after receiving remote notification.
     *
     * @param data input Remote Management Data
     */
    void handleNotification(ByteArray data);

    /**
     * Listener for remote management events
     *
     * @param listener MdesRemoteManagementEventListener
     */
    void registerMdesRemoteManagementListener(MdesRemoteManagementEventListener listener);

    /**
     * Request for Change/Set the PIN for this card.
     *
     * @param digitizedCardId  Instance of digitized card id.
     * @param currentMobilePin The entered current PIN .
     * @param newMobilePin     The entered new PIN.
     */
    void requestForMobilePinChange(String digitizedCardId,
                                   ByteArray currentMobilePin, ByteArray newMobilePin)
            throws InvalidInput, McbpCryptoException;

    /**
     * Get status of recent task requested from CMS-D.
     *
     * @param requestType Request Type.
     */
    void requestForTaskStatus(RemoteManagementRequestType requestType)
            throws InvalidInput;


    /**
     * Return the pending request
     *
     * @return PendingRetryRequest
     */
    PendingRetryRequest getPendingRequest();

    /**
     * Cancel the current pending request.
     */
    void cancelPendingRequest();

    /**
     * Force retry of pending action.
     */
    void forceRetry();

    /**
     * Check the general status of CMS-D.
     */
    void getSystemHealth();

    /**
     * @return true if any pending action is remaining to execute, false otherwise.
     */
    boolean isAnyActionPending();

    /**
     * @return true if a Remote Management request is processing and have not received response
     * from CMS-D.
     */
    boolean isProcessing();
}
