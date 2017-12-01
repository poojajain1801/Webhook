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

import com.mastercard.mcbp.card.cvm.PinListener;
import com.mastercard.mcbp.userinterface.DisplayTransactionInfo;

/**
 * Interface for events related to a contactless payment.
 */
public interface ProcessContactlessListener {
    /**
     * Event raised after the PIN has been entered and MPP lite has been initialised.  At this point
     * we are now waiting for the user to tap the contactless terminal to complete the
     * authorized payment.
     */
    void onContactlessReady();

    /**
     * Event raised when a contactless payment has been completed.
     *
     * @param info Instance of {@link DisplayTransactionInfo}
     *             containing details of the transaction.
     */
    void onContactlessPaymentCompleted(DisplayTransactionInfo info);

    /**
     * Event raised when an contactless payment has been aborted.
     *
     * @param info Instance of {@link DisplayTransactionInfo}
     *             containing details of the transaction.
     */
    void onContactlessPaymentAborted(DisplayTransactionInfo info);

    /**
     * Event raised during a contactless payment when the user is required to enter their PIN.
     *
     * @param pinListener The listener to call once the PIN has been entered.
     */
    void onPinRequired(PinListener pinListener);
}
