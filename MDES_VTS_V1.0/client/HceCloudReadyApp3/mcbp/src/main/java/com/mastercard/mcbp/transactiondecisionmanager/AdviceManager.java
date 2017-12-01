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

package com.mastercard.mcbp.transactiondecisionmanager;

import com.mastercard.mcbp.transactiondecisionmanager.advice.Advice;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalInformation;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionInformation;

/**
 * Interface to manage transaction advice outcome from the MPP Lite to the Wallet
 */
public interface AdviceManager {
    /**
     * Advice the outcome of a either a contactless or remote payment transaction
     *
     * @param masterCardAdvice       The MasterCard advice
     * @param transactionInformation The Transaction Information
     * @param terminalInformation    The Terminal Information
     * @return The Wallet advice for the Remote Payment Transaction
     */
    Advice adviseTransactionOutcome(
            final Advice masterCardAdvice,
            final TransactionInformation transactionInformation,
            final TerminalInformation terminalInformation);
}
