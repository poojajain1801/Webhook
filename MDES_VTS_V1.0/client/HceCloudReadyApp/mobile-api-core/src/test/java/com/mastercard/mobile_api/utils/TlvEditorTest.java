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

package com.mastercard.mobile_api.utils;

import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for the TLV related functionality
 */
public class TlvEditorTest {
    @Test
    public void testRebuildByteArray() throws Exception {
        final ByteArray input =
                ByteArray.of("A528500A4D6173746572436172648701019F38099F1D089F1A029F3501BF0C0A"
                             + "9F6E07005600003134008407A0000000041010");

        TlvEditor tlvEditor = TlvEditor.of(input.getBytes());
        assert tlvEditor != null;

        final byte[] edited = tlvEditor.rebuildByteArray();

        Assert.assertEquals(input.toHexString(), ByteArray.of(edited).toHexString());
    }

    @Test
    public void testAddTlv() throws Exception {
        final ByteArray input =
                ByteArray.of("A528500A4D6173746572436172648701019F38099F1D089F1A029F3501BF0C0A"
                             + "9F6E07005600003134008407A0000000041010");

        TlvEditor tlvEditor = TlvEditor.of(input.getBytes());
        assert tlvEditor != null;
        tlvEditor.addTlv(ByteArray.of("9F34").getBytes(), ByteArray.of("001122").getBytes());

        final ByteArray edited = ByteArray.of(tlvEditor.rebuildByteArray());

        final ByteArray expected =
                ByteArray.of("A528500A4D6173746572436172648701019F38099F1D089F1A029F3501BF0C0A"
                             + "9F6E07005600003134008407A00000000410109F3403001122");

        Assert.assertEquals(expected.toHexString(), edited.toHexString());
    }

    @Test
    public void testRecord1() throws Exception {
        final ByteArray input = ByteArray.of(
                "7081919F6C0200019F62060000000000F09F630600000000F0E05634423534313333333930303030"
                + "30313531335E202F5E323031323230313333303030333333303030323232323230303031313131"
                + "309F6401049F650200F09F66020F0E9F6B135413339000001513D20122019000990000000F9F67"
                + "01049F69199F6A049F7E019F02065F2A029F1A029C019A039F15029F3501");

        final TlvEditor tlvEditor = TlvEditor.of(input.getBytes());
        assert tlvEditor != null;

        final TlvEditor recordContent = TlvEditor.of(tlvEditor.getValue("70"));
        assert recordContent != null;

        final byte[] tag = recordContent.getValue("9F63");
        final String expected = "00000000F0E0";

        Assert.assertEquals(expected, ByteArray.of(tag).toHexString());
    }
}