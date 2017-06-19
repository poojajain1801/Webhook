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

/**
 * Specify the Terminal Type
 */
public enum TerminalType {
    BANK_ATTENDED,
    BANK_UNATTENDED,
    MERCHANT_ATTENDED,
    MERCHANT_UNATTENDED,
    CARDHOLDER_OPERATED,
    UNKNOWN;

    /**
     * Extract the Terminal Type information from the terminal type contained in the C-APDU
     *
     * @param terminalType The terminal type byte as in the C-APDU
     * @return The Terminal Type
     */
    public static TerminalType of(final byte terminalType) {
        switch (terminalType) {
            case (0x11):
            case (0x12):
            case (0x13):
                return TerminalType.BANK_ATTENDED;
            case (0x14):
            case (0x15):
            case (0x16):
                return TerminalType.BANK_UNATTENDED;
            case (0x21):
            case (0x22):
            case (0x23):
                return TerminalType.MERCHANT_ATTENDED;
            case (0x24):
            case (0x25):
            case (0x26):
                return TerminalType.MERCHANT_UNATTENDED;
            case (0x34):
            case (0x35):
            case (0x36):
                return TerminalType.CARDHOLDER_OPERATED;
            default:
                return TerminalType.UNKNOWN;
        }
    }
}
