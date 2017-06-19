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

/*******************************************************************************
 * The following coding styles have been followed (block indentation is 4):
 * https://source.android.com/source/code-style.html
 * https://google-styleguide.googlecode.com/svn/trunk/javaguide.html
 *******************************************************************************/

package com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu;

import com.mastercard.mcbp.card.mpplite.apdu.Iso7816;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class InvalidCommandApduTest {
    /**
     * The foo method throw an exception based on the value of the input parameter
     * */
    private void foo(int input) throws InvalidCommandApdu {
        switch (input) {
            case 1:
                throw new InvalidCommandApdu("You can record an error message here");
            case 2:
                throw new InvalidCla("You can record an error message here");
            case 3:
                throw new InvalidIns("You can record an error message here");
            case 4:
                throw new InvalidP1P2("You can record an error message here");
            case 5:
                throw new InvalidP1P2("You can record an error message here");
            case 6:
                throw new InvalidLc("You can record an error message here");
            default:
                // Do nothing, just return
        }
    }

    /**
     * The bar method is responsible for calling the foo method and catching potential exceptions.
     * bar is called by the test case method
     * @return SW_NO_ERROR (e.g. 9000) if no exceptions are thrown
     * */
    private byte[] bar(int input) {
        try {
            foo(input);
        } catch (InvalidCommandApdu e) {
            return e.getIso7816StatusWordApdu();
        }
        return ResponseApduFactory.successfulProcessing();
    }

    /**
     * The actual test method. Probes the bar function with multiple input values
     * */
    @Test
    public void test() {
        assertArrayEquals(ResponseApduFactory.of(Iso7816.SW_NO_ERROR), bar(0));
        assertArrayEquals(ResponseApduFactory.of(Iso7816.SW_UNKNOWN), bar(1));
        assertArrayEquals(ResponseApduFactory.of(Iso7816.SW_CLA_NOT_SUPPORTED), bar(2));
        assertArrayEquals(ResponseApduFactory.of(Iso7816.SW_INS_NOT_SUPPORTED), bar(3));
        assertArrayEquals(ResponseApduFactory.of(Iso7816.SW_WRONG_P1P2), bar(4));
        assertArrayEquals(ResponseApduFactory.of(Iso7816.SW_WRONG_P1P2), bar(5));
        assertArrayEquals(ResponseApduFactory.of(Iso7816.SW_WRONG_LENGTH), bar(6));
    }
}