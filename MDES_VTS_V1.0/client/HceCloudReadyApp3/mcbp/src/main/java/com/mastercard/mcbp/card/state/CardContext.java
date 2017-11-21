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
import com.mastercard.mcbp.card.cvm.ChValidator;
import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpResult;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLog;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.mcbpv1.MppLiteMcbpV1Factory;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.lde.services.LdeMcbpCardService;
import com.mastercard.mcbp.transactiondecisionmanager.AdviceManager;
import com.mastercard.mcbp.transactiondecisionmanager.ConsentManager;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidDigitizedCardProfile;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mcbp.utils.exceptions.mpplite.InvalidState;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Structure to keep the internal status of the MCBP Card within different internal states
 */
public class CardContext implements CardInternalState {
    /**
     * Digitized Card Id
     */
    private final String mDigitizedCardId;

    /**
     * Reference to the LDE Card Service
     */
    private final LdeMcbpCardService mLdeMcbpCardService;

    /**
     * Keep track of the current card internal state
     */
    private CardInternalState mCardInternalState = null;

    /**
     * The instance of the MPP Lite to be used by this MCBP Card
     */
    private final MppLite mMppLite;

    /**
     * Contactless Transaction listener. It is set when the contactless mode is started.
     */
    private CardListener mCardListener = null;

    /**
     * Cardholder validator method for this card
     */
    private ChValidator mChValidator;

    /**
     * First Tap Listener
     */
    private ContactlessTransactionListener mFirstTapListener;

    /**
     * Default constructor
     */
    public CardContext(final String digitizedCardId,
                       final LdeMcbpCardService ldeMcbpCardService,
                       final MppLiteModule mppLiteCardProfile,
                       final TransactionCredentialsManager transactionCredentialsManager,
                       final ChValidator chValidator,
                       final ConsentManager consentManager)
            throws InvalidDigitizedCardProfile {
        this.mDigitizedCardId = digitizedCardId;
        this.mLdeMcbpCardService = ldeMcbpCardService;
        this.mChValidator = chValidator;

        if (mppLiteCardProfile == null) {
            throw new InvalidDigitizedCardProfile("Invalid Card Profile");
        }

        // Create the MPP Lite object - it will remain the same for the entire lifecycle of this
        // context
        mMppLite = MppLiteMcbpV1Factory.buildV1(mppLiteCardProfile,
                                                transactionCredentialsManager,
                                                chValidator,
                                                consentManager);

        // Let's go to MCBP Card initialized state
        mCardInternalState = new InitializedState(this, mMppLite);
    }

    /***
     * Set the Card Listener
     */
    public void setCardListener(final CardListener cardListener) {
        mCardListener = cardListener;
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public void startContactlessPayment(final BusinessLogicTransactionInformation
                                                businessLogicTransactionInformation)
            throws InvalidCardStateException, McbpCryptoException, InvalidInput, LdeNotInitialized {
        mCardInternalState.startContactlessPayment(businessLogicTransactionInformation);
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public void stopContactLess() throws InvalidCardStateException {
        mCardListener = null;
        mCardInternalState.stopContactLess();
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public void startRemotePayment()
            throws InvalidCardStateException, DsrpIncompatibleProfile {
        mCardInternalState.startRemotePayment();
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public DsrpResult getTransactionRecord(final DsrpInputData dsrpInputData)
            throws InvalidCardStateException {
        return mCardInternalState.getTransactionRecord(dsrpInputData);
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public ByteArray processApdu(final ByteArray apdu) throws InvalidCardStateException {
        return mCardInternalState.processApdu(apdu);
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public void processOnDeactivated() {
        mCardInternalState.processOnDeactivated();
    }

    /**
     * Get the Internal State of the Card
     *
     * @return The MCBP Card state
     */
    CardInternalState getCurrentState() {
        return mCardInternalState;
    }

    /**
     * Get the Cardholder Validator for this card
     *
     * @return The Cardholder validator object
     */
    public ChValidator getChValidator() {
        return mChValidator;
    }

    /**
     * Get the Digitized Card Id for this card
     */
    String getDigitizedCardId() {
        return mDigitizedCardId;
    }

    /**
     * Get the Interface to access the LDE Card Service
     *
     * @return A reference to LDE Card Service Interface
     */
    LdeMcbpCardService getLdeMcbpCardService() {
        return mLdeMcbpCardService;
    }

    /**
     * Get the Card Listener
     */
    CardListener getCardListener() {
        return mCardListener;
    }

    /**
     * Go to Stopped State
     */
    void toInitializedState(final boolean cancelPayment) {
        if (cancelPayment) {
            mMppLite.cancelPayment();
        }
        mCardInternalState = new InitializedState(this, mMppLite);
    }

    /**
     * Go to the contactless Ready State and use the First Tap listener for the transaction
     */
    void toContactlessReadyState() {
        final ContactlessTransactionListener listener;
        if (mFirstTapListener == null) {
            listener = getDummyContactlessTransactionListener();
        } else {
            listener = mFirstTapListener;
        }
        // A dummy listener is used since this is the first tap transaction
        toContactlessReadyState(listener, new BusinessLogicTransactionInformation());
    }

    /**
     * Go to Contactless Ready State
     */
    void toContactlessReadyState(
            final ContactlessTransactionListener contactlessTransactionListener,
            final BusinessLogicTransactionInformation businessLogicTransactionInformation) {
        try {
            // For backward compatibility we initialize the MPP Lite with the same function,
            // although we will actually pass credentials and CVM status later via the callback
            // mechanism
            mMppLite.startContactLessPayment(contactlessTransactionListener,
                                             businessLogicTransactionInformation);
        } catch (final InvalidInput e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (final InvalidState e) {
            throw new IllegalStateException("The MPP Lite had been already started");
        }
        mCardInternalState = new ContactlessReadyState(this, mMppLite);
    }

    /**
     * Go to Contactless Transaction Started State
     */
    void toContactlessTransactionStarted() {
        mCardInternalState = new ContactlessTransactionStarted(this, mMppLite);
    }

    /**
     * Go to Contactless Transaction Completed State
     */
    void toContactlessTransactionCompleted() {
        mCardInternalState = new ContactlessTransactionCompleted(this, mMppLite);
    }

    /**
     * Go to the Contactless Transaction Completed State
     */
    void toRemotePaymentReadyState() {
        mCardInternalState = new RemotePaymentReadyState(this, mMppLite);
    }

    public final void setFirstTapListener(final ContactlessTransactionListener cardListener) {
        mFirstTapListener = new ContactlessTransactionListener() {
            @Override
            public void onContactlessReady() {
                if (cardListener == null) {
                    return;
                }
                cardListener.onContactlessReady();
            }

            @Override
            public void onContactlessTransactionCompleted(final ContactlessLog contactlessLog) {
                // Notify the cardholder validator that we are done
                mChValidator.notifyTransactionCompleted();
                toContactlessTransactionCompleted();
                if (cardListener == null) {
                    return;
                }
                cardListener.onContactlessTransactionCompleted(contactlessLog);
                contactlessLog.wipe();
            }

            @Override
            public void onContactlessTransactionAbort(final ContactlessLog contactlessLog) {
                // Notify the cardholder validator that we are done
                mChValidator.notifyTransactionCompleted();
                if (cardListener == null) {
                    return;
                }
                cardListener.onContactlessTransactionAbort(contactlessLog);
                contactlessLog.wipe();
            }
        };
    }

    /**
     * Utility function to create a dummy Contactless Transaction Listener that is used for the
     * first tap in case one is not provided
     */
    private ContactlessTransactionListener getDummyContactlessTransactionListener() {
        return new ContactlessTransactionListener() {
            @Override
            public void onContactlessReady() {
                // Intentionally no-op
            }

            @Override
            public void onContactlessTransactionCompleted(final ContactlessLog contactlessLog) {
                toContactlessTransactionCompleted();
            }

            @Override
            public void onContactlessTransactionAbort(final ContactlessLog contactlessLog) {
                // Intentionally no-op
            }
        };
    }
}