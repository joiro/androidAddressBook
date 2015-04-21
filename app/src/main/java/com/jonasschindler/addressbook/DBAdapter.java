package com.jonasschindler.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBAdapter {

    private static DBHelper dbHelper;
    public DBAdapter(Context context, String name, CursorFactory factory, int version) {
        dbHelper = new DBHelper(context, name, factory, version);
    }

    static class DBHelper extends SQLiteOpenHelper {

        public static final String TABLE = "contacts";
        public static final String ID = "_id";
        public static final String FIRSTNAME = "firstName";
        public static final String LASTNAME = "lastName";
        public static final String PHONE = "phone";
        public static final String PHONE_TWO = "phoneTwo";
        public static final String EMAIL = "email";
        public static final String EMAIL_TWO = "emailTwo";
        public static final String IMAGE = "image";
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
              FIRSTNAME + " VARCHAR(128) NOT NULL," + LASTNAME + " VARCHAR(128) NOT NULL," + PHONE + " VARCHAR(30) ," + PHONE_TWO + " VARCHAR(30) ," + EMAIL + " VARCHAR(30) ," + EMAIL_TWO + " VARCHAR(30) ," + IMAGE + " BLOB" + ");";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE;

        public DBHelper(Context context,String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("addressApp", "DBHelper onCreate");
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {
            Log.d("addressApp", "DBHelper onUpgrade");
            db.execSQL(DROP_TABLE);
            db.execSQL(CREATE_TABLE);
        }
    }
}