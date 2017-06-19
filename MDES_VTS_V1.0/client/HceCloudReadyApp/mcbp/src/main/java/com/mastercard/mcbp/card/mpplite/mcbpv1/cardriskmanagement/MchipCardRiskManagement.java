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

import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcCommandApdu;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless.ContactlessUtils;
import com.mastercard.mcbp.card.mpplite.mcbpv1.state.ContactlessContext;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionInformation;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionRange;

import static com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager
        .Scope.CONTACTLESS;

/**
 * Card Risk Management for M-CHIP technology
 */
class MchipCardRiskManagement extends CardRiskManagement {
    /**
     * Generate AC Command APDU
     */
    private final GenerateAcCommandApdu mCommandApdu;

    /**
     * Business Logic amount (the amount as specified by the upper layers).
     * This is used by the Wallet to impose a context into the MPP lite
     */
    private final byte[] mBusinessLogicAmount;

    /**
     * Flag indicating whether the business logic requires an exact amount to be approved.
     * If true, such amount is specified in the mBusinessLogicAmount variable
     */
    private final boolean mIsBusinessLogicExactAmount;

    /**
     * Business Logic currency code (related to the business logic amount)
     */
    private final byte[] mCurrencyCode;

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
     * @param cvr                          The current CVR values for this transaction. Please
     *                                     note that this function may modify values in CVR
     * @param ciacDecline                  The Ciac decline as per card profile
     * @param transactionCredentialManager Call back to the credential
     *                                     manager to check the
     *                                     status of the credentials (both if available and
     *                                     accessible)
     */
    MchipCardRiskManagement(final GenerateAcCommandApdu commandApdu,
                            final ContactlessContext context,
                            final CardVerificationResults cvr,
                            final byte[] ciacDecline,
                            final TransactionCredentialsManager transactionCredentialManager) {
        this.mCommandApdu = commandApdu;
        this.mBusinessLogicAmount =
                (context.getBlAmount() != null) ? context.getBlAmount().getBytes() : null;
        this.mIsBusinessLogicExactAmount = context.isBlExactAmount();
        this.mCurrencyCode =
                (context.getBlCurrency() != null) ? context.getBlCurrency().getBytes() : null;
        this.mCiacDecline = ciacDecline;
        this.mCvr = cvr;
        this.mTransactionCredentialsManager = transactionCredentialManager;
    }

    /**
     * Check the transaction conditions for M-CHIP
     *
     * @return True, if the transaction conditions are met. False otherwise.
     */
    @Override
    protected final boolean transactionConditionsAllowed() {
        return !mCvr.isCiacDeclineMatchFound(mCiacDecline);
    }

    /**
     * Check whether there is a context match
     *
     * @return True if context matches, false otherwise
     */
    @Override
    protected boolean isContextMatching() {
        return ContactlessUtils.isContextMatching(mCommandApdu.getAuthorizedAmount(),
                                                  mCommandApdu.getTransactionCurrencyCode(),
                                                  mBusinessLogicAmount,
                                                  mCurrencyCode,
                                                  mIsBusinessLogicExactAmount);
    }

    @Override
    protected boolean isTransitTransaction() {
        return ContactlessUtils.isTransitTransaction(mCommandApdu.getTransactionType()[0],
                                                     mCommandApdu.getMerchantCategoryCode(),
                                                     mCommandApdu.getAuthorizedAmount());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final boolean isCdCvmRequired(final TransactionInformation transactionInformation) {
        return transactionInformation.getTransactionRange() == TransactionRange.HIGH_VALUE &&
               transactionInformation.hasTerminalDelegatedCdCvm();
    }

    /**
     * Handle the notification of a new Authorization according to M-CHIP rules
     */
    @Override
    protected final void notifyNewAuthorization() {
        switch (getCurrentAssessment()) {
            case AGREE:
            case ABORT:
            case DECLINE:
                // Do nothing on purpose
        }
    }

    /**
     * Handle the notification of a newly added reason according to M-CHIP rules
     */
    @Override
    protected final void notifyAddReason(final Reason reason) {
        switch (reason) {
            case MISSING_CD_CVM:
                //TODO: Discuss what happens to the approval if the wallet overrides us?
                // Other bits in the CVR related to lack of CD CVM will be set after the Wallet
                // decision (for example lack of access to the credentials)
                mCvr.indicateTerminalErroneouslyConsiderOfflinePinOk();
                break;
            default:
                // No updates to the CVR in other cases
        }
    }

    /**
     * Check whether the credentials are subject to CVM for MCHIP technology and for a particular
     * transaction range
     *
     * @param transactionRange The transaction range of this transaction.
     * @return True, if UMD credentials are subject to CVM, false otherwise.
     */
    @Override
    protected final boolean areUmdCredentialsSubjectToCvmFor(
            final TransactionRange transactionRange) {
        return mTransactionCredentialsManager
                .areUmdCredentialsSubjectToCvmFor(transactionRange, CONTACTLESS);
    }

    /**
     * Check whether there are valid credentials for MCHIP Contactless
     *
     * @return True, if there are valid credentials available for MCHIP contactless, false otherwise
     */
    @Override
    protected final boolean hasValidCredentials() {
        return mTransactionCredentialsManager.hasValidCredentialsFor(CONTACTLESS);
    }
}
