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

package com.mastercard.mcbp.transactiondecisionmanager.transaction;

import com.mastercard.mcbp.transactiondecisionmanager.input.CvmResults;
import com.mastercard.mcbp.transactiondecisionmanager.input.MobileSupportIndicator;
import com.mastercard.mcbp.transactiondecisionmanager.input.TerminalCapabilities;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;

/**
 * Transaction Value related information (e.g. Low Value vs High Value)
 */
public enum TransactionRange {
    /**
     * Low Value Transaction
     */
    LOW_VALUE,

    /**
     * High Value Transaction
     */
    HIGH_VALUE,

    /**
     * The range of the transaction could not be determined
     */
    UNKNOWN;

    /**
     * Determine the transaction range for a MCHIP transaction by using the CVM Results of the
     * Generate AC C-APDU
     *
     * @param cvmResults The CVM Results from the Generate AC Command APDU
     * @return The transaction range information for a MCHIP transaction
     */
    static TransactionRange forMchip(final CvmResults cvmResults) {
        if (cvmResults == null) {
            return UNKNOWN;
        }
        return cvmResults.isNoCvmToBePerformed() ? LOW_VALUE : HIGH_VALUE;
    }


    /**
     * Determine the transaction range for a Remote Payment transaction
     */
    static TransactionRange forRemotePayment() {
        return UNKNOWN;
    }

    /**
     * Determine the transaction range for a Magstripe Transaction using the Mobile Support
     * Indicator and the Terminal Capabilities of the ComputeCC C-APDU
     *
     * @param mobileSupportIndicator The Mobile Support Indicator
     * @param terminalCapabilities   The Terminal Capabilities
     * @return The transaction range information for a Magstripe transaction
     */
    static TransactionRange forMagstripe(final MobileSupportIndicator mobileSupportIndicator,
                                         final TerminalCapabilities terminalCapabilities) {

        if (terminalCapabilities == null || !terminalCapabilities.isSupportedByTheTerminal()) {
            // Check the Mobile Support Indicator
            if (mobileSupportIndicator.isCdCvmRequired()) {
                return HIGH_VALUE;
            }
            return UNKNOWN;
        }

        if (terminalCapabilities.isNoCvmOnlySupported()) {
            return LOW_VALUE;
        }

        if (terminalCapabilities.isCvmOtherThanNoCvmSupported()) {
            return HIGH_VALUE;
        }

        // We should never get down here.
        return UNKNOWN;
    }
}
