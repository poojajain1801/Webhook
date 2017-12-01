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
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.card.mobilekernel.CryptogramInput;
import com.mastercard.mcbp.card.mobilekernel.TransactionOutput;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;

/**
 * Generic Interface representing MppLite States.<br>
 * <br>
 */

public interface MppLiteState {

    /**
     * This method enables to load a Card Profile into the freshly created
     * MppLite object. Loading a Card Profile is a prerequisite to any payment
     * transaction.
     *
     * @param profile Instance of MppLiteModule
     * @since 1.0.0
     */
    void initialize(MppLiteModule profile) throws InvalidInput;

    /**
     * This event will occur on start remote payment.
     *
     * @since 1.0.6a
     */
    void startRemotePayment() throws DsrpIncompatibleProfile;

    /**
     * This event will occur on start contact-less  payment.
     *
     * @param listener The contactless transaction listener to notify the outcome
     * @param trxInfo  Business Logic information for this transaction
     * @since 1.0.6a
     */
    void startContactLessPayment(final ContactlessTransactionListener listener,
                                 final BusinessLogicTransactionInformation trxInfo)
            throws InvalidInput;

    /**
     * This event will occur on cancel payment.
     *
     * @since 1.0.0
     */
    void cancelPayment();

    /**
     * Based on the transaction context provided in input, this method returns
     * an Application Cryptogram as well as all the chip data elements necessary
     * to build a DE55 or UCAF authorization message.
     *
     * @param input CryptogramInput
     * @return Instance of TransactionOutput
     * @since 1.0.0
     */
    TransactionOutput createRemoteCryptogram(CryptogramInput input) throws McbpCryptoException,
            InvalidInput;

    /**
     * Processes incoming command APDUs received from the Contactless reader,
     * and returns response APDUs
     *
     * @param apdu byte array of incoming command APDUs received from the Contactless reader
     * @since 1.0.0
     */
    byte[] processApdu(byte[] apdu);

    /**
     * Deletes the transaction credentials if any, and erases the Card Profile
     * from MppLite.
     *
     * @since 1.0.0
     */
    void stop();
}
