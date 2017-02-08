package com.example.donghyunkim.andr_final_project;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Donghyun Kim on 2016-12-12.
 */

public class ListViewAdapter extends ArrayAdapter<Memo> {
    // Declare Variables
    LayoutInflater inflater;
    private List<Memo> arrayList;


    public ListViewAdapter(Context context, int resource, List<Memo> plist) {
        super(context, resource, plist);
        this.arrayList = plist;
        inflater = LayoutInflater.from(getContext());
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Memo getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        Memo m = arrayList.get(position);
        return m.getId();
    }

    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_view_items, null);
        }

        // Set the results into TextViews
        Memo m = arrayList.get(position);
        TextView item = (TextView) view.findViewById(R.id.item);
        item.setText(m.getTitle());

        return view;
    }
}
