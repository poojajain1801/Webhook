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

package com.mastercard.mcbp.utils.exceptions.cardholdervalidator;

import com.mastercard.mcbp.utils.exceptions.UncheckedExceptionTest;
import com.mastercard.mcbp.utils.exceptions.McbpUncheckedException;
import com.mastercard.mcbp.utils.returncodes.ErrorCode;
import com.mastercard.mcbp.utils.returncodes.ReturnCode;
import com.mastercard.mcbp.utils.returncodes.SuccessCode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CardholderValidationNotSuccessfulTest extends UncheckedExceptionTest {
    /**
     * The foo method throw an exception based on the value of the input parameter
     * */

    @Override
    protected void foo(int input) throws McbpUncheckedException {
        if (input < 0) {
            throw new CardholderValidationNotSuccessful("You can add an error message here");
        }
    }

    /**
     * The bar method calls the foo method and catches potential exceptions.
     * @return SuccessCode.OK is everything went well, an error code otherwise
     * */
    @Override
    protected ReturnCode bar(int input) {
        try {
            foo(input);
        } catch (CardholderValidationNotSuccessful e) {
            return e.getErrorCode();
        } catch (McbpUncheckedException e) {
            throw new RuntimeException("It should not happen: " + e.getErrorCode());
        }
        return SuccessCode.OK;
    }

    /**
     * The actual test method. Probes the bar function with multiple input values
     * */
    @Test
    public void test() {
        assertEquals(SuccessCode.OK,  bar(0));
        assertEquals(ErrorCode.CH_VALIDATION_ERROR, bar(-1));
    }
}