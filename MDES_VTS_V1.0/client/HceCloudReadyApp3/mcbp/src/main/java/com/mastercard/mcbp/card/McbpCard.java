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

package com.mastercard.mcbp.card;

import com.mastercard.mcbp.businesslogic.ExecutionEnvironment;
import com.mastercard.mcbp.card.mpplite.*;
import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpResult;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentials;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.RemotePaymentListener;
import com.mastercard.mcbp.utils.exceptions.cardholdervalidator.CardholderValidationNotSuccessful;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.lde.SessionKeysNotAvailable;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.ContactlessIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mobile_api.payment.cld.Cld;

/**
 * Represents a digitized card. McbpCard is a sub-module of the Business Logic
 * that is responsible for the management of a Digitized Card. A Digitized Card
 * is represented as an object of McbpCard module that is managed by the Mobile
 * Payment Application. The Mobile Payment Application contains a list of
 * MCBPCards. This list is created, populated and maintained by the Lde. A
 * McbpCard may call upon other helper sub-modules such as ChValidator. A
 * ChValidator is a helper object to which a McbpCard can delegate the
 * cardholder validation for a Digitized Card.<br>
 * <br>
 */
public interface McbpCard {

    /**
     * According to MCBP 1.0 Specification only allowed CVM method is DEVICE_MOBILE_PIN
     */
    String CVM_DEVICE_MOBILE_PIN = "DEVICE_MOBILE_PIN";

    /**
     * Get card profile id  for the card
     *
     * @return value of card profile id.
     * @since 1.0.0
     */
    String getDigitizedCardId();

    /**
     * get card layout description.
     *
     * @return Instance of Cld.
     * @since 1.0.0
     *
     * @deprecated We encourage using Card Metadata to store card specific information.
     */
    @Deprecated
    Cld getCardLayout();

    /**
     * Get the Card metadata information
     *
     * (deserialization is responsibility of the application level)
     *
     * @return The Card metadata information as String
     * @since 1.0.2
     * */
    String getCardMetadata();

    /**
     * Set the Card Metadata that can be set by the application layer
     *
     * (serialization is responsibility of the application level)
     *
     * @param metadata The Card Metadata as String
     * @since 1.0.2
     * */
     void setCardMetadata(String metadata);

    /**
     * Get the maximum Pin Try (if applicable)
     *
     * @return the Maximum Number of Mobile PIN tries
     * @since 1.0.0
     * */
    int getMaximumPinTry();

    /**
     * Get Cardholder Verification Method Reset Timeout value.
     *
     * @return The value of the Cardholder Verification Method Reset Timeout
     * @since 1.0.0
     */
    int getCvmResetTimeOut();

    /**
     * Get dual tap timeout value.
     *
     * @return The value of the dual tap timeout
     * @since 1.0.0
     */
    int getDualTapTimeOut();

    /**
     * Get number of payment left for card.
     *
     * @return the number of payments left for this card
     * @since 1.0.0
     */
    int numberPaymentsLeft();

    /**
     *  Check for Contact-less payment supported or not.
     *
     * @return True if the card supports contactless payments
     * @since 1.0.0
     */
    boolean isClSupported();

    /**
     * Check for Remote payment supported or not.
     *
     * @return True if the card supports digital secure remote payments
     * @since 1.0.0
     */
    boolean isRpSupported();

    /**
     * activates the card before a contactless transaction. It loads the card
     * profile
     *
     * @param cardListener Instance of CardListener
     * @since 1.0.0
     */
    void activateContactless(final CardListener cardListener)
            throws ContactlessIncompatibleProfile, InvalidInput;

    /**
     * Enables the associated MppLite module to perform a contactless
     * transaction Session keys obtained from the Lde will be used
     *
     * @param businessLogicTransactionInformation The Transaction Information as recorded by the
     *                                            business logic
     * @since 1.0.0
     */
    void startContactless(
            final BusinessLogicTransactionInformation businessLogicTransactionInformation)
            throws McbpCryptoException, InvalidInput, LdeNotInitialized, SessionKeysNotAvailable;


    /***
     * Set the Listener for the First Tap. If none is set the Card will not notify the completion
     * of the first tap (e.g. two taps scenario not supported by the UI application
     *
     * @param cardListener The Listener the card will call after the execution of the first tap
     *
     * @since 1.0.3
     */
    void setFirstTapListener(final ContactlessTransactionListener cardListener);

    /**
     * Clears transaction credentials from the associated MppLite which should
     * generate declined transactions if used.
     * @since 1.0.0
     */
    void stopContactLess() throws InvalidCardStateException;

    /**
     * Creates and initializes a MppLite module to be ready for subsequent call to execute a remote
     * payment cryptogram generation
     *
     * @param listener Instance of RemotePaymentListener
     * @param envData Instance of ExecutionEnvironment
     * @since 1.0.0
     */
    void activateRemotePayment(final RemotePaymentListener listener,
                               final ExecutionEnvironment envData)
            throws DsrpIncompatibleProfile, LdeNotInitialized, CardholderValidationNotSuccessful,
            McbpCryptoException, InvalidInput, SessionKeysNotAvailable;

    /**
     * Processes input online commerce transaction data and generates an online
     * commerce authorization transaction data.
     *
     * @param dsrpData Instance of DsrpInputData
     *
     * @return DsrpResult
     * @since 1.0.0
     */
    DsrpResult getTransactionRecord(DsrpInputData dsrpData) throws InvalidCardStateException;

    /**
     * Check whether the Card has been initialized
     *
     * @return if card initialized then return true otherwise false
     * @since 1.0.0
     */
    boolean isInitialized();

    /**
     * Process a C-APDU and return a R-APDU
     *
     * @param apdu byte array data for processApdu.
     *
     * @return byte array of processApdu response.
     * @since 1.0.3
     */
    byte[] processApdu(final byte[] apdu);

    /**
     * Utility function to process the NFC deactivation signal handled by the
     * HCE service
     * @since 1.0.0
     */
    void processOnDeactivated();
}
