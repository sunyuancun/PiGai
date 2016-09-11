package com.cikuu.pigai.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cikuu.pigai.R;

import java.util.ArrayList;

public class StudentInformationAdapter extends BaseAdapter {

    public static class Information {
        public String name;
        public String value;

        public Information(String n, String v) {
            name = n;
            value = v;
        }
    }

    private ArrayList<Information> mListItems;
    private Activity activity;
    private LayoutInflater inflater;

    public StudentInformationAdapter(Activity activity, ArrayList<Information> listItems) {
        this.activity = activity;
        mListItems = listItems;
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.student_info_listview, null);

        Information item = mListItems.get(position);
        TextView info = (TextView) convertView.findViewById(R.id.user_info);
        TextView info_value = (TextView) convertView.findViewById(R.id.user_value);
        ImageView bindPhoneIcon = (ImageView) convertView.findViewById(R.id.image);
        info.setText(item.name);
        info_value.setText(item.value);

        if (position == 5) {
            bindPhoneIcon.setImageResource(R.drawable.right_arrow);
        } else {
            bindPhoneIcon.setImageResource(R.drawable.right_arrow);
        }

        return convertView;
    }
}
