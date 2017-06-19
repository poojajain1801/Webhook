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

package com.mastercard.mcbp.api;

import com.mastercard.mcbp.listeners.MdesCmsDedicatedTaskStatus;

/**
 * Internal implementation of the CMS Dedicated Task Status. Only known values are allowed
 */
enum MdesCmsDedicatedTaskStatusImpl implements MdesCmsDedicatedTaskStatus {
    /**
     * The task has been received and is pending processing.
     */
    PENDING("PENDING"),
    /**
     * The task is currently in progress.
     */
    IN_PROGRESS("IN_PROGRESS"),
    /**
     * The task was completed successfully.
     */
    COMPLETED("COMPLETED"),
    /**
     * The task was processed but failed to complete successfully.
     */
    FAILED("FAILED"),
    /**
     * The task is no more valid or unknown
     */
    INVALID_TASK_ID("INVALID_TASK_ID");


    /**
     * {@inheritDoc}
     * */
    public String toString() {
        return statusValue;
    }

    /**
     * String value of a given Task Status Enum
     * */
    private final String statusValue;

    /**
     * Private constructor to assign String to each Enum value
     * */
    MdesCmsDedicatedTaskStatusImpl(final String status) {
        statusValue = status;
    }
}
