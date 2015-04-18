package com.jonasschindler.addressbook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class AddContactActivity extends Activity {

    //DBAdapter.DBHelper dbHelper;
    private static DBAdapter dbHelper;
    //MainActivity mainActivity = new MainActivity();

    String firstName;
    Toast toast;
    String lastName, phone, mail;
    EditText addFirstName, addLastName, addPhone, addMail;
    ImageView addImage;

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

        dbHelper = new DBAdapter(this);
        //dbHelper.getWritableDatabase();
    }

    public void saveToDb(View view) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap contactImage = ((BitmapDrawable)addImage.getDrawable()).getBitmap();
        contactImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] photo = baos.toByteArray();

        firstName = addFirstName.getText().toString();
        lastName = addLastName.getText().toString();
        phone = addPhone.getText().toString();
        mail = addMail.getText().toString();

        if (!firstName.isEmpty()) {

            dbHelper.insertData(firstName, lastName, phone, mail, photo);

            // Start the MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            toast.makeText(this, "Name must be entered!", Toast.LENGTH_SHORT).show();
        }

    }

    public void openPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(AddContactActivity.this, addImage);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //Toast.makeText(EditContactActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
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