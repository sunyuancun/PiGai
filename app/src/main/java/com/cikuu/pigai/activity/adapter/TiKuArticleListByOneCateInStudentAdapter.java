package com.cikuu.pigai.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cikuu.pigai.R;
import com.cikuu.pigai.businesslogic.TiKuArticleByOneCategory;

import java.util.List;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-04-21
 * Time: 11:50
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class TiKuArticleListByOneCateInStudentAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<TiKuArticleByOneCategory> articleItems;

    public TiKuArticleListByOneCateInStudentAdapter(Activity activity, List<TiKuArticleByOneCategory> articleItems) {
        this.activity = activity;
        this.articleItems = articleItems;
    }


    @Override
    public int getCount() {
        return articleItems.size();
    }

    @Override
    public Object getItem(int position) {
        return articleItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tiku_article_list_by_one_cate_in_student_row, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.articleTime = (TextView) convertView.findViewById(R.id.endtime);
            holder.articleNumber = (TextView) convertView.findViewById(R.id.submittedNumberPerson);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TiKuArticleByOneCategory tiKuArticleByOneCategory = articleItems.get(position);

        // title
        holder.title.setText(tiKuArticleByOneCategory.getArticleTitle());
        holder.articleNumber.setText(String.valueOf(tiKuArticleByOneCategory.getCount()));
        String endTime = String.format("截止：%s", tiKuArticleByOneCategory.getEndTime());
        holder.articleTime.setText(endTime);

        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView articleNumber;
        TextView articleTime;
    }
}
