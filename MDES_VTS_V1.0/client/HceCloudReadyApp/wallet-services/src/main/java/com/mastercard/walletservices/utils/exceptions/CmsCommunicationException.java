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

package com.mastercard.walletservices.utils.exceptions;

/**
 * Generic exception for all HTTP/s related communication.
 */
public class CmsCommunicationException extends Exception {

    public static final int GENERAL_HTTP_ERROR_CODE = 101;
    /**
     * Error code associated with this exception.
     */
    private int errorCode = GENERAL_HTTP_ERROR_CODE;
    /**
     * Error message associcated with this exception
     */
    private String errorMessage = null;
    /**
     * Cause that created this exception.
     */
    private Throwable cause;

    public CmsCommunicationException(int errorCode, String reason) {
        super(reason);
        this.errorCode = errorCode;
        this.errorMessage = reason;
    }

    public CmsCommunicationException(String reason) {
        super(reason);
        this.errorMessage = reason;
    }

    public CmsCommunicationException(String reason, Throwable throwable) {
        super(reason, throwable);
        this.cause = throwable;
        this.errorMessage = reason;
    }

    /**
     * Return error message
     *
     * @return String - Error message
     */
    @Override
    public String getMessage() {
        return this.errorMessage;
    }

    /**
     * Retrive Error code associated with this exception.
     *
     * @return Int - Error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Cause which generated this exception.
     *
     * @return Throwable - cause
     */
    @Override
    public Throwable getCause() {
        return cause;
    }
}

