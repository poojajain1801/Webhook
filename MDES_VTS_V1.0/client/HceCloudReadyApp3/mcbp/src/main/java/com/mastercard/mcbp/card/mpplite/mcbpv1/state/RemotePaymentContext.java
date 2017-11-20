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

package com.mastercard.mcbp.card.mpplite.mcbpv1.state;

import com.mastercard.mcbp.card.cvm.ChValidator;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.transactiondecisionmanager.AdviceManager;
import com.mastercard.mcbp.transactiondecisionmanager.ConsentManager;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Advice;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalInformation;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionInformation;

/***
 * Utility class to keep the relevant remote payment context information
 */
public final class RemotePaymentContext {
    /**
     * The call back method to allow the MPP Lite to request for Credentials when needed
     * */
    private final TransactionCredentialsManager mTransactionCredentialsManager;

    /***
     * The call back to the CVM Manager functions
     */
    private final ChValidator mCardHolderValidator;

    /***
     * The call back to the Consent Manager functionality
     */
    private final ConsentManager mConsentManager;

    /**
     * The call back to the Advice Manager
     */
    private final AdviceManager mAdviceManager;

    /**
     * Default constructor
     * @param credentialsManager The listener to retrieve credentials when needed
     * @param cardholderValidator The Card Holder Validator call back interface to check whether
     *                            the user was authenticated or not
     * @param consentManager The consent manager call back interface to check for user
     *                       consent when needed
     * @param adviceManager The Advice Manager to ask for the Wallet Advice during the transaction
     */
    public RemotePaymentContext(final TransactionCredentialsManager credentialsManager,
                                final ChValidator cardholderValidator,
                                final ConsentManager consentManager,
                                final AdviceManager adviceManager) {
        this.mTransactionCredentialsManager = credentialsManager;
        this.mCardHolderValidator = cardholderValidator;
        this.mConsentManager = consentManager;
        this.mAdviceManager = adviceManager;
    }

    /**
     * Flag specifying whether the Cardholder Verification Method has been entered or not
     * @return true if the CVM has been entered, false otherwise
     * */
    public boolean isCvmEntered() {
        return mCardHolderValidator.isAuthenticated();
    }

    /**
     * Get the Transaction Credentials Manager
     *
     * @return The transaction credentials manager
     */
    public final TransactionCredentialsManager getTransactionCredentialsManager() {
        return mTransactionCredentialsManager;
    }

    /***
     * Check whether the consent has been given
     *
     * @return true if the CVM has been entered, false otherwise
     */
    public final boolean isConsentGiven() {
        return mConsentManager.isConsentGiven();
    }

    /**
     * Get the Wallet Advice on the transaction, if a Wallet Advice Manager has been set.
     * Otherwise, it will echo back the MasterCard advice.
     * @param masterCardAdvice The MasterCard Advice
     * @return The Wallet Advice, if a Wallet Advice Manager has been set. Otherwise, it echoes back
     *         the MasterCard advice.
     */
    public final Advice getWalletAdvice(final Advice masterCardAdvice,
                                        final TransactionInformation transactionInformation,
                                        final TerminalInformation terminalInformation) {
        if (mAdviceManager == null) {
            return masterCardAdvice;
        }
        return mAdviceManager.adviseTransactionOutcome(masterCardAdvice,
                                                       transactionInformation,
                                                       terminalInformation);
    }
}
