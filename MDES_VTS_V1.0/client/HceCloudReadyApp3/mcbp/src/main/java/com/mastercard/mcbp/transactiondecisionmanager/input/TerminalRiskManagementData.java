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

package com.mastercard.mcbp.transactiondecisionmanager.input;

import com.mastercard.mcbp.card.mpplite.apdu.emv.DolValues;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;

import java.util.Arrays;

/**
 * The Terminal Risk Management data is modeled as object to provide easy access to utility
 * functions
 */
public final class TerminalRiskManagementData {
    public static final String TERMINAL_RISK_MANAGEMENT_DATA_TAG = "9F1D";

    /**
     * Terminal Risk Management Data as received in the C-APDU
     */
    final byte[] mTerminalRiskManagementData;

    /**
     * Build the Terminal Risk Management Data from the received PDOL data elements
     * @param pdolValues The list of PDOL values
     * @return The Terminal Risk Management object
     */
    public static TerminalRiskManagementData of(final DolValues pdolValues) {
        return of(pdolValues.getValueByTag(TERMINAL_RISK_MANAGEMENT_DATA_TAG));
    }

    /**
     * Build a Terminal Risk Management Data
     * @param terminalRiskManagementData The Terminal Risk Management Data as in the C-APDU
     * @return The Terminal Risk Management Data object
     */
    public static TerminalRiskManagementData of(final byte[] terminalRiskManagementData) {
        return terminalRiskManagementData == null ?
                null : new TerminalRiskManagementData(terminalRiskManagementData);
    }

    /**
     * The constructor is not available, please use the static factory instead.
     */
    private TerminalRiskManagementData(final byte[] terminalRiskManagementData) {
        if (terminalRiskManagementData == null || terminalRiskManagementData.length != 8) {
            // In case of invalid input
            throw new MppLiteException("Invalid Terminal Risk Management Data");
        } else {
            mTerminalRiskManagementData = terminalRiskManagementData;
        }
    }

    /**
     * Check whether this data element is supported by the terminal (i.e. a non empty data was
     * received)
     * @return True if the data element is supported by the terminal, false otherwise.
     */
    public final boolean isSupportedByTheTerminal() {
        return !Arrays.equals(mTerminalRiskManagementData, new byte[8]);
    }

    /**
     * Check whether CD CVM is supported in the Terminal Risk Management Data
     * @return True if CD CVM is supported, false otherwise.
     */
    public final boolean isCdCvmSupported() {
        return (mTerminalRiskManagementData[0] & 0x04) == 0x04;
    }
}
