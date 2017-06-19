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
import com.mastercard.mcbp.card.mobilekernel.DsrpOutputData;

/**
 * Interface for events related to a DSRP payment.
 */
public interface ProcessDsrpListener {
    /**
     * Event raised when there is an error during a DSRP payment.
     */
    void onRemotePaymentError();

    /**
     * Event raised when a DSRP payment has been completed.
     *
     * @param outputData Instance of {@link DsrpOutputData}
     *                   containing the information from the completed payment.
     */
    void onRemotePaymentComplete(DsrpOutputData outputData);

    /**
     * Event raised during a DSRP payment when the user is required to enter their PIN.
     *
     * @param pinListener The listener to call to once the PIN has been entered.
     */
    void onPinRequired(PinListener pinListener);
}
