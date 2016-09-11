package com.cikuu.pigai.activity.teacher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.adapter.StudentArticleListInTeacherArticleListAdapter;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.activity.utils.TimeShowUtils;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class StudentArticleListInTeacherArticleActivity extends ActionBarActivity
        implements VolleyRequest.StudentArticleListCallback,
        View.OnClickListener {

    @InjectView(R.id.requireEndtime_in_teacher)
    TextView requireEndtimeInTeacher;
    @InjectView(R.id.articleId)
    TextView articleId;
    @InjectView(R.id.answerStudentNumber)
    TextView answerStudentNumber;
    @InjectView(R.id.articleTitle)
    TextView articleTitle;
    @InjectView(R.id.yaoqiu)
    TextView yaoqiu;
    @InjectView(R.id.studentArticleList)
    ListView studentArticleListView;
    @InjectView(R.id.listEmptyTextView)
    TextView listEmptyTextView;


    private ProgressDialog pDialog;
    private List<StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle> articleList =
            new ArrayList<StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle>();

    private StudentArticleListInTeacherArticleListAdapter adapter;
    private VolleyRequest mHttpRequest;
    private String mArticleTitle;
    private String mArticleNumber;
    private String mArticleSubmittedCount;
    private String mArticleEndtime;

    private long mArticleID;
    private int mPositionInListView;

    private int mStart = 0;
    private int mStep = 20;
    private int articleListItemCount = 0;

    private long mUid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_article_list_in_teacher_article2);
        ButterKnife.inject(this);

        mArticleTitle = this.getIntent().getStringExtra("TITLE");
        mArticleNumber = this.getIntent().getStringExtra("ARTICLE_ID");
        mArticleSubmittedCount = this.getIntent().getStringExtra("COUNT");
        mArticleEndtime = this.getIntent().getStringExtra("END_TIME");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(String.valueOf(mArticleNumber) + "号作文");

        requireEndtimeInTeacher.setText(TimeShowUtils.showTimeOfStudentHome(mArticleEndtime));
        articleId.setText("作文号" + mArticleNumber);
        answerStudentNumber.setText(mArticleSubmittedCount);
        articleTitle.setText(mArticleTitle);
        yaoqiu.setOnClickListener(this);
        mArticleID = Long.parseLong(mArticleNumber);

        adapter = new StudentArticleListInTeacherArticleListAdapter(this, articleList);
        studentArticleListView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("载入中...");
        pDialog.show();

        mUid = getLoginUid();

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mStudentArticleListCallback = this;
        mHttpRequest.GetStudentArticleListInTeacher(mUid, mArticleID, mStart);

        studentArticleListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:

                        if (studentArticleListView.getLastVisiblePosition() == studentArticleListView.getAdapter().getCount() - 1) {

                            if (articleListItemCount != 0) {
                                mStart += mStep;
                                //    can't clear articleList
                                mHttpRequest.GetStudentArticleListInTeacher(mUid, mArticleID, mStart);
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
        studentArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View itemClicked, int
                    position, long id) {

                StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle article =
                        (StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle)
                                studentArticleListView.getAdapter().getItem(position);
                long essay_id = article.mEssayId;
                Intent intent = new Intent(StudentArticleListInTeacherArticleActivity.this, StudentArticleInTeacherActivity.class);

                intent.putExtra("ESSAY_ID", String.valueOf(essay_id));
                intent.putExtra("ARTICLE_ID", String.valueOf(mArticleNumber));

                mPositionInListView = position;
                startActivity(intent);
            }

        });

    }

    private long getLoginUid() {
        long uid = 0;
        try {
            long student_uid = Student.GetInstance().mStudentDescription.mUid;
            long teacher_uid = Teacher.GetInstance().mDescription.mUid;
            String savedUserType = SharedPreferenceUtil.getUserTypeInSP(StudentArticleListInTeacherArticleActivity.this);
            if (savedUserType != null && savedUserType.equals(String.valueOf(VolleyRequest.User_Type_Student))) {
                uid = student_uid;
            }

            if (savedUserType != null && savedUserType.equals(String.valueOf(VolleyRequest.User_Type_Teacher))) {
                uid = teacher_uid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uid;
    }


    public void StudentArticles(List<StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle> articleItems) {
        hidePDialog();
        articleListItemCount = articleItems.size();

        List<StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle> oldList = new ArrayList<StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle>(articleList);
        Collections.copy(oldList, articleList);

        articleList.clear();
        articleList.addAll(oldList);
        articleList.addAll(articleItems);
        adapter.notifyDataSetChanged();

        if (articleList.size() == 0) {
            listEmptyTextView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    public void ErrorNetwork() {
        hidePDialog();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.yaoqiu) {
            Intent requrementIntent = new Intent(StudentArticleListInTeacherArticleActivity.this, ArticleRequirementInTeacherActivity.class);
            requrementIntent.putExtra("ARTICLEID", mArticleID);
            startActivity(requrementIntent);
        }
    }

    public static boolean SCORE_CHANGED = false;
    public static double SCORE = 0;

    @Override
    public void onResume() {
        if (SCORE_CHANGED) {
            //     articleList.get(mPositionInListView).mScore = SCORE;
            // must  clear articleList
            articleList.clear();
            adapter.notifyDataSetChanged();
            mStart = 0;
            mHttpRequest.GetStudentArticleListInTeacher(mUid, mArticleID, mStart);
            SCORE_CHANGED = false;
        }
        super.onResume();
        MobclickAgent.onResume(this);
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_article_list_in_teacher_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }

        if (id == R.id.action_fenxi) {
            Intent intent = new Intent(StudentArticleListInTeacherArticleActivity.this, StudentArticleSummaryInTeacherActivity.class);
            intent.putExtra("rid", String.valueOf(mArticleID));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }

}
