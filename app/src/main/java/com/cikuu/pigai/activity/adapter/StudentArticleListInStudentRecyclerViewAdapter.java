package com.cikuu.pigai.activity.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.ScoreTools;
import com.cikuu.pigai.activity.utils.TimeShowUtils;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.httprequest.VolleyRequest;

import java.util.List;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-05-11
 * Time: 14:49
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class StudentArticleListInStudentRecyclerViewAdapter extends RecyclerView.Adapter<StudentArticleListInStudentRecyclerViewAdapter.ViewHolder> {

    private List<StudentArticle> articleItems = null;
    private int sdk = android.os.Build.VERSION.SDK_INT;
    Context mContext;
    LayoutInflater inflater;

    MyItemClickListener mItemClickListener;
    MyItemLongClickListener mItemLongClickListener;

    public StudentArticleListInStudentRecyclerViewAdapter(Context activity, List<StudentArticle> datas) {
        this.articleItems = datas;
        this.mContext = activity;
        inflater = LayoutInflater.from(mContext);
    }

    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    /**
     * 设置Itemc长按监听
     *
     * @param listener
     */
    public void setOnItemLongClickListener(MyItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return articleItems.size();
    }

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

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_submitted_article_in_student2, viewGroup, false);
        return new ViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

    //将数据与界面进行绑定的操作
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);

        if (type == VolleyRequest.ENetworkArticle) {
            StudentArticle m = articleItems.get(position);

            viewHolder.title.setText(m.mTitle);
            viewHolder.articleFirstSentence.setText(m.mSample_content);
            viewHolder.submittime.setText(mContext.getResources().getString(R.string.submittime) + m.getSubmittedTime());

            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.articleType.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
            } else {
                viewHolder.articleType.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
            }

            if (m.mType == 2) {
                viewHolder.articleType.setText("人工阅");
            } else if (m.mType == 4) {
                viewHolder.articleType.setText("重写");
            } else {
                viewHolder.articleType.setText("句酷阅");
            }

            if (m.mArticleId == 10) {
                viewHolder.articleCategory.setText("自测");
            } else if (m.mTeacherName.equals("题库作文") && m.mArticleId != 10) {
                viewHolder.articleCategory.setText("题库作文");
            } else {
                if (TextUtils.isEmpty(m.mTeacherName) || null == m.mTeacherName) {
                    viewHolder.articleCategory.setText("教师");
                } else {
                    viewHolder.articleCategory.setText(m.mTeacherName);
                }
            }

            if (m.mArticleId == 10) {
                viewHolder.endtime.setVisibility(View.GONE);
            } else {
                viewHolder.endtime.setVisibility(View.VISIBLE);
                String endshow = TimeShowUtils.showTimeOfStudentHome(m.mEndtime);

                if (endshow.equals(TimeShowUtils.OUTTIME_OF_SUBMIT_ARTICLE)) {
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolder.endtime.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
                    } else {
                        viewHolder.endtime.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
                    }
                } else {
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolder.endtime.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
                    } else {
                        viewHolder.endtime.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
                    }
                }
                viewHolder.endtime.setText(endshow);
            }

            double RealScore = ScoreTools.NumberChange(m.mScore);
            String highValue = String.valueOf((int) RealScore);
            int higgValueLen = highValue.length();
            String lowValue = String.valueOf((int) (RealScore * 10) % 10);
            if (!lowValue.equals("0")) {
                SpannableString builder = new SpannableString(highValue + "." + lowValue);
                builder.setSpan(new RelativeSizeSpan(0.5f), higgValueLen, higgValueLen + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.score.setText(builder);
            } else {
                viewHolder.score.setText(highValue);
            }

            if (m.mScore >= 60) {
                viewHolder.score.setTextColor(Color.RED);
            }

            if (m.mScore < 60) {
                viewHolder.score.setTextColor(Color.RED);
            }

            if (m.mScore < 1 && lowValue.equals("0")) {
                viewHolder.score.setText("0");
            }

        }

        if (type == VolleyRequest.EDatabaseArticle) {
            //         holder.icon.setImageResource(R.drawable.draft);
            StudentArticle m = articleItems.get(position);

            viewHolder.title.setText(m.mTitle);
            viewHolder.articleFirstSentence.setText(m.getContent());
            viewHolder.submittime.setText(mContext.getResources().getString(R.string.savetime) + m.getSubmittedTime());
            viewHolder.score.setText("");

            if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.articleType.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_yellow));
            } else {
                viewHolder.articleType.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_yellow));
            }

            viewHolder.articleType.setText("草稿");

            if (m.mArticleId == 10) {
                viewHolder.endtime.setVisibility(View.GONE);
            } else {
                viewHolder.endtime.setVisibility(View.VISIBLE);
                String endshow = TimeShowUtils.showTimeOfStudentHome(m.mEndtime);
                if (endshow.equals(TimeShowUtils.OUTTIME_OF_SUBMIT_ARTICLE)) {
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolder.endtime.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
                    } else {
                        viewHolder.endtime.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_grey));
                    }
                } else {
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolder.endtime.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
                    } else {
                        viewHolder.endtime.setBackground(mContext.getResources().getDrawable(R.drawable.student_home_ui_background_blue));
                    }
                }
                viewHolder.endtime.setText(endshow);
            }

            String mTeacherName = m.getmTeacherName();
            if (m.mArticleId == 10) {
                viewHolder.articleCategory.setText(m.mTeacherName);
            } else if (m.mArticleId != 10 && mTeacherName.equals("题库作文")) {
                viewHolder.articleCategory.setText("题库作文");
            } else {
                if (TextUtils.isEmpty(mTeacherName) || mTeacherName == null) {
                    viewHolder.articleCategory.setText("教师");
                } else {
                    viewHolder.articleCategory.setText(m.mTeacherName);
                }
            }

        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView articleType;
        TextView articleCategory;
        TextView title;
        TextView endtime;
        TextView submittime;
        TextView score;
        TextView articleFirstSentence;
        LinearLayout right_arrow;

        private MyItemClickListener mListener;
        private MyItemLongClickListener mLongClickListener;


        public ViewHolder(View rootView, MyItemClickListener listener, MyItemLongClickListener longClickListener) {
            super(rootView);

            this.mListener = listener;
            this.mLongClickListener = longClickListener;
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);

            title = (TextView) rootView.findViewById(R.id.articleTitle);
            articleFirstSentence = (TextView) rootView.findViewById(R.id.articleFirstSentence);
            endtime = (TextView) rootView.findViewById(R.id.endtime);
            submittime = (TextView) rootView.findViewById(R.id.submitTime);
            articleType = (TextView) rootView.findViewById(R.id.articlePiGaiType);
            articleCategory = (TextView) rootView.findViewById(R.id.articleCategory);
            score = (TextView) rootView.findViewById(R.id.submitted);
            right_arrow = (LinearLayout) rootView.findViewById(R.id.right_arrow);

        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null) {
                mLongClickListener.onItemLongClick(view, getAdapterPosition());
            }
            return true;
        }
    }


    //item 点击 接口
    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }

    //item 长按点击接口
    public interface MyItemLongClickListener {
        void onItemLongClick(View view, int postion);
    }

}
