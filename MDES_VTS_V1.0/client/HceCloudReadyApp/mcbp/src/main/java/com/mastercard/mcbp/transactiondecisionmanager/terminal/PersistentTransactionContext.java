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

package com.mastercard.mcbp.transactiondecisionmanager.terminal;

import com.mastercard.mcbp.transactiondecisionmanager.input.MobileSupportIndicator;

/**
 * Information about terminal support for a persistent transaction context (i.e. two taps)
 */
public enum PersistentTransactionContext {
    YES,
    NO,
    UNKNOWN;

    /**
     * Utility function to determine the status of the Persistent Transaction Context
     *
     * @param mobileSupportIndicator The Mobile Support Indicator field of the C-APDU
     * @return The Persistent Transaction Context
     */
    public static PersistentTransactionContext forMagstripe(
            final MobileSupportIndicator mobileSupportIndicator) {
        return isMobileSupportedIn(mobileSupportIndicator);
    }

    /**
     * Utility function to determine the status of the Persistent Transaction Context
     *
     * @param mobileSupportIndicator The Mobile Support Indicator field of the C-APDU
     * @return The Persistent Transaction Context
     */
    public static PersistentTransactionContext forMchip(
            final MobileSupportIndicator mobileSupportIndicator) {
        return isMobileSupportedIn(mobileSupportIndicator);
    }

    /**
     * Return the Persistent Transaction Context based on the Mobile Support Indicator.
     * We use the same for both Magstripe and MCHIP
     */
    private static PersistentTransactionContext isMobileSupportedIn(
            final MobileSupportIndicator msi) {
        return msi.isMobileSupported() ? PersistentTransactionContext.YES
                                       : PersistentTransactionContext.NO;
    }
}
