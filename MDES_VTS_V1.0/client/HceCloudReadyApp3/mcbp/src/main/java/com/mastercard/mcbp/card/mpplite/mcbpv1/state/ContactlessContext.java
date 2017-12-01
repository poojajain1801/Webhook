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

import com.mastercard.mcbp.card.BusinessLogicTransactionInformation;
import com.mastercard.mcbp.card.cvm.ChValidator;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless.ContactlessTransactionContext;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.transactiondecisionmanager.AdviceManager;
import com.mastercard.mcbp.transactiondecisionmanager.ConsentManager;
import com.mastercard.mcbp.transactiondecisionmanager.advice.Advice;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalInformation;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import java.util.List;

/**
 * Define the Contactless Context that will be used by all the Contactless Ready sub states
 */
public final class ContactlessContext {
    /**
     * Business Logic flag specifying whether the exact amount has been requested
     */
    private final boolean mExactAmount;

    /**
     * Business Logic Amount
     */
    private final ByteArray mAmount;

    /**
     * bl Currency
     */
    private final ByteArray mCurrency;

    /**
     * Contactless Transaction Context
     */
    private final ContactlessTransactionContext mContactlessTransactionContext;

    /**
     * The Card Profile associated with the context
     */
    private final MppLiteModule mCardProfile;

    /**
     * The call back method to allow the MPP Lite to request for Credentials when needed
     */
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
     * List of additional PDOLs that the MPP Lite should request to the POS during the transaction
     */
    private List<DolRequestList.DolItem> mAdditionalPdolList;

    /**
     * List of additional UDOLs that the MPP Lite should request to the POS during the transaction
     */
    private List<DolRequestList.DolItem> mAdditionalUdolList;

    /**
     * Flag indicating whether M-CHIP should be masked in AIP for US transactions
     */
    private final boolean mMaskMchipInAipForUsTransactions;

    /**
     * Current transaction state
     */
    private ContactlessReadySubState mState;

    /**
     * Contactless Transaction Listener
     */
    private final ContactlessTransactionListener mContactlessTransactionListener;

    /**
     * Specify whether a notification should be provided via the listener
     * This is used in case of an exception or error to make sure we inform listeners
     */
    private boolean mNotificationRequested = false;

    /***
     * Constructor
     *
     * @param profile                         The MPP Card Profile
     * @param transactionCredentialsManager   The listener to retrieve credentials when needed
     * @param cardholderValidator             The Card Holder Validator call back interface to check
     *                                        whether the user was authenticated or not
     * @param consentManager                  The consent manager call back interface to check for
     *                                        user consent when needed
     * @param adviceManager                   The Advice Manager to ask for the Wallet Advice during
     *                                        the transaction
     * @param contactlessTransactionListener  The Contactless Transaction Listener to follow card
     *                                        events
     * @param maskMchipInAipForUsTransactions Flag indicating whether M-CHIP should be masked for
     *                                        US transactions
     * @param trxInfo                         Input transaction info such as amount, currency code,
     *                                        etc
     * @param additionalPdolList              The list of additional PDOLs that the MPP Lite should
     *                                        request to the reader during the transaction
     * @param additionalUdolList              The list of additional UDOLs that the MPP Lite should
     *                                        request to the reader during the transaction
     */
    public ContactlessContext(final MppLiteModule profile,
                              final TransactionCredentialsManager transactionCredentialsManager,
                              final ChValidator cardholderValidator,
                              final ConsentManager consentManager,
                              final AdviceManager adviceManager,
                              final List<DolRequestList.DolItem> additionalPdolList,
                              final List<DolRequestList.DolItem> additionalUdolList,
                              final boolean maskMchipInAipForUsTransactions,
                              final ContactlessTransactionListener contactlessTransactionListener,
                              final BusinessLogicTransactionInformation trxInfo) {
        this.mContactlessTransactionContext = new ContactlessTransactionContext();

        // Initialize MCBP 1.0+ related parameters.
        this.mCardHolderValidator = cardholderValidator;
        this.mConsentManager = consentManager;
        this.mTransactionCredentialsManager = transactionCredentialsManager;
        this.mAdviceManager = adviceManager;
        this.mAdditionalPdolList = additionalPdolList;
        this.mAdditionalUdolList = additionalUdolList;
        this.mMaskMchipInAipForUsTransactions = maskMchipInAipForUsTransactions;

        // IF input parameter amount (long) is not negative,
        // THEN
        // convert it to a 6-byte BCD buffer, left-padded with leading zeroes,
        // and associate the conversion result to attribute CL.blAmount;
        // and associate the conversion result to attribute CL.blCurrency;
        final long amount = trxInfo.getAmount();

        if (amount >= 0) {
            this.mAmount = ByteArray.of(Utils.longToBcd(amount, 6));
            this.mCurrency = ByteArray.of(Utils.longToBcd(trxInfo.getCurrencyCode(), 2));
        } else {
            this.mAmount = null;
            this.mCurrency = null;
        }
        this.mExactAmount = trxInfo.isExactAmount();

        // Everything seems to be good. Set the card profile
        this.mCardProfile = profile;

        // Set the listener
        this.mContactlessTransactionListener = contactlessTransactionListener;
    }

    /**
     * Flag specifying whether the business logic requires the transaction amount to match the
     * business logic amount
     *
     * @return True if a match with the exact amount is required, false otherwise.
     */
    public final boolean isBlExactAmount() {
        return mExactAmount;
    }

    /**
     * Get the Business Logic Currency Code
     *
     * @return The Business Logic Currency Code
     */
    public final ByteArray getBlCurrency() {
        return mCurrency;
    }

    /**
     * Get the Business Logic Amount (relevant in case exact amount is requested)
     *
     * @return The value specified by the business logic for the amount
     */
    public final ByteArray getBlAmount() {
        return mAmount;
    }

    /**
     * Get the Card Profile associated with this context
     *
     * @return The Card Profile
     */
    public final MppLiteModule getCardProfile() {
        return mCardProfile;
    }

    /***
     * Check whether the CVM has been entered
     *
     * @return true if the CVM has been entered, false otherwise
     */
    public final boolean isCvmEntered() {
        return mCardHolderValidator.isAuthenticated();
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
     *
     * @param masterCardAdvice       The MasterCard Advice
     * @param transactionInformation The Transaction Information
     * @param terminalInformation    The Terminal Information
     * @return The Wallet Advice, if a Wallet Advice Manager has been set. Otherwise, it echoes back
     * the MasterCard advice.
     */
    public final Advice getWalletAdvice(final Advice masterCardAdvice,
                                        final com.mastercard.mcbp
                                                .transactiondecisionmanager.transaction
                                                .TransactionInformation transactionInformation,
                                        final TerminalInformation terminalInformation) {
        if (mAdviceManager == null) {
            return masterCardAdvice;
        }
        return mAdviceManager.adviseTransactionOutcome(masterCardAdvice,
                                                       transactionInformation,
                                                       terminalInformation);
    }

    /**
     * Get The Contactless Transaction Context
     *
     * @return The Contactless Transaction Context
     */
    public final ContactlessTransactionContext getTransactionContext() {
        return mContactlessTransactionContext;
    }

    /***
     * Get the listener for the transaction events
     *
     * @return The ContactlessTransactionListener for the transaction events
     */
    public final ContactlessTransactionListener getTransactionListener() {
        return mContactlessTransactionListener;
    }

    /**
     * Get the Transaction Credentials Manager
     */
    public final TransactionCredentialsManager getTransactionCredentialsManager() {
        return mTransactionCredentialsManager;
    }

    /**
     * Get the additional list of PDOLs that the MPP Lite should request to the reader during
     * the transaction
     *
     * @return The list of additional PDOLs to be requested.
     */
    public final List<DolRequestList.DolItem> getAdditionalPdolList() {
        return mAdditionalPdolList;
    }

    /**
     * Get the additional list of UDOLs that the MPP Lite should request to the reader during
     * the transaction
     *
     * @return The list of additional UDOLs to be requested.
     */
    public final List<DolRequestList.DolItem> getAdditionalUdolList() {
        return mAdditionalUdolList;
    }

    /**
     * Specify whether the MPP Lite should mask M-CHIP support in AIP in case of a US transaction
     * @return True if M-Chip masking is enabled, false otherwise.
     */
    public boolean isMaskMchipInAipForUsTransactions() {
        return mMaskMchipInAipForUsTransactions;
    }

    /**
     * Set the state to Contactless Not Selected
     */
    public final void setContactlessNotSelectedState() {
        this.mState = new ContactlessNotSelectedState(this);
    }

    /**
     * Set the state to Contactless Selected
     */
    public final void setContactlessSelectedState() {
        this.mState = new ContactlessSelectedState(this);
    }

    /**
     * Set the state to Contactless Initiated
     */
    public final void setContactlessInitiatedState() {
        this.mState = new ContactlessInitiatedState(this);
    }

    /**
     * Get the current state
     *
     * @return The current state
     */
    public final ContactlessReadySubState getState() {
        return mState;
    }

    /**
     * Set that a notification has been requested in case of successful processing using the
     * listener
     */
    public final void requestListenerNotification() {
        mNotificationRequested = true;
    }

    /**
     * Return whether a notification via the listener has been requested
     */
    public final boolean isNotificationRequested() {
        return mNotificationRequested;
    }

    /**
     * Wipe this context
     */
    public void wipe() {
        Utils.clearByteArray(mAmount);
        Utils.clearByteArray(mCurrency);
        mContactlessTransactionContext.wipe();
        mState = null;
    }

}
