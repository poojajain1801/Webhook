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

/*
   Test class to validate parsing of apdu length greater than 7F.
 */
public class ComputeCcCommandApduTest {

    @Test
    public void testOfUdolLength82(){
        final byte[] apdu =
                ByteArray.of("802A8E8085000007650100000000099909780056001409110000220000006969696"
                        +  "9696969696969696969696969696969696969696969696969696969696969696969"
                        +  "6969696969696969696969696969696969696969696969696969696969696969696"
                        +  "9696969696969696969696969696969696969696969696969696969696969696969"
                        +  "6969696900").getBytes();

        byte[] udol = ByteArray.of("9F69039F66699F02039F7E049F1A055F2A029F6A039C039A019F35019F1D03").getBytes();
        DolRequestList dolRequestList;
        dolRequestList = DolRequestList.of(udol);

        final ComputeCcCommandApdu computeCcCommandApdu =
                new ComputeCcCommandApdu(apdu, dolRequestList);

        final String expectedUdol = "000007650100000000099909780056001409110000220000006969696969"
                                    +"69696969696969696969696969696969696969696969696969696969696"
                                    +"96969696969696969696969696969696969696969696969696969696969"
                                    +"69696969696969696969696969696969696969696969696969696969696"
                                    +"96969696969696969696969696969";

        final String receivedUdol = ByteArray.of(computeCcCommandApdu.getUdol()).toHexString();
        Assert.assertEquals(expectedUdol, receivedUdol);
    }
}
