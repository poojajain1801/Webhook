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

package com.mastercard.mcbp.utils.exceptions;

import com.mastercard.mobile_api.utils.exceptions.ErrorCode;

/**
 * Generic MCBP exception error codes list.
 */
public interface McbpErrorCode extends ErrorCode {
    /**
     * Unknown error occurred
     */
    int GENERAL_ERROR = 1101;
    /**
     * Cryptographic error occurred
     */
    int CRYPTO_ERROR = 1102;
    /**
     * Error occurred while executing operation on CMS-D
     */
    int SERVER_ERROR = 1103;
    /**
     * Local encrypted database error occurred
     */
    int LDE_ERROR = 1104;
    /**
     * Error occurred at SSL level while communication
     */
    int SSL_ERROR_CODE = 1106;
    /**
     * Http timeout occurred
     */
    int HTTP_TIMEOUT_OCCURRED = 1107;
    /**
     * Failed to retrieve public key certificate.
     */
    int FAILED_TO_RETRIEVE_CERTIFICATE = 1108;
    /**
     * Card is already provision
     */
    int CARD_ALREADY_PROVISION = 1109;
    /**
     * User already register with CMS-D
     */
    int USER_ALREADY_REGISTER = 1110;
}
