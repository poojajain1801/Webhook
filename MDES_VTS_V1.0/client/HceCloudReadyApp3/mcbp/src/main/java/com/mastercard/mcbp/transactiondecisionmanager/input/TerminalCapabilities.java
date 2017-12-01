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

/**
 * The Terminal Capabilities is modeled as object to provide easy access to utility functions
 */
public final class TerminalCapabilities {
    /**
     * The index of the byte that indicates Cardholder Verification Method capabilities
     */
    public final static int CVM_CAPABILITY_BYTE = 1;

    /**
     * The Terminal Capabilities data as received in the C-APDU
     */
    final byte[] mTerminalCapabilities;

    /**
     * Build an object
     *
     * @param terminalCapabilities The Terminal Capabilities as received from the C-APDU
     * @return An object of Terminal Capabilities
     */
    public static TerminalCapabilities of(final byte[] terminalCapabilities) {
        return terminalCapabilities == null ? null : new TerminalCapabilities(terminalCapabilities);
    }

    /**
     * Constructor is not available, please use the static factory method instead
     */
    private TerminalCapabilities(final byte[] terminalCapabilities) {
        mTerminalCapabilities = terminalCapabilities;
    }

    /**
     * Check whether the Terminal Capabilities indicates support for at least one CVM method
     *
     * @return True, if it indicates at least one CVM as supported. False otherwise.
     */
    public final boolean isCvmOtherThanNoCvmSupported() {
        // Check if any of the 4 most left bits are set
        return (mTerminalCapabilities[CVM_CAPABILITY_BYTE] & 0x00F0) != 0x00;
    }

    /**
     * Check whether the Terminal Capabilities indicates support for no CVM only
     *
     * @return True, if no CVM only support is specified, false otherwise.
     */
    public final boolean isNoCvmOnlySupported() {
        // We check that the No CVM Required is the only bit set in the terminal capabilities
        return mTerminalCapabilities[CVM_CAPABILITY_BYTE] == 0x08;
    }

    /**
     * Check whether the Terminal Capabilities indicates support for no CVM
     *
     * @return True, if no CVM support is specified, false otherwise.
     */
    public final boolean isNoCvmSupported() {
        return (mTerminalCapabilities[CVM_CAPABILITY_BYTE] & 0x0F) == 0x08;
    }


    /**
     * Check whether the Terminal indicates Online PIN and Signature to be performed
     *
     * @return True, if the Terminal indicates both Online PIN and Signature. False, otherwise.
     */
    public final boolean isOnlinePinAndSignature() {
        return (mTerminalCapabilities[CVM_CAPABILITY_BYTE] & 0x60) == 0x60;
    }

    /**
     * Check whether the Terminal indicates Online PIN only to be performed
     *
     * @return True, if the Terminal indicates Online PIN only. False, otherwise.
     */
    public final boolean isOnlinePinOnly() {
        return (mTerminalCapabilities[CVM_CAPABILITY_BYTE] & 0x00F8) == 0x40;
    }

    /**
     * Check whether the Terminal indicates Signature only to be performed
     *
     * @return True, if the Terminal indicates Signature only. False, otherwise.
     */
    public final boolean isSignatureOnly() {
        return (mTerminalCapabilities[CVM_CAPABILITY_BYTE] & 0x00F8) == 0x20;
    }

    /**
     * Check whether this data element is supported by the terminal (i.e. the CVM capability byte
     * is not 0x00)
     * @return True if the data element is supported by the terminal, false otherwise.
     */
    public final boolean isSupportedByTheTerminal() {
        return mTerminalCapabilities[CVM_CAPABILITY_BYTE] != 0x00;
    }
}

