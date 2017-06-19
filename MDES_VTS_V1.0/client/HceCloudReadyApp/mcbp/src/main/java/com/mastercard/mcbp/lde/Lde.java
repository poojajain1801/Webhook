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

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.card.McbpCardImpl;
import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.profile.DigitizedCardProfile;
import com.mastercard.mcbp.card.profile.McbpDigitizedCardProfileWrapper;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.lde.containers.DigitizedCardTemplate;
import com.mastercard.mcbp.lde.data.SessionKey;
import com.mastercard.mcbp.lde.services.LdeBusinessLogicService;
import com.mastercard.mcbp.lde.services.LdeMcbpCardService;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.WalletState;
import com.mastercard.mcbp.remotemanagement.mdes.TimeUtils;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.utils.PropertyStorageFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidDigitizedCardProfile;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeAlreadyInitialized;
import com.mastercard.mcbp.utils.exceptions.lde.LdeCheckedException;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.lde.ProvisioningSukFailedException;
import com.mastercard.mcbp.utils.exceptions.lde.SessionKeysNotAvailable;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionLoggingError;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionStorageLimitReach;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DuplicateMcbpCard;
import com.mastercard.mcbp.utils.lde.Utils;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.tlv.ParsingException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The local database delivers the following basic set of functions: <li>-Setup
 * of secure storage <li>-Secure Storage of data delivered by the Credentials
 * Management System <li>-Retrieval of securely stored data <br>
 */
class Lde implements LdeBusinessLogicService, LdeRemoteManagementService, LdeMcbpCardService {
    /**
     * Dummy card id to use while insertion of a mobile key after successful registration of MPA.
     * Used for backward architecture compatibility.
     */
    public static final String DUMMY_CARD_ID = "012345678901234567890123";
    /**
     * Type for mobile config key.
     */
    public static final String TYPE_MOBILE_CONFIDENTIALITY_KEY = "confidentiality_key";
    /**
     * Type for mobile mac key.
     */
    public static final String TYPE_MOBILE_MAC_KEY = "mac_key";
    /**
     * Type for mobile transport key.
     */
    public static final String TYPE_MOBILE_TRANSPORT_KEY = "transport_key";
    /**
     * Type for mobile data encryption key.
     */
    public static final String TYPE_MOBILE_DEK_KEY = "dataencryption_key";
    /**
     * Type for mobile data encryption key.
     */
    public static final String TYPE_MOBILE_KEY_SET_ID = "keySetId";
    /**
     * Underlying database implementation
     */
    private final McbpDataBase mMcbpDataBase;

    /**
     * Digitized cards
     */
    private Map<String, DigitizedCardTemplate> mDigitizedCardTemplateMap;
    /**
     * Logger service
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    /**
     * Constructor to initialize Lde.
     *
     * @param mcbpDataBase underlying data base implementation
     */
    public Lde(final McbpDataBase mcbpDataBase) throws ParsingException, McbpCryptoException,
            InvalidInput {
        this.mMcbpDataBase = mcbpDataBase;
        initializeContainers();
    }

    /**
     * Initialize containers.
     */
    private void initializeContainers() throws ParsingException, McbpCryptoException, InvalidInput {

        mDigitizedCardTemplateMap = new LinkedHashMap<>();

        if (isLdeInitialized()) {
            // filling user interaction container:
            createDigitizeCardTemplateFromDcCp(mMcbpDataBase.getAllCards());
        }
    }

    /**
     * Create digitized card cache.
     *
     * @param profiles Map of card profile.
     */
    private void createDigitizeCardTemplateFromDcCp(
            final Map<String, DigitizedCardProfile> profiles) {
        for (String id : profiles.keySet()) {
            DigitizedCardProfile profile = profiles.get(id);
            mDigitizedCardTemplateMap.put(id, new DigitizedCardTemplate(id, profile));
        }
    }

    /**
     * Provision the digitize card profile in to database.
     *
     * @param cardProfile Card Profile as {@link McbpDigitizedCardProfileWrapper}.
     * @throws McbpCryptoException
     * @throws InvalidInput
     * @throws LdeNotInitialized
     * @throws DuplicateMcbpCard
     */
    @Override
    public void provisionDigitizedCardProfile(final McbpDigitizedCardProfileWrapper cardProfile)
            throws McbpCryptoException, InvalidInput, LdeNotInitialized, DuplicateMcbpCard {
        validateLdeState();
        mMcbpDataBase.provisionDigitizedCardProfile(cardProfile);
    }

    /**
     * provisionDC_SUK
     * Provisions a set of keys associated to a card profile into the Lde.
     *
     * @param suk Instance of SingleUseKey.
     * @throws InvalidInput
     * @throws McbpCryptoException
     * @throws LdeCheckedException
     */
    @Override
    public void provisionSingleUseKey(final SingleUseKey suk)
            throws InvalidInput, McbpCryptoException, LdeCheckedException {

        validateLdeState();

        if (suk == null) {
            throw new InvalidInput("Invalid Suk");
        }

        String digitizedCardId = suk.getDigitizedCardId().toHexString();
        mMcbpDataBase.provisionSingleUseKey(digitizedCardId, suk);

        int singleUseKeyCount = mMcbpDataBase.getSingleUseKeyCount(digitizedCardId);

        if (singleUseKeyCount == 0) {
            throw new ProvisioningSukFailedException("Provisioning of Suk failed!");
        }
    }

    /**
     * Wipe all the data related to digitize card identified by digitize card id.
     *
     * @param digitizedCardId - Identifier of digitize card
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    @Override
    public void wipeDigitizedCard(final ByteArray digitizedCardId)
            throws LdeNotInitialized, InvalidInput {
        validateLdeState();
        mMcbpDataBase.wipeTransactionLogs(digitizedCardId.toHexString());
        wipeDcSuk(digitizedCardId);
        mMcbpDataBase.wipeDigitizedCardProfile(digitizedCardId.toHexString());
        getDigitalizedCardTemplateHashtable().remove(digitizedCardId.toHexString());
    }

    /**
     * Wipe all the data of MPA and bring its state to freshly installed one.
     *
     * @throws LdeNotInitialized
     */
    @Override
    public void resetMpaToInstalledState() throws LdeNotInitialized {
        validateLdeState();
        // Clear all data from database like Card Profile,SUKs and Mobile keys
        mMcbpDataBase.resetMpaToInstalledState();
        //Remove all the cards from memory
        if (mDigitizedCardTemplateMap != null) {
            mDigitizedCardTemplateMap.clear();
            mDigitizedCardTemplateMap = null;
        }
        mDigitizedCardTemplateMap = new LinkedHashMap<>();
        //Remove all the data from Shared preference
        PropertyStorageFactory.getInstance().removeAll();
    }

    /**
     * Modify the state of Digitize card profile to active.
     *
     * @throws McbpCryptoException
     * @throws InvalidInput
     * @throws LdeNotInitialized
     */
    @Override
    public void activateProfile(final String digitizedCardId)
            throws McbpCryptoException, InvalidInput, LdeNotInitialized {
        validateLdeState();
        if (!mDigitizedCardTemplateMap.containsKey(digitizedCardId)) {
            mDigitizedCardTemplateMap.put(digitizedCardId, new DigitizedCardTemplate(
                    digitizedCardId, mMcbpDataBase.getDigitizedCardProfile(digitizedCardId)));
        }
        mMcbpDataBase.activateProfile(digitizedCardId);
    }

    /**
     * Wipe data related to all card but not of user.
     *
     * @throws LdeNotInitialized
     */
    @Override
    public void remoteWipeWallet() throws LdeNotInitialized {
        validateLdeState();
        mMcbpDataBase.remoteWipeWallet();
        mDigitizedCardTemplateMap = new LinkedHashMap<>();
    }

    /**
     * Returns list of available card ids.
     *
     * @return List of Available card profile id.
     * @throws LdeNotInitialized
     */
    @Override
    public List<String> getListOfAvailableCardId() throws LdeNotInitialized {
        validateLdeState();
        return mMcbpDataBase.getListOfAvailableCardId();
    }

    /**
     * Wipe all the Single Use Key of digitize card.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    @Override
    public void wipeDcSuk(final ByteArray digitizedCardId) throws LdeNotInitialized, InvalidInput {
        validateLdeState();
        mMcbpDataBase.wipeSingleUseKey(digitizedCardId.toHexString());
    }

    /**
     * Wipe specific Suk according to digitize card id and suk id from Lde.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @param singleUseKeyId  suk id.
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    @Override
    public void wipeDcSuk(final String digitizedCardId, final String singleUseKeyId)
            throws LdeNotInitialized, InvalidInput {
        validateLdeState();
        mMcbpDataBase.wipeSingleUseKey(digitizedCardId, singleUseKeyId);
    }

    /**
     * Retrieve SUK count.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return Single Use Key count
     */
    @Override
    public int getSingleUseKeyCount(final String digitizedCardId)
            throws LdeNotInitialized, InvalidInput {
        validateLdeState();
        return mMcbpDataBase.getSingleUseKeyCount(digitizedCardId);
    }

    /**
     * Retrieve list of available ATCs.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return ByteArray of available ATCs
     * @throws InvalidInput
     * @throws LdeNotInitialized
     */
    @Override
    public ByteArray getAvailableATCs(final String digitizedCardId) throws InvalidInput,
            LdeNotInitialized {
        validateLdeState();
        return mMcbpDataBase.getAvailableATCs(digitizedCardId);
    }

    /**
     * Get the status of all the transaction credentials
     *
     * @param tokenUniqueReference token unique reference number of card
     * @return An array containing the status of each transaction credential
     * @throws InvalidInput
     */
    @Override
    public TransactionCredentialStatus[] getAllTransactionCredentialStatus(
            String tokenUniqueReference) throws InvalidInput {
        return mMcbpDataBase.getAllTransactionCredentialStatus(tokenUniqueReference);
    }

    /**
     * Get the Mobile Key Set Id
     *
     * @return The mobile keyset id as string, null if it the SDK has not been registered yet
     * (i.e. no mobile keys have been received)
     */
    private String getMobileKeySetId() {
        return mMcbpDataBase.getMobileKeySetId();
    }

    /**
     * Get the Mobile Key Set Id for the current set of mobile keys. Null if the SDK has
     * not been registered
     */
    @Override
    public ByteArray getMobileKeySetIdAsByteArray() {
        final String mobileKeySetId = getMobileKeySetId();
        return mobileKeySetId != null ?
               ByteArray.of(mobileKeySetId.getBytes(Charset.defaultCharset())) : null;
    }

    /**
     * Get the Transport Key (MDES variant only)
     *
     * @return Byte array of Transport key
     * @throws LdeNotInitialized
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Override
    public ByteArray getTransportKey() throws LdeNotInitialized, McbpCryptoException, InvalidInput {
        checkForValidMobileKeySetId();
        return getMobileKey(getMobileKeySetId(), DUMMY_CARD_ID, TYPE_MOBILE_TRANSPORT_KEY);
    }

    /**
     * Get the Mac Key (applicable to both MCBPv1 and MDES variants)
     *
     * @return Byte array of Mac key
     * @throws LdeNotInitialized
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Override
    public ByteArray getMacKey() throws LdeNotInitialized, McbpCryptoException, InvalidInput {
        checkForValidMobileKeySetId();
        return getMobileKey(getMobileKeySetId(), DUMMY_CARD_ID, TYPE_MOBILE_MAC_KEY);
    }

    /**
     * Get the Data Encrypting Key (MDES variant only)
     *
     * @return Byte array of Data Encrypting key
     * @throws LdeNotInitialized
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Override
    public ByteArray getDataEncryptionKey() throws LdeNotInitialized, McbpCryptoException,
            InvalidInput {
        checkForValidMobileKeySetId();
        return getMobileKey(getMobileKeySetId(), DUMMY_CARD_ID, TYPE_MOBILE_DEK_KEY);
    }

    /**
     * Get the Data Confidentiality Key (applicable to MCBPv1 variant only)
     *
     * @return Byte array of Data Confidentiality key
     * @throws LdeNotInitialized
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Override
    public ByteArray getConfidentialityKey() throws LdeNotInitialized, McbpCryptoException,
            InvalidInput {
        checkForValidMobileKeySetId();
        return getMobileKey(getMobileKeySetId(), DUMMY_CARD_ID, TYPE_MOBILE_CONFIDENTIALITY_KEY);
    }

    /**
     * Retrieve mobile key of given parameters.
     *
     * @param mobileKeySetId  mobile key set id of key to be retrieve.
     * @param digitizedCardId digitize card id of key to be retrieve.
     * @param type            Type of key.
     * @return Mobile key value.
     * @throws LdeNotInitialized
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    private ByteArray getMobileKey(final String mobileKeySetId, final String digitizedCardId,
                                   final String type) throws LdeNotInitialized,
            McbpCryptoException, InvalidInput {
        validateLdeState();
        return mMcbpDataBase.getMobileKey(mobileKeySetId, digitizedCardId, type);
    }

    /**
     * Insert a mobile key into Database.
     *
     * @param keyValue        Key value.
     * @param mobileKeySetId  mobile key set id associated with key.
     * @param digitizedCardId Digitize card id associated with key.
     * @param keyType         Type of key.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    private void insertMobileKey(final ByteArray keyValue, final String mobileKeySetId,
                                 final String digitizedCardId,
                                 final String keyType) throws McbpCryptoException, InvalidInput {
        mMcbpDataBase.insertMobileKey(keyValue, mobileKeySetId, digitizedCardId, keyType);
    }

    /**
     * Insert Mobile KeySetId into Database.
     *
     * @param mobileKeySetId The Mobile KeySetId
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Override
    public void insertMobileKeySetId(final String mobileKeySetId)
            throws McbpCryptoException, InvalidInput {
        insertMobileKey(ByteArray.of(new byte[16]), mobileKeySetId, DUMMY_CARD_ID,
                        TYPE_MOBILE_KEY_SET_ID);
    }

    /**
     * Insert Transport Key into Database.
     * Note : We store Transport Key by encrypting it with Database key.
     *
     * @param transportKey Transport key in form of ByteArray
     * @throws McbpCryptoException If any exception occur while encrypting data
     * @throws InvalidInput        If input data is invalid
     */
    @Override
    public void insertTransportKey(final ByteArray transportKey)
            throws McbpCryptoException, InvalidInput {

        insertMobileKey(transportKey, getMobileKeySetId(), DUMMY_CARD_ID,
                        TYPE_MOBILE_TRANSPORT_KEY);
    }

    /**
     * Insert Mac Key into Database.
     * Note : We store Mac Key by encrypting it with Database key.
     *
     * @param macKey Mac Key in form of ByteArray
     * @throws McbpCryptoException If any exception occur while encrypting data
     * @throws InvalidInput        If input data is invalid
     */
    @Override
    public void insertMacKey(final ByteArray macKey)
            throws McbpCryptoException, InvalidInput {
        insertMobileKey(macKey, getMobileKeySetId(), DUMMY_CARD_ID, TYPE_MOBILE_MAC_KEY);
    }

    /**
     * Insert Data Encryption Key into Database.
     * Note : We store Data Encryption Key by encrypting it with Database key.
     *
     * @param dataEncryptionKey Data Encryption Key in form of ByteArray
     * @throws McbpCryptoException If any exception occur while encrypting data
     * @throws InvalidInput        If input data is invalid
     */
    @Override
    public void insertDataEncryptionKey(final ByteArray dataEncryptionKey)
            throws McbpCryptoException, InvalidInput {
        insertMobileKey(dataEncryptionKey, getMobileKeySetId(), DUMMY_CARD_ID,
                        TYPE_MOBILE_DEK_KEY);
    }

    /**
     * Insert Confidentiality Key into Database.
     * Note : We store Confidentiality Key by encrypting it with Database key.
     * This API is for backward compatibility
     *
     * @param confidentialityKey Confidentiality Key  in form of ByteArray
     * @throws McbpCryptoException If any exception occur while encrypting data
     * @throws InvalidInput        If input data is invalid
     */
    @Override
    public void insertConfidentialityKey(final ByteArray confidentialityKey)
            throws McbpCryptoException, InvalidInput {
        insertMobileKey(confidentialityKey, getMobileKeySetId(), DUMMY_CARD_ID,
                        TYPE_MOBILE_CONFIDENTIALITY_KEY);
    }

    /**
     * Insert a new entry in the mapping between tokenUniqueReference and DigitizedCardId
     * We may remove this in future versions should we support only one remote protocol
     *
     * @param tokenUniqueReference The Token Unique Reference
     * @param digitizedCardId      Digitize card id associated with key.
     * @throws InvalidInput If input data is invalid
     */
    @Override
    public void insertTokenUniqueReference(final String tokenUniqueReference,
                                           final String digitizedCardId) throws InvalidInput {
        mMcbpDataBase.insertTokenUniqueReference(tokenUniqueReference, digitizedCardId);
    }

    /**
     * Get card id associated with given token unique reference.
     *
     * @param tokenUniqueReference Token unique reference.
     * @return Card id.
     * @throws InvalidInput
     */
    @Override
    public String getCardIdFromTokenUniqueReference(final String tokenUniqueReference)
            throws InvalidInput {
        return mMcbpDataBase.getCardIdFromTokenUniqueReference(tokenUniqueReference);
    }

    /**
     * Get token unique reference of given card id.
     *
     * @param digitizedCardId card id.
     * @return Token unique reference.
     * @throws InvalidInput
     */
    @Override
    public String getTokenUniqueReferenceFromCardId(final String digitizedCardId)
            throws InvalidInput {
        return mMcbpDataBase.getTokenUniqueReferenceFromCardId(digitizedCardId);
    }

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
    @Override
    public void insertOrUpdateTransactionCredentialStatus(final String digitizedCardId,
                                                          final ByteArray atc,
                                                          final TransactionCredentialStatus
                                                                  .Status status)
            throws InvalidInput {
        String tokenUniqueReference =
                mMcbpDataBase.getTokenUniqueReferenceFromCardId(digitizedCardId);
        TransactionCredentialStatus credentialStatus = new TransactionCredentialStatus();
        credentialStatus.setAtc(Integer.parseInt(atc.toHexString(), 16));
        credentialStatus
                .setTimestamp(TimeUtils.getFormattedDate(new Date(System.currentTimeMillis())));
        credentialStatus.setStatus(status.toString());
        mMcbpDataBase.insertOrUpdateTransactionCredentialStatus(credentialStatus,
                                                                tokenUniqueReference);
    }

    /**
     * This function returns the last 4 digits of the PAN for display by the wallet
     *
     * @param tokenUniqueReference tokenUniqueReference of the card for which the PAN is requested
     * @return String last 4 digits of the PAN for the wallet to display
     * @throws InvalidInput
     * @since 1.0.4
     */
    @Override
    public String getDisplayablePanDigits(final String tokenUniqueReference) throws InvalidInput {
        validateLdeState();
        String cardId = mMcbpDataBase.getCardIdFromTokenUniqueReference(tokenUniqueReference);
        return Utils.getLastFourDigitOfPAN(cardId);
    }


    /**
     * This function fetches transaction identifier for a transaction using transaction date and
     * transaction atc.
     *
     * @param transactionDate transaction date
     * @param transactionAtc  transaction atc
     * @return TransactionIdentifier
     * @since 1.0.6a
     */
    @Override
    public ByteArray getTransactionIdentifier(final ByteArray transactionDate,
                                              final ByteArray transactionAtc) {
        validateLdeState();
        return mMcbpDataBase.getTransactionIdentifier(transactionDate, transactionAtc);

    }

    /**
     * Delete transaction credential status which are not in active state.
     *
     * @param digitizedCardId Card id.
     * @throws InvalidInput in case of invalid card id.
     */
    @Override
    public void deleteTransactionCredentialStatusOtherThanActive(final String digitizedCardId)
            throws InvalidInput {
        String tokenUniqueReference =
                mMcbpDataBase.getTokenUniqueReferenceFromCardId(digitizedCardId);
        mMcbpDataBase.deleteOtherThanActiveTransactionCredentialStatus(tokenUniqueReference);
    }

    /**
     * Delete all transaction credential status of given card id.
     *
     * @param digitizedCardId Card id.
     * @throws InvalidInput in case of invalid card id.
     */
    @Override
    public void deleteAllTransactionCredentialStatus(final String digitizedCardId)
            throws InvalidInput {
        String tokenUniqueReference =
                mMcbpDataBase.getTokenUniqueReferenceFromCardId(digitizedCardId);
        mMcbpDataBase.deleteAllTransactionCredentialStatus(tokenUniqueReference);
    }

    /**
     * Updates remote management URL.
     *
     * @param url Url to update.
     * @throws InvalidInput in case of invalid url.
     */
    @Override
    public void updateRemoteManagementUrl(final String url) throws InvalidInput {
        mMcbpDataBase.updateRemoteManagementUrl(mMcbpDataBase.getCmsMpaId(), url);
    }

    /**
     * Get digitized card cache.
     *
     * @return Map of DigitalizedCardTemplate.
     */
    private Map<String, DigitizedCardTemplate> getDigitalizedCardTemplateHashtable() {
        return mDigitizedCardTemplateMap;
    }

    /**
     * Check Lde is initialized or not.
     *
     * @throws LdeNotInitialized exception.
     */
    private void validateLdeState() throws LdeNotInitialized {
        if (!isLdeInitialized()) {
            throw new LdeNotInitialized("LDE not initialized");
        }
    }

    /**
     * Initializes the Lde with the initialization data and move it from
     * {@link com.mastercard.mcbp.lde.LdeState#UNINITIALIZED UNINITIALIZED}
     * to {@link com.mastercard.mcbp.lde.LdeState#INITIALIZED INITIALIZED}
     * state so that it is ready for Remote Management. Namely it shall:
     * <ul>
     * <li>Create all data containers</li>
     * <li>Store initialization data</li>
     * </ul>
     * <p/>
     * Initialization of LDE required following data:
     * <ul>
     * <li>CMS_MPA_ID</li>
     * <li>RNS_MPA_ID</li>
     * <li>LDE_STATE</li>
     * <li>REMOTE_MANAGEMENT_URL</li>
     * <li>MPA_FGP</li>
     * <li>MOBILE_CONF_KEY</li>
     * <li>MOBILE_MAC_KEY</li>
     * </ul>
     *
     * @param initParams instance of {@link com.mastercard.mcbp.lde.LdeInitParams}
     * @throws McbpCryptoException
     * @throws InvalidInput
     * @throws LdeAlreadyInitialized - If LDE is not initialized and its API are being used.
     */
    @Override
    public void initializeLde(final LdeInitParams initParams)
            throws McbpCryptoException, InvalidInput, LdeAlreadyInitialized {
        if (isLdeInitialized()) {
            throw new LdeAlreadyInitialized("LDE is already initialized");
        }
        mMcbpDataBase.initializeLde(initParams);
    }

    /**
     * Delete token unique reference of given card it.
     *
     * @param digitizeCardId Card id.
     * @throws InvalidInput in case of invalid card id.
     */
    @Override
    public void deleteTokenUniqueReference(final String digitizeCardId) throws InvalidInput {
        mMcbpDataBase.deleteTokenUniqueReference(digitizeCardId);
    }

    /**
     * Retrieve list of all {@link McbpCard}
     *
     * @param refresh true to retrieve the cards from the database; false to retrieve a cached
     *                version.
     * @return list of {@link McbpCard}
     * @throws LdeNotInitialized
     */
    @Override
    public ArrayList<McbpCard> getMcbpCards(final boolean refresh) throws LdeNotInitialized {
        validateLdeState();
        ArrayList<McbpCard> mcbpCards = new ArrayList<>();
        if (refresh) {
            mDigitizedCardTemplateMap.clear();
            try {
                createDigitizeCardTemplateFromDcCp(mMcbpDataBase.getAllCards());
            } catch (McbpCryptoException | InvalidInput e) {
                mLogger.d(e.getMessage());
            }
        }

        for (String id : mDigitizedCardTemplateMap.keySet()) {
            final McbpCard card;
            final DigitizedCardTemplate cardTemplate = mDigitizedCardTemplateMap.get(id);

            try {
                card = new McbpCardImpl(cardTemplate, this);
            } catch (final InvalidDigitizedCardProfile e) {
                // A broken card should not end up in the database in the first place, however
                // if we spot one we delete it!
                try {
                    wipeDigitizedCard(ByteArray.of(cardTemplate.getDigitizedCardId()));
                } catch (final InvalidInput e1) {
                    throw new IllegalArgumentException("The database appears to be corrupted " + e);
                }
                // We go to the next card and ignore the current one.
                continue;
            }
            mcbpCards.add(card);
        }
        return mcbpCards;
    }

    /**
     * Stores data to be displayed to the user
     *
     * @param data stream encoded in readable format as ASCII
     * @throws LdeNotInitialized exception.
     */
    @Override
    public void storeInformationDelivery(final String data) throws LdeNotInitialized {
        validateLdeState();
        mMcbpDataBase.storeInformationDelivery(data);
    }

    /**
     * Retrieves User Information that may have been sent as part of Remote Management.
     *
     * @return value
     * @throws LdeNotInitialized exception.
     */
    @Override
    public String fetchStoredInformationDelivery() throws LdeNotInitialized {
        validateLdeState();
        return mMcbpDataBase.fetchStoredInformationDelivery();
    }

    /**
     * Retrieves last 10 transaction logs associated with digitized card.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return List of TransactionLog.
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    @Override
    public List<TransactionLog> getTransactionLogs(final String digitizedCardId)
            throws LdeNotInitialized, InvalidInput {
        validateLdeState();
        return mMcbpDataBase.getTransactionLogs(digitizedCardId);
    }

    /**
     * Check LDE initialize state.
     *
     * @return true if LDE state is initialized; otherwise false.
     */
    @Override
    public boolean isLdeInitialized() {
        return (mMcbpDataBase.getLdeState() == LdeState.INITIALIZED);
    }

    /**
     * Store a transaction log in the monitoring container in the Lde.
     *
     * @param log the transaction log to be stored in the Lde
     * @throws TransactionStorageLimitReach
     * @throws LdeNotInitialized
     * @throws TransactionLoggingError
     */
    @Override
    public void addToLog(final TransactionLog log)
            throws TransactionStorageLimitReach, LdeNotInitialized, TransactionLoggingError {
        validateLdeState();
        mMcbpDataBase.addToLog(log);
    }

    /**
     * Retrieve next available session key for transaction for specified card.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return Instance of {@link SingleUseKey}
     * @throws SessionKeysNotAvailable
     * @throws McbpCryptoException
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    @Override
    public SingleUseKey getNextSessionKey(final String digitizedCardId)
            throws SessionKeysNotAvailable, McbpCryptoException, LdeNotInitialized, InvalidInput {
        validateLdeState();
        final SingleUseKey suk = mMcbpDataBase.getNextSessionKey(digitizedCardId);
        if (suk == null) {
            throw new SessionKeysNotAvailable("No Session Keys available");
        }
        return suk;
    }

    /**
     * Get the next session of remote payment session keys
     *
     * @param digitizedCardId The Digitized Card Id for which the Session Key is being requested
     * @return The next Session Key for Remote Payment
     * @throws InvalidInput
     * @throws McbpCryptoException
     * @throws SessionKeysNotAvailable
     * @throws LdeNotInitialized
     * @since 1.0.3
     */
    @Override
    public SessionKey getNextRemotePaymentSessionKeys(final String digitizedCardId)
            throws InvalidInput, McbpCryptoException, SessionKeysNotAvailable, LdeNotInitialized {
        return getNextSessionKey(digitizedCardId).getSessionKey(SingleUseKey.Type.REMOTE_PAYMENT);
    }

    /**
     * Get the next set of contactless session keys
     *
     * @param digitizedCardId The Digitized Card ID for which the Session Key is being requested
     * @return The Next Session Key for Contactless Payment
     * @throws InvalidInput
     * @throws McbpCryptoException
     * @throws SessionKeysNotAvailable
     * @throws LdeNotInitialized
     * @since 1.0.3
     */
    @Override
    public SessionKey getNextContactlessSessionKeys(final String digitizedCardId)
            throws InvalidInput, McbpCryptoException, SessionKeysNotAvailable, LdeNotInitialized {
        return getNextSessionKey(digitizedCardId).getSessionKey(SingleUseKey.Type.CONTACTLESS);
    }

    /**
     * Wipe User information.
     *
     * @throws LdeNotInitialized
     */
    public void wipeUserInformation() throws LdeNotInitialized {
        validateLdeState();
        mMcbpDataBase.wipeUserInformation();
    }

    /**
     * The function enables the wallet to set card status as suspended.
     * The wallet receives this information from the Payment App Server.
     *
     * @param cardIdentifier id of the card to be suspended.
     * @throws InvalidInput
     */
    @Override
    public void suspendCard(String cardIdentifier) throws InvalidInput {
        validateLdeState();
        mMcbpDataBase.suspendCardProfile(cardIdentifier);
    }

    /**
     * Get the current state of a card.
     *
     * @param cardIdentifier id of the card to get the state.
     * @return card state
     * @throws InvalidInput
     */
    @Override
    public ProfileState getCardState(final String cardIdentifier) throws InvalidInput {
        validateLdeState();
        return mMcbpDataBase.getCardState(cardIdentifier);
    }

    /**
     * Delete all SUKs.
     *
     * @throws LdeNotInitialized
     */
    @Override
    public void wipeAllSuks() throws LdeNotInitialized {
        validateLdeState();
        mMcbpDataBase.wipeAllSingleUseKey();
    }

    /**
     * Delete all transaction credential status from database
     */
    @Override
    public void wipeAllTransactionCredentialStatus() {
        mMcbpDataBase.deleteAllTransactionCredentialStatus();
    }

    /**
     * This method updates the cache (DigitizeCardTemplate) which holds all cards.
     *
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Override
    public void updateDigitizedCardTemplate() throws McbpCryptoException, InvalidInput {
        validateLdeState();
        mDigitizedCardTemplateMap = new LinkedHashMap<>();
        Map<String, DigitizedCardProfile> allCards = mMcbpDataBase.getAllCards();
        for (String id : allCards.keySet()) {
            DigitizedCardProfile profile = allCards.get(id);
            mDigitizedCardTemplateMap.put(id, new DigitizedCardTemplate(id, profile));
        }
    }

    /**
     * Get the status of card profile
     *
     * @param cardIdentifier id of the card to get status
     * @return true if card profile is already provision else return false.
     * @throws LdeNotInitialized
     */
    @Override
    public boolean isCardProfileAlreadyProvision(String cardIdentifier) throws LdeNotInitialized {
        validateLdeState();
        return mMcbpDataBase.isCardAlreadyProvision(cardIdentifier);
    }

    /**
     * Update the state of wallet
     *
     * @param walletState Wallet State
     * @throws InvalidInput
     */
    @Override
    public void updateWalletState(WalletState walletState) throws InvalidInput {
        mMcbpDataBase.updateWalletState(mMcbpDataBase.getCmsMpaId(), walletState);
    }

    /**
     * Get the state of wallet
     *
     * @return WalletState
     */
    @Override
    public WalletState getWalletState() {
        return mMcbpDataBase.getWalletState();
    }

    /**
     * Un-register previously registered user.
     * Clear all the keys, cards from database
     */
    @Override
    public void unregister() {
        // Clear all data from database like Card Profile,SUKs and Mobile keys
        mMcbpDataBase.unregister();
        //Clear in-memory mapping of digitized card template
        if (mDigitizedCardTemplateMap != null) {
            mDigitizedCardTemplateMap.clear();
            mDigitizedCardTemplateMap = null;
        }
        mDigitizedCardTemplateMap = new LinkedHashMap<>();
    }

    /**
     * Returns number of cards provisioned
     *
     * @return The number of provisioned cards
     */
    @Override
    public long getNumberOfCardsProvisioned() {
        return mMcbpDataBase.getNumberOfCardsProvisioned();
    }

    /**
     * Fetch CMS MPA Id from database.
     *
     * @return CMSMpaId as ByteArray
     */
    @Override
    public ByteArray getCmsMpaId() {
        return ByteArray.of(mMcbpDataBase.getCmsMpaId());
    }

    /**
     * Retrieve RemoteManagement Url from database.(MDES only)
     */
    @Override
    public String getUrlRemoteManagement() {
        return mMcbpDataBase.getUrlRemoteManagement();
    }

    /**
     * Retrieve device fingerprint from database.
     */
    @Override
    public ByteArray getMpaFingerPrint() {
        return mMcbpDataBase.getMpaFingerPrint();
    }

    /**
     * Check whether a valid mobile key set id has been provisioned
     *
     * @throws InvalidInput In case no mobile key set id is found
     */
    private void checkForValidMobileKeySetId() throws InvalidInput {
        if (!isInitialized()) {
            throw new InvalidInput("No valid mobile key set id can be found. Have you registered?");
        }
    }

    /**
     * Check whether the SDK has been initialized for remote management operations (e.g. it has
     * received valid mobile keys)
     *
     * @return true if a mobileKeySetId is in the database, false otherwise
     */
    private boolean isInitialized() {
        return getMobileKeySetId() != null;
    }
}
