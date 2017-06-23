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

/**
 * Expected User Action on Point of Interaction
 */
public enum ExpectedUserActionOnPoi {
    /**
     * The User will not be requested for any action
     * */
    NONE,

    /**
     * The User will be requested to enter Online PIN
     * */
    ONLINE_PIN,

    /**
     * The User will be requested for Signature
     * */
    SIGNATURE,

    /**
     * The User will be requested for either Online Pin or Signature, but it could not be determined
     */
    ONLINE_PIN_OR_SIGNATURE,

    /**
     * User action could not be determined or information was not available
     * */
    UNKNOWN;

    /**
     * Build the Expected User Action on POI for Remote Payment
     */
    public static ExpectedUserActionOnPoi forRemotePayment() {
        return NONE;
    }

    /**
     * Build the Expected User Action on POI for MCHIP
     * @param cvmResults The CVM Results as received in the C-APDU
     */
    public static ExpectedUserActionOnPoi forMchip(final CvmResults cvmResults) {
        if (cvmResults == null) {
            return UNKNOWN;
        }

        if (cvmResults.isNoCvmToBePerformed() || cvmResults.isCdCvmOnlyToBePerformed()) {
            return NONE;
        }

        if (cvmResults.isOnlinePinToBePerformed()) {
            return ONLINE_PIN;
        }

        if (cvmResults.isSignatureToBePerformed()) {
            return SIGNATURE;
        }

        return UNKNOWN;
    }

    /**
     * Build the Expected User Action on POI for Magstripe
     * @param mobileSupportIndicator The Mobile Support Indicator as received in the C-APDU
     * @param terminalCapabilities The Terminal Capabilities as received in the C-APDU
     */
    public static ExpectedUserActionOnPoi forMagstripe(
            final MobileSupportIndicator mobileSupportIndicator,
            final TerminalCapabilities terminalCapabilities) {
        if (terminalCapabilities == null) {
            // Check the Mobile Support Indicator
            if (mobileSupportIndicator.isCdCvmRequired()) {
                return NONE;
            }
            return UNKNOWN;
        }

        if (terminalCapabilities.isNoCvmOnlySupported() ||
            mobileSupportIndicator.isCdCvmRequired()) {
            return NONE;
        }

        if (terminalCapabilities.isOnlinePinAndSignature()) {
            return ONLINE_PIN_OR_SIGNATURE;
        }

        if (terminalCapabilities.isOnlinePinOnly()) {
            return ONLINE_PIN;
        }

        if (terminalCapabilities.isSignatureOnly()) {
            return SIGNATURE;
        }

        return UNKNOWN;
    }
}
