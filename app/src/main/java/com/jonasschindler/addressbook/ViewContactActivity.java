package com.jonasschindler.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class ViewContactActivity extends Activity {

    // Instance of the DBHelper class
    private static DBAdapter helper;

    private int contactId;
    private String contactFirstName, contactLastName, contactPhone, contactEmail;
    byte[] photo;
    private TextView nameView, phoneView, mailView;
    private ImageView contactImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        contactImage = (ImageView) findViewById(R.id.contactImage);
        contactImage.setImageResource(R.drawable.ic_launcher);
        nameView  = (TextView) findViewById(R.id.firstNameView);
        phoneView = (TextView) findViewById(R.id.phoneNumberView);
        mailView = (TextView) findViewById(R.id.emailAddressView);

        helper = new DBAdapter(this);

        // Receive the name information for the contact to show from the MainActivity
        Bundle bundle = getIntent().getExtras();
        contactId = bundle.getInt("contactId");
        displayDetails();
    }

    public void displayDetails() {
        //ArrayList contactDetails = helper.getContactDetails(contactId);

        String columns[] = new String[] {
                ContentProviderContract.FIRSTNAME,
                ContentProviderContract.LASTNAME,
                ContentProviderContract.PHONE,
                ContentProviderContract.EMAIL,
                ContentProviderContract.IMAGE
        };

        ContentResolver cr = getContentResolver();
        final Uri contactsUri = ContentProviderContract.CONTACTS_URI;
        Cursor cursor = cr.query(contactsUri, columns, ContentProviderContract.ID+"="+contactId, null, null, null);
        Log.d("addressApp","cursor: "+cursor);
        ArrayList contactDetails = new ArrayList();
        while(cursor.moveToNext()) {
            contactFirstName = cursor.getString(0);
            contactLastName = cursor.getString(1);
            contactPhone = cursor.getString(2);
            contactEmail = cursor.getString(3);
            photo = cursor.getBlob(4);

        }

        //contactFirstName = (String) contactDetails.get(0);
        //contactLastName = (String) contactDetails.get(1);
        //contactPhone = (String) contactDetails.get(2);
        //contactEmail = (String) contactDetails.get(3);
        //byte[] photo = (byte[]) contactDetails.get(4);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
        Bitmap image= BitmapFactory.decodeStream(imageStream);

        nameView.setText(contactFirstName +" "+contactLastName);
        hideView(contactPhone, phoneView);
        hideView(contactEmail, mailView);
        contactImage.setImageBitmap(image);
    }

    public void hideView(String field, TextView view) {
        if(field.isEmpty()) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(field);
        }
    }

    public void callContact(View view) {
        Uri number = Uri.parse("tel:" + phoneView.getText());
        Intent callIntent = new Intent(Intent.ACTION_CALL, number);
        startActivity(callIntent);
    }

    public void openEmail(View view) {
        Uri mail = Uri.parse("mailto:"+mailView.getText());
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO, mail);
        startActivity(Intent.createChooser(mailIntent, "Send Email"));
    }

    public void editDetails() {
        // Start EditContact Activity with Id information
        Bundle bundle = new Bundle();
        bundle.putInt("contactId",contactId);
        Intent intent = new Intent(this, EditContactActivity.class); //.putExtras(bundle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void deleteDetails() {
        // Call the delete Method in the DBHelper class
        helper.deleteContact(contactId);

        // Return to the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public Dialog alertDialog() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Are you sure you want to delete this contact?");
                //.setTitle("Delete Contact: "+contactName);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                deleteDetails();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.d("addressApp", "alert cancelled");
            }
        });

        // 3. Get the AlertDialog from create()
        return builder.create();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit_contact) {
            this.editDetails();
            return true;
        }
        if (id == R.id.delete_contact) {
            this.alertDialog().show();
            //this.deleteDetails();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
