package com.sjha.nanodegreepopularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sjha on 15-08-08.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {


    private TextView authorText;
    private TextView contentText;

    public ReviewAdapter(Context context, ArrayList<Review> review) {
        super(context, 0, review);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Review reviewData = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_page_viewer, parent, false);
        }

        authorText = (TextView)convertView.findViewById(R.id.authorText);
        contentText = (TextView)convertView.findViewById(R.id.contentText);

        authorText.setText("Author: " + reviewData.getAuthor());
        contentText.setText(reviewData.getContent());

        return convertView;
    }
}
