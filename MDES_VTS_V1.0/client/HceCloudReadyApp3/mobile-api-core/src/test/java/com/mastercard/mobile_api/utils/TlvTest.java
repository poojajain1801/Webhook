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

import org.apache.commons.codec.binary.Hex;

import static org.junit.Assert.*;

/**
 * Unit test for Tlv single tone object
 */
public class TlvTest {

    @org.junit.Test
    public void testCreateString() throws Exception {
        String expectedTlv = "7081869F6C0200019F62060000000000F09F6306000000000F0E" +
                "562942353431333333393030303030313531335E202F5E3230313232323630303030303030303030" +
                "3030309F6401049F650200F09F66020F0E9F6B135413339000001513D20122260000000000000F9F" +
                "6701049F69199F6A049F7E019F02065F2A029F1A029C019A039F15029F3501";
        String tag = "70";
        String value = "9F6C0200019F62060000000000F09F6306000000000F0E56294235343133333339303030" +
                "3030313531335E202F5E32303132323236303030303030303030303030309F6401049F650200F09" +
                "F66020F0E9F6B135413339000001513D20122260000000000000F9F6701049F69199F6A049F7E01" +
                "9F02065F2A029F1A029C019A039F15029F3501";

        assertEquals(expectedTlv, Tlv.create(tag, value));

        expectedTlv = "9F6C020001";
        tag = "9F6C";
        value = "0001";

        assertEquals(expectedTlv, Tlv.create(tag, value));
    }

    @org.junit.Test
    public void testCreateByte() throws Exception {
        String hexData = "562942353431333333393030303030313531335E202F5E" +
                "3230313232323630303030303030303030303030";
        String hexValue = "42353431333333393030303030313531335E202F5E" +
                "3230313232323630303030303030303030303030";
        byte[] expectedValue = Hex.decodeHex(hexData.toCharArray());
        byte tag[] = Hex.decodeHex("56".toCharArray());
        byte[] value = Hex.decodeHex(hexValue.toCharArray());
        assertArrayEquals(expectedValue, Tlv.create(tag, value));
    }
}