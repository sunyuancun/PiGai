package com.cikuu.pigai.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.Time;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.teacher.StudentArticleInTeacherActivity;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.CommentBase;
import com.cikuu.pigai.businesslogic.Teacher;

import java.io.Console;
import java.util.Calendar;
import java.util.List;

public class CommentsAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<CommentBase> mCommentsItems;
    public boolean mPlayOrPause = false;
    public int mClickPos = 0;
    private boolean hideDeleteButton = false;
    AppController.CacheImageLoader imageLoader = AppController.getInstance().getCacheImageLoader();
    Calendar c = Calendar.getInstance();

    public CommentsAdapter(Activity activity, List<CommentBase> commentsItems) {
        this.activity = activity;
        this.mCommentsItems = commentsItems;
    }

    @Override
    public int getCount() {
        return mCommentsItems.size();
    }

    @Override
    public Object getItem(int location) {
        return mCommentsItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.comments_list_row, null);
        NetworkImageView teacherCommentPic = (NetworkImageView) convertView.findViewById(R.id.teacherImage);
        TextView commentDuration = (TextView) convertView.findViewById(R.id.commentDuration);
        TextView commentsContent = (TextView) convertView.findViewById(R.id.commentContent);
        TextView commentsDate = (TextView) convertView.findViewById(R.id.commentsDate);
        ImageView playImage = (ImageView) convertView.findViewById(R.id.playImage);
        TextView commentByWho = (TextView) convertView.findViewById(R.id.commentByWho);
        Button deleteButton = (Button) convertView.findViewById(R.id.deleteCommentBtn);
        RelativeLayout voiceLayout = (RelativeLayout) convertView.findViewById(R.id.voiceContent);


        if (hideDeleteButton)
            deleteButton.setVisibility(View.INVISIBLE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StudentArticleInTeacherActivity holder = (StudentArticleInTeacherActivity) activity;
                holder.DeleteComment(position);
            }
        });

        CommentBase m = mCommentsItems.get(position);
        if (m.mType == CommentBase.ETEXTCOMMENT) {
            playImage.setVisibility(View.INVISIBLE);
            voiceLayout.setVisibility(View.INVISIBLE);
            commentsContent.setVisibility(View.VISIBLE);
            commentsContent.setText(m.mTextComments);

        }
        if (m.mType == CommentBase.EVOICECOMMENT) {
//            frameLayout.setBackgroundResource(R.drawable.play_background);
            playImage.setVisibility(View.VISIBLE);
            voiceLayout.setVisibility(View.VISIBLE);
            if (position == mClickPos && mPlayOrPause) {
                playImage.setImageResource(R.drawable.pause);
            } else {
                playImage.setImageResource(R.drawable.play);
                commentsContent.setVisibility(View.INVISIBLE);
                commentsContent.setText("");

                c.setTimeInMillis(m.mDuration * 1000);
                String sTime = String.format("%tM:%tS", c, c);
                commentDuration.setText(sTime + "''");
            }
        }

        commentsDate.setText(m.mDate);
        commentByWho.setText(m.mTeacherName);

        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all
        //   teacherCommentPic.setDefaultImageResId(R.drawable.ic_launcher);
        teacherCommentPic.setErrorImageResId(R.drawable.ic_launcher);
        teacherCommentPic.setImageUrl(m.mPic, imageLoader);
        return convertView;
    }

    public void HideDeleteButton() {
        hideDeleteButton = true;
    }

}