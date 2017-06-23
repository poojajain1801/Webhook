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

import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test methods for the Terminal Capabilities
 */
public class TerminalCapabilitiesTest {
    @Test
    public void testIsHighValue() throws Exception {
        assertEquals(true,
                     TerminalCapabilities.of(valueOf("002000")).isCvmOtherThanNoCvmSupported());
        assertEquals(true,
                     TerminalCapabilities.of(valueOf("004000")).isCvmOtherThanNoCvmSupported());
        assertEquals(true,
                     TerminalCapabilities.of(valueOf("006000")).isCvmOtherThanNoCvmSupported());
        assertEquals(true,
                     TerminalCapabilities.of(valueOf("00F000")).isCvmOtherThanNoCvmSupported());
        assertEquals(false,
                     TerminalCapabilities.of(valueOf("000800")).isCvmOtherThanNoCvmSupported());
    }

    @Test
    public void testIsLowValue() throws Exception {
        assertEquals(true,  TerminalCapabilities.of(valueOf("000800")).isNoCvmOnlySupported());
        assertEquals(false, TerminalCapabilities.of(valueOf("00F800")).isNoCvmOnlySupported());
        assertEquals(false, TerminalCapabilities.of(valueOf("006800")).isNoCvmOnlySupported());
        assertEquals(false, TerminalCapabilities.of(valueOf("004800")).isNoCvmOnlySupported());
    }

    @Test
    public void testIsOnlinePinAndSignature() throws Exception {
        assertEquals(true,  TerminalCapabilities.of(valueOf("006000")).isOnlinePinAndSignature());
        assertEquals(false, TerminalCapabilities.of(valueOf("004000")).isOnlinePinAndSignature());
        assertEquals(false, TerminalCapabilities.of(valueOf("002000")).isOnlinePinAndSignature());
        assertEquals(true,  TerminalCapabilities.of(valueOf("007000")).isOnlinePinAndSignature());
        assertEquals(true,  TerminalCapabilities.of(valueOf("00F000")).isOnlinePinAndSignature());
    }

    @Test
    public void testIsOnlinePinOnly() throws Exception {
        assertEquals(true,  TerminalCapabilities.of(valueOf("004000")).isOnlinePinOnly());
        assertEquals(false, TerminalCapabilities.of(valueOf("005000")).isOnlinePinOnly());
        assertEquals(false, TerminalCapabilities.of(valueOf("006000")).isOnlinePinOnly());
        assertEquals(false, TerminalCapabilities.of(valueOf("00F000")).isOnlinePinOnly());
    }

    @Test
    public void testIsSignatureOnly() throws Exception {
        assertEquals(true,  TerminalCapabilities.of(valueOf("002000")).isSignatureOnly());
        assertEquals(false, TerminalCapabilities.of(valueOf("006000")).isSignatureOnly());
        assertEquals(false, TerminalCapabilities.of(valueOf("00A000")).isSignatureOnly());
        assertEquals(false, TerminalCapabilities.of(valueOf("00F000")).isSignatureOnly());
    }

    private byte[] valueOf(final String input) {
        return ByteArray.of(input).getBytes();
    }
}