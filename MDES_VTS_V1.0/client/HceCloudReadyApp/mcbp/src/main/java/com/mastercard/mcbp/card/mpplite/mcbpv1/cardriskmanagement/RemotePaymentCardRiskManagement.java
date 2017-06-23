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

package com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement;

import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionInformation;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionRange;

import static com.mastercard.mcbp.card.mpplite.mcbpv1.credentials
        .TransactionCredentialsManager.Scope.REMOTE_PAYMENT;

/**
 * Card Risk Management for Remote Payment technology
 */
class RemotePaymentCardRiskManagement extends CardRiskManagement {
    /**
     * The CIAC Decline as in the card profile
     */
    private final byte[] mCiacDecline;

    /**
     * The CVR values as per the current transaction
     */
    private final CardVerificationResults mCvr;

    /**
     * The Transaction Credential Manager to check the status of the credentials
     */
    private final TransactionCredentialsManager mTransactionCredentialsManager;

    /**
     * Both ciac decline and cvr are needed when building the object
     *
     * @param cvr                           The current CVR values for this transaction. Please
     *                                      note that this function may modify values in CVR
     * @param ciacDecline                   The Ciac decline as per card profile
     * @param transactionCredentialsManager The Transaction Credentials Manager
     */
    RemotePaymentCardRiskManagement(
            final CardVerificationResults cvr,
            final byte[] ciacDecline,
            final TransactionCredentialsManager transactionCredentialsManager) {
        this.mCiacDecline = ciacDecline;
        this.mCvr = cvr;
        this.mTransactionCredentialsManager = transactionCredentialsManager;
    }

    /**
     * Check the transaction conditions for Remote Payment
     *
     * @return True, if the transaction conditions are met. False otherwise.
     */
    @Override
    protected boolean transactionConditionsAllowed() {
        return !mCvr.isCiacDeclineMatchFound(mCiacDecline);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCdCvmRequired(final TransactionInformation transactionInformation) {
        return transactionInformation.getTransactionRange() != TransactionRange.LOW_VALUE;
    }

    /**
     * Handle the notification of a new Authorization according to Remote Payment rules
     */
    @Override
    protected void notifyNewAuthorization() {
        switch (getCurrentAssessment()) {
            default:
                // Do nothing on purpose
        }
    }

    /**
     * Handle the notification of a newly added reason according to Remote Payment rules
     */
    @Override
    protected void notifyAddReason(final Reason reason) {
        switch (reason) {
            default:
                // Do nothing on purpose
        }
    }

    @Override
    protected boolean isContextMatching() {
        // There is no context concept in Remote Payment currently.
        return true;
    }

    @Override
    protected boolean isTransitTransaction() {
        //There is no transit concept in Remote Payment
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean areUmdCredentialsSubjectToCvmFor(final TransactionRange transactionRange) {
        return mTransactionCredentialsManager
                .areUmdCredentialsSubjectToCvmFor(transactionRange, REMOTE_PAYMENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasValidCredentials() {
        return mTransactionCredentialsManager.hasValidCredentialsFor(REMOTE_PAYMENT);
    }
}
