package com.cikuu.pigai.activity.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.adapter.TeacherListInStudentAdapter;
import com.cikuu.pigai.businesslogic.SearchedTeacherInfoInStudent;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.httprequest.VolleyRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearcherTeacherActivity extends ActionBarActivity implements VolleyRequest.FindTeacherListByTeacherNameAndStudentSchoolCallback {


    VolleyRequest mHttpRequest;
    ProgressDialog mProgressDialog;

    TextView teacher_no_empty_view;
    ListView teacherListView;
    TeacherListInStudentAdapter teacherListAdapter;
    List<SearchedTeacherInfoInStudent> mTeacherList;
    private int mStart = 0;
    int teacherListItemCount = 0;

    private static String TEACHERNAME_UTF8 = "";
    private static String STUDENTSCHOOL_UTF8 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcher_teacher);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("老师列表");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.show();

        final String TEACHERNAME = getIntent().getStringExtra("TEACHERNAME");
        final String STUDENTSCHOOL = getIntent().getStringExtra("STUDENTSCHOOL");
        try {
            TEACHERNAME_UTF8 = URLEncoder.encode(TEACHERNAME, "UTF-8");
            STUDENTSCHOOL_UTF8 = URLEncoder.encode(STUDENTSCHOOL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        teacher_no_empty_view = (TextView) findViewById(R.id.teacher_no_empty_view);
        teacherListView = (ListView) findViewById(R.id.teacherList);
        mTeacherList = new ArrayList<>();
        teacherListAdapter = new TeacherListInStudentAdapter(this, mTeacherList);
        teacherListView.setAdapter(teacherListAdapter);

        //  teacherListView.setEmptyView(findViewById(R.id.teacher_no_empty_view));

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mFindTeacherListByTeacherNameAndStudentSchoolCallback = this;
        mHttpRequest.FindTeacherListByTeacherNameAndStudentSchool(TEACHERNAME_UTF8, STUDENTSCHOOL_UTF8, mStart);

        teacherListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        if (teacherListView.getLastVisiblePosition() == teacherListView.getAdapter().getCount() - 1) {
                            if (teacherListItemCount != 0) {
                                showProgressDialog();
                                mStart += 10;
                                mHttpRequest.FindTeacherListByTeacherNameAndStudentSchool(TEACHERNAME_UTF8, STUDENTSCHOOL_UTF8, mStart);
                            }
                        }
                        break;
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        teacherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int
                    position, long id) {
                SearchedTeacherInfoInStudent searchedTeacherInfoInStudent = (SearchedTeacherInfoInStudent) teacherListView.getAdapter().getItem(position);
                long teacherUid = searchedTeacherInfoInStudent.getTeacherUid();
                Intent searchTeacherArticlesIntent = new Intent(SearcherTeacherActivity.this, TeacherArticleListInStudentActivity.class);
                searchTeacherArticlesIntent.putExtra("TEACHERUID", teacherUid);
                startActivity(searchTeacherArticlesIntent);
                SearcherTeacherActivity.this.finish();
            }
        });

    }

    @Override
    public void getTeacherList(List<SearchedTeacherInfoInStudent> teacherList) {
        teacherListItemCount = teacherList.size();
        List<SearchedTeacherInfoInStudent> newList = new ArrayList<SearchedTeacherInfoInStudent>(mTeacherList);
        Collections.copy(newList, mTeacherList);
        for (int i = 0; i < teacherList.size(); i++) {
            newList.add(teacherList.get(i));
        }
        mTeacherList.clear();
        mTeacherList.addAll(newList);
        teacherListAdapter.notifyDataSetChanged();

        if (mTeacherList.size() > 10 && teacherList.size() == 0) {
            Toast.makeText(this, "全部老师加载完成", Toast.LENGTH_SHORT).show();
        } else if (mTeacherList.size() == 0 && teacherList.size() == 0) {
            teacher_no_empty_view.setVisibility(View.VISIBLE);
        }
        hideProgressDialog();
    }

    @Override
    public void ErrorNetwork() {
        hideProgressDialog();
    }

    private void hideProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        try {
            if (mProgressDialog != null)
                mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searcher_teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
