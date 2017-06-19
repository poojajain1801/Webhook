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

import com.mastercard.mcbp.transactiondecisionmanager.input.CvmResults;
import com.mastercard.mcbp.transactiondecisionmanager.input.MobileSupportIndicator;
import com.mastercard.mcbp.transactiondecisionmanager.input.TerminalRiskManagementData;

/**
 * Information about terminal support for CD-CVM
 */
public enum CdCvmSupport {
    YES,
    NO,
    UNKNOWN;

    /**
     * Utility function to determine the status of the CD CVM is active on the POS
     *
     * @param cvmResults           The CVM Results as received in the C-APDU
     * @param terminalRiskManagementData The Terminal Risk Management Data
     * @return The status of the CD CVM on the POS
     */
    public static CdCvmSupport forMchip(
            final CvmResults cvmResults,
            final TerminalRiskManagementData terminalRiskManagementData) {
        final boolean cdCvmToBePerformed = cvmResults.isCdCvmToBePerformed();

        if (terminalRiskManagementData != null) {
            if (terminalRiskManagementData.isCdCvmSupported()) {
                return CdCvmSupport.YES;
            }
            if (cdCvmToBePerformed) {
                return CdCvmSupport.YES;
            }
            return CdCvmSupport.NO;
        }

        // In case we do not have the Risk Management Data we fall back to the CVM Results
        return cdCvmToBePerformed ? CdCvmSupport.YES : CdCvmSupport.UNKNOWN;
    }

    /**
     * Utility function to determine whether the CD CVM is active on the POS
     *
     * @param mobileSupportIndicator     The Mobile Support Indicator field of the C-APDU
     * @param terminalRiskManagementData The Terminal Risk Management Data
     * @return The status of the CD CVM on the POS
     */
    public static CdCvmSupport forMagstripe(
            final MobileSupportIndicator mobileSupportIndicator,
            final TerminalRiskManagementData terminalRiskManagementData) {

        final boolean cdCvmRequired = mobileSupportIndicator.isCdCvmRequired();

        if (terminalRiskManagementData != null) {
            if (terminalRiskManagementData.isCdCvmSupported()) {
                return CdCvmSupport.YES;
            }

            if (cdCvmRequired) {
                return CdCvmSupport.YES;
            }

            if (terminalRiskManagementData.isSupportedByTheTerminal()) {
                return CdCvmSupport.NO;
            }
        }

        if (cdCvmRequired) {
            return CdCvmSupport.YES;
        }
        return CdCvmSupport.NO;
    }
}
