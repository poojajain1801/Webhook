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
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.card.mobilekernel.CryptogramInput;
import com.mastercard.mcbp.card.mobilekernel.TransactionOutput;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mpplite.InvalidState;

/**
 * <h3>Describes the state: INITIALIZED:</h3> In this state, the MppLite object
 * contains a Card Profile compatible with Contactless and/or Remote Payment,
 * but does not contain any Transaction Credentials. It is unable to perform any
 * Contactless or Remote Payment. The MppLite object arrives in state
 * INITIALIZED:
 * <p/>
 * <pre>
 * -From state STOPPED, after a successful execution of method initialize() that has loaded the
 * Card
 * Profile,
 * -From state RP_READY, after execution of method createRemoteCryptogram() which consumes the
 * Transaction Credentials,
 * -From state CL_INITIATED, after execution of method processAPDU(GENERATE AC/COMPUTE
 * CRYPTO.CHECKSUM) which consumes the Transaction Credentials,
 * -From states RP_READY, CL_NOT_SELECTED, CL_SELECTED and CL_INITIATED (see below), after
 * execution
 * of method #cancelPayment()
 * </pre>
 * <p/>
 * <p/>
 * <h4>Method Acceptance</h4>
 * <p/>
 * <pre>
 *  ******************************************
 *  initialize()                = ERROR_STATE
 *  ******************************************
 *  startRemotePayment()        = OK
 *  ******************************************
 *  createRemoteCryptogram()    = ERROR_STATE
 *  ******************************************
 *  startContactlessPayment()   = OK
 *  ******************************************
 *  stop()                      = OK
 *  ******************************************
 *  cancelPayment()             = ERROR_STATE
 *  ******************************************
 *  processAPDU()               = (SW12='6985')
 *  ******************************************
 * </pre>
 */
final class InitializedState implements MppLiteState {

    /**
     * The MPP Lite State Context
     * */
    private final MppLiteStateContext mMppLiteStateContext;

    /**
     * Constructor
     * @param mppLiteStateContext The MPP Lite Context must be provided
     * */
    public InitializedState(MppLiteStateContext mppLiteStateContext) {
        this.mMppLiteStateContext = mppLiteStateContext;
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public void initialize(MppLiteModule profile) {
        throw new InvalidState("Invalid state to initialize the MPP Lite");
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final void startRemotePayment()
            throws DsrpIncompatibleProfile {

        MppLiteModule profile = mMppLiteStateContext.getProfile();

        // Check the compatibility of the Card Profile with Remote Payment:
        if (profile.getRemotePaymentData() == null) {
            throw new DsrpIncompatibleProfile("The profile does not support DSRP");
        }

        // Copy input parameters to attributes of MppLite:
        // Associate attribute credentials from MppLite object with input
        // parameter trxCredentials;

        final RpReadyState rpReadyState = new RpReadyState(mMppLiteStateContext);

        // set state to RP_READY
        mMppLiteStateContext.setState(rpReadyState);
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final void startContactLessPayment(final ContactlessTransactionListener listener,
                                              final BusinessLogicTransactionInformation trxInfo)
            throws InvalidInput {

        // The Contactless Ready State is responsible to check the input parameters and whether
        // they are valid
        final ContactlessReadyState contactlessReadyState =
                new ContactlessReadyState(mMppLiteStateContext, listener, trxInfo);

        mMppLiteStateContext.setState(contactlessReadyState);

        // Notify that we are ready for contactless. We need to notify here after we are sure the
        // new state has been set.
        listener.onContactlessReady();
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final void cancelPayment() {
        throw new InvalidState("Invalid state for cancelPayment");
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final TransactionOutput createRemoteCryptogram(CryptogramInput input) {
        throw new InvalidState("Invalid state for createRemoteCryptogram");
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final byte[] processApdu(byte[] apdu) {
        return ResponseApduFactory.conditionsOfUseNotSatisfied();
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final void stop() {
        MppLiteModule profile = mMppLiteStateContext.getProfile();

        // 1 call cancel payment
        cancelPayment();

        // 3 Wipe profile
        profile.wipe();

        mMppLiteStateContext.setStoppedState();
    }
}
