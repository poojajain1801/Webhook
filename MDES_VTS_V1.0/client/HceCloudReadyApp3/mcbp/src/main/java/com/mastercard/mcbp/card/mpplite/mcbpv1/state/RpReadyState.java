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
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.remotepayment.RemoteCryptogram;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.card.mobilekernel.CryptogramInput;
import com.mastercard.mcbp.card.mobilekernel.TransactionOutput;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mpplite.InvalidState;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;

/**
 * <h3>Describes the state: RP_READY:</h3> In this state, the MppLite object
 * contains a Card Profile compatible with Remote Payment, and has been
 * successfully armed with a set of Transaction Credentials. It is ready to
 * execute a Remote Payment transaction. The MppLite object arrives in state
 * RP_READY from state INITIALIZED, after a successful execution of method
 * startRemotePayment().
 * <p/>
 * <p/>
 * <h4>Method Acceptance</h4>
 * <p/>
 * <pre>
 *  ******************************************
 *  initialize()                = ERROR_STATE
 *  ******************************************
 *  startRemotePayment()        = ERROR_STATE
 *  ******************************************
 *  createRemoteCryptogram()    = OK
 *  ******************************************
 *  startContactlessPayment()   = ERROR_STATE
 *  ******************************************
 *  stop()                      = OK
 *  ******************************************
 *  cancelPayment()             = OK
 *  ******************************************
 *  processAPDU()               = (SW12='6985')
 *  ******************************************
 * </pre>
 */

final class RpReadyState implements MppLiteState {
    /**
     * The current MPP Lite state context
     * */
    private final MppLiteStateContext mMppLiteStateContext;

    /**
     * The Remote Payment context
     * */
    private final RemotePaymentContext mContext;
    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    public RpReadyState(final MppLiteStateContext mppLiteStateContext) {
        this.mMppLiteStateContext = mppLiteStateContext;

        this.mContext =
                new RemotePaymentContext(mMppLiteStateContext.getTransactionCredentialsManager(),
                                         mMppLiteStateContext.getChValidator(),
                                         mMppLiteStateContext.getConsentManager(),
                                         mMppLiteStateContext.getAdviceManager());
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void initialize(MppLiteModule profile) {
        throw new InvalidState("Invalid State for initialize");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void startRemotePayment() {
        throw new InvalidState("Invalid State for startRemotePayment");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void startContactLessPayment(final ContactlessTransactionListener listener,
                                        final BusinessLogicTransactionInformation trxInfo) {
        throw new InvalidState("Invalid State for startContactLessPayment");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void cancelPayment() {
        mMppLiteStateContext.setState(new InitializedState(mMppLiteStateContext));
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public TransactionOutput createRemoteCryptogram(final CryptogramInput input)
            throws McbpCryptoException, InvalidInput {
        if (input == null) {
            // REM 1.4
            cancelPayment();
            throw new InvalidInput("Invalid Input Data");
        }
        final MppLiteModule profile = mMppLiteStateContext.getProfile();
        final TransactionOutput output;
        try {
            final RemoteCryptogram cryptogram = new RemoteCryptogram(profile, input, mContext);
            output = cryptogram.build();
        } catch (final MppLiteException e) {
            mLogger.d(e.getMessage()); // We log the error and return null
            return null;
        } finally {
            // Either in case or success or failure we cancel the payment
            cancelPayment();
        }
        return output;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public byte[] processApdu(byte[] apdu) {
        return ResponseApduFactory.conditionsOfUseNotSatisfied();
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void stop() {
        // 1 call cancel payment
        cancelPayment();
        // 2 wipe profile
        MppLiteModule profile = mMppLiteStateContext.getProfile();
        if (profile != null) {
            // 2 call profile.wipe
            profile.wipe();
        }

        // Go to Stopped State
        mMppLiteStateContext.setStoppedState();
    }
}
