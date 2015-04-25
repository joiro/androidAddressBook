package com.jonasschindler.addressbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    private static DBHelper dbHelper;
    public DBAdapter(Context context, String name, CursorFactory factory, int version) {
        dbHelper = new DBHelper(context, name, factory, version);
    }

    static class DBHelper extends SQLiteOpenHelper {

        // tableName & columnNames of the dataBase table
        public static final String TABLE = "contacts";
        public static final String ID = "_id";
        public static final String FIRSTNAME = "firstName";
        public static final String LASTNAME = "lastName";
        public static final String PHONE = "phone";
        public static final String PHONE_TWO = "phoneTwo";
        public static final String EMAIL = "email";
        public static final String EMAIL_TWO = "emailTwo";
        public static final String IMAGE = "image";

        // query that creates the table
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
              FIRSTNAME + " VARCHAR(128) NOT NULL," + LASTNAME + " VARCHAR(128) NOT NULL," + PHONE + " VARCHAR(30) ," + PHONE_TWO + " VARCHAR(30) ," + EMAIL + " VARCHAR(30) ," + EMAIL_TWO + " VARCHAR(30) ," + IMAGE + " BLOB" + ");";

        // query that deletes the existing table
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE;

        public DBHelper(Context context,String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // when no dataBase exists, create the table
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        // when dataBase is upgraded, delete the table and create an empty one
        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {
            db.execSQL(DROP_TABLE);
            db.execSQL(CREATE_TABLE);
        }
    }
}