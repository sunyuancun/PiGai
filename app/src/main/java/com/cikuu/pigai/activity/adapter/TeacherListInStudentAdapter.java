package com.cikuu.pigai.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.student.SearcherTeacherActivity;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.SearchedTeacherInfoInStudent;

import java.util.List;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2015-10-12
 * Time: 12:06
 * Protect: PiGai
 */
public class TeacherListInStudentAdapter extends BaseAdapter {
    Context context;
    List<SearchedTeacherInfoInStudent> teacherList;
    AppController.CacheImageLoader imageLoader;
    private LayoutInflater inflater;

    public TeacherListInStudentAdapter(Context context, List<SearchedTeacherInfoInStudent> teacherList) {
        this.context = context;
        this.teacherList = teacherList;

        imageLoader = AppController.getInstance().getCacheImageLoader();
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all

    }

    @Override
    public int getCount() {
        return teacherList.size();
    }

    @Override
    public Object getItem(int i) {
        return teacherList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.teacher_info_listview_in_student, null);

        NetworkImageView teacherPhoto = (NetworkImageView) convertView.findViewById(R.id.userPhoto);
        TextView teacher_name = (TextView) convertView.findViewById(R.id.teacher_name);
        TextView teacher_school = (TextView) convertView.findViewById(R.id.teacher_school);
        TextView teacherAllArticleCount = (TextView) convertView.findViewById(R.id.allArticleCount);

        SearchedTeacherInfoInStudent searchedTeacherInfoInStudent = teacherList.get(position);
        teacher_name.setText(searchedTeacherInfoInStudent.getTeacherName());
        teacher_school.setText(searchedTeacherInfoInStudent.getTeacherSchool());
        teacherAllArticleCount.setText("共" + searchedTeacherInfoInStudent.getArticleCount() + "篇作文");
        teacherPhoto.setImageUrl(searchedTeacherInfoInStudent.getTeacherImage(), imageLoader);
        return convertView;
    }
}
