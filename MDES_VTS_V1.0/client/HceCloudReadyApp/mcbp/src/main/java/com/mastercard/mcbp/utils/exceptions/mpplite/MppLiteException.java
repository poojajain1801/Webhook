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

package com.mastercard.mcbp.utils.exceptions.mpplite;

import com.mastercard.mcbp.card.mpplite.apdu.Iso7816;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import com.mastercard.mcbp.utils.exceptions.McbpUncheckedException;
import com.mastercard.mcbp.utils.returncodes.ErrorCode;

/**
 * Base class for all the exceptions related to the Mpp Lite operations (except crypto operations,
 * which are handled with a separate exception hierarchy)
 */
public class MppLiteException extends McbpUncheckedException {
    /**
     * ISO 7816 Status Word associated with this exception
     * */
    private final char mIso7816StatusWord;

    /**
     * Constructor.  A reason must be provided
     * @param reason A string describing the reason for the exception
     * */
    public MppLiteException(String reason) {
        super(reason, ErrorCode.INTERNAL_ERROR);
        this.mIso7816StatusWord = Iso7816.SW_UNKNOWN;
    }

    /**
     * Constructor.  A reason and an error code must be provided
     * @param reason A string describing the reason for the exception
     * @param errorCode A code that binds the exception with a MCBP Error Code
     * */
    public MppLiteException(String reason, final ErrorCode errorCode) {
        super(reason, (errorCode != null ? errorCode: ErrorCode.INTERNAL_ERROR));
        this.mIso7816StatusWord = Iso7816.SW_UNKNOWN;
    }

    /**
     * Constructor.  A reason, an error code, and an ISO 7816 status word must be provided
     * @param reason A string describing the reason for the exception
     * @param errorCode A code that binds the exception with a MCBP Error Code
     * */
    public MppLiteException(String reason, final ErrorCode errorCode, char iso7816StatusWord) {
        super(reason, (errorCode != null ? errorCode: ErrorCode.INTERNAL_ERROR));
        this.mIso7816StatusWord = iso7816StatusWord;
    }

    /**
     * Get the ISO 7816 APDU associated with this error
     *
     * @return A ISO 7816 APDU whose content is the only ISO 7816 Status Word associated with this
     *         exception
     * */
    final public byte[] getIso7816StatusWordApdu() {
        return ResponseApduFactory.of(mIso7816StatusWord);
    }
}