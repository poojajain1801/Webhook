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
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * In this state the MPP Lite has been initialize to handle a contactless transaction
 */
class ContactlessReadyState extends GenericState {
    /***
     * Default constructor for the state. The context must be provided
     * @param cardContext The MCBP Card State context
     */
    public ContactlessReadyState(final CardContext cardContext,
                                 final MppLite mppLite) {
        super(cardContext, mppLite);
    }

    /**
     * {@inheritDoc}
     *
     * @param businessLogicTransactionInformation The Transaction Information as recorded by the
     *                                            business logic
     * */
    @Override
    public void startContactlessPayment(
            final BusinessLogicTransactionInformation businessLogicTransactionInformation)
            throws InvalidCardStateException, McbpCryptoException, InvalidInput, LdeNotInitialized {
        throw new InvalidCardStateException("Invalid state for startContactlessPayment");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void stopContactLess() {
        toInitializedState(true);
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
        toContactlessTransactionStarted();
        return getCardContext().getCurrentState().processApdu(apdu);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void processOnDeactivated() {
        notifyTransactionFailed();
        toInitializedState(true);
    }
}
