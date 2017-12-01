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
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.transactiondecisionmanager.AdviceManager;
import com.mastercard.mcbp.transactiondecisionmanager.ConsentManager;

import java.util.List;

/**
 * {@link MppLiteState} container used by the
 * {@link MppLite} which allows to switch from an MppLiteState
 * to another
 */
public final class MppLiteStateContext {
    /**
     * MPP Lite Card Profile Module
     */
    private final MppLiteModule mProfile;

    /**
     * The Transaction Credentials Manager that can be used to request for credentials when needed
     */
    private final TransactionCredentialsManager mTransactionCredentialsManager;

    /**
     * Reference to the Consent Manager used to check whether or not consent was provided
     */
    private final ConsentManager mConsentManager;

    /**
     * Reference to the advice manager that the MPP Lite will call back during the cryptogram
     * generation for transaction outcome advice
     */
    private final AdviceManager mAdviceManager;

    /**
     * Reference to the Card Holder Validator object to be used for authentication
     */
    private final ChValidator mChValidator;

    /**
     * List of additional PDOLs to be requested by the MPP Lite during the transaction
     */
    private final List<DolRequestList.DolItem> mAdditionalPdolList;

    /**
     * List of additional UDOLs to be requested by the MPP Lite during the transaction
     */
    private final List<DolRequestList.DolItem> mAdditionalUdolList;


    /**
     * Flag indicating whether M-CHIP support should be masked in AIP for US transactions
     */
    private final boolean mMaskMchipInAipForUsTransactions;

    /**
     * MppLite State
     */
    private MppLiteState mMppLiteState = null;

    /**
     * Create an MPP Lite State Context object by specifying the CVM Manager and the Broker to be
     * used. It also initialize the current state by default into Stopped State
     *
     * @param consentManager The CVM Manager to be used during the cryptogram generation
     * @param chValidator    The instance of the Broker used to verify whether the user has been
     *                       authenticated
     */
    public MppLiteStateContext(final MppLiteModule mppLiteModule,
                               final TransactionCredentialsManager credentialsManager,
                               final ChValidator chValidator,
                               final ConsentManager consentManager,
                               final AdviceManager adviceManager,
                               final List<DolRequestList.DolItem> additionalPdolList,
                               final List<DolRequestList.DolItem> additionalUdolList,
                               final boolean maskMchipInAipForUsTransactions) {
        this.mMppLiteState = new StoppedState(this);
        this.mProfile = mppLiteModule;
        this.mTransactionCredentialsManager = credentialsManager;
        this.mChValidator = chValidator;
        this.mConsentManager = consentManager;
        this.mAdviceManager = adviceManager;
        this.mAdditionalPdolList = additionalPdolList;
        this.mAdditionalUdolList = additionalUdolList;
        this.mMaskMchipInAipForUsTransactions = maskMchipInAipForUsTransactions;
    }

    /**
     * Get the CVM Manager instance
     */
    public final ConsentManager getConsentManager() {
        return mConsentManager;
    }

    /**
     * Get the Broker instance
     */
    public final ChValidator getChValidator() {
        return mChValidator;
    }

    /**
     * Get the Advice Manager
     *
     * @return The Transaction Advice Manager
     */
    public final AdviceManager getAdviceManager() {
        return mAdviceManager;
    }

    /***
     * Get the Transaction Credentials Listener
     *
     * @return the Transaction Credentials Listener that can be used to retrieve credentials when
     * needed
     */
    public final TransactionCredentialsManager getTransactionCredentialsManager() {
        return mTransactionCredentialsManager;
    }

    /**
     * Get list of additional PDOLs to be requested by the MPP Lite
     */
    public final List<DolRequestList.DolItem> getAdditionalPdolList() {
        return mAdditionalPdolList;
    }

    /**
     * Get list of additional UDOLs to be requested by the MPP Lite
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
     * Return the current state
     *
     * @return the current state
     */
    public final MppLiteState getState() {
        return mMppLiteState;
    }

    /**
     * Set the state
     *
     * @param state The next Mpp Lite State
     */
    public final void setState(final MppLiteState state) {
        this.mMppLiteState = state;
    }

    /**
     * Set the state Stopped state as active state*
     */
    public final void setStoppedState() {
        this.mMppLiteState = new StoppedState(this);
    }

    /**
     * Set the state Stopped state as active state*
     */
    public final void setInitializedState() {
        this.mMppLiteState = new InitializedState(this);
    }

    /**
     * Get the the Mpp Lite which is associated with this context
     *
     * @return The MppLiteModule object for this card profile
     */
    public final MppLiteModule getProfile() {
        return mProfile;
    }

    /**
     * Wipe the context (i.e. wipe the profile)
     */
    public final void wipe() {
        if (mProfile != null) mProfile.wipe();
    }
}
