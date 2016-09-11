package com.cikuu.pigai.activity.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.ScoreTools;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.StudentArticleDetail;
import com.cikuu.pigai.httprequest.VolleyRequest;

import java.util.List;

public class StudentArticleListInTeacherArticleListAdapter extends BaseAdapter {


    public static class StudentArticleInTeacherArticle {
        public long mEssayId;
        public String mStudentName;
        public String mDate;
        public String mTitle;
        public String mContent;
        public double mScore;
        public int mType;
        public String mStudentHead;

        public StudentArticleInTeacherArticle() {

        }
    }

    int sdk = android.os.Build.VERSION.SDK_INT;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<StudentArticleInTeacherArticle> mStudentArticleItems;
    private AppController.CacheImageLoader imageLoader;

    public StudentArticleListInTeacherArticleListAdapter(Activity activity, List<StudentArticleInTeacherArticle> articleItems) {
        this.mActivity = activity;
        this.mStudentArticleItems = articleItems;
        imageLoader = AppController.getInstance().getCacheImageLoader();
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all
    }

    @Override
    public int getCount() {
        return mStudentArticleItems.size();
    }

    @Override
    public Object getItem(int location) {
        return mStudentArticleItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (mInflater == null)
            mInflater = (LayoutInflater) mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.student_article_list_in_teacher_article, null);


        NetworkImageView student_head = (NetworkImageView) convertView.findViewById(R.id.student_head);
        TextView score = (TextView) convertView.findViewById(R.id.score);
        TextView studentName = (TextView) convertView.findViewById(R.id.studentName);
        TextView date = (TextView) convertView.findViewById(R.id.submiteDate);
        TextView title = (TextView) convertView.findViewById(R.id.articleTitle);
        //     TextView content = (TextView) convertView.findViewById(R.id.articleContent);
        TextView article_type = (TextView) convertView.findViewById(R.id.article_type);


        // getting movie data for the row
        StudentArticleInTeacherArticle m = mStudentArticleItems.get(position);

        double RealScore = ScoreTools.NumberChange(m.mScore);
        String highValue = String.valueOf((int) RealScore);
        int higgValueLen = highValue.length();
        String lowValue = String.valueOf((int) (RealScore * 10) % 10);

        if (!lowValue.equals("0")) {
            SpannableString builder = new SpannableString(highValue + "." + lowValue);
            builder.setSpan(new RelativeSizeSpan(0.5f), higgValueLen, higgValueLen + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            score.setText(builder);
        } else {
            score.setText(highValue);
        }

        if (m.mStudentHead != null) {
            student_head.setImageUrl(m.mStudentHead, imageLoader);
        }
//        if (m.mScore >= 60) {
//            score.setTextColor(Color.parseColor("#5db633"));
//            score.setBackgroundResource(R.drawable.scored);
//        } else {
//            score.setTextColor(Color.parseColor("#d80d17"));
//            score.setBackgroundResource(R.drawable.scored_unqualified);
//        }

        if (m.mType == 2) {
            article_type.setText("人");
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                article_type.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.student_home_ui_background_yellow));
            } else {
                article_type.setBackground(mActivity.getResources().getDrawable(R.drawable.student_home_ui_background_yellow));
            }

//            studentName.getPaint().setFakeBoldText(false);
        } else {
            article_type.setText("酷");
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                article_type.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
            } else {
                article_type.setBackground(mActivity.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
            }
//            studentName.getPaint().setFakeBoldText(true);
        }

        studentName.setText(m.mStudentName);
        date.setText(m.mDate);
        title.setText(m.mTitle);

        return convertView;
    }
}