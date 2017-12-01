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
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless.Select;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLog;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless.ContactlessTransactionContext;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLogImpl;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.card.mobilekernel.CryptogramInput;
import com.mastercard.mcbp.card.mobilekernel.TransactionOutput;
import com.mastercard.mcbp.card.mpplite.apdu.CommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.Iso7816;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ComputeCcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GetProcessingOptionsCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ReadRecordCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import com.mastercard.mcbp.card.mpplite.apdu.emv.SelectCommandApdu;
import com.mastercard.mcbp.utils.DateUtils;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.ContactlessIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mpplite.InvalidState;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * <h3>Describes the state: CL_READY:</h3>
 * <p/>
 * In this state, the MppLite object contains a Card Profile compatible
 * with Contactless Payment, and it has been successfully armed with a set
 * of Transaction Credentials.
 */
final class ContactlessReadyState implements MppLiteState {

    /**
     * It is used to keep the context when moving from one state to another
     */
    private final MppLiteStateContext mMppLiteStateContext;

    /**
     * The contactless context. Used to keep track of the current transaction
     */
    private final ContactlessContext mContactlessContext;

    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    /***
     * Constructor for the Contactless Ready State. Transaction information and credentials must be
     * provided.
     *
     * @param mppLiteStateContext The MPP Lite State Context
     * @param listener            The transaction event listener
     * @param trxInfo             The transaction information such as amount and currency from
     *                            the Business Logic. This is relevant for a 2 taps transaction
     * @throws InvalidInput In case the input parameters are not valid
     */
    public ContactlessReadyState(final MppLiteStateContext mppLiteStateContext,
                                 final ContactlessTransactionListener listener,
                                 final BusinessLogicTransactionInformation trxInfo)
            throws InvalidInput {
        this.mMppLiteStateContext = mppLiteStateContext;

        final MppLiteModule profile = mMppLiteStateContext.getProfile();

        // 2 Check the compatibility of the Card Profile with Contactless
        // Payment:
        // if profile.getContactlessPaymentData() returns null, return
        // ERROR_INCOMPATIBLE_PROFILE
        if (profile.getContactlessPaymentData() == null) {
            throw new ContactlessIncompatibleProfile("The profile does not support contactless");
        }

        if (trxInfo == null) {
            throw new InvalidInput("Invalid input data");
        }

        final int currencyCode = trxInfo.getCurrencyCode();
        if (currencyCode < 0 || currencyCode > 999) {
            throw new InvalidInput("Invalid input data");
        }

        final long amount = trxInfo.getAmount();
        if (amount < 0 || amount > BusinessLogicTransactionInformation.MAX_AMOUNT) {
            throw new InvalidInput("Invalid input data");
        }
        // testing listener
        if (null == listener) {
            throw new InvalidInput("Invalid input data");
        }

        mContactlessContext =
                new ContactlessContext(profile,
                                       mMppLiteStateContext.getTransactionCredentialsManager(),
                                       mMppLiteStateContext.getChValidator(),
                                       mMppLiteStateContext.getConsentManager(),
                                       mMppLiteStateContext.getAdviceManager(),
                                       mMppLiteStateContext.getAdditionalPdolList(),
                                       mMppLiteStateContext.getAdditionalUdolList(),
                                       mMppLiteStateContext.isMaskMchipInAipForUsTransactions(),
                                       listener,
                                       trxInfo);

        // 7. Set the state machine to state CL_NOT_SELECTED;
        mContactlessContext.setContactlessNotSelectedState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void initialize(MppLiteModule profile) {
        throw new InvalidState("Invalid state (ContactlessReadyState) for initialize");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void startRemotePayment() {
        throw new InvalidState("Invalid state for startRemotePayment");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void startContactLessPayment(final ContactlessTransactionListener listener,
                                              final BusinessLogicTransactionInformation trxInfo) {
        throw new InvalidState("Invalid state for startContactLessPayment");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final TransactionOutput createRemoteCryptogram(CryptogramInput input) {
        throw new InvalidState("Invalid state createRemoteCryptogram");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] processApdu(byte[] apdu) {
        final ContactlessReadySubState state = mContactlessContext.getState();
        try {
            switch (CommandApdu.determineApduType(apdu)) {
                case SELECT:
                    return processSelect(new SelectCommandApdu(apdu));
                case GET_PROCESSING_OPTIONS:
                    final DolRequestList pdolList =
                            mContactlessContext.getTransactionContext().getPdolList();
                    return state.processGpo(new GetProcessingOptionsCommandApdu(apdu, pdolList));
                case READ_RECORD:
                    return state.processReadRecord(new ReadRecordCommandApdu(apdu));
                case GENERATE_AC:
                    try {
                        byte[] response = state.processGenerateAc(new GenerateAcCommandApdu(apdu));
                        mContactlessContext.requestListenerNotification();
                        return response;
                    } finally {
                        // Both in case of success or exception we need to go back to initialized
                        // and make sure we cancel the current payment
                        cancelPayment();
                    }
                case COMPUTE_CRYPTOGRAPHIC_CHECKSUM:
                    try {
                        final DolRequestList udolList =
                                mContactlessContext.getTransactionContext().getUdolList();
                        byte[] response =
                                state.processComputeCc(new ComputeCcCommandApdu(apdu, udolList));
                        mContactlessContext.requestListenerNotification();
                        return response;
                    } finally {
                        // Both in case of success or exception we need to go back to initialized
                        // and make sure we cancel the current payment
                        cancelPayment();
                    }

                default:
                    return ResponseApduFactory.instructionCodeNotSupported();
            }
        } catch (final MppLiteException e) {
            return e.getIso7816StatusWordApdu();
        } catch (final RuntimeException e) {
            // We log runtime exceptions and return 6F00 if this happens
            // However, this should never happen
            mLogger.d(e.getMessage());
            return ResponseApduFactory.of(Iso7816.SW_UNKNOWN);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void cancelPayment() {
        if (mContactlessContext != null) {
            final ContactlessTransactionContext trxContext =
                    mContactlessContext.getTransactionContext();

            final ContactlessTransactionListener listener =
                    mContactlessContext.getTransactionListener();

            if (!mContactlessContext.isNotificationRequested() && listener != null) {
                // Let's make sure we set ATC and Date into the transaction context

                if (trxContext.getAtc() == null) {
                    final byte[] atc = mContactlessContext.getTransactionCredentialsManager()
                                                          .getAtcForCancelPayment(
                                                                  TransactionCredentialsManager
                                                                          .Scope.CONTACTLESS);
                    trxContext.setAtc(ByteArray.of(atc));
                }

                if (trxContext.getTrxDate() == null) {
                    ByteArray trxDate = DateUtils.getTodayTransactionDate();
                    trxContext.setTrxDate(trxDate);
                }

                final ContactlessLog contactlessLog = ContactlessLogImpl.generic(trxContext);
                listener.onContactlessTransactionAbort(contactlessLog);
            }

            if (mContactlessContext.getState() != null) {
                mContactlessContext.getState().cancelPayment();
            }
        }
        // We first set the new state and then notify potential listeners
        if (mMppLiteStateContext != null) {
            mMppLiteStateContext.setInitializedState();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void stop() {
        // 1 call cancel payment
        cancelPayment();

        MppLiteModule profile = mMppLiteStateContext.getProfile();
        if (profile != null) {
            // 2 call profile.wipe
            profile.wipe();
        }
        mMppLiteStateContext.setStoppedState();
    }

    /**
     * Execute Process Select command
     *
     * @param apdu The SELECT Command APDU.
     */
    protected final byte[] processSelect(final SelectCommandApdu apdu) {
        final Select select =
                new Select(apdu, mMppLiteStateContext.getProfile(), mContactlessContext);
        return select.response();
    }
}
