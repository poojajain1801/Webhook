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
   Test class to check parsing logic of data with PDOL length equal to and greater than 7E.
 */
public class GetProcessingOptionsCommandApduTest {
    @Test
    public void testOfPdolLength7e(){
        final byte[] apdu =
                ByteArray.of("80A8000080837E21897BBFC8C6C4FE47AC6DB5FB762A75A4D1AB83DD44D25"
                             + "A64AD9F40A84A41D7B656121FE2386DCDD193FF562632A5C819DA12DB01"
                             + "A117647D2E037984382F0794EE627AA606AB4D2C3569D8E8D85C388449D"
                             + "4FF502BC889A3520BEC1C6DC97779D9EB2A915CC2A4C1D91E56FB323ACF"
                             + "E19A7F57F7C1676F588E053096F100").getBytes();


        byte[] pdol = ByteArray.of("9F38039F665B9F4E149F02069f1A06").getBytes();
        DolRequestList dolRequestList;
        dolRequestList = DolRequestList.of(pdol);

        final GetProcessingOptionsCommandApdu getProcessingOptionsCommandApdu =
                new GetProcessingOptionsCommandApdu(apdu, dolRequestList);

        final String expectedPdol = "21897BBFC8C6C4FE47AC6DB5FB762A75A4D1AB83DD44D25"
                + "A64AD9F40A84A41D7B656121FE2386DCDD193FF562632A5C819DA12DB01"
                + "A117647D2E037984382F0794EE627AA606AB4D2C3569D8E8D85C388449D"
                + "4FF502BC889A3520BEC1C6DC97779D9EB2A915CC2A4C1D91E56FB323ACF"
                + "E19A7F57F7C1676F588E053096F1";


        final short lc = getProcessingOptionsCommandApdu.getLc();
        Assert.assertEquals(lc, 128);

        final String receivedPdol = ByteArray.of(getProcessingOptionsCommandApdu.getPdol()).toHexString();
        Assert.assertEquals(expectedPdol,receivedPdol);
    }

    @Test
    public void testOfPdolLength7f(){
        final byte[] apdu =
                ByteArray.of("80A8000081837F2199897BBFC8C6C4FE47AC6DB5FB762A75A4D1AB83DD44D2"
                         + "5A64AD9F40A84A41D7B656121FE2386DCDD193FF562632A5C819DA12DB01A11"
                         + "7647D2E037984382F0794EE627AA606AB4D2C3569D8E8D85C388449D4FF502B"
                         + "C889A3520BEC1C6DC97779D9EB2A915CC2A4C1D91E56FB323ACFE19A7F57F7C"
                         + "1676F588E053096F100").getBytes();

        byte[] pdol = ByteArray.of("9F38039F665C9F4E149F02069f1A06").getBytes();
        DolRequestList dolRequestList;
        dolRequestList = DolRequestList.of(pdol);

        final GetProcessingOptionsCommandApdu getProcessingOptionsCommandApdu =
                new GetProcessingOptionsCommandApdu(apdu, dolRequestList);

        final String expectedPdol = "2199897BBFC8C6C4FE47AC6DB5FB762A75A4D1AB83DD44D25"
                + "A64AD9F40A84A41D7B656121FE2386DCDD193FF562632A5C819DA12DB01"
                + "A117647D2E037984382F0794EE627AA606AB4D2C3569D8E8D85C388449D"
                + "4FF502BC889A3520BEC1C6DC97779D9EB2A915CC2A4C1D91E56FB323ACF"
                + "E19A7F57F7C1676F588E053096F1";


        final short lc = getProcessingOptionsCommandApdu.getLc();
        Assert.assertEquals(lc, 129);

        final String receivedPdol = ByteArray.of(getProcessingOptionsCommandApdu.getPdol()).toHexString();
        Assert.assertEquals(expectedPdol,receivedPdol);
    }

    @Test
    public void testOfPdolLength82(){
        final byte[] apdu =
                ByteArray.of("80A80000858381820021765599897BBFC8C6C4FE47AC6DB5FB762A75A4D1AB83D"
                        + "D44D25A64AD9F40A84A41D7B656121FE2386DCDD193FF562632A5C819DA12DB01A11"
                        + "7647D2E037984382F0794EE627AA606AB4D2C3569D8E8D85C388449D4FF502B"
                        + "C889A3520BEC1C6DC97779D9EB2A915CC2A4C1D91E56FB323ACFE19A7F57F7C"
                        + "1676F588E053096F100").getBytes();

        byte[] pdol = ByteArray.of("9F38039F665F9F4E149F02069f1A06").getBytes();
        DolRequestList dolRequestList;
        dolRequestList = DolRequestList.of(pdol);

        final GetProcessingOptionsCommandApdu getProcessingOptionsCommandApdu =
                new GetProcessingOptionsCommandApdu(apdu, dolRequestList);

        final String expectedPdol = "0021765599897BBFC8C6C4FE47AC6DB5FB762A75A4D1AB83DD44D25"
                + "A64AD9F40A84A41D7B656121FE2386DCDD193FF562632A5C819DA12DB01"
                + "A117647D2E037984382F0794EE627AA606AB4D2C3569D8E8D85C388449D"
                + "4FF502BC889A3520BEC1C6DC97779D9EB2A915CC2A4C1D91E56FB323ACF"
                + "E19A7F57F7C1676F588E053096F1";


        final short lc = getProcessingOptionsCommandApdu.getLc();
        Assert.assertEquals(lc, 133);

        final String receivedPdol = ByteArray.of(getProcessingOptionsCommandApdu.getPdol()).toHexString();
        Assert.assertEquals(expectedPdol,receivedPdol);
    }
}
