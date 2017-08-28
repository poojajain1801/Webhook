package com.comviva.hceservice.common.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.comviva.hceservice.common.RmPendingTask;
import com.comviva.hceservice.fcm.RnsInfo;
import com.comviva.hceservice.tds.TdsRegistrationData;

public class CommonDatabase implements CommonDb {
    private DatabaseHelper commonDb;
    private Context context;

    public CommonDatabase(Context context) {
        this.context = context;
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
            contentValues.put(DatabaseProperties.COL_VTS_INIT_STATE, comvivaSdkInitData.isVtsInitState());
            contentValues.put(DatabaseProperties.COL_MDES_INIT_STATE, comvivaSdkInitData.isMdesInitState());

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
                initData.setInitState(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_INITIALIZE_STATE)) == 1 ? true : false);
                RnsInfo rnsInfo = new RnsInfo();
                rnsInfo.setRegistrationId(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_RNS_ID)));
                rnsInfo.setRnsType(RnsInfo.RNS_TYPE.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseProperties.COL_RNS_TYPE))));
                initData.setRnsInfo(rnsInfo);
                initData.setVtsInitState(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_VTS_INIT_STATE)) == 1 ? true : false);
                initData.setMdesInitState(cursor.getInt(cursor.getColumnIndex(DatabaseProperties.COL_MDES_INIT_STATE)) == 1 ? true : false);
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
}

class DatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_TABLE_APP_PROPERTIES = "CREATE TABLE "
            + DatabaseProperties.TBL_APP_PROPERTIES + " ("
            + DatabaseProperties.COL_INITIALIZE_STATE + " INTEGER, "
            + DatabaseProperties.COL_RNS_ID + " TEXT, "
            + DatabaseProperties.COL_RNS_TYPE + " TEXT, "
            + DatabaseProperties.COL_VTS_INIT_STATE + " TEXT, "
            + DatabaseProperties.COL_MDES_INIT_STATE + " TEXT);";

    public static final String CREATE_TABLE_RM_PENDING_TASK = "CREATE TABLE "
            + DatabaseProperties.TBL_RM_PENDING_TASK + " ("
            + DatabaseProperties.COL_TASK_ID + " TEXT, "
            + DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE + " TEXT);";

    public static final String CREATE_TABLE_TDS_REG = "CREATE TABLE "
            + DatabaseProperties.TBL_TDS_REG + " ("
            + DatabaseProperties.COL_TOKEN_UNIQUE_REFERENCE + " TEXT, "
            + DatabaseProperties.COL_TDS_REG_CODE1 + " TEXT, "
            + DatabaseProperties.COL_TDS_AUTH_CODE + " TEXT, "
            + DatabaseProperties.COL_TDS_URL + " TEXT);";

    public DatabaseHelper(final Context context, final String databaseName) {
        super(context, databaseName, null, DatabaseProperties.DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APP_PROPERTIES);
        db.execSQL(CREATE_TABLE_RM_PENDING_TASK);
        db.execSQL(CREATE_TABLE_TDS_REG);
    }
}
