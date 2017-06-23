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
import com.mastercard.mcbp.utils.exceptions.mpplite.InvalidState;

/**
 * <h3>Describes the state: STOPPED:</h3> In this state, the MppLite object does
 * not contain any Card Profile nor any Transaction Credentials. It is unable to
 * perform any Contactless or Remote Payment. The MppLite object is in state
 * STOPPED at the creation of the object, or upon call to method stop()
 * <p/>
 * <h4>Method Acceptance</h4>
 * <p/>
 * <pre>
 *  ******************************************
 *  initialize()                = OK
 *  ******************************************
 *  startRemotePayment()        = ERROR_STATE
 *  ******************************************
 *  createRemoteCryptogram()    = ERROR_STATE
 *  ******************************************
 *  startContactlessPayment()   = ERROR_STATE
 *  ******************************************
 *  stop()                      = OK
 *  ******************************************
 *  cancelPayment()             = ERROR_STATE
 *  ******************************************
 *  processAPDU()               = (SW12='6985')
 *  ******************************************
 * </pre>
 */

final class StoppedState implements MppLiteState {
    /**
     * MppLite State Context
     */
    private final MppLiteStateContext mMppLiteStateContext;

    /**
     * Constructor
     * @param mppLiteStateContext The MPP Lite State Context object
     * */
    public StoppedState(MppLiteStateContext mppLiteStateContext) {
        super();
        this.mMppLiteStateContext = mppLiteStateContext;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void initialize(final MppLiteModule profile) throws InvalidInput {
        if (profile == null) {
            throw new InvalidInput("Invalid Input Data");
        }

        // State transition matrix mandates that the next state is INITIALIZED
        mMppLiteStateContext.setInitializedState();
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
        throw new InvalidState("Invalid State for cancelPayment");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public TransactionOutput createRemoteCryptogram(final CryptogramInput input) {
        throw new InvalidState("Invalid State for createRemoteCryptogram");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public byte[] processApdu(final byte[] apdu) {
        return ResponseApduFactory.conditionsOfUseNotSatisfied();
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void stop() { /* Do Nothing */ }
}
