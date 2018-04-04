package com.comviva.hceservice.common.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.comviva.hceservice.LukInfo;
import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.fcm.RnsInfo;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.userinterface.MakeDefaultListener;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;

import java.util.Date;

import static com.comviva.hceservice.common.database.CommonDatabase.INTEGER_VALUE;
import static com.comviva.hceservice.common.database.CommonDatabase.TEXT;
import static com.comviva.hceservice.common.database.CommonDatabase.TEXT_COLON;

public class CommonDatabase implements CommonDb {
    private DatabaseHelper commonDb;
    public static final String TEXT = " TEXT, ";
    public static final String TEXT_COLON = " TEXT);";
    public static final String INTEGER_VALUE = " INTEGER, ";

    public CommonDatabase(Context context) {
        this.commonDb = new DatabaseHelper(context, DatabaseProperties.DATABASE_NAME);
    }

    @Override
    public void initializeComvivaSdk(ComvivaSdkInitData comvivaSdkInitData) {
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_APP_PROPERTIES,    // The table to query
                    null,                             // The columns to return
                    null,                             // The columns for the WHERE clause
                    null,                             // The values for the WHERE clause
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseProperties.COL_INITIALIZE_STATE, comvivaSdkInitData.isInitState() ? 1 : 0);
            contentValues.put(DatabaseProperties.COL_RNS_ID, comvivaSdkInitData.getRnsInfo().getRegistrationId());
            contentValues.put(DatabaseProperties.COL_RNS_TYPE, comvivaSdkInitData.getRnsInfo().getRnsType().name());
            contentValues.put(DatabaseProperties.COL_VTS_INIT_STATE, comvivaSdkInitData.isVtsInitialized());
            contentValues.put(DatabaseProperties.COL_MDES_INIT_STATE, comvivaSdkInitData.isMdesInitialized());
            contentValues.put(DatabaseProperties.COL_REPLENISH_THRESOLD_LIMIT, comvivaSdkInitData.getReplenishmentThresold());
            contentValues.put(DatabaseProperties.COL_HVT_SUPPORTED, comvivaSdkInitData.isHvtSupport()?1:0);
            if(comvivaSdkInitData.isHvtSupport()) {
                contentValues.put(DatabaseProperties.COL_HVT_LIMIT, comvivaSdkInitData.getHvtLimit());
            }
            String clientWalletAccId = comvivaSdkInitData.getClientWalletAccountId();
            if((null != clientWalletAccId) && (!clientWalletAccId.isEmpty())) {
                    contentValues.put(DatabaseProperties.COL_CLIENT_WALLET_ACC_ID, comvivaSdkInitData.getClientWalletAccountId());
            }

            // Need to Update only row
            if (cursor.moveToFirst()) {
                cursor.close();
                sqLiteDb.update(DatabaseProperties.TBL_APP_PROPERTIES, contentValues, null, null);
            } else {
                // Insert only row
                cursor.close();
                sqLiteDb.insert(DatabaseProperties.TBL_APP_PROPERTIES, null, contentValues);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
    }

    @Override
    public void setRnsInfo(RnsInfo rnsInfo) {
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_APP_PROPERTIES,    // The table to query
                    null,                             // The columns to return
                    null,                             // The columns for the WHERE clause
                    null,                             // The values for the WHERE clause
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseProperties.COL_RNS_ID, rnsInfo.getRegistrationId());
            contentValues.put(DatabaseProperties.COL_RNS_TYPE, rnsInfo.getRnsType().name());

            // Need to Update only row
            if (cursor.moveToFirst()) {
                cursor.close();
                sqLiteDb.update(DatabaseProperties.TBL_APP_PROPERTIES, contentValues, null, null);
            } else {
                // Insert only row
                cursor.close();
                sqLiteDb.insert(DatabaseProperties.TBL_APP_PROPERTIES, null, contentValues);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
    }

    @Override
    public ComvivaSdkInitData getInitializationData() {
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        ComvivaSdkInitData initData = new ComvivaSdkInitData();
        try {
            sqLiteDb = commonDb.getReadableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_APP_PROPERTIES,    // The table to query
                    null,  // The columns to return
                    null,                             // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                            // don't group the rows
                    null,                            // don't filter by row groups
                    null                             // The sort order
            );

            if (cursor.moveToFirst()) {
                initData.setInitState(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_INITIALIZE_STATE)) == 1);
                RnsInfo rnsInfo = new RnsInfo();
                rnsInfo.setRegistrationId(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_RNS_ID)));
                String rnsType = cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_RNS_TYPE));
                if (rnsType != null) {
                    rnsInfo.setRnsType(RnsInfo.getRnsType(rnsType));
                }
                initData.setRnsInfo(rnsInfo);
                initData.setVtsInitState(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_VTS_INIT_STATE)) == 1);
                initData.setMdesInitState(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_MDES_INIT_STATE)) == 1);
                initData.setHvtSupport(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_HVT_SUPPORTED)) == 1);
                if(initData.isHvtSupport()) {
                    initData.setHvtLimit(cursor.getDouble(cursor.getColumnIndex(DatabaseProperties.COL_HVT_LIMIT)));
                }
                initData.setReplenishmentThresold(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_REPLENISH_THRESOLD_LIMIT)));
                initData.setClientWalletAccountId(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_CLIENT_WALLET_ACC_ID)));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
        return initData;
    }

    @Override
    public void resetDatabase() {
        SQLiteDatabase sqLiteDb = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            sqLiteDb.delete(DatabaseProperties.TBL_APP_PROPERTIES, null, null);
            sqLiteDb.delete(DatabaseProperties.TBL_DEFAULT_CARD, null, null);
        } finally {
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
    }

    @Override
    public boolean setDefaultCard(PaymentCard paymentCard) {
        boolean isSuccess = true;
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_DEFAULT_CARD,    // The table to query
                    null,                             // The columns to return
                    null,                             // The columns for the WHERE clause
                    null,                             // The values for the WHERE clause
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );
            String cardUniqueId = paymentCard.getCardUniqueId();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseProperties.COL_CARD_TYPE, paymentCard.getCardType().name());

            switch (paymentCard.getCardType()) {
                case MDES:
                    contentValues.put(DatabaseProperties.COL_CARD_UNIQUE_ID, cardUniqueId);
                    class ComvivaDefaultListener implements MakeDefaultListener {
                        private boolean isSuccess;

                        @Override
                        public void onSuccess() {
                            isSuccess = true;
                        }

                        @Override
                        public void onAbort() {
                            isSuccess = false;
                        }
                    }
                    ComvivaDefaultListener defaultListener = new ComvivaDefaultListener();
                    McbpCardApi.setAsDefaultCardForContactlessPayment(cardUniqueId, defaultListener);
                    isSuccess = defaultListener.isSuccess;
                    break;

                case VTS:
                    contentValues.put(DatabaseProperties.COL_CARD_UNIQUE_ID, cardUniqueId);
                    break;

                default:
                    break;
            }

            if (!isSuccess) {
                return false;
            }

            // Need to Update only row
            if (cursor.moveToFirst()) {
                cursor.close();
                sqLiteDb.update(DatabaseProperties.TBL_DEFAULT_CARD, contentValues, null, null);
            } else {
                // Insert only row
                cursor.close();
                sqLiteDb.insert(DatabaseProperties.TBL_DEFAULT_CARD, null, contentValues);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
        return true;
    }

    @Override
    public void resetDefaultCard() {
        SQLiteDatabase sqLiteDb = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            sqLiteDb.delete(DatabaseProperties.TBL_DEFAULT_CARD, null, null);
        } finally {
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
    }

    @Override
    public String getDefaultCardUniqueId() {
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            sqLiteDb = commonDb.getReadableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_DEFAULT_CARD,    // The table to query
                    null,                             // The columns to return
                    null,                             // The columns for the WHERE clause
                    null,                             // The values for the WHERE clause
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );

            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_CARD_UNIQUE_ID));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
        return null;
    }

    @Override
    public PaymentCard getDefaultCard() {
        PaymentCard defaultCard = null;
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            sqLiteDb = commonDb.getReadableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_DEFAULT_CARD,    // The table to query
                    null,                             // The columns to return
                    null,                             // The columns for the WHERE clause
                    null,                             // The values for the WHERE clause
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );

            if (cursor.moveToFirst()) {
                String cardUniqueId = cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_CARD_UNIQUE_ID));
                CardType cardType = CardType.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_CARD_TYPE)));

                switch (cardType) {
                    case MDES:
                        McbpCard card = McbpCardApi.getMcbpCard(cardUniqueId);
                        defaultCard = PaymentCard.getPaymentCard(card);
                        break;

                    case VTS:
                        VisaPaymentSDK visaPaymentSDK = VisaPaymentSDKImpl.getInstance();
                        TokenKey tokenKey = visaPaymentSDK.getTokenKeyForProvisionedToken(cardUniqueId);
                        if (tokenKey!=null) {
                            TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                            defaultCard = PaymentCard.getPaymentCard(tokenData);
                        }
                        break;

                    case UNKNOWN:
                        return null;
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
        return defaultCard;
    }

    @Override
    public LukInfo getLukInfoVisa(String cardUniqueId) {
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            sqLiteDb = commonDb.getReadableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_VISA_LUK_INFO,     // The table to query
                    null,       // The columns to return
                    DatabaseProperties.COL_CARD_UNIQUE_ID + "=?",   // The columns for the WHERE clause
                    new String[]{cardUniqueId},             // The values for the WHERE clause
                    null,                                           // don't group the rows
                    null,                                            // don't filter by row groups
                    null                                            // The sort order
            );

            if (cursor.moveToFirst()) {
                LukInfo lukInfo = new LukInfo();
                lukInfo.setCard(cardUniqueId);
                lukInfo.setNoOfPaymentsRemaining(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_MAX_PAYMENTS)));
                String strKeyExp = cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_LUK_EXP_TS));
                Date keyExp = new Date(Long.parseLong(strKeyExp));
                lukInfo.setKeyExpTime(keyExp);
                return lukInfo;
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
        return null;
    }

    @Override
    public boolean consumeLuk(PaymentCard card) {
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_VISA_LUK_INFO,    // The table to query
                    null,                             // The columns to return
                    DatabaseProperties.COL_CARD_UNIQUE_ID + "=?",   // The columns for the WHERE clause
                    new String[]{card.getCardUniqueId()},             // The values for the WHERE clause
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );
            if(cursor.moveToFirst()) {
                int noOfPmts = cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_MAX_PAYMENTS));
                cursor.close();

                noOfPmts--;
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseProperties.COL_MAX_PAYMENTS, noOfPmts);

                return sqLiteDb.update(DatabaseProperties.TBL_VISA_LUK_INFO, contentValues,
                        DatabaseProperties.COL_CARD_UNIQUE_ID + "=?",
                        new String[]{card.getCardUniqueId()}) == 1;
            } else {
                return false;
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
    }

    @Override
    public boolean insertLukInfo(LukInfo lukInfo) {
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_VISA_LUK_INFO,    // The table to query
                    null,                             // The columns to return
                    DatabaseProperties.COL_CARD_UNIQUE_ID + "=?",   // The columns for the WHERE clause
                    new String[]{lukInfo.getCardUniqueId()},             // The values for the WHERE clause
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );

            if (cursor.moveToFirst()) {
                cursor.close();
                return false;
            } else {
                cursor.close();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseProperties.COL_CARD_UNIQUE_ID, lukInfo.getCardUniqueId());
                contentValues.put(DatabaseProperties.COL_MAX_PAYMENTS, lukInfo.getNoOfPaymentsRemaining());
                contentValues.put(DatabaseProperties.COL_LUK_EXP_TS, Long.toString(lukInfo.getKeyExpTime().getTime()));

                sqLiteDb.insert(DatabaseProperties.TBL_VISA_LUK_INFO, null, contentValues);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
        return true;
    }

    @Override
    public boolean updateLukInfo(LukInfo lukInfo) {
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_VISA_LUK_INFO,    // The table to query
                    null,                             // The columns to return
                    DatabaseProperties.COL_CARD_UNIQUE_ID + "=?",   // The columns for the WHERE clause
                    new String[]{lukInfo.getCardUniqueId()},             // The values for the WHERE clause
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );
            if(cursor.moveToFirst()) {
                cursor.close();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseProperties.COL_MAX_PAYMENTS, lukInfo.getNoOfPaymentsRemaining());
                contentValues.put(DatabaseProperties.COL_LUK_EXP_TS, lukInfo.getKeyExpTime().getTime());

                return sqLiteDb.update(DatabaseProperties.TBL_VISA_LUK_INFO, contentValues,
                        DatabaseProperties.COL_CARD_UNIQUE_ID + "=?",
                        new String[]{lukInfo.getCardUniqueId()}) == 1;
            } else {
                return false;
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
    }

    @Override
    public void deleteLukInfo(PaymentCard card) {
        SQLiteDatabase sqLiteDb = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            sqLiteDb.delete(DatabaseProperties.TBL_VISA_LUK_INFO, DatabaseProperties.COL_CARD_UNIQUE_ID, new String[]{card.getCardUniqueId()});
        } finally {
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
    }
}

class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_APP_PROPERTIES = "CREATE TABLE " + DatabaseProperties.TBL_APP_PROPERTIES + " ("
            + DatabaseProperties.COL_INITIALIZE_STATE + INTEGER_VALUE
            + DatabaseProperties.COL_RNS_ID + TEXT
            + DatabaseProperties.COL_RNS_TYPE + TEXT
            + DatabaseProperties.COL_VTS_INIT_STATE + TEXT
            + DatabaseProperties.COL_MDES_INIT_STATE + TEXT
            + DatabaseProperties.COL_REPLENISH_THRESOLD_LIMIT + INTEGER_VALUE
            + DatabaseProperties.COL_HVT_SUPPORTED + INTEGER_VALUE
            + DatabaseProperties.COL_HVT_LIMIT + " REAL, "
            + DatabaseProperties.COL_CLIENT_WALLET_ACC_ID + TEXT_COLON;

    private static final String CREATE_TABLE_DEFAULT_CARD = "CREATE TABLE if not exists " + DatabaseProperties.TBL_DEFAULT_CARD + " ("
            + DatabaseProperties.COL_CARD_UNIQUE_ID + " TEXT,"
            + DatabaseProperties.COL_CARD_TYPE + TEXT_COLON;

    private static final String CREATE_TABLE_VISA_LUK_INFO = "CREATE TABLE if not exists " + DatabaseProperties.TBL_VISA_LUK_INFO + " ("
            + DatabaseProperties.COL_CARD_UNIQUE_ID + " TEXT,"
            + DatabaseProperties.COL_MAX_PAYMENTS + " INTEGER,"
            + DatabaseProperties.COL_LUK_EXP_TS + TEXT_COLON;

    public DatabaseHelper(final Context context, final String databaseName) {
        super(context, databaseName, null, DatabaseProperties.DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Called when DB version upgrade required
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APP_PROPERTIES);
        db.execSQL(CREATE_TABLE_DEFAULT_CARD);
        db.execSQL(CREATE_TABLE_VISA_LUK_INFO);
    }
}
