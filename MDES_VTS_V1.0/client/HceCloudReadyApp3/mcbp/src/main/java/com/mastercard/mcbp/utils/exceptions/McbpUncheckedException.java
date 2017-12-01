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

package com.mastercard.mcbp.utils.exceptions;

import com.mastercard.mcbp.utils.returncodes.ErrorCode;

/**
 * Generic MCBP Exception class
 * It provides an interface for all the exception classes to be used within MCBP modules
 */
public class McbpUncheckedException extends RuntimeException {
    /**
     * The Error code which is associated with the exception
     * */
    private final ErrorCode errorCode;

    /**
     * Constructor.  A reason must be provided
     * @param reason A string describing the reason for the exception
     * */
    public McbpUncheckedException(String reason) {
        super(reason);
        errorCode = ErrorCode.INTERNAL_ERROR;  // Use a generic error if none is specified
    }

    /**
     * Constructor. Both a reason and error code are provided
     * @param reason    A string describing the reason for the exception
     * @param errorCode A code that binds the exception with a MCBP Error Code
     * */
    public McbpUncheckedException(String reason, final ErrorCode errorCode) {
        super(reason);
        this.errorCode = (errorCode != null ? errorCode: ErrorCode.INTERNAL_ERROR);
    }

    /**
     * Get the Error related to the Exception
     * @return The Error Code which is associated with the exception
     * */
    public final ErrorCode getErrorCode() {
        return errorCode;
    }
 }
