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

package com.mastercard.mcbp.lde.services;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.profile.McbpDigitizedCardProfileWrapper;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mcbp.remotemanagement.WalletState;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeCheckedException;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DuplicateMcbpCard;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.List;

/**
 * List out the services LDE offers for remote management of mobile application application.
 */
public interface LdeRemoteManagementService {
    /**
     * Check LDE initialize state.
     *
     * @return true if LDE state is initialized; otherwise false.
     */
    boolean isLdeInitialized();

    /**
     * Provision the digitize card profile in to database.
     *
     * @param cardProfile Card Profile as {@link McbpDigitizedCardProfileWrapper}.
     * @throws McbpCryptoException
     * @throws InvalidInput
     * @throws LdeNotInitialized
     * @throws DuplicateMcbpCard
     */
    void provisionDigitizedCardProfile(final McbpDigitizedCardProfileWrapper cardProfile) throws
            McbpCryptoException, InvalidInput, LdeNotInitialized, DuplicateMcbpCard;

    /**
     * provisionDC_SUK
     * Provisions a set of keys associated to a card profile into the Lde.
     *
     * @param singleUseKey Instance of SingleUseKey.
     * @throws InvalidInput
     * @throws McbpCryptoException
     * @throws LdeCheckedException
     */
    void provisionSingleUseKey(final SingleUseKey singleUseKey)
            throws LdeCheckedException, InvalidInput, McbpCryptoException;

    /**
     * Wipe all the data related to digitize card identified by digitize card id.
     *
     * @param digitizedCardId - Identifier of digitize card
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    void wipeDigitizedCard(final ByteArray digitizedCardId) throws LdeNotInitialized, InvalidInput;

    /**
     * Wipe all the data of MPA and bring its state to freshly installed one.
     *
     * @throws LdeNotInitialized
     */
    void resetMpaToInstalledState() throws LdeNotInitialized;

    /**
     * Modify the state of Digitize card profile to active.
     *
     * @throws McbpCryptoException
     * @throws InvalidInput
     * @throws LdeNotInitialized
     */
    void activateProfile(final String digitizedCardId)
            throws McbpCryptoException, InvalidInput, LdeNotInitialized;

    /**
     * Wipe data related to all card but not of user.
     *
     * @throws LdeNotInitialized
     */
    void remoteWipeWallet() throws LdeNotInitialized;

    /**
     * Returns list of available card ids.
     *
     * @return List of Available card profile id.
     * @throws LdeNotInitialized
     */
    List<String> getListOfAvailableCardId() throws LdeNotInitialized;

    /**
     * Retrieve transaction logs of digitize card identified by its id.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return List of transaction logs.
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    List<TransactionLog> getTransactionLogs(final String digitizedCardId) throws LdeNotInitialized,
            InvalidInput;

    /**
     * Wipe all the Single Use Key of digitize card.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    void wipeDcSuk(final ByteArray digitizedCardId) throws LdeNotInitialized, InvalidInput;

    /**
     * Wipe Single use key of Card.
     *
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    void wipeDcSuk(final String digitizedCardId,
                   final String singleUseKeyId) throws LdeNotInitialized, InvalidInput;

    /**
     * Retrieve SUK count.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return Single Use Key
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    int getSingleUseKeyCount(final String digitizedCardId) throws LdeNotInitialized, InvalidInput;

    /**
     * Retrieve list of available ATCs.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return ByteArray of available ATCs
     */
    ByteArray getAvailableATCs(final String digitizedCardId) throws InvalidInput, LdeNotInitialized;

    /**
     * Get the status of all the transaction credentials
     *
     * @return An array containing the status of each transaction credential
     */
    TransactionCredentialStatus[] getAllTransactionCredentialStatus(
            String tokenUniqueReference) throws InvalidInput;

    /**
     * Get the Transport Key (applicable to MDES variant only)
     *
     * @return Return the transport key as ByteArray
     * @throws LdeNotInitialized
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    ByteArray getTransportKey() throws LdeNotInitialized, McbpCryptoException, InvalidInput;

    /**
     * Get the MAC Key (applicable to both MCBPv1 and MDES variants)
     *
     * @return Return the MAC key as ByteArray
     * @throws LdeNotInitialized
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    ByteArray getMacKey() throws LdeNotInitialized, McbpCryptoException, InvalidInput;

    /**
     * Get the Data Encryption Key (applicable to MDES variant only)
     *
     * @return Return the Data Encryption key as ByteArray
     * @throws LdeNotInitialized
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    ByteArray getDataEncryptionKey() throws LdeNotInitialized, McbpCryptoException, InvalidInput;

    /**
     * Get the Confidentiality Key (applicable to MCBPv1 variant only)
     *
     * @return Return the Confidentiality key as ByteArray
     * @throws LdeNotInitialized
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Deprecated
    ByteArray getConfidentialityKey() throws LdeNotInitialized, McbpCryptoException, InvalidInput;

    /**
     * @return Get the Mobile Key Set Id for the current set of mobile keys. Null if the SDK has
     * not been registered
     */
    ByteArray getMobileKeySetIdAsByteArray();

    /**
     * Insert Mobile KeySetId into Database.
     *
     * @param mobileKeySetId The Mobile KeySetId
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    void insertMobileKeySetId(final String mobileKeySetId) throws McbpCryptoException, InvalidInput;

    /**
     * Insert Transport Key into Database.
     * Note : We store Transport Key by encrypting it with Database key.
     *
     * @param transportKey Transport key in form of ByteArray
     * @throws McbpCryptoException If any exception occur while encrypting data
     * @throws InvalidInput        If input data is invalid
     */
    void insertTransportKey(final ByteArray transportKey) throws McbpCryptoException, InvalidInput;

    /**
     * Insert Mac Key into Database.
     * Note : We store Mac Key by encrypting it with Database key.
     *
     * @param macKey Mac Key in form of ByteArray
     * @throws McbpCryptoException If any exception occur while encrypting data
     * @throws InvalidInput        If input data is invalid
     */
    void insertMacKey(final ByteArray macKey) throws McbpCryptoException, InvalidInput;

    /**
     * Insert Data Encryption Key into Database.
     * Note : We store Data Encryption Key by encrypting it with Database key.
     *
     * @param dataEncryptionKey Data Encryption Key in form of ByteArray
     * @throws McbpCryptoException If any exception occur while encrypting data
     * @throws InvalidInput        If input data is invalid
     */
    void insertDataEncryptionKey(final ByteArray dataEncryptionKey)
            throws McbpCryptoException, InvalidInput;

    /**
     * Insert Confidentiality Key into Database.
     * Note : We store Confidentiality Key by encrypting it with Database key.
     * This API is for backward compatibility
     *
     * @param confidentialityKey Confidentiality Key  in form of ByteArray
     * @throws McbpCryptoException If any exception occur while encrypting data
     * @throws InvalidInput        If input data is invalid
     */
    void insertConfidentialityKey(final ByteArray confidentialityKey)
            throws McbpCryptoException, InvalidInput;

    /**
     * Insert a new entry in the mapping between tokenUniqueReference and DigitizedCardId
     * We may remove this in future versions should we support only one remote protocol
     *
     * @param tokenUniqueReference The Token Unique Reference
     * @param digitizedCardId      Digitize card id associated with key.
     * @throws InvalidInput If input data is invalid
     */
    void insertTokenUniqueReference(final String tokenUniqueReference,
                                    final String digitizedCardId) throws InvalidInput;

    /**
     * Get card id associated with given token unique reference.
     *
     * @param tokenUniqueReference Token unique reference.
     * @return Card id.
     * @throws InvalidInput If input data is invalid
     */
    String getCardIdFromTokenUniqueReference(final String tokenUniqueReference) throws InvalidInput;

    /**
     * Get token unique reference of given card id.
     *
     * @param digitizedCardId card id.
     * @return Token unique reference.
     * @throws InvalidInput If input data is invalid
     */
    String getTokenUniqueReferenceFromCardId(final String digitizedCardId) throws InvalidInput;

    /**
     * Insertion of transaction credential status if status already exist it will update the
     * fields.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @param atc             Application Transaction Counter
     * @param status          Status of Transaction Credentials. Can be one of the following values
     *                        UNUSED_ACTIVE, UNUSED_DISCARDED, USED_FOR_CONTACTLESS, USED_FOR_DSRP
     * @throws InvalidInput
     */
    void insertOrUpdateTransactionCredentialStatus(final String digitizedCardId,
                                                   final ByteArray atc,
                                                   final TransactionCredentialStatus.Status status)
            throws InvalidInput;

    /**
     * Delete transaction credential status which are not in active state.
     *
     * @param digitizedCardId Card id.
     * @throws InvalidInput in case of invalid card id.
     */
    void deleteTransactionCredentialStatusOtherThanActive(final String digitizedCardId)
            throws InvalidInput;

    /**
     * Delete all transaction credential status of given card id.
     *
     * @param digitizedCardId Card id.
     * @throws InvalidInput in case of invalid card id.
     */
    void deleteAllTransactionCredentialStatus(final String digitizedCardId) throws InvalidInput;

    /**
     * Updates remote management URL.
     *
     * @param url Url to update.
     * @throws InvalidInput in case of invalid url.
     */
    void updateRemoteManagementUrl(final String url) throws InvalidInput;

    /**
     * Delete token unique reference of given card it.
     *
     * @param digitizeCardId Card id.
     * @throws InvalidInput in case of invalid card id.
     */
    void deleteTokenUniqueReference(final String digitizeCardId) throws InvalidInput;

    /**
     * The function enables the wallet to set card status as suspended.
     * The wallet receives this information from the Payment App Server.
     *
     * @param cardIdentifier id of the card to be suspended.
     * @throws InvalidInput in case of invalid card id.
     */
    void suspendCard(String cardIdentifier) throws InvalidInput;

    /**
     * Get the current state of a card.
     *
     * @param cardIdentifier Card identifier
     * @return card state
     * @throws InvalidInput in case of invalid card id.
     */
    ProfileState getCardState(String cardIdentifier) throws InvalidInput;

    /**
     * Delete all SUKs.
     *
     * @throws LdeNotInitialized
     */
    void wipeAllSuks() throws LdeNotInitialized;

    /**
     * Delete all transaction credential status from database
     */
    void wipeAllTransactionCredentialStatus();

    /**
     * This method updates the cache (DigitizeCardTemplate) which holds all cards.
     *
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    void updateDigitizedCardTemplate() throws McbpCryptoException, InvalidInput;

    /**
     * Get the status of card profile
     *
     * @param cardIdentifier id of the card to get status
     * @return true if card profile is already provision else return false.
     * @throws LdeNotInitialized
     */
    boolean isCardProfileAlreadyProvision(String cardIdentifier) throws LdeNotInitialized;

    /**
     * Update the state of wallet
     *
     * @param walletState Wallet State
     * @throws InvalidInput
     */
    void updateWalletState(WalletState walletState) throws InvalidInput;

    /**
     * Get the state of wallet
     *
     * @return WalletState
     */
    WalletState getWalletState();

    /**
     * Un-register previously registered user.
     * Clear all the keys,Cards from database
     */
    void unregister();

    /**
     * Returns number of cards provisioned
     *
     * @return The number of provisioned cards
     */
    long getNumberOfCardsProvisioned();

    /**
     * Fetch CMS MPA Id from database.
     *
     * @return CMSMpaId as ByteArray
     */
    ByteArray getCmsMpaId();

    /**
     * Retrieve RemoteManagement Url from database.
     */
    String getUrlRemoteManagement();

    /**
     * Retrieve device fingerprint from database.
     */
    ByteArray getMpaFingerPrint();
}
