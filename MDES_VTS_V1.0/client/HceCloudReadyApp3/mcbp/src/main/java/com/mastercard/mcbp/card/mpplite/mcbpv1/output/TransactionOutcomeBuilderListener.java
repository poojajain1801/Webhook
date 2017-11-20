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

import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentials;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Advice;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;

import java.util.List;

/**
 * Listener to implement technology specific actions once the Transaction Outcome Builder
 * has reached a decision on which data to return.
 */
public interface TransactionOutcomeBuilderListener<T> {

    /**
     * Event raised when the Risk Action Manager decides to approve
     *
     * @param credentials   The Transaction Credentials to be used
     * @param walletReasons Reasons of assessment received from wallet
     * @return An Object dependent on the actual transaction technology
     */
    T approveOnline(final List<Reason> walletReasons, final TransactionCredentials credentials);

    /**
     * Event raised when the Risk Action Manager decides to abort
     *
     * @param walletReasons Reasons of assessment received from wallet
     * @return An Object dependent on the actual transaction technology
     */
    T abort(final List<Reason> walletReasons);

    /**
     * Event raised when the Risk Action Manager decides to decline
     *
     * @param walletReasons Reasons of assessment received from wallet
     * @return An Object dependent on the actual transaction technology
     */
    T decline(final List<Reason> walletReasons);

    /**
     * Event raised when the Risk Action Manager decides to report an error
     *
     * @param walletReasons Reasons of assessment received from wallet
     * @return An Object dependent on the actual transaction technology
     */
    T error(final List<Reason> walletReasons);

    /**
     * Event raised when the Risk Action Manager decides to authenticate
     *
     * @param walletReasons Reasons of assessment received from wallet
     * @param credentials   The Transaction Credentials to be used
     * @return An Object dependent on the actual transaction technology
     */
    T authenticate(final List<Reason> walletReasons, final TransactionCredentials credentials);
}
