package com.cikuu.pigai.activity.adapter;

import android.annotation.TargetApi;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.ScoreTools;
import com.cikuu.pigai.activity.utils.TimeShowUtils;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.httprequest.VolleyRequest;

import java.util.List;

public class StudentArticleListInStudentAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<StudentArticle> articleItems;
    int sdk = android.os.Build.VERSION.SDK_INT;

    public StudentArticleListInStudentAdapter(Context activity, List<StudentArticle> articleItems) {
        this.mContext = activity;
        this.articleItems = articleItems;
        inflater = LayoutInflater.from(mContext);
    }

    public List<StudentArticle> getArticleList() {
        return articleItems;
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
    public int getItemViewType(int position) {
        StudentArticle article = (StudentArticle) getItem(position);
        return article.getNetworkOrDatabase();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.student_submitted_article_in_student2, parent, false);
            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.articleTitle);
            holder.articleFirstSentence = (TextView) convertView.findViewById(R.id.articleFirstSentence);
            holder.endtime = (TextView) convertView.findViewById(R.id.endtime);
            holder.submittime = (TextView) convertView.findViewById(R.id.submitTime);
            holder.articleType = (TextView) convertView.findViewById(R.id.articlePiGaiType);
            holder.articleCategory = (TextView) convertView.findViewById(R.id.articleCategory);
            holder.score = (TextView) convertView.findViewById(R.id.submitted);
            holder.right_arrow = (LinearLayout) convertView.findViewById(R.id.right_arrow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (type == VolleyRequest.ENetworkArticle) {
            StudentArticle m = articleItems.get(position);
            holder.title.setText(m.mTitle);
            holder.articleFirstSentence.setText(m.mSample_content);
            holder.submittime.setText(mContext.getResources().getString(R.string.submittime) + m.getSubmittedTime());

            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.articleType.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
            } else {
                holder.articleType.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
            }

            if (m.mType == 2) {
                holder.articleType.setText("人工阅");
            } else if (m.mType == 4) {
                holder.articleType.setText("重写");
            } else {
                holder.articleType.setText("句酷阅");
            }

            if (m.mArticleId == 10) {
                holder.articleCategory.setText("自测");
            } else if (m.mTeacherName.equals("题库作文") && m.mArticleId != 10) {
                holder.articleCategory.setText("题库作文");
            } else {
                if (TextUtils.isEmpty(m.mTeacherName) || null == m.mTeacherName) {
                    holder.articleCategory.setText("教师");
                } else {
                    holder.articleCategory.setText(m.mTeacherName);
                }
            }

            if (m.mArticleId == 10) {
                holder.endtime.setVisibility(View.GONE);
            } else {
                holder.endtime.setVisibility(View.VISIBLE);
                String endshow = TimeShowUtils.showTimeOfStudentHome(m.mEndtime);

                if (endshow.equals(TimeShowUtils.OUTTIME_OF_SUBMIT_ARTICLE)) {
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.endtime.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
                    } else {
                        holder.endtime.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
                    }
                } else {
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.endtime.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
                    } else {
                        holder.endtime.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
                    }
                }
                holder.endtime.setText(endshow);
            }
//            holder.endtime.setText("截止：" + m.mEndtime);
//            holder.submittime.setText(m.mSubmittedTime);
//            java.text.DecimalFormat df = new java.text.DecimalFormat("#.#");
//            String socreText = df.format(m.mScore);
            double RealScore = ScoreTools.NumberChange(m.mScore);
            String highValue = String.valueOf((int) RealScore);
            int higgValueLen = highValue.length();
            String lowValue = String.valueOf((int) (RealScore * 10) % 10);
            if (!lowValue.equals("0")) {
                SpannableString builder = new SpannableString(highValue + "." + lowValue);
                builder.setSpan(new RelativeSizeSpan(0.5f), higgValueLen, higgValueLen + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.score.setText(builder);
            } else {
                holder.score.setText(highValue);
            }

            if (m.mScore >= 60) {
//                holder.score.setTextColor(Color.parseColor("#5db62f"));
                holder.score.setTextColor(Color.RED);
            }

            if (m.mScore < 60) {
                holder.score.setTextColor(Color.RED);
            }

            if (m.mScore < 1 && lowValue.equals("0")) {
                holder.score.setText("0");
            }
            return convertView;
        }

        if (type == VolleyRequest.EDatabaseArticle) {
            //         holder.icon.setImageResource(R.drawable.draft);
            StudentArticle m = articleItems.get(position);

            holder.title.setText(m.mTitle);
            holder.articleFirstSentence.setText(m.getContent());
            holder.submittime.setText(mContext.getResources().getString(R.string.savetime) + m.getSubmittedTime());
            holder.score.setText("");

            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.articleType.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_yellow));
            } else {
                holder.articleType.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_yellow));
            }

            holder.articleType.setText("草稿");

            if (m.mArticleId == 10) {
                holder.endtime.setVisibility(View.GONE);
            } else {
                holder.endtime.setVisibility(View.VISIBLE);
                String endshow = TimeShowUtils.showTimeOfStudentHome(m.mEndtime);
                if (endshow.equals(TimeShowUtils.OUTTIME_OF_SUBMIT_ARTICLE)) {
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.endtime.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
                    } else {
                        holder.endtime.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
                    }
                } else {
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.endtime.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
                    } else {
                        holder.endtime.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
                    }
                }
                holder.endtime.setText(endshow);
            }

            String mTeacherName = m.getmTeacherName();
            if (m.mArticleId == 10) {
                holder.articleCategory.setText(m.mTeacherName);
            } else if (m.mArticleId != 10 && mTeacherName.equals("题库作文")) {
                holder.articleCategory.setText("题库作文");
            } else {
                if (TextUtils.isEmpty(mTeacherName) || mTeacherName == null) {
                    holder.articleCategory.setText("教师");
                } else {
                    holder.articleCategory.setText(m.mTeacherName);
                }
            }

//            holder.endtime.setText("作文草稿");
//            holder.submittime.setText("");

//            holder.articleCategory.setText("");


//            holder.articleType.setText("");
//            }else if (m.mType == 4){
//                submittedNumber.setText("重写");
//            }else {
//                submittedNumber.setText("句酷批改阅");
//            }

            return convertView;
        }

        return null;
    }


    class ViewHolder {
        TextView articleType;
        TextView articleCategory;
        TextView title;
        TextView endtime;
        TextView submittime;
        TextView score;
        ImageView icon;
        public TextView articleFirstSentence;
        public LinearLayout right_arrow;
    }

}
