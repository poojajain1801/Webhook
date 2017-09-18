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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.mastercard.mcbp.lde.DatabaseInfo.CMS_MPA_ID;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_ATC;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_CARD_ID;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_CARD_PIN_STATE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_HASH;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_IDN;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_INIT_STATE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_LATITUDE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_LIFE_CYCLE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_LONGITUDE;
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_MNO;
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
import static com.mastercard.mcbp.lde.DatabaseInfo.COL_WSP_NAME;
import static com.mastercard.mcbp.lde.DatabaseInfo.DATABASE_VERSION;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_CARD_PROFILES_LIST;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_CARD_TRANSACTIONS_LIST;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_ENVIRONMENT_CONT;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_MOBILE_KEYS;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_SUK_LIST;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_TOKEN_UNIQUE_REFERENCE_LIST;
import static com.mastercard.mcbp.lde.DatabaseInfo.TABLE_TRANSACTION_CREDENTIAL_STATUS;
import static com.mastercard.mcbp.lde.DatabaseInfo.TRANSACTION_CREDENTIAL_STATUS;

/**
 * A Helper class to manage database schema and its version
 */
class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * Helper class to upgrade database
     */
    private DatabaseEventListener mDatabaseEventListener;

    /**
     * Create Environment table query
     */
    public static final String CREATE_TABLE_ENVIRONMENT =
            "CREATE TABLE "
            + TABLE_ENVIRONMENT_CONT + " ("
            + CMS_MPA_ID + " BLOBv PRIMARY KEY NOT NULL, "
            + COL_INIT_STATE + " INTEGER NOT NULL, "
            + COL_WALLET_STATE + " INTEGER NOT NULL, "
            + COL_URL + " TEXT, "
            + COL_MPA_FGP + " BLOB NOT NULL, "
            + COL_LIFE_CYCLE + " TEXT, "
            + COL_MNO + " TEXT , "
            + COL_LATITUDE + " DOUBLE , "
            + COL_LONGITUDE + " DOUBLE , "
            + COL_WSP_NAME + " TEXT , "
            + COL_WALLET_PIN_STATE + " INTEGER NOT NULL); ";
    /**
     * Create Card Profiles table query
     */
    public static final String CREATE_TABLE_CARD_PROFILES =
            "CREATE TABLE "
            + TABLE_CARD_PROFILES_LIST + " ("
            + COL_CARD_ID + " TEXT PRIMARY KEY NOT NULL, "
            + COL_PROFILE_STATE + " INTEGER NOT NULL, "
            + COL_PROFILE_DATA + " BLOB NOT NULL, "
            + COL_CARD_PIN_STATE + " INTEGER NOT NULL); ";
    /**
     * Create Token Unique Reference table query
     */
    public static final String CREATE_TABLE_TOKEN_UNIQUE_REFERENCE =
            "CREATE TABLE "
            + TABLE_TOKEN_UNIQUE_REFERENCE_LIST + " ("
            + COL_TOKEN_UNIQUE_REFERENCE + " TEXT PRIMARY KEY NOT NULL, "
            + COL_CARD_ID + " TEXT unique); ";
    /**
     * Create SingleUseKey table query
     */
    public static final String CREATE_TABLE_SUK =
            "CREATE TABLE " + TABLE_SUK_LIST + " ("
            + COL_SUK_ID + " TEXT NOT NULL, "
            + COL_SUK_INFO + " BLOB NOT NULL, "
            + COL_SUK_CL_UMD + " BLOB, "
            + COL_SUK_CL_MD + " BLOB, "
            + COL_SUK_RP_UMD + " BLOB, "
            + COL_SUK_RP_MD + " BLOB, "
            + COL_IDN + " BLOB NOT NULL, "
            + COL_ATC + " BLOB NOT NULL, "
            + COL_HASH + " BLOB NOT NULL, "
            + COL_CARD_ID + " TEXT NOT NULL, "
            + "PRIMARY KEY (" + COL_CARD_ID + "," + COL_ATC + "," + COL_SUK_ID + ")); ";
    /**
     * Create Transaction Logs table query
     */
    public static final String CREATE_TABLE_TRANSACTIONS =
            "CREATE TABLE "
            + TABLE_CARD_TRANSACTIONS_LIST + " ("
            + COL_CARD_ID + " TEXT NOT NULL, "
            + COL_TRANSACTION_TIMESTAMP + " INTEGER  PRIMARY KEY NOT NULL, "
            + COL_TRANSACTION_ATC + " TEXT NOT NULL, "
            + COL_TRANSACTION_DATE + " TEXT NOT NULL, "
            + COL_TRANSACTION_ID + " BLOB, "
            + COL_TRANSACTION_LOG + " BLOB NOT NULL " + " ); ";
    /**
     * Create Mobile Keys table query
     */
    public static final String CREATE_TABLE_MOBILE_KEY =
            "CREATE TABLE "
            + TABLE_MOBILE_KEYS + " (" + COL_MOBILE_KEY_SET_ID + " TEXT, "
            + COL_MOBILE_KEY_TYPE + " TEXT NOT NULL, "
            + COL_MOBILE_KEY_VALUE + " BLOB NOT NULL, "
            + COL_CARD_ID + " TEXT, "
            + "PRIMARY KEY (" + COL_CARD_ID + ","
            + COL_MOBILE_KEY_SET_ID + "," + COL_MOBILE_KEY_TYPE + ")); ";
    /**
     * Create Transaction Credential Status table query
     */
    public static final String CREATE_TABLE_TRANSACTION_CREDENTIAL_STATUS =
            "CREATE TABLE "
            + TABLE_TRANSACTION_CREDENTIAL_STATUS + " ("
            + COL_TOKEN_UNIQUE_REFERENCE + " TEXT NOT NULL, "
            + COL_ATC + " INTEGER, "
            + TRANSACTION_CREDENTIAL_STATUS + " TEXT NOT NULL, "
            + COL_TRANSACTION_TIMESTAMP + " TEXT NOT NULL, "
            + "PRIMARY KEY (" + COL_TOKEN_UNIQUE_REFERENCE + "," + COL_ATC + ")); ";


    /**
     * Drop Transaction List table query
     */
    public static final String DROP_TABLE_CARD_TRANSACTIONS_LIST =
            "DROP TABLE IF EXISTS " + TABLE_CARD_TRANSACTIONS_LIST;

    /**
     * Default constructor to initialize DatabaseHelper class
     *
     * @param context               Application context
     * @param databaseName          Name of the database
     * @param databaseEventListener Listener to get database events
     */
    public DatabaseHelper(final Context context, final String databaseName,
                          final DatabaseEventListener databaseEventListener) {
        super(context, databaseName, null, DATABASE_VERSION);
        this.mDatabaseEventListener = databaseEventListener;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        mDatabaseEventListener.onCreate(db);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        mDatabaseEventListener.onUpdate(db, oldVersion, newVersion);
    }
}