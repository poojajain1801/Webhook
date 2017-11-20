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
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Advice;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalInformation;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.Purpose;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionInformation;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidSingleUseKey;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;

import java.util.List;

/**
 * The Transaction outcome builder acts upon the Advice and is responsible for building the
 * response for the current transaction
 * <p/>
 * To accommodate both Contactless and Remote Payment the interface is a template interface
 * to allow a given return type to be specified
 */
public class TransactionOutcomeBuilder<T> {
    /**
     * The advice that will be used for decision
     */
    private final Advice mAdvice;

    /**
     * The Transaction Information for the current transaction
     */
    private final TransactionInformation mTransactionInformation;

    /**
     * The Terminal Information for the current transaction
     */
    private final TerminalInformation mTerminalInformation;

    /**
     * The Credentials Manager to request for valid credentials, if needed
     */
    private final TransactionCredentialsManager mTransactionCredentialsManager;

    /**
     * The listener that is responsible for technology specific implementation of the action
     */
    private final TransactionOutcomeBuilderListener<T> mListener;

    /**
     * Build a Card Risk Action Manager object
     *
     * @param advice                        The advice based on which the decision should be made
     * @param transactionInformation        The information for the current transaction
     * @param terminalInformation           The terminal information for the current transaction
     * @param transactionCredentialsManager The credential manager that will be used to retrieve
     *                                      credentials, if needed
     * @param listener                      The listener that will be responsible for
     *                                      implementing the technology
     *                                      specific actions
     */
    public TransactionOutcomeBuilder(final Advice advice,
                                     final TransactionInformation transactionInformation,
                                     final TerminalInformation terminalInformation,
                                     final TransactionCredentialsManager
                                             transactionCredentialsManager,
                                     final TransactionOutcomeBuilderListener<T> listener) {
        this.mAdvice = advice;
        this.mTransactionInformation = transactionInformation;
        this.mTerminalInformation = terminalInformation;
        this.mTransactionCredentialsManager = transactionCredentialsManager;
        if (listener == null) {
            throw new MppLiteException("Invalid action manager listener");
        }
        this.mListener = listener;
    }

    /**
     * Prepare a response based on the outcome of the action manager.
     *
     * @return The Response in a format that is suitable to the actual underlying technology
     * (e.g. Contactless Magstripe, Contactless M-CHIP, or Remote Payment)
     */
    public final T buildResponse() {
        switch (mAdvice.getAssessment()) {
            case AGREE:
                return handleAgree(mTransactionInformation.getPurpose());
            case ABORT:
                return handleAbort(mAdvice.getReasons());
            case DECLINE:
                return handleDecline();
            case ERROR:
            default:
                return handleError(mAdvice.getReasons());
        }
    }

    /**
     * Utility function to handle the agree decision
     */
    private T handleAgree(final Purpose purpose) {
        // Let's try to get a valid credential
        final TransactionCredentials transactionCredentials;
        try {
            transactionCredentials = getValidCredentials();
        } catch (final InvalidSingleUseKey e) {
            // We were supposed to get valid credentials, but we could not get them
            // Flag the issue and try complete the first tap potentially
            return handleDecline();
        }

        if (purpose == Purpose.AUTHENTICATE) {
            return mListener.authenticate(mAdvice.getReasons(), transactionCredentials);
        }

        // Otherwise approve online
        return mListener.approveOnline(mAdvice.getReasons(), transactionCredentials);
    }

    /**
     * Utility function to handle Abort decisions
     */
    private T handleAbort(final List<Reason> reasons) {
        final boolean isConsentMissing = reasons.contains(Reason.MISSING_CONSENT);
        final boolean isCdCvmMissing =
                reasons.contains(Reason.MISSING_CD_CVM) ||
                reasons.contains(Reason.CREDENTIALS_NOT_ACCESSIBLE_WITHOUT_CVM);

        if (!isConsentMissing && !isCdCvmMissing) {
            // Lack of reasons to do a meaningful action
            handleDecline();
        }

        return mListener.abort(mAdvice.getReasons());
    }

    /**
     * Utility function to handle Decline decisions
     */
    private T handleDecline() {
        return mListener.decline(mAdvice.getReasons());
    }

    /**
     * Utility function to handle Error decisions
     */
    private T handleError(final List<Reason> reasons) {
        return mListener.error(reasons);
    }

    /**
     * Utility function to retrieve credentials when needed
     *
     * @return True if the operation was completed successfully. It returns false only in case
     * valid credentials were requested, but none could be provided. In that case,
     * credentials are still initialized with random data.
     */
    private TransactionCredentials getValidCredentials() throws InvalidSingleUseKey {
        final TransactionCredentialsManager.Scope scope;

        switch (mTerminalInformation.getTerminalTechnology()) {
            case CONTACTLESS_EMV:
            case CONTACTLESS_MAGSTRIPE:
                scope = TransactionCredentialsManager.Scope.CONTACTLESS;
                break;
            case REMOTE_DSRP_EMV:
            case REMOTE_DSRP_UCAF:
                scope = TransactionCredentialsManager.Scope.REMOTE_PAYMENT;
                break;
            default:
                // We should never be here...
                throw new MppLiteException("Unsupported Terminal Technology");
        }

        final TransactionCredentials transactionCredentials =
                mTransactionCredentialsManager.getValidUmdAndMdCredentialsFor(scope);

        // If something went wrong we will fallback to Random credentials, but we will return false
        if (transactionCredentials == null) {
            throw new InvalidSingleUseKey("Unable to access valid credentials");
        }

        return transactionCredentials;
    }
}
