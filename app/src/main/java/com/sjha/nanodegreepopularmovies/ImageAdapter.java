package com.sjha.nanodegreepopularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by sjha on 15-07-21.
 */
public class ImageAdapter extends ArrayAdapter {


    private LayoutInflater inflater;
    private Context context;
    private String[] imageCollection;

    public ImageAdapter(Context context, String[] imageCollection){

        super(context, R.layout.image_container, imageCollection);

        this.context = context;
        this.imageCollection  = imageCollection;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if(convertView == null){
            convertView = inflater.inflate(R.layout.image_container, parent, false);
        }


        Picasso.with(context)
                .load(imageCollection[position])
                .fit()
                .into((ImageView) convertView);

        return convertView;
    }
}



