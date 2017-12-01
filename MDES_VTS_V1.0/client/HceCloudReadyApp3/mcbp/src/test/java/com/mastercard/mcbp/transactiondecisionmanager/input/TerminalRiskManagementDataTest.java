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
 * Test case for Terminal Risk Management Data element
 */
public class TerminalRiskManagementDataTest {

    @Test
    public void testIsSupportedByTheTerminal() throws Exception {
        assertEquals(false, TerminalRiskManagementData.of(valueOf("0000000000000000"))
                                                      .isSupportedByTheTerminal());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0100000000000000"))
                                                      .isSupportedByTheTerminal());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0200000000000000"))
                                                      .isSupportedByTheTerminal());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0200000000000000"))
                                                      .isSupportedByTheTerminal());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0300000000000000"))
                                                      .isSupportedByTheTerminal());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0400000000000000"))
                                                      .isSupportedByTheTerminal());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0000000000000001"))
                                                      .isSupportedByTheTerminal());
    }

    @Test
    public void testIsCdCvmSupported() throws Exception {
        assertEquals(false, TerminalRiskManagementData.of(valueOf("0000000000000000"))
                                                      .isCdCvmSupported());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0400000000000000"))
                                                      .isCdCvmSupported());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0500000000000000"))
                                                      .isCdCvmSupported());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0600000000000000"))
                                                      .isCdCvmSupported());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0700000000000000"))
                                                      .isCdCvmSupported());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0C00000000000000"))
                                                      .isCdCvmSupported());
        assertEquals(true,  TerminalRiskManagementData.of(valueOf("0D00000000000001"))
                                                      .isCdCvmSupported());
        assertEquals(false, TerminalRiskManagementData.of(valueOf("0100000000000001"))
                                                      .isCdCvmSupported());
        assertEquals(false, TerminalRiskManagementData.of(valueOf("0300000000000001"))
                                                      .isCdCvmSupported());
        assertEquals(false, TerminalRiskManagementData.of(valueOf("0B00000000000001"))
                                                      .isCdCvmSupported());
        assertEquals(false, TerminalRiskManagementData.of(valueOf("0A00000000000001"))
                                                      .isCdCvmSupported());
    }

    private byte[] valueOf(final String input) {
        return ByteArray.of(input).getBytes();
    }
}