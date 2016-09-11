package com.cikuu.pigai.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cikuu.pigai.R;
import com.cikuu.pigai.businesslogic.Article;

import java.util.List;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2015-10-12
 * Time: 17:32
 * Protect: PiGai
 */
public class TeacherArticleListInStudentAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Article> articleItems;

    public TeacherArticleListInStudentAdapter(Activity activity, List<Article> articleItems) {
        this.activity = activity;
        this.articleItems = articleItems;
    }


    @Override
    public int getCount() {
        return articleItems.size();
    }

    @Override
    public Object getItem(int location) {
        return articleItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.teacher_article_list_in_student_row, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.articleTime = (TextView) convertView.findViewById(R.id.endtime);
            holder.articleNumber = (TextView) convertView.findViewById(R.id.submittedNumberPerson);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // getting movie data for the row
        Article m = articleItems.get(position);

        // title
        holder.title.setText(m.getTitle());
        holder.articleNumber.setText(String.valueOf(m.getCount()));
        String endTime = String.format("截止：%s", m.getEndTime());
        holder.articleTime.setText(endTime);

        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView articleNumber;
        TextView articleTime;
    }

}
