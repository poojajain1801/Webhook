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
package com.mastercard.mcbp.lde;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.profile.DigitizedCardProfile;
import com.mastercard.mcbp.card.profile.McbpDigitizedCardProfileWrapper;
import com.mastercard.mcbp.card.profile.PinState;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.remotemanagement.WalletState;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeAlreadyInitialized;
import com.mastercard.mcbp.utils.exceptions.lde.LdeCheckedException;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionLoggingError;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionStorageLimitReach;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DuplicateMcbpCard;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.List;
import java.util.Map;

interface McbpDataBase {
    /**
     * Max number of transaction to be stored in database according to MCBP MPA FD Specification.
     */
    int MAX_NO_OF_TX_IN_DB = 10;

    /**
     * Get the Current MPP Lite implementation to be used when creating a new card
     *
     * @return "java" if the Java MPP Lite implementation has to be used, "native" for the C++
     * implementation using Java Native Interface
     */
    String getMppLiteType();

    /**
     * Retrieve all single use key of given card id.
     *
     * @param digitizedCardId card id.
     * @return List of single use keys.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    List<SingleUseKey> getAllSingleUseKeys(String digitizedCardId) throws
            McbpCryptoException, InvalidInput;

    /**
     * Get token unique reference for given card id.
     *
     * @param digitizedCardId digitize card Id.
     * @return Token unique reference.
     */
    String getTokenUniqueReferenceFromCardId(String digitizedCardId) throws InvalidInput;

    /**
     * Returns Lde Initialization STATE
     *
     * @return the Lde State
     */
    LdeState getLdeState();

    /**
     * Initializes Lde with Initialization parameter send from the CMS. Sets the
     * Lde state to {@link LdeState#INITIALIZED}
     *
     * @param initParams instance of LdeInitParams.
     */
    void initializeLde(LdeInitParams initParams) throws McbpCryptoException, InvalidInput,
            LdeAlreadyInitialized;

    /**
     * Update the state of wallet
     *
     * @param walletState Wallet State
     */
    void updateWalletState(final String cmsMpaId, WalletState walletState) throws InvalidInput;

    /**
     * Get the state of wallet
     *
     * @return WalletState
     */
    WalletState getWalletState();

    /**
     * Un-register previously registered user.
     * Clear all the keys from database
     */
    void unregister();

    /**
     * Returns number of cards provisioned
     *
     * @return The number of cards that have been provisioned
     */
    long getNumberOfCardsProvisioned();

    /**
     * Wipes all Wallet data
     */
    void resetMpaToInstalledState();

    /**
     * Get a specific mobile key. Future release may simplify this API as only one set of mobile
     * key set IDs will be supported
     *
     * @return The mobile key for a specific mobile key set id and digitized card id
     */
    ByteArray getMobileKey(String mobileKeySetId, String digitizedCardId, String type)
            throws InvalidInput, McbpCryptoException;

    /**
     * Insert token unique reference and it's corresponding card id.
     *
     * @param tokenUniqueReference Token unique reference.
     * @param digitizedCardId      Card Id.
     * @return Row ID of the newly inserted row, or -1 if an error occurred
     * @throws InvalidInput
     */
    long insertTokenUniqueReference(String tokenUniqueReference, String digitizedCardId)
            throws InvalidInput;

    /**
     * Provisions a Card profile as sent by the CMS The Card profile will be set
     * to {@link ProfileState#UNINITIALIZED}
     * The card profile will be activated when the
     * {@link #activateProfile(String)} method is call
     *
     * @param cardProfile Card Profile as McbpDigitizedCardProfileWrapper.
     */
    void provisionDigitizedCardProfile(McbpDigitizedCardProfileWrapper cardProfile)
            throws McbpCryptoException, InvalidInput, DuplicateMcbpCard;

    /**
     * Activates a card profile. The card profile state is set to
     * {@link ProfileState#INITIALIZED}
     *
     * @param digitizedCardId 17 bytes long identifier.
     */
    void activateProfile(String digitizedCardId) throws InvalidInput;

    /**
     * @param digitizedCardId Card identifier of card profile to suspend or un-suspend.
     * @throws InvalidInput
     */
    void suspendCardProfile(String digitizedCardId) throws InvalidInput;

    /**
     * Provision a suk
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @param suk             Instance of SingleUseKey.
     */
    void provisionSingleUseKey(String digitizedCardId, SingleUseKey suk) throws
            McbpCryptoException, InvalidInput, LdeCheckedException;

    /**
     * Returns all digitized cards profiles stored in database
     *
     * @return Map of DigitizedCardProfile.
     */
    Map<String, DigitizedCardProfile> getAllCards() throws McbpCryptoException, InvalidInput;

    /**
     * Returns SingleUseKey count.
     *
     * @param digitizedCardId 17 digit digitized Card identifier.
     * @return Count of SingleUseKey associate with given digitized Card identifier.
     * @throws InvalidInput if digitized card id is not valid.
     */
    int getSingleUseKeyCount(String digitizedCardId) throws InvalidInput;

    /**
     * Returns the list of successfully provision card profile ids
     */
    List<String> getListOfAvailableCardId();

    /**
     * Retrieves the next session key that can be used for a contact-less payment.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return SingleUseKey.
     */
    SingleUseKey getNextSessionKey(String digitizedCardId) throws McbpCryptoException, InvalidInput;

    /**
     * Wipe specific Suk according to digitize card id and suk id from Lde.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @param singleUseKeyId  suk id.
     */
    void wipeSingleUseKey(String digitizedCardId, String singleUseKeyId) throws InvalidInput;

    /**
     * Wipe all data linked to specific digitized card.
     *
     * @param digitizedCardId 17 bytes long identifier.
     */
    void wipeDigitizedCardProfile(String digitizedCardId) throws InvalidInput;

    /**
     * Wipe all the Suks specific to digitize card id from Lde.
     *
     * @param digitizedCardId 17 bytes long identifier.
     */
    void wipeSingleUseKey(String digitizedCardId) throws InvalidInput;

    /**
     * Wipe all suks.
     */
    void wipeAllSingleUseKey();

    /**
     * store a transaction log in the monitoring container in the Lde
     *
     * @param transactionLog the transaction log to be stored in the Lde
     */
    void addToLog(TransactionLog transactionLog) throws TransactionStorageLimitReach,
            TransactionLoggingError;

    /**
     * Retrieves last 10 transaction logs associated with digitized card.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return List of TransactionLog.
     */
    List<TransactionLog> getTransactionLogs(String digitizedCardId) throws InvalidInput;

    /**
     * Stores data to be displayed to the user
     *
     * @param data stream encoded in readable format as ASCII
     */
    void storeInformationDelivery(String data);

    /**
     * Retrieves User Information that may have been sent as part of Remote
     * Management.
     *
     * @return value
     */
    String fetchStoredInformationDelivery();

    /**
     * Wipe User information.
     */
    void wipeUserInformation();

    /**
     * Delete all cards and SUKs from data base.
     */
    void remoteWipeWallet();

    /**
     * Get Card Profile associated to a given digitizedCardId
     *
     * @param digitizedCardId The digitizedCardId for which the card profile has to be returned
     * @return The card profile. Null if the digitizedCardId does not exist.
     */
    DigitizedCardProfile getDigitizedCardProfile(String digitizedCardId) throws
            McbpCryptoException, InvalidInput;

    /**
     * Wipe the transactions log for a given digitizedCardId
     *
     * @param digitizedCardId The digitizedCardId for which the transactions log has to be erased
     */
    void wipeTransactionLogs(String digitizedCardId) throws InvalidInput;

    /**
     * Retrieve list of available ATCs.
     *
     * @param digitizedCardId 17 bytes long identifier.
     */
    ByteArray getAvailableATCs(String digitizedCardId) throws
            InvalidInput;

    /**
     * Insertion of mobile key
     *
     * @param keyValue        key value.
     * @param mobileKeySetId  mobile key set id.
     * @param digitizedCardId card id.
     * @param keyType         type of key.
     * @throws InvalidInput
     * @throws McbpCryptoException
     */
    void insertMobileKey(ByteArray keyValue, String mobileKeySetId,
                         String digitizedCardId, String keyType)
            throws InvalidInput, McbpCryptoException;

    /**
     * Retrieve mobile key set id from given card id.
     *
     * @return The mobile key set id, if it has been set. Null otherwise.
     */
    String getMobileKeySetId();

    /**
     * Retrieve card id from given token unique reference.
     *
     * @param tokenUniqueReference Token unique reference.
     * @return Card id.
     * @throws InvalidInput
     */
    String getCardIdFromTokenUniqueReference(String tokenUniqueReference) throws InvalidInput;

    /**
     * Insertion or modification of transaction credential status.
     *
     * @param transactionCredentialStatus Transaction credential status.
     * @param tokenUniqueReference        Token unique reference.
     * @throws InvalidInput
     */
    void insertOrUpdateTransactionCredentialStatus(
            TransactionCredentialStatus transactionCredentialStatus,
            String tokenUniqueReference)
            throws InvalidInput;

    /**
     * Retrieve list of all Transaction credential status of given token unique reference.
     *
     * @param tokenUniqueReference Token unique reference.
     * @return TransactionCredentialStatus list.
     * @throws InvalidInput
     */
    TransactionCredentialStatus[] getAllTransactionCredentialStatus(String tokenUniqueReference)
            throws InvalidInput;

    /**
     * Delete all transaction credential status.
     *
     * @param tokenUniqueReference Token unique reference.
     * @throws InvalidInput
     */
    void deleteAllTransactionCredentialStatus(String tokenUniqueReference)
            throws InvalidInput;

    /**
     * Delete all transaction credential status which are not in active state of given token unique
     * reference.
     *
     * @param tokenUniqueReference Token unique reference.
     * @throws InvalidInput
     */
    void deleteOtherThanActiveTransactionCredentialStatus(String tokenUniqueReference)
            throws InvalidInput;

    /**
     * Deletes token unique reference of given card id.
     *
     * @param digitizeCardId Card id.
     */
    void deleteTokenUniqueReference(String digitizeCardId) throws InvalidInput;

    /**
     * Delete all transaction credential from table.
     */
    void deleteAllTransactionCredentialStatus();

    /**
     * Get the current state of a card.
     *
     * @param cardIdentifier card identifier
     * @return card state
     */
    ProfileState getCardState(String cardIdentifier) throws InvalidInput;

    /**
     * This method updates wallet pin state.
     *
     * @param pinState New pin state.
     */
    void updateWalletPinState(PinState pinState);

    /**
     * @return Current state of wallet pin.
     */
    PinState getWalletPinState();

    /**
     * This method updates pin state of a card.
     *
     * @param digitizeCardId card identifier
     * @param pinState       New Pin state.
     */
    void updateCardPinState(String digitizeCardId, PinState pinState);

    /**
     * Retrieve the Pin state for given card id.
     *
     * @param digitizeCardId card identifier for which pin state to be read.
     * @return Pin state of given card id.
     */
    PinState getCardPinState(String digitizeCardId);

    /**
     * Get the status of card profile
     *
     * @return true if card profile is already provision else return false.
     */
    boolean isCardAlreadyProvision(String cardIdentifier);

    /**
     * Fetch CMS MPA Id from database.
     *
     * @return CMSMpaId
     */
    String getCmsMpaId();

    /**
     * Retrieve RemoteManagement Url from database.
     */
    String getUrlRemoteManagement();

    /**
     * Retrieve device fingerprint from database.
     */
    ByteArray getMpaFingerPrint();

    /**
     * Update remote management url.
     *
     * @param cmsMpaId Cms Mpa id.
     * @param url      The CMS URL
     * @throws InvalidInput
     */
    void updateRemoteManagementUrl(final String cmsMpaId, String url) throws InvalidInput;

    /**
     * This function fetches transaction identifier for a transaction using transaction date and
     * transaction atc.
     *
     * @param transactionDate ByteArray
     * @param transactionAtc  ByteArray
     * @return TransactionIdentifier
     * @since 1.0.6a
     */
    ByteArray getTransactionIdentifier(ByteArray transactionDate, ByteArray transactionAtc);
}
