package com.jonasschindler.addressbook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import java.io.ByteArrayOutputStream;


public class EditContactActivity extends Activity {

    // Instance of the DBHelper class
    private static DBAdapter dbHelper;

    private ImageView editImage;
    private int contactId;
    private String firstName, lastName, phone, mail;
    private EditText editFirstName, editLastName, editPhone, editMail;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        editImage = (ImageView) findViewById(R.id.editImage);
        editImage.setImageResource(R.drawable.ic_launcher);
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editPhone = (EditText) findViewById(R.id.editPhone);
        editMail = (EditText) findViewById(R.id.editMail);

        //dbHelper = new DBAdapter(this);

        // Receive the id information for the contact to edit from the ViewContact Activity
        Bundle bundle = getIntent().getExtras();
        //contactId = bundle.getInt("contactId");

/*
        // Get all information from the contact and fill in the editText fields
        ArrayList contactDetails = dbHelper.getContactDetails(contactId);
        String contactFirstName = (String) contactDetails.get(0);
        String contactLastName = (String) contactDetails.get(1);
        String contactPhone = (String) contactDetails.get(2);
        String contactEmail = (String) contactDetails.get(3);
        byte[] photo = (byte[]) contactDetails.get(4);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
        Bitmap theImage= BitmapFactory.decodeStream(imageStream);
        editFirstName.setText(contactFirstName);
        editLastName.setText(contactLastName);
        editPhone.setText(contactPhone);
        editMail.setText(contactEmail);
        editImage.setImageBitmap(theImage);
    }
*/
    // Confirm edit of contactDetails
    public void editContact(View view) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap contactImage = ((BitmapDrawable)editImage.getDrawable()).getBitmap();
        contactImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] photo = baos.toByteArray();

        // Get the information from the editText fields
        firstName = editFirstName.getText().toString();
        lastName = editLastName.getText().toString();
        phone = editPhone.getText().toString();
        mail = editMail.getText().toString();

        // Call the updateContact Method in the DBHelper class
        //dbHelper.updateContact(contactId, firstName, lastName, phone, mail, photo);

        // Return to the ViewContact Activity
        Bundle bundle = new Bundle();
        bundle.putInt("contactId", contactId);
        Intent intent = new Intent(this, ViewContactActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void openPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(EditContactActivity.this, editImage);
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
                editImage.setImageBitmap(photo);
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
        getMenuInflater().inflate(R.menu.menu_edit_contact, menu);
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
