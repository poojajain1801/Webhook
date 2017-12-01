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

package com.mastercard.mcbp.utils.returncodes;

/**
 * List of Error Codes. They are used to map exceptions to a specific error code.
 * For example, error codes may be used to inform the UI about a specific event or error condition.
 * <p/>
 * The list of error codes is defined on Table 3 of MPA Functional Description 1.0
 */
public enum ErrorCode implements ReturnCode {
    /**
     * Invalid input data provided
     */
    INVALID_INPUT,                          // ERROR_INVALID_INPUT

    /**
     * The Digitized Card ID is not valid
     */
    INVALID_DIGITIZED_CARD_ID,              // ERROR_INVALID_DC_ID

    /**
     * The Digitized Card Profile is not valid
     */
    INVALID_DIGITIZED_CARD_PROFILE,        //

    /**
     * The format of one or more of the Digitized Card Single Use Keys (DC_SUKs) is invalid.
     */
    INVALID_DIGITIZED_CARD_SINGLE_USE_KEY,  // ERROR_INVALID_DC_SUK

    /**
     * The Digitized Card ID does not exist in the Local Database Encrypted (Lde)
     */
    DIGITIZED_CARD_ID_NOT_FOUND,            // ERROR_CARD_NOT_FOUND

    /**
     * The Local Database Encrypted (Lde) is not initialized
     */
    LDE_NOT_INITIALIZED,                    // ERROR_UNINITIALIZED_LDE

    /**
     * The Local Database Encrypted (Lde) is already initialized
     */
    LDE_ALREADY_INITIALIZED,                // ERROR_LDE_ALREADY_INITIALIZED

    /**
     * The received transaction log record format is invalid
     */
    INVALID_LOG_RECORD_FORMAT,              // ERROR_INVALID_LOG_RECORD_FORMAT

    /**
     * Digitized Card Single Use Keys (DC_SUKs) queue of the referenced Digitized Card is empty
     */
    NO_SESSION_KEYS_AVAILABLE,              // ERROR_NO_SESSION_KEYS_AVAILABLE

    /**
     * No User Information has been pushed by the CMS, or No information pushed by CMS and not read
     * before by the Business Logic module
     */
    NO_USER_INFORMATION_FOUND,              // ERROR_NO_USER_INFORMATION_FOUND

    /**
     * The MPP Lite is in the wrong state to perform the requested operation.
     * This error code is used slightly different than in the specifications since the state of the
     * MPP Lite is not exposed to other objects.
     */
    WRONG_STATE,                            // ERROR_STATE

    /**
     * The Card Profile is not compatible with the requested operation (i.e. Remote Payment or
     * Contactless transaction)
     */
    INCOMPATIBLE_PROFILE,                   // ERROR_INCOMPATIBLE_PROFILE

    /**
     * The format of received credentials is incorrect for a Remote Payment Transaction
     */
    INVALID_REMOTE_PAYMENT_CREDENTIALS,     // ERROR_CREDENTIALS

    /**
     * The format of received credentials is incorrect for a Contactless Transaction
     */
    INVALID_CONTACTLESS_CREDENTIALS,        //

    /**
     * Component not initialized
     */
    NOT_INITIALIZED,                        // ERROR_UNINITIALIZED

    /**
     * Error occurred during processing
     */
    INTERNAL_ERROR,                         // INTERNAL_ERROR

    /**
     * An error occurred during remote management operations
     */
    REMOTE_MANAGEMENT_ERROR,                // INTERNAL_ERROR


    /**
     * Incompatible data retrieved during processing
     */
    UNEXPECTED_DATA,                        // ERROR_UNEXPECTED_DATA

    /**
     * Unexpected Protocol Message
     */
    UNEXPECTED_PROTOCOL_MESSAGE,            //

    /**
     * McbpCard is unable to log the transaction
     */
    LOGGING_ERROR,                          // ERROR_LOGGING

    /**
     * Card Holder Validator authentication was not successful
     */
    CH_VALIDATION_ERROR,                    // ERROR_CHVALIDATION

    /**
     * The protocol message is not valid (e.g. MAC not matching)
     */
    INVALID_PROTOCOL_MESSAGE,              //

    /**
     * A cryptographic error has occurred
     */
    CRYPTO_ERROR,                          //

    /**
     * Lde Generic Error
     */
    LDE_ERROR,                              // General error code for LDE related error

    /**
     * If provisioning of SUK failed
     */
    PROVISIONING_SUK_FAILED_ERROR,            //

    /**
     * Raised when the number of transactions stored in the database exceeds the maximum number
     */
    TRANSACTION_STORAGE_LIMIT_REACH,

    /**
     * An error has occurred when registering with Google Cloud Messaging
     */
    GCM_REGISTRATION_FAILED,                          // GCM registration failed

    /**
     * An error code if one Cms-D operation is already processing and another operation is
     * requested
     */
    ALREADY_IN_PROCESS,
}


