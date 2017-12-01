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
public class DolRequestListTest {

    @Test
    public void testOf() throws Exception {
        final ByteArray initialPdol = ByteArray.of("9F1D089F1A029F3501");
        final DolRequestList pdol = DolRequestList.of(initialPdol.getBytes());

        pdol.addTag("9F34", 0x03);
        pdol.addTag("57", 19);
        pdol.addTag("58", 211);
        pdol.addTag("59", 117);
        pdol.addTag("60", 19);
        pdol.addTag("9F43", 0x81);
        pdol.addTag("9F35", 0x11);
        pdol.addTag("60", 214);

        final ByteArray expectedPdol =
                ByteArray.of("9F1D089F1A029F35019F340357135881D3597560139F438181");


        Assert.assertEquals(expectedPdol.toHexString(),
                            ByteArray.of(pdol.getBytes()).toHexString());
    }
}