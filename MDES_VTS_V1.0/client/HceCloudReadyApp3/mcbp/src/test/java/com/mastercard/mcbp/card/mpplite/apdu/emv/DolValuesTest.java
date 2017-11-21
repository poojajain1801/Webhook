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

/**
 * Test class to verify that we can correctly manage and update the PDOL list when needed
 */
public class DolValuesTest {

    @Test
    public void testOfListAndData() throws Exception {
        final ByteArray initialDol = ByteArray.of("9F1D089F1A029F3501");

        // We first generate a PDOL List
        final DolRequestList dolRequestList = DolRequestList.of(initialDol.getBytes());

        dolRequestList.addTag("9F34", 0x03);
        dolRequestList.addTag("57", 0x01);
        dolRequestList.addTag("9F22", 0x7F);
        dolRequestList.addTag("9F21", 0x80);


        final ByteArray expectedDolList =
                ByteArray.of("9F1D089F1A029F35019F340357019F227F9F218180");

        // First let's verify that the new DOL list is as expected.
        Assert.assertEquals(expectedDolList.toHexString(),
                            ByteArray.of(dolRequestList.getBytes()).toHexString());

        final ByteArray receivedDolValues = ByteArray.of("010203040506070801020101020301");
        final DolValues updatedPdolValues = DolValues.of(dolRequestList,
                                                         receivedDolValues.getBytes());

        Assert.assertEquals("0102030405060708",
                            ByteArray.of(updatedPdolValues.getValueByTag("9F1D")).toHexString());
        Assert.assertEquals("0102",
                            ByteArray.of(updatedPdolValues.getValueByTag("9F1A")).toHexString());
        Assert.assertEquals("01",
                            ByteArray.of(updatedPdolValues.getValueByTag("9F35")).toHexString());
        Assert.assertEquals("010203",
                            ByteArray.of(updatedPdolValues.getValueByTag("9F34")).toHexString());
        Assert.assertEquals("01",
                            ByteArray.of(updatedPdolValues.getValueByTag("57")).toHexString());
    }

    @Test
    public void testOfListAndDataWithMissingItemInData() throws Exception {
        final ByteArray initialDolRequestList = ByteArray.of("9F1D089F1A029F3501");

        // We first generate a PDOL List
        final DolRequestList dolRequestList = DolRequestList.of(initialDolRequestList.getBytes());

        dolRequestList.addTag("9F34", 0x03);
        dolRequestList.addTag("57", 0x01);

        final ByteArray expectedDolList =
                ByteArray.of("9F1D089F1A029F35019F34035701");

        // Verify that the new list is correct
        Assert.assertEquals(expectedDolList.toHexString(),
                            ByteArray.of(dolRequestList.getBytes()).toHexString());

        final ByteArray pdolData = ByteArray.of("0102030405060708010201010203");
        final DolValues updatePdol = DolValues.of(dolRequestList, pdolData.getBytes());

        Assert.assertEquals("0102030405060708",
                            ByteArray.of(updatePdol.getValueByTag("9F1D")).toHexString());
        Assert.assertEquals("0102", ByteArray.of(updatePdol.getValueByTag("9F1A")).toHexString());
        Assert.assertEquals("01", ByteArray.of(updatePdol.getValueByTag("9F35")).toHexString());
        Assert.assertEquals("010203", ByteArray.of(updatePdol.getValueByTag("9F34")).toHexString());
        Assert.assertEquals(null, updatePdol.getValueByTag("57"));
    }
}