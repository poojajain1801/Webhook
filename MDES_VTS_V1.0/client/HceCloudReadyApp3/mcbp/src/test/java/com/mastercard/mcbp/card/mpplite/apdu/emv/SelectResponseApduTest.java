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

package com.mastercard.mcbp.card.mpplite.apdu.emv;

import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test the Select Response APDU when initialized with the profile value and a list of modified
 * PDOL values
 */
public class SelectResponseApduTest {

    @Test
    public void testOf() throws Exception {
        final byte[] profileResponse =
                ByteArray.of("6F338407A0000000041010A528500A4D6173746572436172648701019F3809"
                             + "9F1D089F1A029F3501BF0C0A9F6E0700560000313400").getBytes();

        final DolRequestList.DolItem additionalPdol1 =
                new DolRequestList.DolItem("9F4E", 20);   // New "9F4E14"
        final DolRequestList.DolItem additionalPdol3 =
                new DolRequestList.DolItem("9F02", 6);    // Redundant
        final DolRequestList.DolItem additionalPdol4 =
                new DolRequestList.DolItem("9F1D", 0x11); // Redundant
        final DolRequestList.DolItem additionalPdol5 =
                new DolRequestList.DolItem("9F1A", 3);    // Redundant
        final DolRequestList.DolItem additionalPdol6 =
                new DolRequestList.DolItem("9F1A", 20);   // Redundant
        final DolRequestList.DolItem additionalPdol2 =
                new DolRequestList.DolItem("9F02", 6);    // New 9F0206
        final DolRequestList.DolItem additionalPdol7 =
                new DolRequestList.DolItem("9F31", 128);  // New 9F318180

        final List<DolRequestList.DolItem> additionalPdolEntries = new ArrayList<>();
        additionalPdolEntries.add(additionalPdol1);
        additionalPdolEntries.add(additionalPdol2);
        additionalPdolEntries.add(additionalPdol3);
        additionalPdolEntries.add(additionalPdol4);
        additionalPdolEntries.add(additionalPdol5);
        additionalPdolEntries.add(additionalPdol6);
        additionalPdolEntries.add(additionalPdol7);

        final SelectResponseApdu selectResponseApdu =
                SelectResponseApdu.of(profileResponse, additionalPdolEntries);
        assert selectResponseApdu != null;

        final ByteArray expected =
                ByteArray.of("6F3D8407A0000000041010A532500A4D6173746572436172648701019F3813"
                             + "9F1D089F1A029F35019F4E149F02069F318180BF0C0A"
                             + "9F6E07005600003134009000");

        final ByteArray actual = selectResponseApdu.getByteArray();

        Assert.assertEquals(expected.toHexString(), actual.toHexString());
    }
}