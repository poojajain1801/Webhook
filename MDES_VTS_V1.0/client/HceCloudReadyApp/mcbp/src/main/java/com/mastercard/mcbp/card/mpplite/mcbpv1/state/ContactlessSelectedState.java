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

import com.mastercard.mcbp.card.mpplite.apdu.emv.ComputeCcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GetProcessingOptionsCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ReadRecordCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless.GetProcessingOptions;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless.ReadRecord;

/**
 * <h3>Describes the state: CL_SELECTED:</h3> When the MppLite object is in this
 * state, the contactless payment terminal has already started the Contactless
 * transaction by selecting the payment application. The MppLite object arrives
 * in state CL_SELECTED from state CL_NOT_SELECTED, after a successful execution
 * of method processApdu(SELECT Aid) or processApdu(SELECT AID_ALTERNATE).
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
 *  createRemoteCryptogram()    = ERROR_STATE
 *  ******************************************
 *  startContactlessPayment()   = ERROR_STATE
 *  ******************************************
 *  stop()                      = OK
 *  ******************************************
 *  cancelPayment()             = OK
 *  ******************************************
 *  processAPDU()               = R-APDU as processed by the MPP Lite
 *  ******************************************
 * </pre>
 */

final class ContactlessSelectedState implements ContactlessReadySubState {
    /**
     * The Contactless Context
     */
    private ContactlessContext mContext;

    /**
     * Default constructor
     *
     * @param contactlessContext The Contactless Context to allow the state to operate properly
     */
    public ContactlessSelectedState(final ContactlessContext contactlessContext) {
        this.mContext = contactlessContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] processComputeCc(final ComputeCcCommandApdu apdu) {
        return ResponseApduFactory.conditionsOfUseNotSatisfied();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] processReadRecord(final ReadRecordCommandApdu apdu) {
        return ReadRecord.of(apdu, mContext).getResponse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] processGenerateAc(final GenerateAcCommandApdu apdu) {
        return ResponseApduFactory.conditionsOfUseNotSatisfied();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] processGpo(final GetProcessingOptionsCommandApdu apdu) {
        return GetProcessingOptions.response(apdu, mContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void cancelPayment() {
        if (mContext != null) {
            // Wipe the context as we have stored the important information into the log and
            // this state will soon be deleted
            mContext.wipe();
        }
    }
}
