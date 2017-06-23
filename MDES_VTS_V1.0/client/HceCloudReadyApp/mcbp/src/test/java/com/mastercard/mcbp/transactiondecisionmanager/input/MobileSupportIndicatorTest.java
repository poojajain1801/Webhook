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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for the Mobile Support Indicator
 */
public class MobileSupportIndicatorTest {
    @Test
    public void testIsMobileSupported() throws Exception {
        assertEquals(true,  MobileSupportIndicator.of((byte)0x01).isMobileSupported());
        assertEquals(false, MobileSupportIndicator.of((byte)0x02).isMobileSupported());
        assertEquals(true,  MobileSupportIndicator.of((byte)0x03).isMobileSupported());
        assertEquals(false, MobileSupportIndicator.of((byte)0x04).isMobileSupported());
        assertEquals(false, MobileSupportIndicator.of((byte)0xF0).isMobileSupported());
        assertEquals(false, MobileSupportIndicator.of((byte)0x06).isMobileSupported());
    }

    @Test
    public void testIsCdCvmRequired() throws Exception {
        assertEquals(false, MobileSupportIndicator.of((byte)0x01).isCdCvmRequired());
        assertEquals(true,  MobileSupportIndicator.of((byte)0x02).isCdCvmRequired());
        assertEquals(true,  MobileSupportIndicator.of((byte)0x03).isCdCvmRequired());
        assertEquals(false, MobileSupportIndicator.of((byte)0x04).isCdCvmRequired());
        assertEquals(false, MobileSupportIndicator.of((byte)0xF0).isCdCvmRequired());
        assertEquals(true,  MobileSupportIndicator.of((byte)0x06).isCdCvmRequired());
    }

    @Test
    public void testIsSupportedByTheTerminal() throws Exception {
        assertEquals(true,  MobileSupportIndicator.of((byte)0x01).isSupportedByTheTerminal());
        assertEquals(true,  MobileSupportIndicator.of((byte)0x02).isSupportedByTheTerminal());
        assertEquals(true,  MobileSupportIndicator.of((byte)0x03).isSupportedByTheTerminal());
        assertEquals(false, MobileSupportIndicator.of((byte)0x04).isSupportedByTheTerminal());
        assertEquals(true,  MobileSupportIndicator.of((byte)0x05).isSupportedByTheTerminal());
        assertEquals(true,  MobileSupportIndicator.of((byte)0x06).isSupportedByTheTerminal());
        assertEquals(true,  MobileSupportIndicator.of((byte)0x07).isSupportedByTheTerminal());
        assertEquals(false, MobileSupportIndicator.of((byte)0x08).isSupportedByTheTerminal());
    }
}