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

package com.mastercard.mcbp.utils;

import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class UnitTestReadTrackRecord {
    String data = null;

    @Before
    public void setUp() throws Exception {
        data =
                "7081869F6C0200019F62060000000000F09F6306000000000F0E562942353335303433303237323"
                + "130383131335E202F5E31383031323236303030303030303030303030309F6401049F650200F0"
                + "9F66020F0E9F6B135350430272108113D18012260000000000000F9F6701049F69199F6A049F7"
                + "E019F02065F2A029F1A029C019A039F15029F3501";
    }

    @Test
    public void testTrack1Data() throws Exception {
        ByteArray recordValue = ByteArray.of(data);
        byte[] recordData = recordValue.getBytes();
        if (recordData[0] == (byte) 0X70) {
            byte lenghtByte = (byte) (recordData[1] & 0x80);
            int initialLenghtOffset = 0;
            if (lenghtByte == (byte) 0x80) {
                // More than one byte length Calculate
                initialLenghtOffset = (recordData[1] & (byte) 0X0F) + 1;
            } else {
                // One byte length
                initialLenghtOffset = 1;
            }
            ByteArray actualData = recordValue.copyOfRange(initialLenghtOffset + 1, recordValue
                    .getLength());
            byte[] actualDataBytes = actualData.getBytes();
            int offset = 0;
            while (offset < actualDataBytes.length) {
                if (actualDataBytes[offset] == (byte) 0x9F) {
                    if (actualDataBytes[offset + 1] == (byte) 0x6B) {
                        ByteArray byteArray = actualData
                                .copyOfRange(offset + 3, offset + 3 + actualDataBytes[2 + offset]);
                    }
                    offset = offset + (2 + 1 + actualDataBytes[2 + offset]);
                } else {
                    ByteArray byteArray = actualData
                            .copyOfRange(offset + 2, offset + 2 + actualDataBytes[1 + offset]);
                    offset = offset + (1 + 1 + actualDataBytes[1 + offset]);
                }
            }
        }
    }
}
