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

import com.mastercard.mcbp.transactiondecisionmanager.advice.Advice;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Assessment;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionInformation;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionRange;

import java.util.ArrayList;
import java.util.List;

/**
 * Card Risk Management to assess both financial and Cardholder Verification method related risks
 */
public abstract class CardRiskManagement {
    /**
     * Current authorization (it is updated during the decision process)
     */
    private Assessment mCurrentAssessment = Assessment.AGREE;
    /**
     * List of Reasons. Reasons are added to the list while the analysis is performed
     */
    private final List<Reason> mReasons = new ArrayList<>();

    /**
     * Get the MasterCard advice related to the Card Risk Management analysis
     * (both financial and CVM risks are assessed)
     *
     * @param transactionInformation The Transaction Information
     * @param isConsentGiven         Flag indicating whether consent was given
     * @param isCvmEntered           Flag indicating whether CVM was entered (aka user is
     *                               authenticated)
     * @return The MasterCard advice for the Card Risk Management
     */
    public final Advice getMasterCardAdvice(final TransactionInformation transactionInformation,
                                            final boolean isConsentGiven,
                                            final boolean isCvmEntered) {
        final TransactionRange transactionRange = transactionInformation.getTransactionRange();

        // Check if the transaction is TRANSIT - we do not support transit in 1.0
        if (isTransitTransaction()) {
            handleTransitNotSupported();
        }

        // Check consistency between taps within the same context
        if (!isContextMatching()) {
            handleNoContextMatching();
        }

        // Check Financial risks first
        if (!transactionConditionsAllowed()) {
            handleConditionsNotAllowed();
        }

        // Check CVM Risks now
        if (!isConsentGiven) {
            handleNoConsent();
        }

        // Check if the CD CVM was required, but not entered
        if (isCdCvmRequired(transactionInformation) && !isCvmEntered) {
            handleNoCdCvm();
        }

        // Check that if we go Online we do actually have valid credentials and we can access them!

        if (areUmdCredentialsSubjectToCvmFor(transactionRange) && !isCvmEntered) {
            // If we decide to approve, we may not have valid credentials
            // Let's propose try an ABORT for lack of credentials
            handleCredentialsNotAccessible();
        }

        if (!hasValidCredentials()) {
            handleCredentialsNotAvailable();
        }

        return generateAdvice();
    }

    private Advice generateAdvice() {
        return new Advice(mCurrentAssessment, mReasons);
    }

    /**
     * Access method that sub classes can use to get access to the current authorization
     *
     * @return The current authorization
     */
    protected final Assessment getCurrentAssessment() {
        return mCurrentAssessment;
    }

    /**
     * Check whether the context between this transaction matches any previous tap with the same
     * context
     *
     * @return True if the context matches, false otherwise.
     */
    protected abstract boolean isContextMatching();

    /**
     * Check whether the transaction being performed is transit
     *
     * @return True if the transaction is transit, false otherwise.
     */
    protected abstract boolean isTransitTransaction();

    /**
     * Check whether the Issuer allows international / domestic transactions
     * Each technology specific class will have to implement the logic for this. For example,
     * Magstripe has different rules from M-CHIP to check whether Issuer Conditions are met
     *
     * @return True if the transaction is allowed, false otherwise
     */
    protected abstract boolean transactionConditionsAllowed();

    /**
     * Notify to the child class that a new authorization has been set. If interested, the child
     * can check the new authorization by using {@link #getCurrentAssessment()}.
     */
    protected abstract void notifyNewAuthorization();

    /**
     * Notify to the implementer of the technology specific class that a new reason has been
     * added to the list of reasons
     * For example, this allows the M-CHIP version of this module to set the proper values in CVR
     *
     * @param reason The newly added Reason
     */
    protected abstract void notifyAddReason(final Reason reason);

    /**
     * Utility function to check whether the CVM is required
     *
     * @param transactionInformation Transaction Information
     * @return True if the CD CVM is required, false otherwise
     */
    protected abstract boolean isCdCvmRequired(final TransactionInformation transactionInformation);

    /**
     * Utility function to check whether the Credentials are subject to CVM
     *
     * @param transactionRange The transaction range
     * @return True, if credentials require CVM, false otherwise
     */
    protected abstract boolean areUmdCredentialsSubjectToCvmFor(
            final TransactionRange transactionRange);

    /**
     * Check whether there are valid credentials available in the system
     *
     * @return True, if credentials are available, false otherwise
     */
    protected abstract boolean hasValidCredentials();

    /**
     * Utility function to manage lack of context between two taps
     */
    private void handleNoContextMatching() {
        addReason(Reason.CONTEXT_NOT_MATCHING);
        changeAuthorization(Assessment.ERROR);
    }

    /**
     * Utility function to manage the lack of consent
     */
    private void handleNoConsent() {
        addReason(Reason.MISSING_CONSENT);
        changeAuthorization(Assessment.ABORT);
    }

    /**
     * Utility function to manage the lack of CD-CVM
     */
    private void handleNoCdCvm() {
        addReason(Reason.MISSING_CD_CVM);
        changeAuthorization(Assessment.ABORT);
    }

    /**
     * Utility function to manage cases where conditions are not allowed by the Issuer
     */
    private void handleConditionsNotAllowed() {
        addReason(Reason.TRANSACTION_CONDITIONS_NOT_ALLOWED);
        changeAuthorization(Assessment.DECLINE);
    }

    /**
     * Utility function to manage the lack of access to the credentials
     */
    private void handleCredentialsNotAccessible() {
        addReason(Reason.CREDENTIALS_NOT_ACCESSIBLE_WITHOUT_CVM);
        changeAuthorization(Assessment.ABORT);
    }

    /**
     * Utility function to manage the lack of credentials (not available)
     */
    private void handleCredentialsNotAvailable() {
        addReason(Reason.CREDENTIALS_NOT_AVAILABLE);
        changeAuthorization(Assessment.DECLINE);
    }

    /**
     * Utility function to manage the unsupported transit transactions
     */
    private void handleTransitNotSupported() {
        addReason(Reason.UNSUPPORTED_TRANSIT);
        changeAuthorization(Assessment.DECLINE);
    }

    /**
     * Utility function to notify to sub classes a change in the authorization
     *
     * @return true, if the authorization was changed, false if failed (because the severity of the
     * new authorization is lower than the existing one).
     */
    private boolean changeAuthorization(final Assessment assessment) {
        if (assessment.getSeverityLevel() > mCurrentAssessment.getSeverityLevel()) {
            mCurrentAssessment = assessment;
            notifyNewAuthorization();
            return true;
        }
        return false;
    }

    /**
     * Utility function to add a reason in the decision reason list. It also notifies to sub classes
     * about a new reason being added
     *
     * @param reason The reason to be added to the list
     */
    private void addReason(final Reason reason) {
        mReasons.add(reason);
        notifyAddReason(reason);
    }
}
