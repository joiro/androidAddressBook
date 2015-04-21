package com.jonasschindler.addressbook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
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
        return rowView;

    }

    @Override
    public Filter getFilter(){
        return new Filter(){

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if (constraint != null && constraint.length() > 0) {
                    List<String> filterList = new ArrayList<String>();
                    for(Object element : item){
                        if(element.toString().toLowerCase().contains(constraint)){
                            String elementString = (String) element.toString();
                            filterList.add(elementString);
                        }
                    }

                    result.values = filterList;
                    result.count = filterList.size();
                }else {
                    result.values = item;
                    result.count = item.size();
                }
                return result;


            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                for (String item : (List<String>) results.values) {
                    add(item);
                }
                notifyDataSetChanged();

            }

        };
    }
}
