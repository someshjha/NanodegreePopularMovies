package com.sjha.nanodegreepopularmovies;

import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by sjha on 15-07-23.
 */
public class DialogAdapter extends ArrayAdapter<String> {

    private Context mContext;

    public DialogAdapter(Context context, int resource, int textViewResourceId, String[] arrayList) {
        super(context, resource, textViewResourceId, arrayList);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String number = getItem(position);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_sort, null);
            holder = new ViewHolder();
            holder.lblSort = (TextView)convertView.findViewById(R.id.sortText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        if (number != null) {
            holder.lblSort.setText(PhoneNumberUtils.formatNumber(number));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lblSort;
    }

}
