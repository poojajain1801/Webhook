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

package com.mastercard.mobile_api.utils.exceptions.http;

import com.mastercard.mobile_api.utils.exceptions.ErrorCode;

/**
 * Generic exception for all HTTP/s related communication.
 */
public class HttpException extends Exception implements ErrorContext {

    /**
     * Error code associated with this exception.
     */
    private int errorCode = ErrorCode.UNKNOWN_HTTP_ERROR_CODE;
    /**
     * Error message associated with this exception
     */
    private String errorMessage = null;
    /**
     * Cause that created this exception.
     */
    private Exception cause;
    /**
     * Retry after time
     */
    private int retryAfterTime;

    public HttpException(int errorCode, String reason) {
        super(reason);
        this.errorCode = errorCode;
        this.errorMessage = reason;
    }

    public HttpException(int errorCode, String reason, int retryAfterValue) {
        super(reason);
        this.errorCode = errorCode;
        this.errorMessage = reason;
        this.retryAfterTime = retryAfterValue;
    }

    public HttpException(String reason) {
        super(reason);
        this.errorMessage = reason;
    }

    public HttpException(String reason, Exception throwable) {
        super(reason, throwable);
        this.errorCode = ErrorCode.UNKNOWN_HTTP_ERROR_CODE;
        this.cause = throwable;
        this.errorMessage = reason;
    }

    public HttpException(int errorCode, String reason, Exception throwable) {
        super(reason, throwable);
        this.errorCode = errorCode;
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
     * Retrieve Error code associated with this exception.
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
    public Exception getCause() {
        return cause;
    }

    /**
     * Return retry after time
     *
     * @return retryAfterTime retry after time
     */
    public int getRetryAfterTime() {
        return retryAfterTime;
    }
}
