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

import com.mastercard.mcbp.listeners.MdesCmsDedicatedPinChangeResult;

/**
 * Internal implementation of the CMS Dedicated PIN change result. Only known values are allowed
 */
enum MdesCmsDedicatedPinChangeResultImpl implements MdesCmsDedicatedPinChangeResult {
    /**
     * Pin change is successfully completed.
     */
    SUCCESS("SUCCESS"),
    /**
     * Invalid Pin provided.
     */
    INCORRECT_PIN("INCORRECT_PIN");

    /**
     * Return the String value of the
     */
    public String toString() {
        return result;
    }

    /**
     * String value of a given result
     */
    private final String result;

    /**
     * Private constructor to assign String to each Enum value
     */
    MdesCmsDedicatedPinChangeResultImpl(final String result) {
        this.result = result;
    }
}
