package com.jonasschindler.addressbook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomArrayAdapter<String> extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList item;
    private final ArrayList image;

    // custom ArrayAdapter that uses a custom layout xml file to display and image and a text
    // it takes to arrayLists that contain images and contact items
    public CustomArrayAdapter(Activity context, ArrayList item, ArrayList image) {
        super(context, R.layout.rowlayout, item);

        this.context=context;
        this.item=item;
        this.image=image;
    }

    // gets called when the user clicks a item. gets send the position
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.rowlayout, null,true);

        // Instantiation of the views
        TextView contactName = (TextView) rowView.findViewById(R.id.itemname);
        ImageView contactImage = (ImageView) rowView.findViewById(R.id.icon);

        // stores the contact information, retrieved with the position at the arrayLists
        // displays the contact information in the text and images views
        contactName.setText(item.get(position).toString());
        byte[] photo = (byte[]) image.get(position);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
        Bitmap theImage= BitmapFactory.decodeStream(imageStream);
        contactImage.setImageBitmap(theImage);
        try {
            imageStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rowView;
    }
}
