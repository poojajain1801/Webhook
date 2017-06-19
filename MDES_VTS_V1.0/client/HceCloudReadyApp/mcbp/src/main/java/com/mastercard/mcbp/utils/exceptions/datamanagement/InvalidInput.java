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

package com.mastercard.mcbp.utils.exceptions.datamanagement;

import com.mastercard.mcbp.utils.exceptions.McbpCheckedException;
import com.mastercard.mcbp.utils.returncodes.ErrorCode;

/**
 * Base class for all the exceptions related to unexpected data
 */
public class InvalidInput extends McbpCheckedException{

    /**
     * Constructor.  A reason must be provided
     * @param reason A string describing the reason for the exception
     * */
     public InvalidInput(String reason) {
        super(reason, ErrorCode.INVALID_INPUT);
     }
     /**
      * Constructor.  A reason and an error code must be provided
      * @param reason A string describing the reason for the exception
      * @param errorCode A code that binds the exception with a MCBP Error Code
      * */
      public InvalidInput(String reason, final ErrorCode errorCode) {
          super(reason, (errorCode != null ? errorCode: ErrorCode.INVALID_INPUT));
      }
}

