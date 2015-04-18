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

    String firstName, lastName;

    private static DBHelper dbHelper;
    public DBAdapter(Context context, String name, CursorFactory factory, int version) {
        dbHelper = new DBHelper(context, name, factory, version);
    }
/*
    public long insertData(String firstName, String lastName, String phone, String mail, byte[] image) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.FIRSTNAME, firstName);
        contentValues.put(DBHelper.LASTNAME, lastName);
        contentValues.put(DBHelper.PHONE, phone);
        contentValues.put(DBHelper.EMAIL, mail);
        contentValues.put(DBHelper.IMAGE, image);
        long id = db.insert(DBHelper.TABLE, null, contentValues);
        return id;
    }

    public ArrayList getAllNames() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String [] columns = {DBHelper.FIRSTNAME, DBHelper.LASTNAME};
        Cursor cursor = db.query(DBHelper.TABLE, columns, null, null, null, null, null);
        ArrayList list = new ArrayList();
        while(cursor.moveToNext()) {
            firstName = cursor.getString(0);
            lastName = cursor.getString(1);
            list.add(firstName +" "+ lastName);
        }
        cursor.close();
        return list;
    }

    public ArrayList getAllImages() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String [] columns = {DBHelper.IMAGE};
        Cursor cursor = db.query(DBHelper.TABLE, columns, null, null, null, null, null);
        ArrayList list = new ArrayList();
        while(cursor.moveToNext()) {
            byte[] image = cursor.getBlob(0);
            list.add(image);
        }
        cursor.close();
        return list;
    }

    public ArrayList getAllIds() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String [] columns = {DBHelper.ID};
        Cursor cursor = db.query(DBHelper.TABLE, columns, null, null, null, null, null);
        ArrayList list = new ArrayList();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            list.add(id);
        }
        cursor.close();
        return list;
    }

    public ArrayList getContactDetails(int contactId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String [] columns = {DBHelper.FIRSTNAME, DBHelper.LASTNAME, DBHelper.PHONE, DBHelper.EMAIL, DBHelper.IMAGE};
        Cursor cursor = db.query(DBHelper.TABLE, columns, DBHelper.ID + "='"+ contactId+"'", null, null, null, null);
        ArrayList contactDetails = new ArrayList();
        while(cursor.moveToNext()) {
            String contactFirstName = cursor.getString(0);
            String contactLastName = cursor.getString(1);
            String contactPhone = cursor.getString(2);
            String contactEmail = cursor.getString(3);
            byte[] image =cursor.getBlob(4);
            contactDetails.add(contactFirstName);
            contactDetails.add(contactLastName);
            contactDetails.add(contactPhone);
            contactDetails.add(contactEmail);
            contactDetails.add(image);
        }
        cursor.close();
        return contactDetails;
    }

    public void updateContact(int id, String firstName, String lastName, String phone, String mail, byte[] image) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.FIRSTNAME, firstName);
        contentValues.put(DBHelper.LASTNAME, lastName);
        contentValues.put(DBHelper.PHONE, phone);
        contentValues.put(DBHelper.EMAIL, mail);
        contentValues.put(DBHelper.IMAGE, image);
        db.update(DBHelper.TABLE, contentValues, DBHelper.ID+"="+ id, null);
    }

    public int deleteContact(int contactId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(DBHelper.TABLE, DBHelper.ID + "=" + contactId, null);
        return count;
    }
*/
    static class DBHelper extends SQLiteOpenHelper {

        //public static final int DATABASE_VERSION = 9;
        //public static final String DATABASE_NAME = "contactsDB";
        public static final String TABLE = "contacts";
        public static final String ID = "_id";
        public static final String FIRSTNAME = "firstName";
        public static final String LASTNAME = "lastName";
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";
        public static final String IMAGE = "image";
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
              FIRSTNAME + " VARCHAR(128) NOT NULL," + LASTNAME + " VARCHAR(128) NOT NULL," + PHONE + " VARCHAR(30) ," + EMAIL + " VARCHAR(30) ," + IMAGE + " BLOB" + ");";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE;

        public DBHelper(Context context,String name, CursorFactory factory,
                        int version) {

            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.d("addressApp", "DBHelper onCreate");
            db.execSQL(CREATE_TABLE);
            db.execSQL("INSERT INTO contacts (firstName, lastName, phone, email) " + "VALUES " +
                    "('Jonas', 'Schindler', '1234', 'jonas.schindler@gmail.com') ");
            db.execSQL("INSERT INTO contacts (firstName, lastName, phone, email) " + "VALUES " +
                    "('Peter', 'Fonda', '5678', 'example@example.com') ");
            db.execSQL("INSERT INTO contacts (firstName, LastName, phone, email) " + "VALUES " +
                    "('Jimmy', 'Page', '91011', 'beispiel@gmail.com') ");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {
            Log.d("addressApp", "DBHelper onUpgrade");
            db.execSQL(DROP_TABLE);
            db.execSQL(CREATE_TABLE);
        }
    }
}