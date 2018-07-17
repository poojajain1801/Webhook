package com.comviva.mdesapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by amit.randhawa on 26-03-2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String DATABASE_NAME = "card_Info_Database";
    private final String TABLE_NAME = "cardInfo";
    private final String KEY_NAME = "cardID";
    private final String KEY_VALUE = "imageBlob";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + KEY_NAME + "PRIMARY  " + KEY_VALUE  + " TEXT" + ")";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
