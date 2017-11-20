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

package com.mastercard.mcbp.card.mpplite.mcbpv1.output;

/**
 * Provides a high level summary of what has happened in this transaction
 */
public enum TransactionSummary {
    /**
     * The MPP Lite returned a cryptogram using valid credentials and the POS can perform
     * online authorization. Please note that the transaction is not yet completed as approval
     * is always online. This indicates that the MPP Lite has done all it could to authorize
     */
    AUTHORIZE_ONLINE,

    /**
     * The MPP Lite returned a cryptogram using valid credentials and the POS can perform
     * online authentication. Please note that the transaction is not yet completed as approval
     * is always online. This indicates that the MPP Lite has done all it could to authenticate
     */
    AUTHENTICATE_ONLINE,

    /**
     * The MPP Lite indicated to the POS to keep the context active for a subsequent
     * tap
     */
    ABORT_PERSISTENT_CONTEXT,

    /**
     * The MPP Lite could not determine if the POS will be able to keep the context active for a
     * subsequent tap. The transaction may need to be restarted by the merchant.
     */
    ABORT_UNKNOWN_CONTEXT,

    /**
     * The MPP Lite decline the transaction. The context is closed.
     */
    DECLINE,

    /**
     * The MPP Lite returned an error. The context is closed.
     */
    ERROR,

    /**
     * The MPP Lite detected a conflict between two taps within the same context (e.g. amount of
     * second tap differs from amount of first tap)
     */
    ERROR_CONTEXT_CONFLICT
}
