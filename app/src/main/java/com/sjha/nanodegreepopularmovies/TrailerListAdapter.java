package com.sjha.nanodegreepopularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sjha on 15-08-09.
 */
public class TrailerListAdapter extends ArrayAdapter<Trailer> {


    private TextView typeText;

    public TrailerListAdapter(Context context, ArrayList<Trailer> trailer) {
        super(context, 0, trailer);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailer trailerData = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_trailer, parent, false);
        }

        typeText = (TextView)convertView.findViewById(R.id.trailerTxt);
        typeText.setText(trailerData.getType());
        return convertView;
    }

}
