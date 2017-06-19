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
 * The following coding style has been used for this file
 * https://google-styleguide.googlecode.com/svn/trunk/javaguide.html
 *******************************************************************************/

package com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu;

import com.mastercard.mcbp.card.mpplite.apdu.Iso7816;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mcbp.utils.returncodes.ErrorCode;

/**
 * Invalid input data provided
 */
public class InvalidCommandApdu extends MppLiteException {
    /**
     * Constructor.
     * @param reason A string describing the reason for the exception
     * */
    public InvalidCommandApdu(String reason) {
        super(reason, ErrorCode.INTERNAL_ERROR, Iso7816.SW_UNKNOWN);
    }

    /**
     * Constructor.
     * @param reason        A string describing the reason for the exception
     * @param apduErrorCode The Iso7816 Response APDU that is associated with this exception
     *
     * */
    public InvalidCommandApdu(String reason, char apduErrorCode) {
        super(reason, ErrorCode.INTERNAL_ERROR, apduErrorCode);
    }
}