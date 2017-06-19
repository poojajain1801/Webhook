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
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * Interface to describe the common functions of the MCBP Card Internal states
 */
public interface CardInternalState {
    /**
     * Start a contactless payment and move the card to that state
     *
     * @param businessLogicTransactionInformation The Transaction Information as recorded by the
     *                                            business logic
     * @throws InvalidCardStateException
     * @throws McbpCryptoException
     * @throws InvalidInput
     * @throws LdeNotInitialized
     * @since 1.0.6a
     */
    void startContactlessPayment(
            final BusinessLogicTransactionInformation businessLogicTransactionInformation)
            throws InvalidCardStateException, McbpCryptoException, InvalidInput,
            LdeNotInitialized;

    /**
     * Stop an ongoing contactless transaction
     *
     * @since 1.0.6a
     */
    void stopContactLess() throws InvalidCardStateException;

    /**
     * Start the Remote Payment by initializing the MPP Lite
     *
     * @since 1.0.6a
     */
    void startRemotePayment() throws InvalidCardStateException, DsrpIncompatibleProfile;

    /**
     * Calculate the DSRP Transaction Record
     *
     * @param dsrpInputData The DSRP Input data that will be used for the cryptogram generation
     * @return The DSRP result data, which includes the DSRP Output Data
     * @throws InvalidCardStateException
     * @since 1.0.6a
     */
    DsrpResult getTransactionRecord(DsrpInputData dsrpInputData) throws InvalidCardStateException;

    /**
     * Process a C-APDU and return a R-APDU
     *
     * @param apdu byte array data for processApdu.
     * @return byte array of processApdu response.
     * @since 1.0.6a
     */
    ByteArray processApdu(final ByteArray apdu) throws InvalidCardStateException;

    /**
     * Utility function to process the NFC deactivation signal handled by the
     * HCE service
     *
     * @since 1.0.6a
     */
    void processOnDeactivated();
}
