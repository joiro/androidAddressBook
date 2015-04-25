package com.jonasschindler.addressbook;

import android.net.Uri;

public class ContentProviderContract {

    public static final String AUTHORITY = "com.jonasschindler.addressbook.MyContentProvider";

    public static final Uri CONTACTS_URI = Uri.parse("content://"+AUTHORITY+"/contacts");

    public static final String ID = "_id";

    public static final String FIRSTNAME = "firstName";

    public static final String LASTNAME = "lastName";

    public static final String PHONE = "phone";

    public static final String PHONE_TWO = "phoneTwo";

    public static final String EMAIL = "email";

    public static final String EMAIL_TWO = "emailTwo";

    public static final String ADDRESS = "address";

    public static final String IMAGE = "image";
}
