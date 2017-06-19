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

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.credentials.SingleUseKeyContent;
import com.mastercard.mcbp.card.profile.DigitizedCardProfile;
import com.mastercard.mcbp.card.profile.McbpDigitizedCardProfileWrapper;
import com.mastercard.mcbp.card.profile.PinState;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.remotemanagement.WalletState;
import com.mastercard.mcbp.remotemanagement.mdes.TimeUtils;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeAlreadyInitialized;
import com.mastercard.mcbp.utils.exceptions.lde.LdeCheckedException;
import com.mastercard.mcbp.utils.exceptions.lde.LdeUncheckedException;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionLoggingError;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionStorageLimitReach;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DuplicateMcbpCard;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mcbp_android.BuildConfig;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static com.mastercard.mcbp.card.profile.PinState.PIN_NOT_SET;
import static com.mastercard.mcbp.lde.DatabaseInfo.CMS_MPA_ID;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_ATC;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_CARD_ID;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_CARD_PIN_STATE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_HASH;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_IDN;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_INIT_STATE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_MOBILE_KEY_SET_ID;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_MOBILE_KEY_TYPE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_MOBILE_KEY_VALUE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_MPA_FGP;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_PROFILE_DATA;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_PROFILE_STATE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_SUK_CL_MD;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_SUK_CL_UMD;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_SUK_ID;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_SUK_INFO;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_SUK_RP_MD;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_SUK_RP_UMD;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_TOKEN_UNIQUE_REFERENCE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_TRANSACTION_ATC;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_TRANSACTION_DATE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_TRANSACTION_ID;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_TRANSACTION_LOG;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_TRANSACTION_TIMESTAMP;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_URL;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_WALLET_PIN_STATE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_WALLET_STATE;
import static com.mastercard.mcbp.lde.DatabaseInfo.DATABASE_NAME;
import static com.mastercard.mcbp.lde.DatabaseInfo.KEY_CREATED;
import static com.mastercard.mcbp.lde.DatabaseInfo.KEY_DEVICE_ID;
import static com.mastercard.mcbp.lde.DatabaseInfo.KEY_STORAGE;
import static com.mastercard.mcbp.lde.DatabaseInfo.PREFS_NAME;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_CARD_PROFILES_LIST;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_CARD_TRANSACTIONS_LIST;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_ENVIRONMENT_CONT;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_MOBILE_KEYS;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_SUK_LIST;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_TOKEN_UNIQUE_REFERENCE_LIST;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_TRANSACTION_CREDENTIAL_STATUS;
import static com.mastercard.mcbp.lde.DatabaseInfo.TRANSACTION_CREDENTIAL_STATUS;

/**
 * Class to manage all the database related operation like :
 * <li>Create Database</li>
 * <li>Insert Records</li>
 * <li>update Records</li>
 * <li>Delete Records</li>
 * Additionally this class also generate key to store sensitive data.
 */
class AndroidBasicMcbpDataBase implements McbpDataBase, DatabaseEventListener {
    /**
     * Crypto service
     */
    private final CryptoService mCryptoService;
    /**
     * Logger service
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);
    /**
     * Database helper class
     */
    private final DatabaseHelper mDatabaseHelper;
    /**
     * Android's Context object
     */
    private final Context mContext;

    /**
     * Constructor to initialize database.
     *
     * @param context application Context.
     */
    public AndroidBasicMcbpDataBase(final Context context) {
        this.mContext = context;
        this.mDatabaseHelper = new DatabaseHelper(context, DATABASE_NAME, this);

        if (BuildConfig.NATIVE_CRYPTO_SERVICE) {
            CryptoServiceFactory.enableNativeCryptoService();
        }
        mCryptoService = CryptoServiceFactory.getDefaultCryptoService();
    }

    // **************************** LDE STATE APIs ************************** //

    /**
     * Initializes Lde with Initialization parameter send from the CMS sets the
     * Lde state to {@link LdeState#INITIALIZED}
     *
     * @param initParams instance of LdeInitParams.
     * @throws McbpCryptoException
     * @throws InvalidInput
     * @throws LdeAlreadyInitialized
     */
    @Override
    public void initializeLde(LdeInitParams initParams) throws McbpCryptoException, InvalidInput,
            LdeAlreadyInitialized {

        if (initParams == null || !(initParams.isValid())) {
            throw new InvalidInput("Invalid input params");
        }

        final SQLiteDatabase sqlLite = mDatabaseHelper.getWritableDatabase();

        final long count =
                DatabaseUtils.queryNumEntries(sqlLite, TABLE_ENVIRONMENT_CONT);

        if (count > 0) {
            throw new LdeAlreadyInitialized("Lde is already initialized");
        } else {
            SQLiteStatement sqLiteStatement =
                    sqlLite.compileStatement("INSERT INTO " + TABLE_ENVIRONMENT_CONT
                                             + " ( " + CMS_MPA_ID + " , " + COL_INIT_STATE
                                             + " , " + COL_URL + " , " + COL_MPA_FGP + " , "
                                             + COL_WALLET_PIN_STATE + " , " + COL_WALLET_STATE +
                                             " ) " + " VALUES (?,?,?,?,?,?);");

            sqLiteStatement.bindString(1, initParams.getCmsMpaId().toHexString());
            sqLiteStatement.bindLong(2, LdeState.INITIALIZED.getValue());
            String url = initParams.getUrlRemoteManagement();
            sqLiteStatement.bindString(3, url == null ? "" : url);
            sqLiteStatement.bindBlob(4, initParams.getMpaFingerPrint().getBytes());
            sqLiteStatement.bindLong(5, PIN_NOT_SET.getValue());
            sqLiteStatement.bindLong(6, WalletState.NOTREGISTER.getValue());
            long l = sqLiteStatement.executeInsert();
            sqLiteStatement.clearBindings();
            if (l == -1) {
                throw new LdeUncheckedException("Unable to update the database");
            }
        }
    }

    /**
     * Get Lde state.
     *
     * @return if Lde state is initialized then return LdeState.INITIALIZED otherwise LdeState
     * .UNINITIALIZED.
     */
    @Override
    public LdeState getLdeState() {
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        final String query = "SELECT " + CMS_MPA_ID + ", " + COL_INIT_STATE +
                             " FROM " + TABLE_ENVIRONMENT_CONT;

        final Cursor cursor = sqliteDatabase.rawQuery(query, null);

        final LdeState state;

        if (cursor.moveToFirst()) {
            state = LdeState.valueOf(cursor.getInt(cursor.getColumnIndex(COL_INIT_STATE)));
        } else {
            state = LdeState.UNINITIALIZED;
        }
        cursor.close();

        return state;
    }

    // ***********************************************************************//

    // **************************** CARD PROFILE APIs *********************** //

    /**
     * Provision a Card profile as sent by the CMS The Card profile will be set
     * to {@link ProfileState#UNINITIALIZED}
     * The card profile will be activated when the
     * {@link #activateProfile(String)} method is call
     *
     * @param cardProfile Card Profile as McbpDigitizedCardProfileWrapper.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Override
    public void provisionDigitizedCardProfile(final McbpDigitizedCardProfileWrapper cardProfile)
            throws McbpCryptoException, InvalidInput {

        if (cardProfile == null) throw new InvalidInput("Invalid Card Profile");

        final String digitizedCardId = cardProfile.getCardId();

        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }

        if (isCardAlreadyProvision(digitizedCardId)) {
            throw new DuplicateMcbpCard(digitizedCardId, "Card already provisioned");
        }

        final String cardProfileJson = cardProfile.toDigitizedCardProfile().toJsonString();

        final byte[] cardProfileBytes = cardProfileJson.getBytes();
        final byte[] encryptedCardProfile = encrypt(cardProfileBytes);

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        SQLiteStatement sqLiteStatement =
                sqliteDatabase.compileStatement("INSERT INTO " + TABLE_CARD_PROFILES_LIST
                                                + " ( " + COL_CARD_ID
                                                + " , " + COL_PROFILE_DATA
                                                + " , " + COL_PROFILE_STATE
                                                + " , " + COL_CARD_PIN_STATE
                                                + " ) "
                                                + " VALUES (?,?,?,?);");

        sqLiteStatement.bindString(1, digitizedCardId);
        sqLiteStatement.bindBlob(2, encryptedCardProfile);
        sqLiteStatement.bindLong(3, ProfileState.UNINITIALIZED.getValue());
        sqLiteStatement.bindLong(4, PIN_NOT_SET.getValue());
        long l = sqLiteStatement.executeInsert();
        sqLiteStatement.clearBindings();
        if (l == -1) {
            throw new LdeUncheckedException("Unable to update the database");
        }
        // Clear temporarily used variables
        Utils.clearByteArray(cardProfileBytes);
        Utils.clearByteArray(encryptedCardProfile);
    }

    /**
     * Activates a card profile. The card profile state is set to
     * {@link ProfileState#INITIALIZED}
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @throws InvalidInput
     */
    @Override
    public void activateProfile(final String digitizedCardId) throws InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        SQLiteStatement sqLiteStatement =
                sqliteDatabase.compileStatement("UPDATE " + TABLE_CARD_PROFILES_LIST
                                                + " SET " + COL_PROFILE_STATE + " = ?"
                                                + " WHERE " + COL_CARD_ID + " = ? ;");
        sqLiteStatement.bindLong(1, ProfileState.INITIALIZED.getValue());
        sqLiteStatement.bindString(2, digitizedCardId);
        int i = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (i == 0) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    /**
     * Suspend the card profile. The card profile state is set to
     * {@link ProfileState#SUSPENDED}
     *
     * @param digitizedCardId Card identifier of card profile to suspend or un-suspend.
     * @throws InvalidInput
     */
    @Override
    public void suspendCardProfile(final String digitizedCardId) throws InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        SQLiteStatement sqLiteStatement =
                sqliteDatabase.compileStatement("UPDATE " + TABLE_CARD_PROFILES_LIST
                                                + " SET " + COL_PROFILE_STATE + " = ?"
                                                + " WHERE " + COL_CARD_ID + " = ? ;");
        sqLiteStatement.bindLong(1, ProfileState.SUSPENDED.getValue());
        sqLiteStatement.bindString(2, digitizedCardId);
        int i = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (i == 0) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    /**
     * Returns all digitized cards profiles stored in database
     *
     * @return Map of DigitizedCardProfile.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Override
    public LinkedHashMap<String, DigitizedCardProfile> getAllCards()
            throws McbpCryptoException, InvalidInput {
        final LinkedHashMap<String, DigitizedCardProfile> table = new LinkedHashMap<>();
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        final String query = "SELECT " + COL_CARD_ID + ", " + COL_PROFILE_DATA +
                             " FROM " + TABLE_CARD_PROFILES_LIST;

        final Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (!cursor.moveToFirst()) {
            return table;
        }

        do {
            final int profileIdColumn = cursor.getColumnIndex(COL_PROFILE_DATA);
            final String digitizedCardId = cursor.getString(cursor.getColumnIndex(COL_CARD_ID));
            final byte[] profile = decryptAes(cursor.getBlob(profileIdColumn));

            DigitizedCardProfile ptpCpCms = DigitizedCardProfile.valueOf(profile);
            table.put(digitizedCardId, ptpCpCms);
        } while (cursor.moveToNext());
        cursor.close();

        // #MCBP_LOG_BEGIN
        mLogger.d("Number of digitized card(s): " + String.valueOf(table.size()));
        // #MCBP_LOG_END

        return table;
    }

    /**
     * Delete all Card Profiles.
     *
     * @param sqliteDatabase SQLiteDatabase instance
     */
    private void wipeAllCardProfiles(final SQLiteDatabase sqliteDatabase) {
        sqliteDatabase.delete(TABLE_CARD_PROFILES_LIST, null, null);
    }

    /**
     * Returns number of cards provisioned
     *
     * @return The number of cards that have been provisioned
     */
    @Override
    public long getNumberOfCardsProvisioned() {
        return DatabaseUtils.queryNumEntries(mDatabaseHelper.getWritableDatabase(), DatabaseInfo
                .TABLE_CARD_PROFILES_LIST);
    }

    /**
     * Wipe all data linked to specific digitized card.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @throws InvalidInput
     */
    @Override
    public void wipeDigitizedCardProfile(String digitizedCardId) throws InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }

        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();

        String whereClause = COL_CARD_ID + " = ? ";
        String[] args = new String[]{digitizedCardId};

        long numEntries = DatabaseUtils
                .queryNumEntries(sqliteDatabase, TABLE_CARD_PROFILES_LIST, whereClause, args);

        if (numEntries == 0) {
            return;
        }

        SQLiteStatement sqLiteStatement = sqliteDatabase
                .compileStatement("DELETE FROM " + TABLE_CARD_PROFILES_LIST
                                  + " WHERE " + COL_CARD_ID + " = ? ;");
        sqLiteStatement.bindString(1, digitizedCardId);
        int l = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (l == 0) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    /**
     * Returns the list of successfully provision card profile ids
     */
    @Override
    public List<String> getListOfAvailableCardId() {
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        final String query = "SELECT " + COL_CARD_ID + " FROM " + TABLE_CARD_PROFILES_LIST;
        final Cursor cursor = sqliteDatabase.rawQuery(query, null);

        final List<String> cardIdList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                cardIdList.add(cursor.getString(cursor.getColumnIndex(COL_CARD_ID)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cardIdList;
    }


    /**
     * Get Card Profile associated to a given digitizedCardId
     *
     * @param digitizedCardId The digitizedCardId for which the card profile has to be returned
     * @return The card profile. Null if the digitizedCardId does not exist.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Override
    @Nullable
    public DigitizedCardProfile getDigitizedCardProfile(String digitizedCardId)
            throws McbpCryptoException, InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }
        DigitizedCardProfile result = null;
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        String query = "SELECT " + COL_PROFILE_DATA + " FROM " + TABLE_CARD_PROFILES_LIST +
                       " WHERE " + COL_CARD_ID + " = ?";
        String[] args = new String[]{digitizedCardId};
        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        if (cursor.moveToFirst()) {
            byte[] encryptedCardProfile = cursor.getBlob(cursor.getColumnIndex(COL_PROFILE_DATA));
            byte[] decryptDcCp = decryptAes(encryptedCardProfile);
            result = DigitizedCardProfile.valueOf(decryptDcCp);
        }
        cursor.close();
        return result;
    }

    /**
     * Get the status of card profile
     *
     * @param cardIdentifier The digitizedCardId for which the card profile provision status return
     * @return true if card profile is already provision else return false.
     */
    @Override
    public boolean isCardAlreadyProvision(final String cardIdentifier) {
        if (cardIdentifier == null || cardIdentifier.isEmpty()) {
            return false;
        }
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();
        //Check if already exist, if exist update else insert
        final String whereClause = COL_CARD_ID + " = ?";
        final String[] args = new String[]{cardIdentifier};
        final long count = DatabaseUtils.queryNumEntries(sqliteDatabase,
                                                         TABLE_CARD_PROFILES_LIST,
                                                         whereClause, args);
        return (count != 0);
    }

    /**
     * Get the current state of a card.
     *
     * @param cardIdentifier card identifier
     * @return card state
     * @throws InvalidInput
     */
    @Override
    public ProfileState getCardState(String cardIdentifier) throws InvalidInput {
        if (cardIdentifier == null || cardIdentifier.isEmpty()) {
            throw new InvalidInput("Invalid Card identifier");
        }
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        String query = "SELECT " + COL_PROFILE_STATE +
                       " FROM " + TABLE_CARD_PROFILES_LIST +
                       " WHERE " + COL_CARD_ID + " = ?";
        String[] args = new String[]{cardIdentifier};
        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        ProfileState state = null;
        if (cursor.moveToFirst()) {
            state = ProfileState.valueOf(cursor.getInt(cursor.getColumnIndex(COL_PROFILE_STATE)));
        }
        cursor.close();
        return state;
    }

    // ***********************************************************************//

    // **************************** SUKs APIs ******************************* //

    /**
     * Provision a suk
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @param suk             Instance of SingleUseKey.
     * @throws McbpCryptoException
     * @throws InvalidInput
     * @throws LdeCheckedException
     */
    @Override
    public void provisionSingleUseKey(final String digitizedCardId, final SingleUseKey suk)
            throws McbpCryptoException, InvalidInput, LdeCheckedException {

        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }
        if (suk == null) {
            throw new InvalidInput("Invalid Suk");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        final SingleUseKeyContent singleUseKeyContent = suk.getContent();

        //        If we have duplicate SUK then we will not proceed
        if (isDuplicateSuk(suk)) {
            return;
        }

        SQLiteStatement sqLiteStmt = sqliteDatabase
                .compileStatement("INSERT INTO " + TABLE_SUK_LIST + " ( " + COL_SUK_INFO
                                  + " , " + COL_SUK_ID + " , " + COL_SUK_CL_UMD
                                  + " , " + COL_SUK_CL_MD + " , " + COL_SUK_RP_UMD
                                  + " , " + COL_SUK_RP_MD + " , " + COL_IDN
                                  + " , " + COL_ATC + " , " + COL_HASH + " , " + COL_CARD_ID + " ) "
                                  + " VALUES (?,?,?,?,?,?,?,?,?,?);");

        sqLiteStmt.bindBlob(1, singleUseKeyContent.getInfo().getBytes());
        sqLiteStmt.bindString(2, suk.getId().toHexString());

        if (singleUseKeyContent.getSukContactlessUmd() != null) {
            sqLiteStmt
                    .bindBlob(3, encrypt(singleUseKeyContent.getSukContactlessUmd().getBytes()));
        }
        if (singleUseKeyContent.getSessionKeyContactlessMd() != null) {
            sqLiteStmt.bindBlob(4, encrypt(singleUseKeyContent.getSessionKeyContactlessMd()
                                                              .getBytes()));
        }
        if (singleUseKeyContent.getSukRemotePaymentUmd() != null) {
            sqLiteStmt.bindBlob(5, encrypt(singleUseKeyContent.getSukRemotePaymentUmd()
                                                              .getBytes()));
        }
        if (singleUseKeyContent.getSessionKeyRemotePaymentMd() != null) {
            sqLiteStmt.bindBlob(6, encrypt(singleUseKeyContent.getSessionKeyRemotePaymentMd()
                                                              .getBytes()));
        }
        sqLiteStmt.bindBlob(7, singleUseKeyContent.getIdn().getBytes());
        sqLiteStmt.bindBlob(8, singleUseKeyContent.getAtc().getBytes());
        sqLiteStmt.bindBlob(9, singleUseKeyContent.getHash().getBytes());
        sqLiteStmt.bindString(10, digitizedCardId);
        long rowId = sqLiteStmt.executeInsert();
        sqLiteStmt.clearBindings();
        if (rowId == -1) {
            throw new LdeCheckedException("SUK already exist");
        }
    }

    /**
     * Retrieve all single use key of given card id.
     *
     * @param digitizedCardId card id.
     * @return List of single use keys.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Override
    public List<SingleUseKey> getAllSingleUseKeys(final String digitizedCardId) throws
            McbpCryptoException, InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();
        final List<SingleUseKey> sukList = new ArrayList<>();

        final String query =
                "SELECT " + COL_SUK_ID + ", " + COL_SUK_INFO + ", " + COL_ATC + ", "
                + COL_IDN + ", " + COL_ATC + ", " + COL_HASH + ", " + COL_CARD_ID +
                " FROM " + TABLE_SUK_LIST +
                " WHERE " + COL_CARD_ID + " = ?";

        final String[] args = new String[]{digitizedCardId};
        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        if (!cursor.moveToFirst()) {
            return sukList;
        }

        do {
            sukList.add(getSukAtCursor(cursor, false, false));
        } while (cursor.moveToNext());
        cursor.close();

        mLogger.d("Number of returned SUK: " + sukList.size());
        return sukList;
    }


    /**
     * Returns SingleUseKey count.
     *
     * @param digitizedCardId 17 digit digitized Card identifier.
     * @return Count of SingleUseKey associate with given digitized Card identifier.
     * @throws InvalidInput if digitized card id is not valid.
     */
    @Override
    public int getSingleUseKeyCount(final String digitizedCardId) throws InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();
        final long numEntries = DatabaseUtils
                .queryNumEntries(sqliteDatabase, TABLE_SUK_LIST, COL_CARD_ID + " = ?", new
                        String[]{digitizedCardId});

        return Long.valueOf(numEntries).intValue();
    }

    /***
     * Retrieves the next session key that can be used for a contact-less payment.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return SingleUseKey.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    @Nullable
    @Override
    public SingleUseKey getNextSessionKey(final String digitizedCardId)
            throws McbpCryptoException, InvalidInput {
        mLogger.d("--------get Suk-----------");
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }

        final DigitizedCardProfile digitizedCardProfile = getDigitizedCardProfile(digitizedCardId);
        if (digitizedCardProfile == null) {
            throw new InvalidInput("Invalid Digitized card id: Profile not found");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        final String query = "SELECT * FROM " + TABLE_SUK_LIST + " WHERE " + COL_CARD_ID + " = ?";
        final String[] args = new String[]{digitizedCardId};
        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        final SingleUseKey suk;

        if (cursor.moveToFirst()) {
            suk = getSukAtCursor(cursor, digitizedCardProfile.getContactlessSupported(),
                                 digitizedCardProfile.getRemotePaymentSupported());
        } else {
            suk = null;
        }
        cursor.close();
        return suk;
    }

    /**
     * Wipe specific Suk according to digitize card id and suk id from Lde.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @param singleUseKeyId  suk id.
     * @throws InvalidInput
     */
    @Override
    public void wipeSingleUseKey(String digitizedCardId, String singleUseKeyId) throws
            InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }
        if (singleUseKeyId == null || singleUseKeyId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }

        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        String whereClause = COL_CARD_ID + " = ? " + " AND " + COL_SUK_ID + " = ? ";
        String[] args = new String[]{digitizedCardId, singleUseKeyId};

        long numEntries = DatabaseUtils.queryNumEntries(sqliteDatabase,
                                                        TABLE_SUK_LIST, whereClause, args);
        if (numEntries == 0) {
            return;
        }

        SQLiteStatement sqLiteStatement = sqliteDatabase
                .compileStatement("DELETE FROM " + TABLE_SUK_LIST + " WHERE " + COL_CARD_ID
                                  + " = ? " + " AND " + COL_SUK_ID + " = ? ;");
        sqLiteStatement.bindString(1, digitizedCardId);
        sqLiteStatement.bindString(2, singleUseKeyId);
        int l = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (l == 0) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    /**
     * Wipe all the Suks specific to digitize card id from Lde.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @throws InvalidInput
     */
    @Override
    public void wipeSingleUseKey(String digitizedCardId) throws InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }

        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();

        String whereClause = COL_CARD_ID + " = ? ";
        String[] args = new String[]{digitizedCardId};

        long numEntries = DatabaseUtils
                .queryNumEntries(sqliteDatabase, TABLE_SUK_LIST, whereClause, args);

        if (numEntries == 0) {
            return;
        }

        SQLiteStatement sqLiteStatement = sqliteDatabase
                .compileStatement("DELETE FROM " + TABLE_SUK_LIST
                                  + " WHERE " + COL_CARD_ID + " = ? ;");
        sqLiteStatement.bindString(1, digitizedCardId);
        int l = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (l == 0) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    /**
     * Wipe all suks.
     */
    @Override
    public void wipeAllSingleUseKey() {
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();

        long numEntries = DatabaseUtils
                .queryNumEntries(sqliteDatabase, TABLE_SUK_LIST);
        if (numEntries == 0) {
            return;
        }

        SQLiteStatement sqLiteStatement = sqliteDatabase
                .compileStatement("DELETE FROM " + TABLE_SUK_LIST + ";");
        int deleteCount = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (deleteCount == 0) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    // ***********************************************************************//

    // **************************** WALLET STATUS APIs ********************** //

    /**
     * Update the state of wallet
     *
     * @param walletState Wallet State
     */
    @Override
    public void updateWalletState(final String cmsMpaId, WalletState walletState)
            throws InvalidInput {
        if ((cmsMpaId == null || cmsMpaId.isEmpty() || walletState == null)) {
            throw new InvalidInput("Invalid input");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        SQLiteStatement sqLiteStatement =
                sqliteDatabase.compileStatement("UPDATE " + TABLE_ENVIRONMENT_CONT
                                                + " SET " + COL_WALLET_STATE + " = ?"
                                                + " WHERE " + CMS_MPA_ID + " = ? ;");
        sqLiteStatement.bindLong(1, walletState.getValue());
        sqLiteStatement.bindString(2, cmsMpaId);
        long l = sqLiteStatement.executeInsert();
        sqLiteStatement.clearBindings();
        if (l == -1) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    /**
     * Get Wallet state.
     *
     * @return if Wallet state is register then return LdeState.REGISTER otherwise LdeState
     * .NOTREGISTER.
     */
    @Override
    public WalletState getWalletState() {
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        final String query = "SELECT " + COL_WALLET_STATE + " FROM " + TABLE_ENVIRONMENT_CONT;
        final Cursor cursor = sqliteDatabase.rawQuery(query, null);
        final WalletState state;
        if (cursor.moveToFirst()) {
            state = WalletState.valueOf(cursor.getInt(cursor.getColumnIndex(COL_WALLET_STATE)));
        } else {
            state = WalletState.NOTREGISTER;
        }
        cursor.close();
        return state;
    }

    // ***********************************************************************//

    // **************************** WALLET PIN APIs ************************* //

    /**
     * This method updates wallet pin state.
     *
     * @param pinState New pin state.
     */
    @Override
    public void updateWalletPinState(PinState pinState) {
        PinState walletPinState = getWalletPinState();
        if (walletPinState == pinState) {
            return;
        }
        final SQLiteDatabase sqLiteDb = mDatabaseHelper.getWritableDatabase();

        String query = "UPDATE " + TABLE_ENVIRONMENT_CONT
                       + " SET " + COL_WALLET_PIN_STATE + " = ?";

        SQLiteStatement sqLiteStatement = sqLiteDb.compileStatement(query);
        sqLiteStatement.bindLong(1, pinState.getValue());
        int updateCount = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (updateCount == 0) {
            throw new LdeUncheckedException("Unable to update database");
        }
    }

    /**
     * Returns Current state of wallet pin.
     */
    @Override
    public PinState getWalletPinState() {
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        String query = "SELECT " + COL_WALLET_PIN_STATE +
                       " FROM " + TABLE_ENVIRONMENT_CONT;

        final Cursor cursor = sqliteDatabase.rawQuery(query, null);

        PinState state;
        if (cursor.moveToFirst()) {
            state = PinState.valueOf(cursor.getInt(cursor.getColumnIndex(COL_WALLET_PIN_STATE)));
        } else {
            state = PIN_NOT_SET;
        }
        cursor.close();
        return state;
    }

    // ***********************************************************************//

    // **************************** CARD PIN APIs *************************** //

    /**
     * This method updates pin state of a card.
     *
     * @param digitizeCardId card identifier
     * @param pinState       New Pin state.
     */
    @Override
    public void updateCardPinState(String digitizeCardId, PinState pinState) {
        final SQLiteDatabase sqLiteDb = mDatabaseHelper.getWritableDatabase();


        String query = "UPDATE " + TABLE_CARD_PROFILES_LIST
                       + " SET " + COL_CARD_PIN_STATE + " = ?"
                       + " WHERE " + COL_CARD_ID + " = ? ";

        SQLiteStatement sqLiteStatement = sqLiteDb.compileStatement(query);

        sqLiteStatement.bindLong(1, pinState.getValue());
        sqLiteStatement.bindString(2, digitizeCardId);
        int updateCount = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (updateCount == 0) {
            throw new LdeUncheckedException("Unable to update database");
        }
    }

    /**
     * Retrieve the Pin state for given card id.
     *
     * @param digitizeCardId card identifier for which pin state to be read.
     * @return Pin state of given card id.
     */
    @Override
    public PinState getCardPinState(String digitizeCardId) {
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        String query = "SELECT " + COL_CARD_PIN_STATE + " FROM " + TABLE_CARD_PROFILES_LIST +
                       " WHERE " + COL_CARD_ID + " = ?";

        final String[] args = new String[]{digitizeCardId};

        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        PinState state;
        if (cursor.moveToFirst()) {
            state = PinState.valueOf(cursor.getInt(cursor.getColumnIndex(COL_CARD_PIN_STATE)));
        } else {
            state = PIN_NOT_SET;
        }
        cursor.close();
        return state;
    }

    // ***********************************************************************//

    // **************************** Mobile Keys APIs ************************ //

    /**
     * Insertion of mobile key.
     *
     * @param keyValue        key value.
     * @param mobileKeySetId  mobile key set id.
     * @param digitizedCardId card id.
     * @param keyType         type of key.
     * @throws InvalidInput
     * @throws McbpCryptoException
     */
    @Override
    public void insertMobileKey(final ByteArray keyValue, final String mobileKeySetId,
                                final String digitizedCardId, final String keyType)
            throws InvalidInput, McbpCryptoException {

        if (keyValue == null || keyValue.isEmpty()) {
            throw new InvalidInput("Invalid input");
        }
        if (mobileKeySetId == null || mobileKeySetId.isEmpty()) {
            throw new InvalidInput("Invalid input");
        }
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid input");
        }
        if (keyType == null || keyType.isEmpty()) {
            throw new InvalidInput("Invalid input");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();

        SQLiteStatement sqLiteStatement =
                sqliteDatabase.compileStatement("INSERT INTO " + TABLE_MOBILE_KEYS
                                                + " ( " + COL_MOBILE_KEY_VALUE + " , "
                                                + COL_MOBILE_KEY_SET_ID
                                                + " , " + COL_CARD_ID + " , "
                                                + COL_MOBILE_KEY_TYPE + " ) "
                                                + " VALUES (?,?,?,?);");
        sqLiteStatement.bindBlob(1, encrypt(keyValue.getBytes()));
        sqLiteStatement.bindString(2, mobileKeySetId);
        sqLiteStatement.bindString(3, digitizedCardId);
        sqLiteStatement.bindString(4, keyType);
        final long rowId = sqLiteStatement.executeInsert();
        sqLiteStatement.clearBindings();
        if (rowId == -1) {
            throw new IllegalArgumentException("Unable to store the mobile key");
        }
    }

    /**
     * Retrieve mobile key set id from given card id.
     *
     * @return The mobile key set id, if it has been set. Null otherwise.
     */
    @Override
    public String getMobileKeySetId() {

        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        final String query = "SELECT " + COL_MOBILE_KEY_SET_ID + " FROM " + TABLE_MOBILE_KEYS;
        final Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (!cursor.moveToFirst()) {
            return null;
        }
        final String mobileKeySetId =
                cursor.getString(cursor.getColumnIndex(COL_MOBILE_KEY_SET_ID));

        cursor.close();
        return mobileKeySetId;
    }

    /**
     * Retrieve Mobile Key of given mobile key set id, card id and type.
     *
     * @param mobileKeySetId  Mobile key set identifier.
     * @param digitizedCardId card id.
     * @param type            key type
     * @return ByteArray of mobile key.
     * @throws InvalidInput
     * @throws McbpCryptoException
     */
    @Override
    public ByteArray getMobileKey(final String mobileKeySetId, final String digitizedCardId,
                                  final String type) throws InvalidInput, McbpCryptoException {
        if (mobileKeySetId == null || mobileKeySetId.isEmpty()) {
            throw new InvalidInput("Invalid mobileKeySetId");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();
        final String query = "SELECT " + COL_MOBILE_KEY_VALUE + " FROM " + TABLE_MOBILE_KEYS +
                             " WHERE " + COL_MOBILE_KEY_SET_ID + " = ?" + " AND " +
                             COL_CARD_ID + " = ?" + " AND " + COL_MOBILE_KEY_TYPE + " = ?";
        final String[] args = new String[]{mobileKeySetId, digitizedCardId, type};
        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        if (!cursor.moveToFirst()) {
            throw new InvalidInput("LDE: Unable to find the Mobile Key");
        }

        final byte[] mobileKey = decryptAes(
                cursor.getBlob(cursor.getColumnIndex(COL_MOBILE_KEY_VALUE)));

        cursor.close();

        final ByteArray key = ByteArray.of(mobileKey);
        // Clear temporary data before returning
        Utils.clearByteArray(mobileKey);
        return key;
    }

    // ***********************************************************************//

    // **************************** MPPLite APIs ************************* //

    /**
     * Get the Current MPP Lite implementation to be used when creating a new card
     *
     * @return "java" if the Java MPP Lite implementation has to be used, "native" for the C++
     * implementation using Java Native Interface
     */
    public String getMppLiteType() {
        return BuildConfig.MPP_TYPE;
    }

    // ***********************************************************************//

    // **************************** Transaction Logs APIs *************************** //

    /**
     * Store a transaction log in the monitoring container in the Lde
     *
     * @param transactionLog the transaction log to be stored in the Lde
     * @throws TransactionStorageLimitReach
     * @throws TransactionLoggingError
     */
    @Override
    public void addToLog(TransactionLog transactionLog) throws TransactionStorageLimitReach,
            TransactionLoggingError {
        if (!transactionLog.isValid()) {
            throw new TransactionLoggingError("Invalid transaction mLogger input");
        }
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        try {

            String query = "SELECT " + COL_CARD_ID + ", " + COL_TRANSACTION_TIMESTAMP + ", " +
                           COL_TRANSACTION_LOG + " FROM " + TABLE_CARD_TRANSACTIONS_LIST +
                           " WHERE " + COL_CARD_ID + " = ?" +
                           " ORDER BY " + COL_TRANSACTION_TIMESTAMP + " DESC LIMIT 10";

            String[] args = new String[]{transactionLog.getDigitizedCardId()};
            final Cursor cursor = sqliteDatabase.rawQuery(query, args);
            if (cursor.getCount() > MAX_NO_OF_TX_IN_DB) {
                throw new TransactionStorageLimitReach("More than 10 transactions in the database");
            }

            if (cursor.getCount() == MAX_NO_OF_TX_IN_DB) {
                cursor.moveToLast();
                // Get the last timestamp, so that we can update that row
                long oldTimeStamp =
                        cursor.getLong(cursor.getColumnIndex(COL_TRANSACTION_TIMESTAMP));
                cursor.close();
                // Update the log into db
                updateTransactionLogIntoDatabase(sqliteDatabase, oldTimeStamp, transactionLog);
            } else {  // cursor < 10
                cursor.close();
                insertTransactionLogIntoDatabase(sqliteDatabase, transactionLog,
                                                 Long.toString(System.currentTimeMillis()));
            }
        } finally {
            clearContentValues(values);
            values.clear();
        }
    }

    /**
     * Retrieves last 10 transaction logs associated with digitized card.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return List of TransactionLog.
     * @throws InvalidInput
     */
    @Override
    public List<TransactionLog> getTransactionLogs(String digitizedCardId) throws InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }
        List<TransactionLog> transactionRecords = new ArrayList<>();
        mLogger.d("--------get Transaction Logs-----------");
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();
        String query = "SELECT " + COL_CARD_ID + ", " + COL_TRANSACTION_TIMESTAMP + ", " +
                       COL_TRANSACTION_ATC + ", " + COL_TRANSACTION_DATE + ", " +
                       COL_TRANSACTION_ID + ", " + COL_TRANSACTION_LOG +
                       " FROM " + TABLE_CARD_TRANSACTIONS_LIST + " WHERE " + COL_CARD_ID + " = ?" +
                       " ORDER BY " + COL_TRANSACTION_TIMESTAMP + " DESC LIMIT 10";
        String[] args = new String[]{digitizedCardId};

        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        if (!cursor.moveToFirst()) {
            return transactionRecords;
        }

        do {
            final byte[] txLog = cursor.getBlob(cursor.getColumnIndex(COL_TRANSACTION_LOG));
            final String atc = cursor.getString(cursor.getColumnIndex(COL_TRANSACTION_ATC));
            final String date = cursor.getString(cursor.getColumnIndex(COL_TRANSACTION_DATE));
            final byte[] txId = cursor.getBlob(cursor.getColumnIndex(COL_TRANSACTION_ID));
            transactionRecords.add(parseTransactionData(ByteArray.of(atc),
                                                        ByteArray.of(date),
                                                        txId != null ? ByteArray.of(txId)
                                                                     : ByteArray.of(""),
                                                        ByteArray.of(txLog)));
        } while (cursor.moveToNext());

        cursor.close();
        return transactionRecords;
    }

    /**
     * Wipe the transactions log for a given digitizedCardId
     *
     * @param digitizedCardId The digitizedCardId for which the transactions log has to be erased
     * @throws InvalidInput
     */
    @Override
    public void wipeTransactionLogs(String digitizedCardId) throws InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();

        String whereClause = COL_CARD_ID + " = ? ";
        String[] args = new String[]{digitizedCardId};

        long numEntries = DatabaseUtils
                .queryNumEntries(sqliteDatabase, TABLE_CARD_TRANSACTIONS_LIST, whereClause, args);
        if (numEntries == 0) {
            return;
        }

        SQLiteStatement sqLiteStatement = sqliteDatabase
                .compileStatement("DELETE FROM " + TABLE_CARD_TRANSACTIONS_LIST +
                                  " WHERE " + COL_CARD_ID + " = ? ;");
        sqLiteStatement.bindString(1, digitizedCardId);
        int l = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (l == 0) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    // ***********************************************************************//

    // **************************** Tx Credentials APIs ********************* //

    /**
     * Insertion or modification of transaction credential status.
     *
     * @param transactionCredentialStatus Transaction credential status.
     * @param tokenUniqueReference        Token unique reference.
     * @throws InvalidInput
     */
    @Override
    public void insertOrUpdateTransactionCredentialStatus(
            TransactionCredentialStatus transactionCredentialStatus, String tokenUniqueReference)
            throws InvalidInput {
        if (transactionCredentialStatus == null ||
            transactionCredentialStatus.getStatus() == null ||
            transactionCredentialStatus.getStatus().isEmpty()) {
            throw new InvalidInput("Invalid transaction credential status");
        }
        if (tokenUniqueReference == null || tokenUniqueReference.isEmpty()) {
            throw new InvalidInput("Invalid token unique reference");
        }
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();


        //Check if already exist, if exist update else insert
        final String whereClause = COL_TOKEN_UNIQUE_REFERENCE + " = ?" + " AND " + COL_ATC + " = ?";
        final String[] args = new String[]{tokenUniqueReference, String.valueOf
                (transactionCredentialStatus.getAtc())};
        final long count = DatabaseUtils.queryNumEntries(sqliteDatabase,
                                                         TABLE_TRANSACTION_CREDENTIAL_STATUS,
                                                         whereClause, args);

        if (count > 0) {
            //Update
            updateTransactionCredentialStatus(tokenUniqueReference, transactionCredentialStatus);
        } else {
            //Insert
            insertTransactionCredentialStatus(tokenUniqueReference, transactionCredentialStatus);
        }
    }

    /**
     * Retrieve list of all Transaction credential status of given token unique reference.
     *
     * @param tokenUniqueReference Token unique reference.
     * @return TransactionCredentialStatus list.
     * @throws InvalidInput
     */
    @Override
    public TransactionCredentialStatus[] getAllTransactionCredentialStatus
    (String tokenUniqueReference) throws InvalidInput {

        if (tokenUniqueReference == null || tokenUniqueReference.isEmpty()) {
            throw new InvalidInput("Invalid token unique reference");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();
        final String query = "SELECT " + COL_ATC + ", " + TRANSACTION_CREDENTIAL_STATUS + ", " +
                             COL_TRANSACTION_TIMESTAMP + " FROM " +
                             TABLE_TRANSACTION_CREDENTIAL_STATUS +
                             " WHERE " + COL_TOKEN_UNIQUE_REFERENCE + " = ?";
        final String[] args = new String[]{tokenUniqueReference};
        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        if (!cursor.moveToFirst()) {
            return null;
        }

        final TransactionCredentialStatus[] result =
                new TransactionCredentialStatus[cursor.getCount()];
        int count = 0;
        do {
            final int atc = cursor.getInt(cursor.getColumnIndex(COL_ATC));
            final String status =
                    cursor.getString(cursor.getColumnIndex(TRANSACTION_CREDENTIAL_STATUS));
            String timestamp;
            if (status.equalsIgnoreCase(TransactionCredentialStatus.Status.UNUSED_ACTIVE.name())) {
                timestamp = TimeUtils.getFormattedDate(new Date(System.currentTimeMillis()));
            } else {
                timestamp = cursor.getString(cursor.getColumnIndex(COL_TRANSACTION_TIMESTAMP));
            }
            result[count++] = new TransactionCredentialStatus(atc, status, timestamp);
        } while (cursor.moveToNext());
        cursor.close();
        return result;
    }

    /**
     * Deletes all transaction credentials status for a given tokenUniqueReference
     *
     * @param tokenUniqueReference Token unique reference.
     * @throws InvalidInput
     */
    public void deleteAllTransactionCredentialStatus(String tokenUniqueReference)
            throws InvalidInput {
        if (tokenUniqueReference == null || tokenUniqueReference.isEmpty()) {
            throw new InvalidInput("Invalid token unique reference");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();

        String whereClause = COL_TOKEN_UNIQUE_REFERENCE + " = ? ";
        String[] args = new String[]{tokenUniqueReference};
        long numEntries = DatabaseUtils.queryNumEntries(sqliteDatabase,
                                                        TABLE_TRANSACTION_CREDENTIAL_STATUS,
                                                        whereClause, args);
        if (numEntries == 0) {
            return;
        }

        SQLiteStatement sqLiteStatement = sqliteDatabase.compileStatement(
                "DELETE FROM " + TABLE_TRANSACTION_CREDENTIAL_STATUS + " WHERE "
                + COL_TOKEN_UNIQUE_REFERENCE + " = ? ;");
        sqLiteStatement.bindString(1, tokenUniqueReference);
        int rowsAffected = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (rowsAffected == 0) {
            throw new LdeUncheckedException("Unable to update database");
        }
    }

    /**
     * Delete all transaction credential status which are not in active state of given token unique
     * reference.
     *
     * @param tokenUniqueReference Token unique reference.
     * @throws InvalidInput
     */
    @Override
    public void deleteOtherThanActiveTransactionCredentialStatus(String tokenUniqueReference)
            throws InvalidInput {
        if (tokenUniqueReference == null || tokenUniqueReference.isEmpty()) {
            throw new InvalidInput("Invalid token unique reference");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        final String activeState = TransactionCredentialStatus.Status.UNUSED_ACTIVE.toString();
        final String whereClause = COL_TOKEN_UNIQUE_REFERENCE + " = ? AND " +
                                   TRANSACTION_CREDENTIAL_STATUS + " != ?";
        String[] args = new String[]{tokenUniqueReference, activeState};
        long numEntries = DatabaseUtils.queryNumEntries(sqliteDatabase,
                                                        TABLE_TRANSACTION_CREDENTIAL_STATUS,
                                                        whereClause, args);
        if (numEntries == 0) {
            return;
        }

        SQLiteStatement sqLiteStatement = sqliteDatabase.compileStatement(
                "DELETE FROM " + TABLE_TRANSACTION_CREDENTIAL_STATUS
                + " WHERE " + whereClause + ";");
        sqLiteStatement.bindString(1, tokenUniqueReference);
        sqLiteStatement.bindString(2, activeState);
        int count = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (count == 0) {
            throw new LdeUncheckedException("Unable to update database");
        }

    }

    /**
     * Delete all transaction credential from table.
     */
    @Override
    public void deleteAllTransactionCredentialStatus() {
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        long numEntries = DatabaseUtils
                .queryNumEntries(sqliteDatabase, TABLE_TRANSACTION_CREDENTIAL_STATUS);
        if (numEntries == 0) {
            return;
        }

        SQLiteStatement sqLiteStatement = sqliteDatabase.compileStatement(
                "DELETE FROM " + TABLE_TRANSACTION_CREDENTIAL_STATUS + " ;");
        int rowsAffected = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (rowsAffected == 0) {
            throw new LdeUncheckedException("Unable to update database");
        }
    }

    /**
     * Get token unique reference for given card id.
     *
     * @param digitizedCardId digitize card Id.
     * @return Token unique reference.
     * @throws InvalidInput
     */
    @Override
    public String getTokenUniqueReferenceFromCardId(final String digitizedCardId)
            throws InvalidInput {

        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid digitizedCardId");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        final String query = "SELECT " + COL_TOKEN_UNIQUE_REFERENCE +
                             " FROM " + TABLE_TOKEN_UNIQUE_REFERENCE_LIST +
                             " WHERE " + COL_CARD_ID + " = ?";
        final String[] args = new String[]{digitizedCardId};
        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        if (!cursor.moveToFirst()) {
            cursor.close();
            throw new InvalidInput("Invalid Digitized Card Id");
        }

        final String cardId = cursor.getString(cursor.getColumnIndex(COL_TOKEN_UNIQUE_REFERENCE));

        cursor.close();
        return cardId;
    }

    /**
     * Un-register previously registered user.
     * Clear all the keys from database
     */
    @Override
    public void unregister() {
        mLogger.d("--------unRegister-----------");
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        wipeAllMobileKeys(sqliteDatabase);
        wipeAllCardProfiles(sqliteDatabase);
        wipeAllSingleUseKey();
        wipeAllTransactionLogs(sqliteDatabase);
        wipeAllTokenUniqueReferences(sqliteDatabase);
        wipeAllTransactionCredentialStatus(sqliteDatabase);
        updateWalletPinState(PIN_NOT_SET);
        try {
            updateWalletState(getCmsMpaId(), WalletState.NOTREGISTER);
        } catch (InvalidInput invalidInput) {
            // Ignoring as this will never reach
        }
    }

    /**
     * Clears all database tables
     */
    @Override
    public void resetMpaToInstalledState() {
        mLogger.d("--------resetMpaToInstalledState-----------");
        unregister();
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        wipeEnvironmentContainer(sqliteDatabase);
    }

    /**
     * Insert token unique reference and it's corresponding card id.
     *
     * @param tokenUniqueReference Token unique reference.
     * @param digitizedCardId      Card Id.
     * @return Row ID of the newly inserted row, or -1 if an error occurred
     * @throws InvalidInput
     */
    @Override
    public long insertTokenUniqueReference(final String tokenUniqueReference,
                                           final String digitizedCardId) throws InvalidInput {

        if (tokenUniqueReference == null || tokenUniqueReference.length() == 0) {
            throw new InvalidInput("Invalid Token unique reference");
        }

        if (digitizedCardId == null || digitizedCardId.length() == 0) {
            throw new InvalidInput("Invalid Digitized card id");
        }

        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        SQLiteStatement sqLiteStatement =
                sqliteDatabase.compileStatement("INSERT INTO " + TABLE_TOKEN_UNIQUE_REFERENCE_LIST
                                                + " ( " + COL_TOKEN_UNIQUE_REFERENCE
                                                + " , " + COL_CARD_ID + " ) "
                                                + " VALUES (?,?);");
        sqLiteStatement.bindString(1, tokenUniqueReference);
        sqLiteStatement.bindString(2, digitizedCardId);
        long l = sqLiteStatement.executeInsert();
        sqLiteStatement.clearBindings();
        if (l == -1) {
            throw new LdeUncheckedException("Unable to update the database");
        }
        return l;
    }

    /**
     * Stores data to be displayed to the user
     *
     * @param data stream encoded in readable format as ASCII
     */
    public void storeInformationDelivery(String data) {
    }

    /**
     * Retrieves User Information that may have been sent as part of Remote
     * Management.
     *
     * @return value
     */
    @Override
    public String fetchStoredInformationDelivery() {
        return null;
    }

    /**
     * Wipe User information.
     */
    @Override
    public void wipeUserInformation() {

    }

    /**
     * Delete all cards and SUKs from data base and bring MPA to freshly REGISTERED State.
     */
    @Override
    public void remoteWipeWallet() {
        mLogger.d("--------remoteWipeWallet-----------");
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        sqliteDatabase.delete(TABLE_CARD_PROFILES_LIST, null, null);
        sqliteDatabase.delete(TABLE_SUK_LIST, null, null);
        sqliteDatabase.delete(TABLE_CARD_TRANSACTIONS_LIST, null, null);
    }

    /**
     * Retrieve list of available ATCs.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @throws InvalidInput
     */
    @Override
    public ByteArray getAvailableATCs(String digitizedCardId) throws InvalidInput {

        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid Digitized card id");
        }
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        String query = "SELECT " + COL_ATC + " FROM " + TABLE_SUK_LIST + " WHERE " +
                       COL_CARD_ID + " = ?";
        String[] args = new String[]{digitizedCardId};
        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        ByteArray result = ByteArray.get(0);

        if (!cursor.moveToFirst()) {
            return result;
        }
        do {
            byte[] atcAsBytes = cursor.getBlob(cursor.getColumnIndex(COL_ATC));
            result.append(ByteArray.of(atcAsBytes));
        } while (cursor.moveToNext());
        cursor.close();
        return result;
    }

    /**
     * Retrieve card id from given token unique reference.
     *
     * @param tokenUniqueReference Token unique reference.
     * @return Card id.
     * @throws InvalidInput
     */
    @Override
    public String getCardIdFromTokenUniqueReference(final String tokenUniqueReference)
            throws InvalidInput {

        if (tokenUniqueReference == null || tokenUniqueReference.isEmpty()) {
            throw new InvalidInput("Invalid token unique reference");
        }
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();

        final String query = "SELECT " + COL_CARD_ID +
                             " FROM " + TABLE_TOKEN_UNIQUE_REFERENCE_LIST +
                             " WHERE " + COL_TOKEN_UNIQUE_REFERENCE + " = ?";
        final String[] args = new String[]{tokenUniqueReference};
        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        if (!cursor.moveToFirst()) {
            cursor.close();
            throw new InvalidInput("Invalid token unique reference");
        }

        final String cardId = cursor.getString(cursor.getColumnIndex(COL_CARD_ID));

        cursor.close();
        return cardId;
    }

    /**
     * Deletes token unique reference of given card id.
     *
     * @param digitizedCardId Card id.
     * @throws InvalidInput
     */
    @Override
    public void deleteTokenUniqueReference(final String digitizedCardId) throws InvalidInput {
        if (digitizedCardId == null || digitizedCardId.isEmpty()) {
            throw new InvalidInput("Invalid card id");
        }

        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        final String whereClause = COL_CARD_ID + " = ? ";
        String[] args = new String[]{digitizedCardId};
        long numEntries = DatabaseUtils.queryNumEntries(sqliteDatabase,
                                                        TABLE_TOKEN_UNIQUE_REFERENCE_LIST,
                                                        whereClause, args);
        if (numEntries == 0) {
            return;
        }

        SQLiteStatement sqLiteStatement = sqliteDatabase.compileStatement(
                "DELETE FROM " + TABLE_TOKEN_UNIQUE_REFERENCE_LIST
                + " WHERE " + COL_CARD_ID + " = ?;");
        sqLiteStatement.bindString(1, digitizedCardId);
        int rowsAffected = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (rowsAffected == 0) {
            throw new LdeUncheckedException("Unable to update database");
        }
    }

    /**
     * Get the CMS MPA ID from the database (it assumes that only one CMS MPA ID is available
     *
     * @return cmsMpaId
     */
    @Override
    public String getCmsMpaId() {
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();
        final String query = "SELECT " + CMS_MPA_ID + " FROM " + TABLE_ENVIRONMENT_CONT;
        final Cursor cursor = sqliteDatabase.rawQuery(query, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        final String cmsMpaId = cursor.getString(cursor.getColumnIndex(CMS_MPA_ID));
        cursor.close();
        return cmsMpaId;
    }

    /**
     * Retrieve RemoteManagement Url from database.
     *
     * @return remote management url
     */
    @Override
    public String getUrlRemoteManagement() {
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();
        final String query = "SELECT " + CMS_MPA_ID + ", " + COL_INIT_STATE + ", " +
                             COL_URL + ", " + COL_MPA_FGP + " FROM " + TABLE_ENVIRONMENT_CONT;

        final Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (!cursor.moveToFirst()) {
            throw new IllegalArgumentException("LDE: Unable to fill the environment container");
        }

        final String remoteManagementUrl = cursor.getString(cursor.getColumnIndex(COL_URL));
        cursor.close();

        return remoteManagementUrl;
    }

    /**
     * Retrieve device fingerprint from database.
     *
     * @return mpa finger print
     */
    @Override
    public ByteArray getMpaFingerPrint() {
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();
        final String query = "SELECT " + CMS_MPA_ID + ", " + COL_INIT_STATE + ", " +
                             COL_URL + ", " + COL_MPA_FGP +
                             " FROM " + TABLE_ENVIRONMENT_CONT;

        final Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (!cursor.moveToFirst()) {
            throw new IllegalArgumentException("LDE: Unable to fill the environment container");
        }

        final byte[] mpaFingerPrint = cursor.getBlob(cursor.getColumnIndex(COL_MPA_FGP));
        cursor.close();
        return ByteArray.of(mpaFingerPrint);
    }

    /**
     * Update the remote management URL
     *
     * @param cmsMpaId Cms Mpa id.
     * @param url      The remote management url
     * @throws InvalidInput
     */
    @Override
    public void updateRemoteManagementUrl(final String cmsMpaId, final String url)
            throws InvalidInput {
        if (cmsMpaId == null) return;
        if (url == null || url.isEmpty()) {
            throw new InvalidInput("Invalid input params: " + url);
        }

        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();

        SQLiteStatement sqLiteStatement =
                sqliteDatabase.compileStatement("UPDATE " + TABLE_ENVIRONMENT_CONT
                                                + " SET " + COL_URL + " = ?"
                                                + " WHERE " + CMS_MPA_ID + " = ? ;");
        sqLiteStatement.bindString(1, url);
        sqLiteStatement.bindString(2, cmsMpaId);
        int i = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (i == 0) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    /**
     * Clear the content values by zeroing
     *
     * @param values Content values
     */
    private void clearContentValues(final ContentValues values) {
        if (values != null) {
            Set<String> strings = values.keySet();
            for (String key : strings) {
                Object o = values.get(key);
                if (o instanceof ByteArray) {
                    Utils.clearByteArray((ByteArray) o);
                }
                if (o instanceof String) {
                    o = "000000";
                }
                if (o instanceof byte[]) {
                    Utils.clearByteArray((byte[]) o);
                }
            }
        }
    }

    /**
     * Delete all transaction logs.
     *
     * @param sqliteDatabase SQLiteDatabase instance
     */
    private void wipeAllTransactionLogs(final SQLiteDatabase sqliteDatabase) {
        sqliteDatabase.delete(TABLE_CARD_TRANSACTIONS_LIST, null, null);
    }

    /**
     * Delete all transaction credential logs.
     *
     * @param sqliteDatabase SQLiteDatabase instance
     */
    private void wipeAllTransactionCredentialStatus(final SQLiteDatabase sqliteDatabase) {
        sqliteDatabase.delete(TABLE_TRANSACTION_CREDENTIAL_STATUS, null, null);
    }

    /**
     * Delete all mobile keys.
     *
     * @param sqliteDatabase SQLiteDatabase instance
     */
    private void wipeAllMobileKeys(final SQLiteDatabase sqliteDatabase) {
        sqliteDatabase.delete(TABLE_MOBILE_KEYS, null, null);
    }

    /**
     * Delete all mobile keys.
     *
     * @param sqliteDatabase SQLiteDatabase instance
     */
    private void wipeEnvironmentContainer(final SQLiteDatabase sqliteDatabase) {
        sqliteDatabase.delete(TABLE_ENVIRONMENT_CONT, null, null);
    }

    /**
     * Delete all mobile keys.
     *
     * @param sqliteDatabase SQLiteDatabase instance
     */
    private void wipeAllTokenUniqueReferences(final SQLiteDatabase sqliteDatabase) {
        sqliteDatabase.delete(TABLE_TOKEN_UNIQUE_REFERENCE_LIST, null, null);
    }

    /**
     * Get Suk.
     *
     * @param cursor        cursor value.
     * @param includeClKeys flag value to include contact-less keys or not.
     * @param includeRpKeys flag value to include remote payment keys or not.
     * @return SingleUseKey
     */
    private SingleUseKey getSukAtCursor(Cursor cursor, boolean includeClKeys,
                                        boolean includeRpKeys)
            throws McbpCryptoException, InvalidInput {

        final SingleUseKeyContent singleUseKeyContent = new SingleUseKeyContent();
        final byte[] sukInfo = cursor.getBlob(cursor.getColumnIndex(COL_SUK_INFO));
        final byte[] idn = cursor.getBlob(cursor.getColumnIndex(COL_IDN));
        final byte[] atc = cursor.getBlob(cursor.getColumnIndex(COL_ATC));
        final byte[] hash = cursor.getBlob(cursor.getColumnIndex(COL_HASH));

        singleUseKeyContent.setInfo(ByteArray.of(sukInfo));
        singleUseKeyContent.setIdn(ByteArray.of(idn));
        singleUseKeyContent.setAtc(ByteArray.of(atc));
        singleUseKeyContent.setHash(ByteArray.of(hash));

        // Clean the temporary variables
        Utils.clearByteArray(sukInfo);
        Utils.clearByteArray(idn);
        Utils.clearByteArray(atc);
        Utils.clearByteArray(hash);

        if (includeClKeys) {
            final byte[] sukClUmd = cursor.getBlob(cursor.getColumnIndex(COL_SUK_CL_UMD));
            final byte[] sukClMd = cursor.getBlob(cursor.getColumnIndex(COL_SUK_CL_MD));

            singleUseKeyContent.setSukContactlessUmd(decryptAes(sukClUmd));
            singleUseKeyContent.setSessionKeyContactlessMd(decryptAes(sukClMd));

            // Clean the temporary variables
            Utils.clearByteArray(sukClUmd);
            Utils.clearByteArray(sukClMd);
        }
        if (includeRpKeys) {
            final byte[] sukRpUmd = cursor.getBlob(cursor.getColumnIndex(COL_SUK_RP_UMD));
            final byte[] sukRpMd = cursor.getBlob(cursor.getColumnIndex(COL_SUK_RP_MD));

            singleUseKeyContent.setSukRemotePaymentUmd(decryptAes(sukRpUmd));
            singleUseKeyContent.setSessionKeyRemotePaymentMd(decryptAes(sukRpMd));

            // Clean the temporary variables
            Utils.clearByteArray(sukRpUmd);
            Utils.clearByteArray(sukRpMd);
        }

        final SingleUseKey suk = new SingleUseKey();
        suk.setId(ByteArray.of(cursor.getString(cursor.getColumnIndex(COL_SUK_ID))));
        suk.setContent(singleUseKeyContent);
        suk.setDigitizedCardId(ByteArray.of(cursor.getString(cursor.getColumnIndex(COL_CARD_ID))));
        return suk;
    }

    /**
     * Decrypt data by AES algorithm.
     *
     * @param data byte array of data which need to decrypt.
     * @return byte[] decrypted data.
     */
    private byte[] decryptAes(final byte[] data) throws McbpCryptoException, InvalidInput {
        if (data == null || data.length == 0) {
            throw new InvalidInput("Invalid input data");
        }
        final byte[] key = getDatabaseKey();
        try {
            return decryptAes(data, key);
        } finally {
            Utils.clearByteArray(key);
        }
    }

    /**
     * Decrypt data by AES algorithm.
     *
     * @param data byte array of data which need to decrypt.
     * @param key  Database Key which is generated using IMEI
     * @return byte[] decrypted data.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    private byte[] decryptAes(final byte[] data, final byte[] key)
            throws McbpCryptoException, InvalidInput {
        return mCryptoService.ldeDecryption(data, key);
    }

    /**
     * Get database key.
     *
     * @return byte[] database key
     * @throws McbpCryptoException
     */
    private byte[] getDatabaseKey() throws McbpCryptoException {
        final SharedPreferences preferences =
                mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (!preferences.getBoolean(KEY_CREATED, false)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_STORAGE, mCryptoService.getRandomByteArray(16).toHexString());
            editor.putBoolean(KEY_CREATED, true);
            editor.apply();
        }
        //A 64-bit number (as a hex string) that is randomly generated when the user first sets up
        // the device and should remain constant for the lifetime of the user's device.
        // The value may change if a factory reset is performed on the device.
        // When a device has multiple users (available on certain devices running Android 4.2
        // or higher),
        // each user appears as a completely separate device, so the ANDROID_ID value is unique
        // to each user.
        String deviceId = Settings.Secure
                .getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (preferences.getString(KEY_DEVICE_ID, null) == null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_DEVICE_ID, deviceId);
            editor.apply();
        }
        // Generate MPA Key
        final byte[] mpaKey = BuildConfig.MPA_KEY;
        final byte[] uniqueId = deviceId.getBytes();
        final byte[] uniqueMobileDeviceHash;

        try {
            uniqueMobileDeviceHash = mCryptoService.sha256(uniqueId);
        } catch (final McbpCryptoException e) {
            throw new RuntimeException("Unable to generate the DB key: " + e);
        }

        final byte[] rndStorage = preferences.getString(KEY_STORAGE, "").getBytes();
        final int length = uniqueMobileDeviceHash.length + mpaKey.length + rndStorage.length;
        final byte[] hashInput = new byte[length];

        System.arraycopy(uniqueMobileDeviceHash, 0, hashInput, 0, uniqueMobileDeviceHash.length);
        System.arraycopy(mpaKey, 0, hashInput, uniqueMobileDeviceHash.length, mpaKey.length);
        System.arraycopy(rndStorage, 0, hashInput,
                         uniqueMobileDeviceHash.length + mpaKey.length, rndStorage.length);

        // Clear temporary variables
        Utils.clearByteArray(uniqueMobileDeviceHash);
        Utils.clearByteArray(rndStorage);

        final byte[] generatedKey;
        try {
            generatedKey = mCryptoService.sha256(hashInput);
        } catch (final McbpCryptoException e) {
            throw new RuntimeException("Unable to generate the DB key: " + e);
        }
        Utils.clearByteArray(hashInput);
        return generatedKey;
    }

    /**
     * Insert a transaction log in the transaction table in the Lde
     *
     * @param sqliteDatabase the database
     * @param log            the transaction log to be inserted in the Lde
     * @param timeStamp      transaction time stamp
     */
    private void insertTransactionLogIntoDatabase(final SQLiteDatabase sqliteDatabase,
                                                  final TransactionLog log,
                                                  final String timeStamp) {
        SQLiteStatement sqLiteStatement = sqliteDatabase.compileStatement(
                "INSERT INTO " + TABLE_CARD_TRANSACTIONS_LIST + " ( " + COL_CARD_ID
                + " , " + COL_TRANSACTION_TIMESTAMP + " , " + COL_TRANSACTION_ATC + " , "
                + COL_TRANSACTION_DATE + " , " + COL_TRANSACTION_ID + " , "
                + COL_TRANSACTION_LOG + " ) " + " VALUES (?,?,?,?,?,?);");
        sqLiteStatement.bindString(1, log.getDigitizedCardId());
        sqLiteStatement.bindString(2, timeStamp);
        sqLiteStatement.bindString(3, log.getAtc().toHexString());
        sqLiteStatement.bindString(4, log.getDate().toHexString());
        if (log.getTransactionId() != null) {
            sqLiteStatement.bindBlob(5, log.getTransactionId().getBytes());
        }
        sqLiteStatement.bindBlob(6, getTransactionLogAsByteArray(log).getBytes());

        long l = sqLiteStatement.executeInsert();
        sqLiteStatement.clearBindings();
        if (l == -1) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    /**
     * Parse the transaction data from TransactionLog byte array
     *
     * @param atc  Application transaction counter.
     * @param date Transaction date
     * @param txId Transaction  Id
     * @param data TransactionLog data
     * @return TransactionLog instance
     */
    private TransactionLog parseTransactionData(ByteArray atc, ByteArray date,
                                                ByteArray txId, ByteArray data) {
        ByteArray cardId = data.copyOfRange(0, 17);
        ByteArray un = data.copyOfRange(17, 21);
        byte cryptogramFormat = data.getByte(21);
        byte isJailBroken = data.getByte(22);
        byte isRecentAttackDetected = data.getByte(23);
        ByteArray amount = data.copyOfRange(24, 30);
        ByteArray currencyCode = data.copyOfRange(30, 33);
        return TransactionLog.fromLdeData(cardId.toHexString(), un, atc, date, amount, currencyCode,
                                          cryptogramFormat, txId, (isJailBroken == 1),
                                          (isRecentAttackDetected == 1));
    }

    /**
     * Get the TransactionLog byte array from the TransactionLog
     *
     * @param transactionLog instance of TransactionLog
     * @return byte array of TransactionLog
     */
    private ByteArray getTransactionLogAsByteArray(TransactionLog transactionLog) {
        ByteArray data = ByteArray.of(transactionLog.getDigitizedCardId());

        data.append(transactionLog.getUnpredictableNumber() == null ?
                    ByteArray.of(new byte[4], 4) : transactionLog.getUnpredictableNumber());
        data.appendByte(transactionLog.getCryptogramFormat());
        data.appendByte(transactionLog.isHostingMeJailBroken() ? (byte) 1 : (byte) 0);
        data.appendByte(transactionLog.isRecentAttack() ? (byte) 1 : (byte) 0);
        data.append(transactionLog.getAmount() == null ?
                    ByteArray.of(new byte[6], 6) : transactionLog.getAmount());
        data.append(transactionLog.getCurrencyCode() == null ?
                    ByteArray.of(new byte[2], 2) : transactionLog.getCurrencyCode());
        return data;
    }

    /**
     * Update a transaction log in the transaction table in the Lde
     *
     * @param sqliteDatabase the database
     * @param log            the transaction log to be updated in the Lde
     * @param oldTimeStamp   stored time stamp
     */
    private void updateTransactionLogIntoDatabase(final SQLiteDatabase sqliteDatabase,
                                                  final long oldTimeStamp,
                                                  final TransactionLog log) {
        SQLiteStatement sqLiteStatement = sqliteDatabase
                .compileStatement("UPDATE " + TABLE_CARD_TRANSACTIONS_LIST
                                  + " SET " + COL_CARD_ID + " = ?" + " , "
                                  + COL_TRANSACTION_TIMESTAMP + " = ? " + " , "
                                  + COL_TRANSACTION_ATC + " = ? " + " , "
                                  + COL_TRANSACTION_DATE + " = ? " + " , "
                                  + COL_TRANSACTION_ID + " = ? " + " , "
                                  + COL_TRANSACTION_LOG + " = ? "
                                  + " WHERE " + COL_TRANSACTION_TIMESTAMP + " = ? ;");
        sqLiteStatement.bindString(1, log.getDigitizedCardId());
        sqLiteStatement.bindString(2, Long.toString(System.currentTimeMillis()));
        sqLiteStatement.bindString(3, log.getAtc().toHexString());
        sqLiteStatement.bindString(4, log.getDate().toHexString());
        sqLiteStatement.bindBlob(5, log.getTransactionId().getBytes());
        sqLiteStatement.bindBlob(6, getTransactionLogAsByteArray(log).getBytes());
        sqLiteStatement.bindString(7, Long.toString(oldTimeStamp));
        int i = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (i == 0) {
            throw new LdeUncheckedException("Unable to update the database");
        }
    }

    /**
     * Update a credential status in the transaction credential status table in the Lde
     *
     * @param tokenUniqueReference        the unique reference number of card
     * @param transactionCredentialStatus transaction credential status instance
     */
    private void updateTransactionCredentialStatus(final String tokenUniqueReference,
                                                   final TransactionCredentialStatus
                                                           transactionCredentialStatus) {
        final SQLiteDatabase sqLiteDb = mDatabaseHelper.getWritableDatabase();
        SQLiteStatement sqLiteStatement =
                sqLiteDb.compileStatement("UPDATE " + TABLE_TRANSACTION_CREDENTIAL_STATUS
                                          + " SET " + TRANSACTION_CREDENTIAL_STATUS + " = ?"
                                          + " , " + COL_TRANSACTION_TIMESTAMP + " = ?"
                                          + " WHERE " + COL_TOKEN_UNIQUE_REFERENCE + " = ? "
                                          + "AND " + COL_ATC + " = ? ;");
        sqLiteStatement.bindString(1, transactionCredentialStatus.getStatus());
        sqLiteStatement.bindString(2, transactionCredentialStatus.getTimestamp());
        sqLiteStatement.bindString(3, tokenUniqueReference);
        sqLiteStatement.bindLong(4, transactionCredentialStatus.getAtc());
        int count = sqLiteStatement.executeUpdateDelete();
        sqLiteStatement.clearBindings();
        if (count == 0) {
            throw new LdeUncheckedException("Unable to update database");
        }

    }

    /**
     * Insert a credential status in the transaction credential status table in the Lde
     *
     * @param tokenUniqueReference        the unique reference number of card
     * @param transactionCredentialStatus transaction credential status instance
     */
    private void insertTransactionCredentialStatus(final String tokenUniqueReference,
                                                   final TransactionCredentialStatus
                                                           transactionCredentialStatus) {
        final SQLiteDatabase sqLiteDb = mDatabaseHelper.getWritableDatabase();

        SQLiteStatement sqLiteStatement =
                sqLiteDb.compileStatement("INSERT INTO " + TABLE_TRANSACTION_CREDENTIAL_STATUS
                                          + " ( " + COL_TOKEN_UNIQUE_REFERENCE + " , " + COL_ATC
                                          + " , " + TRANSACTION_CREDENTIAL_STATUS + " , "
                                          + COL_TRANSACTION_TIMESTAMP + " ) "
                                          + " VALUES (?,?,?,?);");
        sqLiteStatement.bindString(1, tokenUniqueReference);
        sqLiteStatement.bindLong(2, transactionCredentialStatus.getAtc());
        sqLiteStatement.bindString(3, transactionCredentialStatus.getStatus());
        sqLiteStatement.bindString(4, transactionCredentialStatus.getTimestamp());
        long rowsAffected = sqLiteStatement.executeInsert();
        sqLiteStatement.clearBindings();
        if (rowsAffected == -1) {
            throw new LdeUncheckedException("Unable to update database");
        }
    }

    /**
     * Encrypt data by AES algorithm.
     *
     * @param data byte array of data which need to encrypt.
     * @return byte[] encrypted data.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    private byte[] encrypt(final byte[] data) throws McbpCryptoException, InvalidInput {
        if (data == null) {
            throw new InvalidInput("Invalid input data");
        }
        final byte[] key = getDatabaseKey();
        try {
            return encrypt(data, key);
        } finally {
            Utils.clearByteArray(key);
        }
    }

    /**
     * Encrypt data by AES algorithm.
     *
     * @param data byte array of data which need to encrypt.
     * @param key  Database Key which is generated using IMEI.
     * @return byte[] decrypted data.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    private byte[] encrypt(final byte[] data, final byte[] key)
            throws McbpCryptoException, InvalidInput {
        return mCryptoService.ldeEncryption(data, key);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param db instance of SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table creation
        db.execSQL(DatabaseHelper.CREATE_TABLE_ENVIRONMENT);
        db.execSQL(DatabaseHelper.CREATE_TABLE_CARD_PROFILES);
        db.execSQL(DatabaseHelper.CREATE_TABLE_SUK);
        db.execSQL(DatabaseHelper.CREATE_TABLE_TRANSACTIONS);
        db.execSQL(DatabaseHelper.CREATE_TABLE_MOBILE_KEY);
        db.execSQL(DatabaseHelper.CREATE_TABLE_TOKEN_UNIQUE_REFERENCE);
        db.execSQL(DatabaseHelper.CREATE_TABLE_TRANSACTION_CREDENTIAL_STATUS);
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db         instance of SQLiteDatabase
     * @param oldVersion old version of database
     * @param newVersion new version of database
     */
    @Override
    public void onUpdate(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        switch (oldVersion) {
            case 2:
            case 0x00010005:
                // Read Mobile keys table and decrypt data using IMEI number
                reEncryptMobileKeys(db);
                // Read Card profile table and decrypt data using IMEI number
                reEncryptCardProfiles(db);
                // Read SUKs table and decrypt data using IMEI number
                reEncryptSuks(db);
                //take backup of all transaction
                //drop old table
                //create new structure for table
                //insert old backed up transaction as per new structure
                modifyTransactionListTable(db);
                break;
            case 3:
            case 0x00010006:
                //take backup of all transaction
                //drop old table
                //create new structure for table
                //insert old backed up transaction as per new structure
                modifyTransactionListTable(db);
                break;
        }
    }

    /**
     * Method used to take backup of all transactions from previous version of db structure and
     * create new structure of transaction table
     *
     * @param db instance of SQLiteDatabase
     */
    private void modifyTransactionListTable(final SQLiteDatabase db) {
        List<TransactionLogWithApplicationCryptogram> transactionLogWithApplicationCryptograms =
                new ArrayList<>();
        mLogger.d("--------get Transaction Logs-----------");
        String query =
                "SELECT " + COL_CARD_ID + ", " + COL_TRANSACTION_TIMESTAMP + ", " +
                COL_TRANSACTION_LOG + " FROM " + TABLE_CARD_TRANSACTIONS_LIST;

        final Cursor cursor = db.rawQuery(query, null);

        if (!cursor.moveToFirst()) {
            cursor.close();

            db.execSQL(DatabaseHelper.DROP_TABLE_CARD_TRANSACTIONS_LIST);
            db.execSQL(DatabaseHelper.CREATE_TABLE_TRANSACTIONS);
            return;
        }
        do {
            final byte[] txLog = cursor.getBlob(cursor.getColumnIndex(COL_TRANSACTION_LOG));
            final String timestamp =
                    cursor.getString(cursor.getColumnIndex(COL_TRANSACTION_TIMESTAMP));
            final TransactionLogWithApplicationCryptogram txLogObj =
                    parseApplicationCryptogramVersionTransactionLogs(ByteArray.of(txLog),
                                                                     timestamp);
            transactionLogWithApplicationCryptograms
                    .add(txLogObj);
        } while (cursor.moveToNext());

        cursor.close();

        db.execSQL(DatabaseHelper.DROP_TABLE_CARD_TRANSACTIONS_LIST);
        db.execSQL(DatabaseHelper.CREATE_TABLE_TRANSACTIONS);

        for (TransactionLogWithApplicationCryptogram transactionLogWithApplicationCryptogram :
                transactionLogWithApplicationCryptograms) {
            insertTransactionLogIntoDatabase(db, transactionLogFromApplicationCryptogramLog(
                    transactionLogWithApplicationCryptogram),
                                             transactionLogWithApplicationCryptogram
                                                     .getTimestamp());
        }
    }

    /**
     * This function fetches transaction identifier for a transaction using transaction date
     * and transaction atc.
     *
     * @param transactionDate ByteArray
     * @param transactionAtc  ByteArray
     * @return TransactionIdentifier
     * @since 1.0.6a
     */
    @Override
    public ByteArray getTransactionIdentifier(final ByteArray transactionDate,
                                              final ByteArray transactionAtc) {

        String query =
                "SELECT " + COL_TRANSACTION_ID +
                " FROM " + TABLE_CARD_TRANSACTIONS_LIST +
                " WHERE " + COL_TRANSACTION_ATC + " = ? AND " + COL_TRANSACTION_DATE + " = ? ";


        String[] args = {transactionAtc.toHexString(), transactionDate.toHexString()};
        SQLiteDatabase sqliteDatabase = mDatabaseHelper.getReadableDatabase();
        final Cursor cursor = sqliteDatabase.rawQuery(query, args);

        if (!cursor.moveToFirst()) {
            return ByteArray.get(0);
        }
        final byte[] transactionIdentifier =
                cursor.getBlob(cursor.getColumnIndex(COL_TRANSACTION_ID));
        cursor.close();
        return ByteArray.of(transactionIdentifier);
    }


    /**
     * Get the TransactionLog from application cryptogram version of TransactionLog
     *
     * @param transactionLogWithApplicationCryptogram information read from application
     *                                                cryptogram Transaction log version of DB
     * @return TransactionLog
     * @since 1.0.6a
     */
    private TransactionLog transactionLogFromApplicationCryptogramLog(
            final TransactionLogWithApplicationCryptogram transactionLogWithApplicationCryptogram) {

        ByteArray transactionIdentifier = ByteArray.get(0);
        return TransactionLog
                .fromLdeData(transactionLogWithApplicationCryptogram.getDigitizedCardId(),
                             transactionLogWithApplicationCryptogram.getUnpredictableNumber(),
                             transactionLogWithApplicationCryptogram.getAtc(),
                             transactionLogWithApplicationCryptogram.getDate(),
                             transactionLogWithApplicationCryptogram.getAmount(),
                             transactionLogWithApplicationCryptogram.getCurrencyCode(),
                             transactionLogWithApplicationCryptogram.getCryptogramFormat(),
                             transactionIdentifier, false, false);

    }

    /**
     * Parse the application cryptogram version of TransactionLogs
     *
     * @param data      transaction log data
     * @param timestamp time stamp
     * @return TransactionLogPreviousVersion
     * @since 1.0.6a
     */
    private TransactionLogWithApplicationCryptogram
    parseApplicationCryptogramVersionTransactionLogs(ByteArray data, String timestamp) {

        final String mDigitizedCardId = data.copyOfRange(0, 17).toHexString();// 17
        final ByteArray mUnpredictableNumber = data.copyOfRange(17, 21);// 4
        final ByteArray mAtc = data.copyOfRange(21, 23);// 2
        final byte mCryptogramFormat = data.getByte(23);// 1
        final ByteArray mApplicationCryptogram = data.copyOfRange(24, 32);// 8
        final boolean mHostingMeJailbroken = (data.getByte(32) == (byte) 1);// 1
        final boolean mRecentAttack = (data.getByte(33) == (byte) 1);// 1
        final ByteArray mDate = data.copyOfRange(34, 37);// 3
        final ByteArray mAmount = data.copyOfRange(37, 43);// 6
        final ByteArray mCurrencyCode = data.copyOfRange(43, 45);// 2

        return new TransactionLogWithApplicationCryptogram(mDigitizedCardId, mUnpredictableNumber,
                                                           mAtc,
                                                           mCryptogramFormat,
                                                           mApplicationCryptogram,
                                                           mHostingMeJailbroken, mRecentAttack,
                                                           mDate,
                                                           mAmount,
                                                           mCurrencyCode, timestamp);
    }


    /**
     * When database upgrade this method will decrypt the suks from database key with imei and
     * re encrypt it with android id based database key.
     *
     * @param db the database
     */
    private void reEncryptSuks(final SQLiteDatabase db) {

        final String query =
                "SELECT " + COL_SUK_INFO + ", " + COL_SUK_ID + ", " + COL_SUK_CL_UMD + ", "
                + COL_SUK_CL_MD + ", " + COL_SUK_RP_UMD + ", " + COL_SUK_RP_MD + ", " + COL_IDN
                + ", " + COL_ATC + ", " + COL_HASH + ", " + COL_CARD_ID +
                " FROM " + TABLE_SUK_LIST;

        final Cursor cursor = db.rawQuery(query, null);
        if (!cursor.moveToFirst()) {
            return;
        }
        SukData[] sukDataArray = new SukData[cursor.getCount()];

        int count = 0;
        do {
            try {
                final byte[] sukInfo = cursor.getBlob(cursor.getColumnIndex(COL_SUK_INFO));
                final String sukId = cursor.getString(cursor.getColumnIndex(COL_SUK_ID));
                byte[] clUmd = cursor.getBlob(cursor.getColumnIndex(COL_SUK_CL_UMD));
                if (clUmd != null) {
                    clUmd = decryptAes(clUmd, getDatabaseKeyUsingImei());
                }
                byte[] clMd = cursor.getBlob(cursor.getColumnIndex(COL_SUK_CL_MD));
                if (clMd != null) {
                    clMd = decryptAes(clMd, getDatabaseKeyUsingImei());
                }
                byte[] rpUmd = cursor.getBlob(cursor.getColumnIndex(COL_SUK_RP_UMD));
                if (rpUmd != null) {
                    rpUmd = decryptAes(rpUmd, getDatabaseKeyUsingImei());
                }
                byte[] rpMd = cursor.getBlob(cursor.getColumnIndex(COL_SUK_RP_MD));
                if (rpMd != null) {
                    rpMd = decryptAes(rpMd, getDatabaseKeyUsingImei());
                }
                final byte[] idn = cursor.getBlob(cursor.getColumnIndex(COL_IDN));
                final byte[] atc = cursor.getBlob(cursor.getColumnIndex(COL_ATC));
                final String hash = cursor.getString(cursor.getColumnIndex(COL_HASH));
                final String cardId = cursor.getString(cursor.getColumnIndex(COL_CARD_ID));
                sukDataArray[count] =
                        new SukData(sukInfo, sukId, clUmd, clMd, rpUmd, rpMd, idn, atc, hash,
                                    cardId);
                count++;
            } catch (McbpCryptoException | InvalidInput e) {
                mLogger.d(e.getMessage());
            }
        }

        while (cursor.moveToNext());
        cursor.close();

        db.delete(TABLE_SUK_LIST, null, null);

        for (SukData sukData : sukDataArray) {
            try {
                SQLiteStatement sqLiteStmt = db
                        .compileStatement("INSERT INTO " + TABLE_SUK_LIST + " ( " + COL_SUK_INFO
                                          + " , " + COL_SUK_ID + " , " + COL_SUK_CL_UMD
                                          + " , " + COL_SUK_CL_MD + " , " + COL_SUK_RP_UMD
                                          + " , " + COL_SUK_RP_MD + " , " + COL_IDN
                                          + " , " + COL_ATC + " , " + COL_HASH + " , "
                                          + COL_CARD_ID + " ) "
                                          + " VALUES (?,?,?,?,?,?,?,?,?,?);");

                sqLiteStmt.bindBlob(1, sukData.getSuksInfo());
                sqLiteStmt.bindString(2, sukData.getSukId());

                if (sukData.getContactlessUmd() != null) {
                    sqLiteStmt
                            .bindBlob(3, encrypt(sukData.getContactlessUmd()));
                }
                if (sukData.getContactlessMd() != null) {
                    sqLiteStmt.bindBlob(4, encrypt(sukData.getContactlessMd()));
                }
                if (sukData.getRemoteUmd() != null) {
                    sqLiteStmt.bindBlob(5, encrypt(sukData.getRemoteUmd()));
                }
                if (sukData.getRemoteMd() != null) {
                    sqLiteStmt.bindBlob(6, encrypt(sukData.getRemoteMd()));
                }
                sqLiteStmt.bindBlob(7, sukData.getIdn());
                sqLiteStmt.bindBlob(8, sukData.getAtc());
                sqLiteStmt.bindString(9, sukData.getHash());
                sqLiteStmt.bindString(10, sukData.getCardId());
                long rowId = sqLiteStmt.executeInsert();
                sqLiteStmt.clearBindings();
                if (rowId == -1) {
                    throw new LdeUncheckedException("Unable to update database");
                }
            } catch (McbpCryptoException | InvalidInput e) {
                mLogger.d(e.getMessage());
            }
        }

    }

    /**
     * When database upgrade this method will decrypt the card profile from database key with
     * imei and re encrypt it with android id based database key.
     *
     * @param db the database
     */
    private void reEncryptCardProfiles(final SQLiteDatabase db) {
        String query =
                "SELECT " + COL_CARD_ID + " , " + COL_PROFILE_DATA + " , " + COL_PROFILE_STATE +
                " , " + COL_CARD_PIN_STATE + " FROM " + TABLE_CARD_PROFILES_LIST;
        final Cursor cursor = db.rawQuery(query, null);
        CardProfileData[] cardProfileDataArray = new CardProfileData[cursor.getCount()];
        if (!cursor.moveToFirst()) {
            return;
        }
        int count = 0;
        do {
            try {
                final String cardId = cursor.getString(cursor.getColumnIndex(COL_CARD_ID));
                final byte[] cardValue = decryptAes(cursor.getBlob(
                        cursor.getColumnIndex(COL_PROFILE_DATA)), getDatabaseKeyUsingImei());
                final long profileState = cursor.getLong(cursor.getColumnIndex(COL_PROFILE_STATE));
                final long pinState = cursor.getLong(cursor.getColumnIndex(COL_CARD_PIN_STATE));
                cardProfileDataArray[count] =
                        new CardProfileData(cardId, cardValue, profileState, pinState);
                count++;
            } catch (McbpCryptoException | InvalidInput e) {
                mLogger.d(e.getMessage());
            }
        } while (cursor.moveToNext());
        cursor.close();

        db.delete(TABLE_CARD_PROFILES_LIST, null, null);
        for (CardProfileData cardProfileData : cardProfileDataArray) {
            try {

                SQLiteStatement sqLiteStatement =
                        db.compileStatement("INSERT INTO " + TABLE_CARD_PROFILES_LIST
                                            + " ( " + COL_CARD_ID + " , " + COL_PROFILE_DATA
                                            + " , " + COL_PROFILE_STATE + " , " + COL_CARD_PIN_STATE
                                            + " ) " + " VALUES (?,?,?,?);");
                sqLiteStatement.bindString(1, cardProfileData.getCardId());
                sqLiteStatement.bindBlob(2, encrypt(cardProfileData.getCardValue()));
                sqLiteStatement.bindLong(3, cardProfileData.getProfileState());
                sqLiteStatement.bindLong(4, cardProfileData.getPinState());
                long l = sqLiteStatement.executeInsert();
                sqLiteStatement.clearBindings();
                if (l == -1) {
                    throw new LdeUncheckedException("Unable to update the database");
                }
            } catch (McbpCryptoException | InvalidInput e) {
                mLogger.d(e.getMessage());
            }
        }
    }

    /**
     * When database upgrade this method will decrypt the mobile keys from database key with
     * imei and re encrypt it with android id based database key.
     *
     * @param db the database
     */
    private void reEncryptMobileKeys(final SQLiteDatabase db) {
        final String query =
                "SELECT " + COL_CARD_ID + " , " + COL_MOBILE_KEY_SET_ID + " , "
                + COL_MOBILE_KEY_TYPE + " , " + COL_MOBILE_KEY_VALUE + " FROM " + TABLE_MOBILE_KEYS;
        final Cursor cursor = db.rawQuery(query, null);

        if (!cursor.moveToFirst()) {
            //no data available.  close cursor
            cursor.close();
            return;
        }
        MobileKeyData[] mobileKeyDataArray = new MobileKeyData[cursor.getCount()];
        int count = 0;
        do {
            try {
                final String cardId = cursor.getString(cursor.getColumnIndex(COL_CARD_ID));
                final String keySetId =
                        cursor.getString(cursor.getColumnIndex(COL_MOBILE_KEY_SET_ID));
                final String keyType = cursor.getString(cursor.getColumnIndex(COL_MOBILE_KEY_TYPE));
                final byte[] keyValue = decryptAes(cursor.getBlob(cursor.getColumnIndex(
                        COL_MOBILE_KEY_VALUE)), getDatabaseKeyUsingImei());
                mobileKeyDataArray[count] = new MobileKeyData(cardId, keySetId, keyType, keyValue);
                count++;
            } catch (McbpCryptoException | InvalidInput e) {
                // Currently ignoring
                mLogger.d(e.getMessage());
            }
        } while (cursor.moveToNext());

        //data read from cursor.  close it
        cursor.close();

        // Here we are ignoring any results
        db.delete(TABLE_MOBILE_KEYS, null, null);
        // Again insert mobile keys by encrypting with new Lde key
        for (MobileKeyData mobileKeyData : mobileKeyDataArray) {
            try {
                SQLiteStatement sqLiteStatement =
                        db.compileStatement("INSERT INTO " + TABLE_MOBILE_KEYS
                                            + " ( " + COL_MOBILE_KEY_VALUE + " , "
                                            + COL_MOBILE_KEY_SET_ID + " , " + COL_CARD_ID + " , "
                                            + COL_MOBILE_KEY_TYPE + " ) " + " VALUES (?,?,?,?);");
                sqLiteStatement.bindBlob(1, encrypt(mobileKeyData.getKeyValue()));
                sqLiteStatement.bindString(2, mobileKeyData.getMobileKeySetId());
                sqLiteStatement.bindString(3, mobileKeyData.getDigitizedCardId());
                sqLiteStatement.bindString(4, mobileKeyData.getKeyType());
                final long rowId = sqLiteStatement.executeInsert();
                if (rowId == -1) {
                    throw new LdeUncheckedException("Unable to store the mobile key");
                }
            } catch (McbpCryptoException | InvalidInput e) {
                mLogger.d(e.getMessage());
            }
        }
    }

    /**
     * Get the database key which is generated using imei
     *
     * @return byte array of database key
     */
    private byte[] getDatabaseKeyUsingImei() {
        final SharedPreferences preferences =
                mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (!preferences.getBoolean(KEY_CREATED, false)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_STORAGE, mCryptoService.getRandomByteArray(16).toHexString());
            editor.putBoolean(KEY_CREATED, true);
            editor.apply();
        }
        //A 64-bit number (as a hex string) that is randomly generated when the user first sets up
        // the device and should remain constant for the lifetime of the user's device.
        // The value may change if a factory reset is performed on the device.
        // When a device has multiple users (available on certain devices running Android 4.2 or
        // higher), each user appears as a completely separate device, so the ANDROID_ID value is
        // unique to each user.
        String deviceId = ((TelephonyManager)
                mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

        if (deviceId == null) {
            deviceId = preferences.getString(KEY_DEVICE_ID, null);
            // We use an empty string as last resort
            deviceId = (deviceId == null) ? "" : deviceId;
        } else {
            if (preferences.getString(KEY_DEVICE_ID, null) == null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_DEVICE_ID, deviceId);
                editor.apply();
            }
        }
        // Generate MPA Key
        final byte[] mpaKey = BuildConfig.MPA_KEY;
        final byte[] uniqueId = deviceId.getBytes();
        final byte[] uniqueMobileDeviceHash;

        try {
            uniqueMobileDeviceHash = mCryptoService.sha256(uniqueId);
        } catch (final McbpCryptoException e) {
            throw new RuntimeException("Unable to generate the DB key: " + e);
        }

        final byte[] rndStorage = preferences.getString(KEY_STORAGE, "").getBytes();
        final int length = uniqueMobileDeviceHash.length + mpaKey.length + rndStorage.length;
        final byte[] hashInput = new byte[length];

        System.arraycopy(uniqueMobileDeviceHash, 0, hashInput, 0, uniqueMobileDeviceHash.length);
        System.arraycopy(mpaKey, 0, hashInput, uniqueMobileDeviceHash.length, mpaKey.length);
        System.arraycopy(rndStorage, 0, hashInput,
                         uniqueMobileDeviceHash.length + mpaKey.length, rndStorage.length);

        // Clear temporary variables
        Utils.clearByteArray(uniqueMobileDeviceHash);
        Utils.clearByteArray(rndStorage);

        final byte[] generatedKey;
        try {
            generatedKey = mCryptoService.sha256(hashInput);
        } catch (final McbpCryptoException e) {
            throw new RuntimeException("Unable to generate the DB key: " + e);
        }
        Utils.clearByteArray(hashInput);
        return generatedKey;
    }

    /**
     * Added API for testing purpose
     */
    void clearAllDataFromDb() {
        final SQLiteDatabase sqliteDatabase = mDatabaseHelper.getWritableDatabase();
        sqliteDatabase.delete(TABLE_MOBILE_KEYS, null, null);
        sqliteDatabase.delete(TABLE_CARD_PROFILES_LIST, null, null);
        sqliteDatabase.delete(TABLE_SUK_LIST, null, null);
        sqliteDatabase.delete(TABLE_CARD_TRANSACTIONS_LIST, null, null);
        sqliteDatabase.delete(TABLE_TOKEN_UNIQUE_REFERENCE_LIST, null, null);
        sqliteDatabase.delete(TABLE_TRANSACTION_CREDENTIAL_STATUS, null, null);
        sqliteDatabase.delete(TABLE_ENVIRONMENT_CONT, null, null);
    }

    /**
     * Check whether the input Single Use Key is already in the database or not
     *
     * @param singleUseKey @Instance SingleUseKey
     * @return true if input single use key is already in database else return false
     * @throws InvalidInput
     */
    private boolean isDuplicateSuk(final SingleUseKey singleUseKey)
            throws InvalidInput, McbpCryptoException {
        List<SingleUseKey> singleUseKeys =
                getAllSingleUseKeys(singleUseKey.getDigitizedCardId().toHexString());
        if (singleUseKeys.contains(singleUseKey)) {
            return true;
        }
        return false;
    }
}
