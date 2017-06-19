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
import com.mastercard.mcbp.card.mpplite.apdu.emv.ComputeCcCommandApdu;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless.ContactlessUtils;
import com.mastercard.mcbp.card.mpplite.mcbpv1.state.ContactlessContext;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionInformation;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionRange;

import java.util.Arrays;

import static com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager
        .Scope.CONTACTLESS;

/**
 * Card Risk Management for Magstripe technology
 */
class MagstripeCardRiskManagement extends CardRiskManagement {
    /**
     * The Compute CC C-APDU
     */
    private final ComputeCcCommandApdu mCommandApdu;

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
     * The CIAC Decline on PPMS (as in the MPP Lite Profile)
     */
    private final byte[] mCiacDeclineOnPpms;

    /**
     * The Terminal Country Code
     */
    private final byte[] mCardRiskManagementCountryCode;

    /**
     * The Transaction Credential Manager to check the status of the credentials
     */
    private final TransactionCredentialsManager mTransactionCredentialsManager;

    /**
     * Build the Card Risk Management object for Magstripe
     *
     * @param commandApdu                   The Compute CC C-APDU
     * @param ciacDeclineOnPpms             The CIAC Decline on PPMS
     * @param cardRiskManagementCountryCode The Card Risk Management Country Code
     * @param transactionCredentialManager  Call back to the credential manager to check the
     *                                      status of the credentials (both if available and
     *                                      accessible)
     */
    MagstripeCardRiskManagement(final ComputeCcCommandApdu commandApdu,
                                final ContactlessContext context,
                                final byte[] ciacDeclineOnPpms,
                                final byte[] cardRiskManagementCountryCode,
                                final TransactionCredentialsManager transactionCredentialManager) {
        this.mCommandApdu = commandApdu;
        this.mBusinessLogicAmount =
                (context.getBlAmount() != null) ? context.getBlAmount().getBytes() : null;
        this.mIsBusinessLogicExactAmount = context.isBlExactAmount();
        this.mCurrencyCode =
                (context.getBlCurrency() != null) ? context.getBlCurrency().getBytes() : null;
        this.mCiacDeclineOnPpms = ciacDeclineOnPpms;
        this.mCardRiskManagementCountryCode = cardRiskManagementCountryCode;
        this.mTransactionCredentialsManager = transactionCredentialManager;
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
        return ContactlessUtils.isTransitTransaction(mCommandApdu.getTransactionType(),
                                                     mCommandApdu.getMerchantCategoryCode(),
                                                     mCommandApdu.getAuthorizedAmount());
    }


    /**
     * Check the transaction conditions for M-CHIP
     *
     * @return True, if the transaction conditions are met. False otherwise.
     */
    @Override
    protected final boolean transactionConditionsAllowed() {
        return isTransactionDomestic() ? areDomesticTransactionsAllowed()
                                       : areInternationalTransactionsAllowed();
    }

    /**
     * Handle the notification of a new Authorization according to Magstripe rules
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
     * Handle the notification of a newly added reason according to Magstripe rules
     */
    @Override
    protected final void notifyAddReason(final Reason reason) {
        switch (reason) {
            default:
                // Do nothing on purpose
        }
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
     * {@inheritDoc}
     */
    @Override
    protected final boolean areUmdCredentialsSubjectToCvmFor(
            final TransactionRange transactionRange) {
        return mTransactionCredentialsManager.areUmdCredentialsSubjectToCvmFor(transactionRange,
                                                                               CONTACTLESS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final boolean hasValidCredentials() {
        return mTransactionCredentialsManager.hasValidCredentialsFor(CONTACTLESS);
    }

    /**
     * Utility function to check whether domestic transactions are allowed
     *
     * @return True, if allowed. False otherwise.
     */
    private boolean areDomesticTransactionsAllowed() {
        return !((mCiacDeclineOnPpms[0] & 0x02) == 0x02);
    }

    /**
     * Utility function to check whether the transaction is domestic
     *
     * @return True, if domestic. False if international.
     */
    private boolean isTransactionDomestic() {
        return Arrays.equals(mCardRiskManagementCountryCode,
                             mCommandApdu.getTerminalCountryCode());
    }

    /**
     * Utility function to check whether international transactions are allowed
     *
     * @return True, if allowed. False otherwise.
     */
    private boolean areInternationalTransactionsAllowed() {
        return !((mCiacDeclineOnPpms[0] & 0x04) == 0x04);
    }
}
