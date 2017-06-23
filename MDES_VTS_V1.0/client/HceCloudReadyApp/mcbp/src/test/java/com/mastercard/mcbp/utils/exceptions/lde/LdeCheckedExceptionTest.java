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

package com.mastercard.mcbp.utils.exceptions.lde;

import com.mastercard.mcbp.utils.exceptions.CheckedExceptionTest;
import com.mastercard.mcbp.utils.exceptions.McbpCheckedException;
import com.mastercard.mcbp.utils.exceptions.McbpUncheckedException;
import com.mastercard.mcbp.utils.returncodes.ErrorCode;
import com.mastercard.mcbp.utils.returncodes.ReturnCode;
import com.mastercard.mcbp.utils.returncodes.SuccessCode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LdeCheckedExceptionTest extends CheckedExceptionTest {
    /**
     * The foo method throw an exception based on the value of the input parameter
     * */
    @Override
    protected void foo(int input) throws McbpCheckedException {
        switch (input) {
            case 1:
                throw new LdeCheckedException("You can record an error message here");
            case 2:
                throw new CardNotFound("You can record an error message here");
            case 3:
                throw new InvalidLogRecordFormat("You can record an error message here");
            case 4:
                throw new LdeAlreadyInitialized("You can record an error message here");
            case 5:
                throw new LdeNotInitialized("You can record an error message here");
            case 6:
                throw new SessionKeysNotAvailable("You can record an error message here");
            case 7:
                throw new TransactionLoggingError("You can record an error message here");
            case 8:
                throw new UserInformationNotFound("You can record an error message here");
            default:
                // Do nothing, just return
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
        } catch (final LdeCheckedException e) {
            // here you can check which sub class the exception belong to and notify other modules
            // (e.g. UI) with specific information / event to properly inform the user
            return e.getErrorCode();
        } catch (final McbpCheckedException e) {
            throw new RuntimeException("It should not happen: " + e.getErrorCode());
        } catch (final McbpUncheckedException e) {
            return e.getErrorCode();
        }
        return SuccessCode.OK;
    }

    /**
     * The actual test method. Probes the bar function with multiple input values
     * */
    @Test
    public void test() {
        assertEquals(SuccessCode.OK,                        bar(0));
        assertEquals(ErrorCode.LDE_ERROR,                   bar(1));
        assertEquals(ErrorCode.DIGITIZED_CARD_ID_NOT_FOUND, bar(2));
        assertEquals(ErrorCode.INVALID_LOG_RECORD_FORMAT,   bar(3));
        assertEquals(ErrorCode.LDE_ALREADY_INITIALIZED,     bar(4));
        assertEquals(ErrorCode.LDE_NOT_INITIALIZED,         bar(5));
        assertEquals(ErrorCode.NO_SESSION_KEYS_AVAILABLE,   bar(6));
        assertEquals(ErrorCode.LOGGING_ERROR,               bar(7));
        assertEquals(ErrorCode.NO_USER_INFORMATION_FOUND,   bar(8));

        assertEquals(SuccessCode.OK,                        zoo(0));
        assertEquals(ErrorCode.LDE_ERROR,                   zoo(1));
        assertEquals(ErrorCode.DIGITIZED_CARD_ID_NOT_FOUND, zoo(2));
        assertEquals(ErrorCode.INVALID_LOG_RECORD_FORMAT,   zoo(3));
        assertEquals(ErrorCode.LDE_ALREADY_INITIALIZED,     zoo(4));
        assertEquals(ErrorCode.LDE_NOT_INITIALIZED,         zoo(5));
        assertEquals(ErrorCode.NO_SESSION_KEYS_AVAILABLE,   zoo(6));
        assertEquals(ErrorCode.LOGGING_ERROR,               zoo(7));
        assertEquals(ErrorCode.NO_USER_INFORMATION_FOUND,   zoo(8));
    }
}
