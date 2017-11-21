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

package com.mastercard.mcbp.transactiondecisionmanager.advice;

/**
 * Possible authorization decisions
 */
public enum Assessment {
    /**
     * Something went wrong, but the MPP Lite may be able to fix it (e.g. with a 2nd tap)
     */
    ABORT(2),
    /**
     * The Card Risk Management agree with the Purpose of the transaction. See:
     * {@link com.mastercard.mcbp.transactiondecisionmanager.transaction.Purpose}.
     */
    AGREE(1),
    /**
     * Decline the transaction and fail at the POS due to some major missing conditions
     */
    DECLINE(3),
    /**
     * Critical error from which we cannot recover such as wrong input data such malformed C-APDU
     */
    ERROR(4);


    /**
     * Constructor (private) to handle a priority associated with each value
     * @param priority The priority for this authorization message
     */
    Assessment(final int priority) {
        mPriority = priority;
    }

    /**
     * Get the priority for this authorization
     * @return The priority for this authorization (the higher the value, the higher the priority)
     */
    public int getSeverityLevel() {
        return mPriority;
    }

    /**
     * The priority associated with this authorization. The higher the number, the higher the
     * priority
     */
    private int mPriority;
}
