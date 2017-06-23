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

package com.mastercard.mcbp.remotemanagement.mdes;

/**
 * CMS-D Remote Management Task Status
 */
public enum ChangePinStatus {
    /**
     * Get task status response "STARTED"
     */
    STARTED("STARTED"),
    /**
     * Get task status response "IN_PROGRESS"
     */
    IN_PROGRESS("IN_PROGRESS"),
    /**
     * Get task status response "FAILED"
     */
    FAILED("FAILED"),
    /**
     * Get task status response "COMPLETED"
     */
    COMPLETED("COMPLETED"),
    /**
     * Get task status response "INVALID_TASK_ID" when provided task id does not match CMSD side.
     */
    INVALID_TASK_ID("INVALID_TASK_ID");

    private final String mStatus;

    /**
     * Constructor
     */
     ChangePinStatus(String status) {
        this.mStatus = status;
    }

    /**
     * Enum to string conversion.
     */
    public String getStatus() {
        return mStatus;
    }

    /**
     * String to Status conversion
     *
     * @param status status in string.
     * @return Equivalent ChangePinStatus.
     */
    public static ChangePinStatus getValue(String status) {
        switch (status) {
            case "STARTED":
                return STARTED;
            case "IN_PROGRESS":
                return IN_PROGRESS;
            case "FAILED":
                return FAILED;
            case "COMPLETED":
                return COMPLETED;
            case "INVALID_TASK_ID":
                return INVALID_TASK_ID;
            default:
                return INVALID_TASK_ID;
        }
    }
}
