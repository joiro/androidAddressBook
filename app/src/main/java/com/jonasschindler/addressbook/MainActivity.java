package com.jonasschindler.addressbook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private int contactId;
    private byte[] image;
    private String firstName, lastName;
    private CustomArrayAdapter<String> customArrayAdapter;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showAllContacts();
        //handleIntent(getIntent());
    }

    // gets contact information from the contentProvider and displays them in the listView
    public void showAllContacts() {

        // the columns to retrieve from the contentProvider
        String columns[] = new String[] {
                ContentProviderContract.ID,
                ContentProviderContract.FIRSTNAME,
                ContentProviderContract.LASTNAME,
                ContentProviderContract.IMAGE
        };

        final Uri contactsUri = ContentProviderContract.CONTACTS_URI;
        // receive cursor from the contentProvider with the contact information
        Cursor cursor = getContentResolver().query(contactsUri, columns, null, null, null, null);
        final ArrayList id = new ArrayList();
        final ArrayList names = new ArrayList();
        final ArrayList images = new ArrayList();
        while(cursor.moveToNext()) {
            contactId = cursor.getInt(0);
            firstName = cursor.getString(1);
            lastName = cursor.getString(2);
            image = cursor.getBlob(3);
            id.add(contactId);
            names.add(firstName + " "+lastName);
            images.add(image);
        }
        cursor.close();

        customArrayAdapter = new CustomArrayAdapter<String>(this, names, images);
        listView = (ListView) findViewById(R.id.contactsListView);
        listView.setAdapter(customArrayAdapter);

        // Click Listener for listView that reads the names of the list items that are clicked
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // retrieve the contactId of the selected contact and attach it to the intent
                int contactId = (int) id.get(i);
                Bundle bundle = new Bundle();
                bundle.putInt("contactId",contactId);

                // create intent to open the contact activity of the selected contact
                Intent intent = new Intent(getApplicationContext(), com.jonasschindler.addressbook.ViewContactActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    // Called when add contact button is clicked, opens the AddContactActivity
    public void addContact(View view) {
        Intent intent = new Intent(this, AddEditContactActivity.class);
        startActivity(intent);
    }

    // Ensures to display the latest data when resuming the activity
    public void onResume() {
        super.onResume();
        this.showAllContacts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}