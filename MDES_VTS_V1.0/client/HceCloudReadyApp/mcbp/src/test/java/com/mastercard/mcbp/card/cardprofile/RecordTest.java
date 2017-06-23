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

package com.mastercard.mcbp.card.cardprofile;

import com.mastercard.mcbp.card.profile.Record;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * Simple Unit Test to validate custom ByteArray conversion for Records
 */
public class RecordTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void toJson() throws Exception {
        String expectedJson = "{\"recordNumber\":1,\"recordValue\":\"7081919F6C0200019F620600000" +
                "00700009F630600000078F0009F640104563442353438303938313530303130303030325E202F5E" +
                "313831313230313130303030303030303030303030303030303030303030309F650200E09F66020" +
                "F1E9F6B135480981500100002D18112011000000000000F9F6701049F69199F6A049F7E019F0206" +
                "5F2A029F1A029C019A039F15029F3501\",\"sfi\":1}";

        com.mastercard.mcbp.card.profile.Record
                record = new com.mastercard.mcbp.card.profile.Record();
        String value = "7081919F6C0200019F62060000000700009F630600000078F0009F6401045634423534383" +
                "03938313530303130303030325E202F5E31383131323031313030303030303030303030303030303" +
                "0303030303030309F650200E09F66020F1E9F6B135480981500100002D18112011000000000000F9" +
                "F6701049F69199F6A049F7E019F02065F2A029F1A029C019A039F15029F3501";
        record.setRecordNumber(Byte.valueOf("01", 16));
        record.setSfi(Byte.valueOf("01", 16));
        record.setRecordValue(ByteArray.of(value));

        assertEquals(expectedJson, record.toJsonString());
    }

    @Test
    public void fromJson() throws Exception {
        String input = "{\"recordNumber\":\"01\",\"recordValue\":\"AABBCC\",\"sfi\":\"01\"}";

        Record record = Record.valueOf(input.getBytes(Charset.defaultCharset()));

        assertEquals(1, record.getRecordNumber());
        assertEquals(1, record.getSfi());
        assertArrayEquals(Hex.decodeHex("AABBCC".toCharArray()),
                          record.getRecordValue().getBytes());
    }
}