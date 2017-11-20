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

package com.mastercard.mcbp.card.mpplite;

import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.BusinessLogicTransactionInformation;
import com.mastercard.mcbp.card.mobilekernel.CryptogramInput;
import com.mastercard.mcbp.card.mobilekernel.TransactionOutput;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * MppLite is software object within the core of Mobile Payment Application
 * (MPA) emulating a Secure Element based MasterCard M/Chip Mobile payment
 * application. In this release of the specification, MppLite supports proximity
 * contactless payments as well as Digital Secure Remote Payment (DSRP) online
 * commerce transactions. Future releases of MPA could expand the role of
 * MppLite for additional services. MppLite is an adaptation of MasterCard
 * M/Chip Mobile Payment Application for the Mobile Payment Application
 * environment, thus some restrictions apply compared to a Secure Element based
 * mobile payment application. This includes, but is not limited to, systematic
 * online authorization during the transaction.<br>
 * <br>
 */
public interface MppLite {
    /**
     * This methods prepares the MPP Lite for a DSRP (remote payment) transaction.
     *
     * @since 1.0.6a
     */
    void startRemotePayment() throws DsrpIncompatibleProfile;

    /**
     * This methods load prepares the MPP Lite to start a contactless transaction
     * The listener for that transaction and the Transaction Info (from business logic), if any
     * can be provided
     *
     * @param listener Instance of ContactlessTransactionListener
     * @param trxInfo  Transaction Information from the Business Logic (for 2 taps scenario)
     * @throws MppLiteException
     * @throws InvalidInput
     * @since 1.0.6a
     */
    void startContactLessPayment(final ContactlessTransactionListener listener,
                                 final BusinessLogicTransactionInformation trxInfo)
            throws MppLiteException, InvalidInput;

    /**
     * Cancels the effect of a prior call to startRemotePayment() or
     * startContactlessPayment(). It deletes the transaction credentials, but
     * the Card Profile is unaltered.
     *
     * @since 1.0.0
     */
    void cancelPayment() throws MppLiteException;

    /**
     * Based on the transaction context provided in input, this method returns
     * an Application Cryptogram as well as all the chip data elements necessary
     * to build a DE55 or UCAF authorization message.
     *
     * @param input CryptogramInput
     * @return TransactionOutput if successful, null in case something went wrong
     * @since 1.0.0
     */
    TransactionOutput createRemoteCryptogram(final CryptogramInput input);

    /**
     * Deletes the transaction credentials if any, and erases the Card Profile
     * from MppLite.
     *
     * @since 1.0.0
     */
    void stop();

    /**
     * Processes incoming command APDUs received from the Contactless reader,
     * and returns response APDUs
     *
     * @param apdu byte array of incoming command APDUs received from the Contactless reader
     * @return The Response APDU
     * @since 1.0.0
     */
    ByteArray processApdu(final ByteArray apdu);
}
