package com.cikuu.pigai.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cikuu.pigai.R;
import com.cikuu.pigai.businesslogic.School;

import java.util.List;

public class SchoolAdapter extends BaseAdapter {
    private List<School> Schools;
    private Context context;

    public SchoolAdapter(Context context, List<School> schools) {
        this.context = context;
        this.Schools = schools;
    }

    @Override
    public int getCount() {
        return Schools.size();
    }

    @Override
    public Object getItem(int position) {
        return Schools.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.school_item_line, null);
        }
        TextView tv_school = (TextView) convertView.findViewById(R.id.tv_school);
        tv_school.setText(Schools.get(position).getSchool());
        return convertView;
    }
}
