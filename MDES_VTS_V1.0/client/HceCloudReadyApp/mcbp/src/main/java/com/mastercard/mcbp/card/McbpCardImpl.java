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

package com.mastercard.mcbp.card;

import com.mastercard.mcbp.businesslogic.ExecutionEnvironment;
import com.mastercard.mcbp.card.cvm.ChValidator;
import com.mastercard.mcbp.card.cvm.ChValidatorListener;
import com.mastercard.mcbp.card.cvm.PinValidator;
import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpResult;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentials;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.RemotePaymentListener;
import com.mastercard.mcbp.card.state.CardContext;
import com.mastercard.mcbp.lde.containers.DigitizedCardTemplate;
import com.mastercard.mcbp.lde.data.SessionKey;
import com.mastercard.mcbp.lde.services.LdeMcbpCardService;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.transactiondecisionmanager.ConsentManager;
import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionRange;
import com.mastercard.mcbp.utils.exceptions.cardholdervalidator.CardholderValidationNotSuccessful;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidDigitizedCardProfile;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.generic.InternalError;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.lde.SessionKeysNotAvailable;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.ContactlessIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.payment.cld.Cld;
import com.mastercard.mobile_api.utils.Utils;

/**
 * Represents a digitized card. McbpCard is a sub-module of the Business Logic
 * that is responsible for the management of a Digitized Card. A Digitized Card
 * is represented as an object of McbpCard module that is managed by the Mobile
 * Payment Application. The Mobile Payment Application contains a list of
 * MCBPCards. This list is created, populated and maintained by the Lde. A
 * McbpCard may call upon other helper sub-modules such as ChValidator. A
 * ChValidator is a helper object to which a McbpCard can delegate the
 * cardholder validation for a Digitized Card.
 */
public final class McbpCardImpl implements McbpCard {
    /**
     * McbpLogger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    /**
     * Digitized card id.
     */
    private final String mDigitizedCardId;

    /**
     * Digitized Card Template
     */
    private final DigitizedCardTemplate mDigitizedCardTemplate;

    /**
     * remote payment  supported flag.
     */
    private final boolean mRpSupported;

    /**
     * contact-less payment supported flag.
     */
    private final boolean mClSupported;

    /***
     * Credentials to be used for the next transaction. They are available after the Mobile PIN
     * is entered
     */
    private TransactionCredentials mTransactionCredentials = null;

    /**
     * Card holder Validator.
     */
    private final ChValidator mChValidator;

    /**
     * Lde McbpCard Service
     */
    private final LdeMcbpCardService mLdeMcbpCardService;

    /**
     * The Card Context which is used by different states to communicate
     */
    private CardContext mCardContext;

    /***
     * Default MCBP Card constructor
     *
     * @param digitizedCardTemplate The Card Profile
     * @param ldeMcbpCardService    The LDE Card Service Interface
     * @throws InvalidDigitizedCardProfile
     */
    public McbpCardImpl(final DigitizedCardTemplate digitizedCardTemplate,
                        final LdeMcbpCardService ldeMcbpCardService)
            throws InvalidDigitizedCardProfile {
        this.mDigitizedCardTemplate = digitizedCardTemplate;
        this.mDigitizedCardId = digitizedCardTemplate.getDigitizedCardId();
        this.mClSupported = digitizedCardTemplate.isClSupported();
        this.mRpSupported = digitizedCardTemplate.isRpSupported();
        this.mLdeMcbpCardService = ldeMcbpCardService;

        if (digitizedCardTemplate.getCvm().equalsIgnoreCase(McbpCard.CVM_DEVICE_MOBILE_PIN)) {
            this.mChValidator = new PinValidator();
        } else {
            this.mChValidator = null;
        }
        this.mCardContext = new CardContext(mDigitizedCardId,
                                            mLdeMcbpCardService,
                                            mDigitizedCardTemplate.getDcCpMpp(),
                                            getTransactionCredentialsManager(),
                                            mChValidator,
                                            getConsentManager());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isRpSupported() {
        return mRpSupported;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isClSupported() {
        return mClSupported;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDigitizedCardId() {
        return mDigitizedCardId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Cld getCardLayout() {
        return mDigitizedCardTemplate.getCld();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getCardMetadata() {
        return mDigitizedCardTemplate.getCardMetadata();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setCardMetadata(String metadata) {
        mDigitizedCardTemplate.setCardMetadata(metadata);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getMaximumPinTry() {
        return mDigitizedCardTemplate.getMaximumPinTry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getCvmResetTimeOut() {
        return mDigitizedCardTemplate.getCvmResetTimeout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getDualTapTimeOut() {
        return mDigitizedCardTemplate.getDualTapResetTimeout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int numberPaymentsLeft() {
        int result = 0;
        try {
            result = this.mLdeMcbpCardService.getSingleUseKeyCount(this.getDigitizedCardId());
        } catch (LdeNotInitialized | InvalidInput e) {
            mLogger.d(e.getMessage());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void activateContactless(final CardListener cardListener) throws InvalidInput {
        if (!isClSupported()) {
            throw new ContactlessIncompatibleProfile("Contactless is not supported");
        }

        if ((mLdeMcbpCardService == null) || cardListener == null || mChValidator == null) {
            throw new InternalError("Unable to activate contactless");
        }

        mCardContext.setCardListener(cardListener);

        // initializes ChValidator with listener
        PinValidator pinValidator = (PinValidator) mChValidator;
        pinValidator.setPinListener(cardListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void startContactless(final BusinessLogicTransactionInformation trxInfo) throws
            McbpCryptoException, InvalidInput, LdeNotInitialized, SessionKeysNotAvailable {
        if ((mLdeMcbpCardService == null) || mChValidator == null) {
            throw new InternalError("Unable to start contactless");
        }

        // Authenticate user and start the contactless on the MPP Lite when done
        authenticateUser(trxInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void stopContactLess() throws InvalidCardStateException {
        mCardContext.stopContactLess();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final void activateRemotePayment(final RemotePaymentListener listener,
                                            final ExecutionEnvironment envData)
            throws McbpCryptoException, InvalidInput, LdeNotInitialized,
            SessionKeysNotAvailable, DsrpIncompatibleProfile {

        if (!mRpSupported) {
            throw new DsrpIncompatibleProfile("DSRP not supported");
        }

        if (mLdeMcbpCardService == null) {
            throw new LdeNotInitialized("Lde not initialized");
        }

        if (mChValidator == null) {
            throw new CardholderValidationNotSuccessful("No Cardholder verification method");
        }

        // initializes ChValidator with listener
        final PinValidator pinValidator = (PinValidator) mChValidator;
        pinValidator.setPinListener(listener);

        final SessionKey suk =
                mLdeMcbpCardService.getNextRemotePaymentSessionKeys(getDigitizedCardId());

        final ChValidatorListener chValidatorListener = new ChValidatorListener() {

            @Override
            public void onSessionKeyReady(final ByteArray sessionKey) {

                try {
                    mLdeMcbpCardService.wipeDcSuk(getDigitizedCardId(), suk.getId());
                } catch (LdeNotInitialized | InvalidInput e) {
                    // Ignoring
                    mLogger.d(e.getMessage());
                }

                try {
                    // We temporarily set it to discarded, we will set back later if used
                    mLdeMcbpCardService.insertOrUpdateTransactionCredentialStatus(
                            mDigitizedCardId,
                            ByteArray.of(suk.getAtc()),
                            TransactionCredentialStatus.Status.UNUSED_DISCARDED);

                } catch (InvalidInput invalidInput) {
                    mLogger.d(invalidInput.getMessage());
                }

                // Create the credentials since we have authenticated the user
                mTransactionCredentials = new TransactionCredentials(sessionKey,
                                                                     suk.getSessionKeyMd(),
                                                                     suk.getAtc(),
                                                                     suk.getIdn());
                try {
                    mCardContext.startRemotePayment();
                } catch (InvalidCardStateException e) {
                    mLogger.d(e.getMessage());
                }
                listener.onRPReady();
            }
        };

        // Authenticate user
        mChValidator.authenticate(suk.getSessionKeyUmd(), chValidatorListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setFirstTapListener(final ContactlessTransactionListener cardListener) {
        mCardContext.setFirstTapListener(cardListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final DsrpResult getTransactionRecord(
            final DsrpInputData dsrpData) throws InvalidCardStateException {
        return mCardContext.getTransactionRecord(dsrpData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isInitialized() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] processApdu(final byte[] apdu) {
        final ByteArray commandApdu = ByteArray.of(apdu);
        try {
            final byte[] responseApdu = mCardContext.processApdu(commandApdu).getBytes();
            return responseApdu;
        } catch (InvalidCardStateException e) {
            mLogger.d(e.getMessage());
        } finally {
            Utils.clearByteArray(commandApdu);
        }
        // If we are here that means something went terribly wrong and we can not continue with
        // transaction.
        return new byte[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void processOnDeactivated() {
        mLogger.d("processOnDeactivate received");
        mCardContext.processOnDeactivated();
    }

    /**
     * Authenticate the user and once completed invokes the start contactless on the MPP Lite object
     *
     * @param trxInfo Transaction Information
     * @throws IllegalArgumentException if something goes wrong
     * @since 1.0.3
     */
    private void authenticateUser(final BusinessLogicTransactionInformation trxInfo) {
        final SessionKey suk;
        try {
            suk = mLdeMcbpCardService.getNextContactlessSessionKeys(mDigitizedCardId);
        } catch (final InvalidInput | SessionKeysNotAvailable |
                McbpCryptoException | LdeNotInitialized e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        mChValidator.authenticate(suk.getSessionKeyUmd(), new ChValidatorListener() {

            @Override
            public void onSessionKeyReady(final ByteArray sessionKey) {

                // remove suk
                try {
                    mLdeMcbpCardService.wipeDcSuk(mDigitizedCardId, suk.getId());
                } catch (final LdeNotInitialized | InvalidInput e) {
                    //Shouldn't reach here.
                    // If we got here it is likely we can delete the keys.
                    // FIXME: This check can be avoided when we redesign the DB
                    mLogger.d(e.getMessage());
                }

                try {
                    mLdeMcbpCardService.insertOrUpdateTransactionCredentialStatus(
                            mDigitizedCardId,
                            ByteArray.of(suk.getAtc()),
                            TransactionCredentialStatus.Status.UNUSED_DISCARDED);

                } catch (InvalidInput invalidInput) {
                    mLogger.d(invalidInput.getMessage());
                }

                // Create the credentials since we have authenticated the user
                mTransactionCredentials = new TransactionCredentials(sessionKey,
                                                                     suk.getSessionKeyMd(),
                                                                     suk.getAtc(),
                                                                     suk.getIdn());

                try {
                    mCardContext.startContactlessPayment(trxInfo);
                } catch (final InvalidInput | McbpCryptoException e) {
                    throw new IllegalArgumentException(e.getMessage());
                } catch (InvalidCardStateException e) {
                    mLogger.d(e.getMessage());
                }
            }
        });
    }

    /***
     * Utility function to generate a valid call back for the Transaction Credentials Manager
     *
     * @return The Transaction Credentials Manager
     */
    private TransactionCredentialsManager getTransactionCredentialsManager() {
        return new TransactionCredentialsManager() {
            @Override
            public TransactionCredentials getValidUmdAndMdCredentialsFor(
                    final Scope scope) {
                switch (scope) {
                    default:
                        return mTransactionCredentials;
                }
            }

            @Override
            public TransactionCredentials getValidMdCredentialsFor(final Scope scope) {
                switch (scope) {
                    default:
                        return mTransactionCredentials;
                }
            }

            @Override
            public TransactionCredentials getRandomCredentials() {
                // Return a Random Transaction Credentials
                return new TransactionCredentials();
            }

            @Override
            public boolean hasValidCredentialsFor(final Scope scope) {
                switch (scope) {
                    default:
                        try {
                            //when processing the last SUK, the database actually has 0 SUK left,
                            // because the SUK is already pulled out of DB when transaction is
                            // initiated
                            //handle that case
                            //When we actually have 0 SUK, the MPPLite processing will
                            // decline the transaction because SUK is actually not available.
                            return mLdeMcbpCardService.getSingleUseKeyCount(mDigitizedCardId) >= 0;
                        } catch (InvalidInput invalidInput) {
                            mLogger.d(invalidInput.getMessage());
                            return false;
                        }
                }
            }

            @Override
            public byte[] getAtcForCancelPayment(final Scope scope) {
                // in future versions of SDK the transaction manager must use random credentials
                // if nobody has taken Umd credentials from it, indicating that the Suk is unused
                switch (scope) {
                    default:
                        ByteArray atc =
                                (mTransactionCredentials != null) ? mTransactionCredentials.getAtc()
                                                                  : getRandomCredentials().getAtc();
                        return atc.getBytes();
                }
            }

            @Override
            public boolean areUmdCredentialsSubjectToCvmFor(final TransactionRange transactionRange,
                                                            final Scope scope) {
                // Always true for Mobile PIN. // TODO: this should link to the CVM being used
                return true;
            }
        };
    }

    /***
     * Utility function to generate a valid call back for the Consent Manager
     *
     * @return The Consent Manager for this card
     */
    private ConsentManager getConsentManager() {
        return new ConsentManager() {
            @Override
            public boolean isConsentGiven() {
                // TODO: Implement the logic to check whether the screen is ON
                return true;
            }
        };
    }
}
