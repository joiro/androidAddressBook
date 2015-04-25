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

    public CustomArrayAdapter(Activity context, ArrayList item, ArrayList image) {
        super(context, R.layout.rowlayout, item);

        this.context=context;
        this.item=item;
        this.image=image;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.rowlayout, null,true);

        TextView contactName = (TextView) rowView.findViewById(R.id.itemname);
        ImageView contactImage = (ImageView) rowView.findViewById(R.id.icon);

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
