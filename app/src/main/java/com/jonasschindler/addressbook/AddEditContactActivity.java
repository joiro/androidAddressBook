package com.jonasschindler.addressbook;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddEditContactActivity extends Activity {


    private byte[] photo;
    private int contactId;
    private boolean newContact;
    private String contactFirstName, contactLastName, contactPhone, contactPhone2, contactEmail, contactEmail2;

    private Button addField;
    private ImageView addImage;
    private LinearLayout phone2Layout, mail2Layout;
    private EditText addFirstName, addLastName, addPhone, addMail, addMail2, addPhone2;

    private static final int IMAGE_SELECTION = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // identify the views
        addImage = (ImageView) findViewById(R.id.addImage);
        addImage.setImageResource(R.drawable.contact_high);
        addFirstName = (EditText) findViewById(R.id.addFirstName);
        addLastName = (EditText) findViewById(R.id.addLastName);
        addPhone = (EditText) findViewById(R.id.addPhone);
        addPhone2 = (EditText) findViewById(R.id.addPhone2);
        addMail = (EditText) findViewById(R.id.addMail);
        addMail2 = (EditText) findViewById(R.id.addMail2);
        phone2Layout = (LinearLayout) findViewById(R.id.phone2Layout);
        mail2Layout = (LinearLayout) findViewById(R.id.mail2Layout);
        addField = (Button) findViewById(R.id.addField);

        // Receive the id information for the contact to edit from the ViewContact Activity
        Bundle bundle = getIntent().getExtras();

        // fill data in editText fields and change activityTile depending if 'add' or 'edit' is called
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

    // fills contacts information to textFields, if 'edit contact' was chosen
    public void fillContactData() {

        // the columns to retrieve from the contentProvider
        String columns[] = new String[] {
                ContentProviderContract.FIRSTNAME,
                ContentProviderContract.LASTNAME,
                ContentProviderContract.PHONE,
                ContentProviderContract.PHONE_TWO,
                ContentProviderContract.EMAIL,
                ContentProviderContract.EMAIL_TWO,
                ContentProviderContract.IMAGE
        };

        final Uri contactsUri = ContentProviderContract.CONTACTS_URI;
        Cursor cursor = getContentResolver()
                .query(contactsUri, columns, ContentProviderContract.ID + "=" + contactId, null, null, null);
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

        // transform & decode image, and put contact information into textFields
        ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
        Bitmap image= BitmapFactory.decodeStream(imageStream);
        addFirstName.setText(contactFirstName);
        addLastName.setText(contactLastName);
        addPhone.setText(contactPhone);
        addPhone2.setText(contactPhone2);
        addMail.setText(contactEmail);
        addMail2.setText(contactEmail2);
        addImage.setImageBitmap(image);
        try {
            imageStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showView(contactPhone2, addPhone2, phone2Layout);
        showView(contactEmail2, addMail2, mail2Layout);
    }

    // displays additional fields for phone / email if their corresponding data strings are not empty
    public void showView(String field, EditText view, LinearLayout layout) {
        if(!field.isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            view.setText(field);
        }
    }

    // creates popupMenu when user clicks imageView to choose source of contactImage
    public void openPopupMenuAddField(View view) {
        PopupMenu popup = new PopupMenu(AddEditContactActivity.this, addField);
        popup.getMenuInflater().inflate(R.menu.popup_menu_add_field, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                // adds a second phone field
                if (id == R.id.addPhone2) {
                    Log.d("addressApp", "email field added");
                    String field = "phone";
                    addField(field);
                    return true;
                }
                // adds a second email field
                if (id == R.id.addEmail2) {
                    Log.d("addressApp", "email field added");
                    String field = "email";
                    addField(field);
                    return true;
                }
                return true;
            }
        });
        popup.show();
    }

    // called when 'addField' button is clicked; displays additional textFields for phone/email
    public void addField(String field) {
        switch (field) {
            case "phone":
                phone2Layout.setVisibility(View.VISIBLE);
                break;
            case "email":
                mail2Layout.setVisibility(View.VISIBLE);
                break;
        }
    }

    // called when user clicks 'save contact' / 'save changes'
    public void saveToDb(View view) {

        // transform image to byteArray and compress it
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap contactImage = ((BitmapDrawable)addImage.getDrawable()).getBitmap();
        contactImage.compress(Bitmap.CompressFormat.PNG, 70, baos);
        photo = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // stores input from textFields
        String firstName = addFirstName.getText().toString();
        String lastName = addLastName.getText().toString();
        String phone = addPhone.getText().toString();
        String phone2 = addPhone2.getText().toString();
        String mail = addMail.getText().toString();
        String mail2 = addMail2.getText().toString();

        // checks if firstName field is empty and displays toast if it is
        if (!firstName.isEmpty()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContentProviderContract.FIRSTNAME, firstName);
            contentValues.put(ContentProviderContract.LASTNAME, lastName);
            contentValues.put(ContentProviderContract.PHONE, phone);
            contentValues.put(ContentProviderContract.PHONE_TWO, phone2);
            contentValues.put(ContentProviderContract.EMAIL, mail);
            contentValues.put(ContentProviderContract.EMAIL_TWO, mail2);
            contentValues.put(ContentProviderContract.IMAGE, photo);

            // checks if contacts gets created or updated depending on the state of the boolean 'newContact'
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
        // here the toast if the name is empty gets created
        else {
            Toast toast = Toast.makeText(this, "First Name cannot be empty!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // creates popupMenu when user clicks imageView to choose source of contactImage
    public void openPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(AddEditContactActivity.this, addImage);
        popup.getMenuInflater().inflate(R.menu.popup_menu_image, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                // opens Camera for option 1
                if (id == R.id.takePhoto) {
                    openCamera();
                    return true;
                }
                // open default galleryApp for option 2
                if (id == R.id.choosePhoto) {
                    chooseFile();
                    return true;
                }
                return true;
            }
        });
        popup.show();
    }

    // sends intent to open cameraApp
    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    // sends intent to open default galleryApp
    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_SELECTION);
    }

    // return method from camera or gallery to receive the image file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // return from camera
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // if an image was taken, it get stored and displayed in the imageView
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                addImage.setImageBitmap(photo);
            } else if (resultCode == RESULT_CANCELED) {
                // user aborted image capture
            } else {
                // Image capture failed, notify user with a toast
                Toast toast = Toast.makeText(this, "Image capture failed!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        // return from gallery
        if (requestCode == IMAGE_SELECTION) {
            if (resultCode == RESULT_OK) {
                // receives the image as an uri..
                Uri uri = data.getData();
                Bitmap image = null;
                try {
                    // .. transforms it to a bitmap..
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // .. and displays it in the imageView
                addImage.setImageBitmap(image);
            }
            else if (resultCode == RESULT_CANCELED) {
                // user aborted image selection
            } else {
                // image selection failed, notify the user with a toast
                Toast toast = Toast.makeText(this, "Image selection failed!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }
}