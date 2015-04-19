package com.jonasschindler.addressbook;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class AddContactActivity extends Activity {

    private String contactFirstName, contactLastName, contactPhone, contactEmail;
    private int contactId;
    private byte[] photo;
    private EditText addFirstName, addLastName, addPhone, addMail;
    private ImageView addImage;
    private boolean newContact;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        addImage = (ImageView) findViewById(R.id.addImage);
        addImage.setImageResource(R.drawable.ic_launcher);
        addFirstName = (EditText) findViewById(R.id.addFirstName);
        addLastName = (EditText) findViewById(R.id.addLastName);
        addPhone = (EditText) findViewById(R.id.addPhone);
        addMail = (EditText) findViewById(R.id.addMail);

        // Receive the id information for the contact to edit from the ViewContact Activity
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            contactId = bundle.getInt("contactId");
            fillContactData();
            setTitle("Edit contact");
            newContact = false;
        } else {
            setTitle("Add new contact");
            newContact = true;
        }
    }

    public void fillContactData() {
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
        while(cursor.moveToNext()) {
            contactFirstName = cursor.getString(0);
            contactLastName = cursor.getString(1);
            contactPhone = cursor.getString(2);
            contactEmail = cursor.getString(3);
            photo = cursor.getBlob(4);
        }

        ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
        Bitmap image= BitmapFactory.decodeStream(imageStream);
        addFirstName.setText(contactFirstName);
        addLastName.setText(contactLastName);
        addPhone.setText(contactPhone);
        addMail.setText(contactEmail);
        addImage.setImageBitmap(image);
    }

    public void saveToDb(View view) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap contactImage = ((BitmapDrawable)addImage.getDrawable()).getBitmap();
        contactImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        photo = baos.toByteArray();

        String firstName = addFirstName.getText().toString();
        String lastName = addLastName.getText().toString();
        String phone = addPhone.getText().toString();
        String mail = addMail.getText().toString();

        if (!firstName.isEmpty()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContentProviderContract.FIRSTNAME, firstName);
            contentValues.put(ContentProviderContract.LASTNAME, lastName);
            contentValues.put(ContentProviderContract.PHONE, phone);
            contentValues.put(ContentProviderContract.EMAIL, mail);
            contentValues.put(ContentProviderContract.IMAGE, photo);

            if (newContact) {
                Uri uri = getContentResolver().insert(ContentProviderContract.CONTACTS_URI, contentValues);
                // Start the ViewContactActivity showing the added contact
                //Bundle bundle = new Bundle();
                //bundle.putInt("contactId", contactId);
                Intent intent = new Intent(this, MainActivity.class);
                //intent.putExtras(bundle);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                // Start the ViewContactActivity showing the edited contact
                int number = getContentResolver().update(ContentProviderContract.CONTACTS_URI, contentValues,ContentProviderContract.ID+"="+contactId, null);
                Bundle bundle = new Bundle();
                bundle.putInt("contactId", contactId);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtras(bundle);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        }
        else {
            Toast toast = Toast.makeText(this, "Name must be entered!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void openPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(AddContactActivity.this, addImage);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.takePhoto) {
                    openCamera();
                    return true;
                }
                if (id == R.id.choosePhoto) {
                    Log.d("addressApp", "choose Photo");
                    return true;
                }
                return true;
            }
        });
        popup.show();
    }

    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("addressApp", "result ok");
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                addImage.setImageBitmap(photo);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}