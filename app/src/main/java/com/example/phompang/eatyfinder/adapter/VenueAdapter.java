package com.example.phompang.eatyfinder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.MiniVenue;

/**
 * Created by phompang on 11/14/2016 AD.
 */

public class VenueAdapter extends ArrayAdapter<CompactVenue> {
    private Context mContext;
    private CompactVenue[] mCompactVenues;
    private LayoutInflater mInflater;

    public VenueAdapter(Context context, int resource, CompactVenue[] objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mCompactVenues = objects;
    }

    @Override
    public int getCount() {
        return mCompactVenues.length;
    }

    @Nullable
    @Override
    public CompactVenue getItem(int position) {
        return mCompactVenues[position];
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        ((TextView) v.findViewById(android.R.id.text1)).setText(mCompactVenues[position].getName());
        return v;
    }
}
