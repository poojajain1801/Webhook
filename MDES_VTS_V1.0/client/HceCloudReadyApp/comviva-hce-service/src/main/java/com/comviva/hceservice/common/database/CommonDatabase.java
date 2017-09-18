package com.comviva.hceservice.common.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.tds.TdsRegistrationData;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.userinterface.MakeDefaultListener;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;
import com.visa.cbp.sdk.facade.data.TokenKey;

public class CommonDatabase implements CommonDb {
    private DatabaseHelper commonDb;

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
                rnsInfo.setRnsType(RnsInfo.RNS_TYPE.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_RNS_TYPE))));
                initData.setRnsInfo(rnsInfo);
                initData.setVtsInitState(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_VTS_INIT_STATE)) == 1);
                initData.setMdesInitState(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_MDES_INIT_STATE)) == 1);
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

    /*@Override
    public RmPendingTask getRmPendingTask() {
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        RmPendingTask rmPendingTask = new RmPendingTask();
        try {
            sqLiteDb = commonDb.getReadableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_RM_PENDING_TASK,    // The table to query
                    null,                             // The columns to return
                    null,                             // The columns for the WHERE clause
                    null,                             // The values for the WHERE clause
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );

            if (cursor.moveToFirst()) {
                rmPendingTask.setTaskId(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_TASK_ID)));
                rmPendingTask.setTokenUniqueReference(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE)));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
        return rmPendingTask;
    }

    @Override
    public void saveRmPendingTask(RmPendingTask rmPendingTask) {
        SQLiteDatabase sqLiteDb = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseProperties.COL_TASK_ID, rmPendingTask.getTaskId());
            contentValues.put(DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE, rmPendingTask.getTokenUniqueReference());
            // Insert task
            sqLiteDb.insert(DatabaseProperties.TBL_RM_PENDING_TASK, null, contentValues);
        } finally {
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
    }*/

    @Override
    public void saveTdsRegistrationCode(TdsRegistrationData tdsRegistrationData) {
        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE, tdsRegistrationData.getTokenUniqueReference());
            contentValues.put(DatabaseProperties.COL_TDS_REG_CODE1, tdsRegistrationData.getTdsRegistrationCode1());
            contentValues.put(DatabaseProperties.COL_TDS_AUTH_CODE, tdsRegistrationData.getAuthenticationCode());
            contentValues.put(DatabaseProperties.COL_TDS_URL, tdsRegistrationData.getTdsUrl());

            sqLiteDb = commonDb.getWritableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_TDS_REG,
                    null,
                    DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE + "=?",
                    new String[]{tdsRegistrationData.getTokenUniqueReference()},
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );

            if (cursor.getCount() == 0) {
                sqLiteDb.insert(DatabaseProperties.TBL_TDS_REG, null, contentValues);
            } else {
                sqLiteDb.update(DatabaseProperties.TBL_TDS_REG,
                        contentValues,
                        DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE + "=" + tdsRegistrationData.getTokenUniqueReference(),
                        null);
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
    public TdsRegistrationData getTdsRegistrationData(String tokenUniqueReference) {
        TdsRegistrationData registrationData = new TdsRegistrationData();

        SQLiteDatabase sqLiteDb = null;
        Cursor cursor = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            cursor = sqLiteDb.query(DatabaseProperties.TBL_TDS_REG,
                    null,
                    DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE + "=?",
                    new String[]{tokenUniqueReference},
                    null,                             // don't group the rows
                    null,                             // don't filter by row groups
                    null                              // The sort order
            );

            if (cursor.moveToFirst()) {
                registrationData.setTokenUniqueReference(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE)));
                registrationData.setAuthenticationCode(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_TDS_AUTH_CODE)));
                registrationData.setTdsUrl(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_TDS_URL)));
                registrationData.setTdsRegistrationCode1(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_TDS_REG_CODE1)));
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (sqLiteDb != null && sqLiteDb.isOpen()) {
                sqLiteDb.close();
            }
        }
        return registrationData;
    }

    @Override
    public void resetDatabase() {
        SQLiteDatabase sqLiteDb = null;
        try {
            sqLiteDb = commonDb.getWritableDatabase();
            sqLiteDb.delete(DatabaseProperties.TBL_APP_PROPERTIES, null, null);
            sqLiteDb.delete(DatabaseProperties.TBL_TDS_REG, null, null);
            sqLiteDb.delete(DatabaseProperties.TBL_RM_PENDING_TASK, null, null);
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
            contentValues.put(DatabaseProperties.COL_CARD_UNIQUE_ID, cardUniqueId);
            contentValues.put(DatabaseProperties.COL_CARD_TYPE, paymentCard.getCardType().name());

            switch (paymentCard.getCardType()) {
                case MDES:
                    class ComvivaDefaultListener implements MakeDefaultListener {
                        public boolean isSuccess;

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
            }

            if (!isSuccess) {
                return isSuccess;
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
        return isSuccess;
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
                        TokenData tokenData = visaPaymentSDK.getTokenData(tokenKey);
                        defaultCard = PaymentCard.getPaymentCard(tokenData);
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
}

class DatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_TABLE_APP_PROPERTIES = "CREATE TABLE " + DatabaseProperties.TBL_APP_PROPERTIES + " ("
            + DatabaseProperties.COL_INITIALIZE_STATE + " INTEGER, "
            + DatabaseProperties.COL_RNS_ID + " TEXT, "
            + DatabaseProperties.COL_RNS_TYPE + " TEXT, "
            + DatabaseProperties.COL_VTS_INIT_STATE + " TEXT, "
            + DatabaseProperties.COL_MDES_INIT_STATE + " TEXT);";

    public static final String CREATE_TABLE_RM_PENDING_TASK = "CREATE TABLE " + DatabaseProperties.TBL_RM_PENDING_TASK + " ("
            + DatabaseProperties.COL_TASK_ID + " TEXT, "
            + DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE + " TEXT);";

    public static final String CREATE_TABLE_TDS_REG = "CREATE TABLE " + DatabaseProperties.TBL_TDS_REG + " ("
            + DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE + " TEXT, "
            + DatabaseProperties.COL_TDS_REG_CODE1 + " TEXT, "
            + DatabaseProperties.COL_TDS_AUTH_CODE + " TEXT, "
            + DatabaseProperties.COL_TDS_URL + " TEXT);";

    public static final String CREATE_TABLE_DEFAULT_CARD = "CREATE TABLE if not exists " + DatabaseProperties.TBL_DEFAULT_CARD + " ("
            + DatabaseProperties.COL_CARD_UNIQUE_ID + " TEXT,"
            + DatabaseProperties.COL_CARD_TYPE + " TEXT);";

    public DatabaseHelper(final Context context, final String databaseName) {
        super(context, databaseName, null, DatabaseProperties.DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APP_PROPERTIES);
        db.execSQL(CREATE_TABLE_DEFAULT_CARD);
        //db.execSQL(CREATE_TABLE_RM_PENDING_TASK);
        //db.execSQL(CREATE_TABLE_TDS_REG);

    }
}
