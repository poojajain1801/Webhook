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
import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpResult;
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * The MCBP Card enters into the ContactlessTransactionCompleted state once a contactless
 * transaction has been successfully completed (e.g. Generate AC or Compute CC completed).
 * It exists from this state only when the phone is removed from the field
 */
class ContactlessTransactionCompleted extends GenericState {
    /***
     * Default constructor for the state. The context must be provided
     * @param cardContext The MCBP Card State context
     */
    public ContactlessTransactionCompleted(final CardContext cardContext,
                                           final MppLite mppLite) {
        super(cardContext, mppLite);
    }

    /**
     * {@inheritDoc}
     *
     * @param businessLogicTransactionInformation*/
    @Override
    public void startContactlessPayment(final BusinessLogicTransactionInformation
                                                    businessLogicTransactionInformation)
            throws InvalidCardStateException {
        throw new InvalidCardStateException("startContactlessPayment");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void stopContactLess() {
        // No operation, but we do not leave from this state until we get a disconnect from the
        // field
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startRemotePayment() throws InvalidCardStateException, DsrpIncompatibleProfile {
        throw new InvalidCardStateException("Invalid State for startRemotePayment");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public DsrpResult getTransactionRecord(final DsrpInputData dsrpInputData)
            throws InvalidCardStateException {
        throw new InvalidCardStateException("Invalid State for getTransactionRecord");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public ByteArray processApdu(final ByteArray apdu) throws InvalidCardStateException {
        return getMppLite().processApdu(apdu);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void processOnDeactivated() {
        toInitializedState(false);
    }
}
