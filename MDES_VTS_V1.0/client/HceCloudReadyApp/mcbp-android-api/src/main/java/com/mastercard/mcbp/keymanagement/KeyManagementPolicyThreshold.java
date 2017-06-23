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

package com.mastercard.mcbp.keymanagement;

import com.mastercard.mcbp.card.McbpCard;

/**
 * A simple key management policy that requests new keys if the number of remaining keys falls below
 * a certain threshold.
 */
public class KeyManagementPolicyThreshold implements KeyManagementPolicy {
    /**
     * The default number of keys to fall below before requesting new keys.
     */
    private static final int DEFAULT_KEY_THRESHOLD_LIMIT = 3;

    /**
     * The number of keys to fall below before requesting new keys.
     */
    private int mKeyThresholdLimit = DEFAULT_KEY_THRESHOLD_LIMIT;

    /**
     * Constructor. Uses the default key threshold limit.
     */
    public KeyManagementPolicyThreshold() {
        // Intentional no-op
    }

    /**
     * Constructor. Allows a custom threshold to be set.
     *
     * @param threshold The key threshold limit.
     */
    public KeyManagementPolicyThreshold(int threshold) {
        mKeyThresholdLimit = threshold;
    }

    @Override
    public boolean shouldRequestNewKeys(McbpCard card) {
        return card.numberPaymentsLeft() < mKeyThresholdLimit;
    }
}
