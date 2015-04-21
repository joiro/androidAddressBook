package com.jonasschindler.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ViewContactActivity extends Activity implements View.OnClickListener{

    private int contactId;
    private String contactFirstName, contactLastName, contactPhone, contactPhone2, contactEmail, contactEmail2;
    byte[] photo;
    private TextView nameView, phoneView, phoneView2, mailView, mailView2;
    private LinearLayout layoutPhone1, layoutPhone2, layoutMail1, layoutMail2;
    private ImageView contactImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        contactImage = (ImageView) findViewById(R.id.contactImage);
        contactImage.setImageResource(R.drawable.contact_high);
        nameView  = (TextView) findViewById(R.id.firstNameView);
        phoneView = (TextView) findViewById(R.id.phoneNumberView);
        phoneView.setOnClickListener(this);
        phoneView2 = (TextView) findViewById(R.id.phoneNumberView2);
        phoneView2.setOnClickListener(this);
        mailView = (TextView) findViewById(R.id.emailAddressView);
        mailView.setOnClickListener(this);
        mailView2 = (TextView) findViewById(R.id.emailAddressView2);
        mailView2.setOnClickListener(this);
        layoutPhone1 = (LinearLayout) findViewById(R.id.layoutPhone1);
        layoutPhone2 = (LinearLayout) findViewById(R.id.layoutPhone2);
        layoutMail1 = (LinearLayout) findViewById(R.id.layoutMail1);
        layoutMail2 = (LinearLayout) findViewById(R.id.layoutMail2);

        // Receive the name information for the contact to show from the MainActivity
        Bundle bundle = getIntent().getExtras();
        contactId = bundle.getInt("contactId");
        displayDetails();
    }

    public void displayDetails() {

        String columns[] = new String[] {
                ContentProviderContract.FIRSTNAME,
                ContentProviderContract.LASTNAME,
                ContentProviderContract.PHONE,
                ContentProviderContract.PHONE_TWO,
                ContentProviderContract.EMAIL,
                ContentProviderContract.EMAIL_TWO,
                ContentProviderContract.IMAGE
        };

        ContentResolver cr = getContentResolver();
        final Uri contactsUri = ContentProviderContract.CONTACTS_URI;
        Cursor cursor = cr.query(contactsUri, columns, ContentProviderContract.ID+"="+contactId, null, null, null);

        while(cursor.moveToNext()) {
            contactFirstName = cursor.getString(0);
            contactLastName = cursor.getString(1);
            contactPhone = cursor.getString(2);
            contactPhone2 = cursor.getString(3);
            contactEmail = cursor.getString(4);
            contactEmail2 = cursor.getString(5);
            photo = cursor.getBlob(6);
        }
        cursor.close();

        ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
        Bitmap image= BitmapFactory.decodeStream(imageStream);

        nameView.setText(contactFirstName +" "+contactLastName);
        showView(contactPhone, phoneView, layoutPhone1);
        showView(contactPhone2, phoneView2, layoutPhone2);
        showView(contactEmail, mailView, layoutMail1);
        showView(contactEmail2, mailView2, layoutMail2);
        contactImage.setImageBitmap(image);
        try {
            imageStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showView(String field, TextView view, LinearLayout layout) {
        if(!field.isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            view.setText(field);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phoneNumberView:
                Uri number = Uri.parse("tel:" + phoneView.getText());
                Intent callIntent = new Intent(Intent.ACTION_CALL, number);
                startActivity(callIntent);
                break;
            case R.id.phoneNumberView2:
                Uri number2 = Uri.parse("tel:" + phoneView2.getText());
                Intent callIntent2 = new Intent(Intent.ACTION_CALL, number2);
                startActivity(callIntent2);
                break;
            case R.id.emailAddressView:
                Uri mail = Uri.parse("mailto:"+mailView.getText());
                Intent mailIntent = new Intent(Intent.ACTION_SENDTO, mail);
                startActivity(Intent.createChooser(mailIntent, "Send Email"));
                break;
            case R.id.emailAddressView2:
                Uri mail2 = Uri.parse("mailto:"+mailView2.getText());
                Intent mailIntent2 = new Intent(Intent.ACTION_SENDTO, mail2);
                startActivity(Intent.createChooser(mailIntent2, "Send Email"));
                break;
        }
    }
/*
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
*/

    public void editDetails() {
        // Start EditContact Activity with Id information
        Bundle bundle = new Bundle();
        bundle.putInt("contactId",contactId);
        Intent intent = new Intent(this, AddEditContactActivity.class); //.putExtras(bundle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void deleteDetails() {
        // Call the delete Method in Content Provider
        int number = getContentResolver().delete(ContentProviderContract.CONTACTS_URI,ContentProviderContract.ID+"="+contactId, null);

        // Return to the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public Dialog alertDialog() {
        // Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this contact?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // user clicked OK button
                deleteDetails();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // user cancelled the dialog
                Log.d("addressApp", "alert cancelled");
            }
        });
        // get the AlertDialog from create()
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
        int id = item.getItemId();

        if (id == R.id.edit_contact) {
            this.editDetails();
            return true;
        }
        if (id == R.id.delete_contact) {
            this.alertDialog().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}