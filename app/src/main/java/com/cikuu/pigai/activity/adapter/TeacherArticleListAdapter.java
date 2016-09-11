package com.cikuu.pigai.activity.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.TimeShowUtils;
import com.cikuu.pigai.businesslogic.Article;

import java.util.List;

public class TeacherArticleListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Article> articleItems;
    int sdk = android.os.Build.VERSION.SDK_INT;

    public TeacherArticleListAdapter(Activity activity, List<Article> articleItems) {
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


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.teacher_article_list_row, null);
            holder = new ViewHolder();
            holder.submittedNumber = (TextView) convertView.findViewById(R.id.submittedNumber);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.articleNumber = (TextView) convertView.findViewById(R.id.articlenumber);
            holder.articleEndTime = (TextView) convertView.findViewById(R.id.articletime);
            holder.articleCreateTime = (TextView) convertView.findViewById(R.id.articlecreate);
            holder.articleType = (TextView) convertView.findViewById(R.id.articletype);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // getting movie data for the row
        Article m = articleItems.get(position);

        //article numbers
        holder.submittedNumber.setText(String.valueOf(m.getCount()));

        // title
        holder.title.setText(m.getTitle());

        String number = String.format("作文号：%d", m.getArticleId());
        holder.articleNumber.setText(number);

        String endTime = TimeShowUtils.showTimeOfStudentHome(m.getEndTime());
        holder.articleEndTime.setText(endTime);

        if (endTime != null) {
            if (endTime.equals(TimeShowUtils.OUTTIME_OF_SUBMIT_ARTICLE)) {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.articleEndTime.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
                } else {
                    holder.articleEndTime.setBackground(activity.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
                }

            } else {
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.articleEndTime.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
                } else {
                    holder.articleEndTime.setBackground(activity.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
                }

            }
        }

        holder.articleType.setText(m.getRequest_type());
        holder.articleCreateTime.setText(m.getCreated_at());

        return convertView;
    }

    class ViewHolder {
        TextView submittedNumber;
        TextView title;
        TextView articleNumber;
        TextView articleEndTime;
        TextView articleCreateTime;
        public TextView articleType;
    }

}