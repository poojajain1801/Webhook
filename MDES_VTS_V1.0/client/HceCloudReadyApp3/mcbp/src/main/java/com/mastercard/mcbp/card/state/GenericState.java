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

package com.mastercard.mcbp.card.state;

import com.mastercard.mcbp.card.BusinessLogicTransactionInformation;
import com.mastercard.mcbp.card.CardListener;
import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpResult;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLog;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.userinterface.DisplayStatus;
import com.mastercard.mcbp.userinterface.DisplayTransactionInfo;
import com.mastercard.mcbp.userinterface.UserInterfaceMcbpHelper;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mobile_api.bytes.ByteArray;

import static com.mastercard.mcbp.userinterface.DisplayStatus.FAILED;

/**
 * MCBP Generic Internal State
 * It is used to implement utility functions and methods common to all the MCBP Card states.
 */
abstract class GenericState implements CardInternalState {
    /**
     * Reference to the context of the MCBP Card
     */
    private final CardContext mCardContext;

    /**
     * Reference to the MPP Lite object
     */
    private final MppLite mMppLite;

    /**
     * {@inheritDoc}
     *
     * @param transactionInfo
     */
    @Override
    public abstract void startContactlessPayment(
            final BusinessLogicTransactionInformation transactionInfo)
            throws InvalidCardStateException, McbpCryptoException, InvalidInput, LdeNotInitialized;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void stopContactLess() throws InvalidCardStateException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void startRemotePayment()
            throws InvalidCardStateException, DsrpIncompatibleProfile;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract DsrpResult getTransactionRecord(final DsrpInputData dsrpInputData)
            throws InvalidCardStateException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract ByteArray processApdu(final ByteArray apdu) throws InvalidCardStateException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void processOnDeactivated();

    /***
     * Generic constructor for the state
     *
     * @param cardContext The Card Context
     * @param mppLite          The MPP Lite
     */
    protected GenericState(final CardContext cardContext,
                           final MppLite mppLite) {
        if (cardContext == null) {
            throw new IllegalArgumentException("The CardStateContext cannot be null");
        }
        if (mppLite == null) {
            throw new IllegalArgumentException("The MPP Lite cannot be null");
        }
        this.mCardContext = cardContext;
        this.mMppLite = mppLite;
    }

    /***
     * Get the MPP Lite to process APDU or remote payment requests
     *
     * @return The instance of the MPP Lite
     */
    protected final MppLite getMppLite() {
        return mMppLite;
    }

    /**
     * Get the Card State Context.
     *
     * @return the current card state context
     */
    protected final CardContext getCardContext() {
        return mCardContext;
    }

    /**
     * Move to the Initialized State
     */
    protected final void toInitializedState(boolean cancelPayment) {
        mCardContext.toInitializedState(cancelPayment);
    }

    /**
     * Move to the Contactless Ready State
     */
    protected final void toContactlessReadyState() {
        mCardContext.toContactlessReadyState();
    }

    /**
     * Move to the Contactless Ready State
     */
    protected final void toContactlessReadyState(
            final ContactlessTransactionListener contactlessTransactionListener,
            final BusinessLogicTransactionInformation businessLogicTransactionInformation) {
        mCardContext.toContactlessReadyState(contactlessTransactionListener,
                                             businessLogicTransactionInformation);
    }

    /**
     * Move to the Contactless Transaction Started State
     */
    protected final void toContactlessTransactionStarted() {
        mCardContext.toContactlessTransactionStarted();
    }

    /**
     * Move to the Contactless Transaction Completed State
     */
    protected final void toContactlessTransactionCompleted() {
        mCardContext.toContactlessTransactionCompleted();
    }

    /**
     * Move to the Remote Payment Ready State
     */
    protected final void toRemotePaymentReadyState() {
        mCardContext.toRemotePaymentReadyState();
    }

    /**
     * Notify to the UI that the transaction has been completed
     */
    protected void notifyTransactionCompleted(final ContactlessLog contactlessLog,
                                              final ByteArray transactionId,
                                              final CardListener cardListener) {
        final DisplayTransactionInfo displayTransactionInfo =
                UserInterfaceMcbpHelper.getDisplayableTransactionInformation(contactlessLog,
                                                                             transactionId);
        cardListener.onTransactionCompleted(displayTransactionInfo);
    }

    /**
     * Notify the Card that the transaction failed
     */
    protected final void notifyTransactionFailed() {
        final CardListener cardListener = mCardContext.getCardListener();
        if (cardListener != null) {
            cardListener.onTransactionCompleted(new DisplayTransactionInfo() {
                @Override
                public String getDisplayableAmount() {
                    return "";
                }

                @Override
                public DisplayStatus getStatus() {
                    return FAILED;
                }

                @Override
                public String getTransactionIdentifier() {
                    return "";
                }
            });
        }
    }


}
