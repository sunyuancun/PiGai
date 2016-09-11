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
import com.cikuu.pigai.activity.adapter.TeacherArticleListInStudentAdapter;
import com.cikuu.pigai.businesslogic.ArticleRequirement;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.businesslogic.Article;
import com.cikuu.pigai.dbmodel.Article_No_Paste_DatabaseHelper;
import com.cikuu.pigai.httprequest.VolleyRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeacherArticleListInStudentActivity extends ActionBarActivity implements VolleyRequest.TeacherArticleListCallback, VolleyRequest.ArticleRequirementCallback {

    ProgressDialog pDialog;
    VolleyRequest mHttpRequest;

    TextView teacherarticle_no_empty_view;
    ListView mTeacherarticleListView;
    TeacherArticleListInStudentAdapter mTeacherArticleListAdapter;
    private List<Article> articleList;

    private int mStart = 0;
    private int articleItemsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_article_list_in_student);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("老师作文列表");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("载入中...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        long teacherUid = getIntent().getLongExtra("TEACHERUID", 0);
        final int teacher_Uid = Integer.parseInt(String.valueOf(teacherUid));

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mTeacherArticleListCallback = this;
        mHttpRequest.mArticleRequirementCallback = this;
        articleList = new ArrayList<Article>();
        teacherarticle_no_empty_view = (TextView) findViewById(R.id.teacherarticle_no_empty_view);
        mTeacherarticleListView = (ListView) findViewById(R.id.teacherarticlelistInStudent);
        mTeacherArticleListAdapter = new TeacherArticleListInStudentAdapter(this, articleList);
        mTeacherarticleListView.setAdapter(mTeacherArticleListAdapter);
        //    mTeacherarticleListView.setEmptyView(findViewById(R.id.teacherarticle_no_empty_view));


        mHttpRequest.GetTeacherArticleListInStudent(teacher_Uid, mStart);

        mTeacherarticleListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        if (mTeacherarticleListView.getLastVisiblePosition() == mTeacherarticleListView.getAdapter().getCount() - 1) {
                            if (articleItemsCount != 0) {
                                showProgressDialog();
                                mStart += 10;
                                mHttpRequest.GetTeacherArticleListInStudent(teacher_Uid, mStart);
                                pDialog.show();
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


        mTeacherarticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long flag) {
                Article article = (Article) mTeacherarticleListView.getAdapter().getItem(position);
                // 如果是双击,1秒内连续点击判断为双击
                //  article.getArticleId() == lastClickId
                if ((Math.abs(lastClickTime - System.currentTimeMillis()) < 1000)) {
                    // lastClickId = 0;
                    lastClickTime = 0;
                    return;
                } else {
                    // lastClickId = article.getArticleId();
                    lastClickTime = System.currentTimeMillis();
                    long articleId = article.getArticleId();
                    if (articleId != 0) {
                        showProgressDialog();
                        mHttpRequest.GetArticleRequirement(articleId);
                    }
                }

            }
        });
    }

    // 双击事件记录最近一次点击的ID
    private long lastClickId = 0;

    // 双击事件记录最近一次点击的时间
    private long lastClickTime = 0;


    @Override
    public void TeacherArticles(List<Article> articleItems) {
        articleItemsCount = articleItems.size();
        List<Article> newList = new ArrayList<Article>(articleList);
        Collections.copy(newList, articleList);

        for (int i = 0; i < articleItems.size(); i++) {
            newList.add(articleItems.get(i));
        }
        articleList.clear();
        articleList.addAll(newList);
        mTeacherArticleListAdapter.notifyDataSetChanged();

        if (articleList.size() > 10 && articleItems.size() == 0) {
            Toast.makeText(this, "全部作文加载完成", Toast.LENGTH_SHORT).show();
        } else if (articleList.size() == 0 && articleItems.size() == 0) {
            teacherarticle_no_empty_view.setVisibility(View.VISIBLE);
        }
        hideProgressDialog();
    }

    @Override
    public void ArticleRequirement(ArticleRequirement articleRequirement) {

        StudentArticle article = new StudentArticle();
        article.mTitle = articleRequirement.mTitle;
        article.mRequirement = articleRequirement.mRequirement;
        article.mArticleId = articleRequirement.mArticleId;
        article.mCount = articleRequirement.mCount;
        article.mNo_paste = articleRequirement.mNo_paste;
        article.mEndtime = articleRequirement.mEndtime;
        if (article.mEndtime == null) {
            article.mEndtime = "";
        }

        //   NO_PASTE  存数据库  mArticleId v  NO_PASTE
        // todo“没有考虑  rid=  10”
        Article_No_Paste_DatabaseHelper.getInstance(TeacherArticleListInStudentActivity.this).createArticleId(article);


        Intent intent = new Intent(TeacherArticleListInStudentActivity.this, StudentArticleStartWritingActivity.class);
        intent.putExtra("TITLE", articleRequirement.mTitle);
        intent.putExtra("REQ", articleRequirement.mRequirement);
        intent.putExtra("END", articleRequirement.mEndtime);
        //mEssayId  =  0  here
        intent.putExtra("ID", String.valueOf(0));
        intent.putExtra("ARTICLEID", String.valueOf(articleRequirement.mArticleId));
        intent.putExtra("COUNT", String.valueOf(articleRequirement.mCount));
        intent.putExtra("FULLSCORE", String.valueOf(articleRequirement.mFullScore));
        intent.putExtra("REQUIRECOUNT", articleRequirement.mRequireCount);
        intent.putExtra("TEACHER", articleRequirement.mTeacher);
        intent.putExtra("No_paste", articleRequirement.mNo_paste);
        intent.putExtra("Image_requre", articleRequirement.mImageRequirement);
        intent.putExtra("TEACHERSMALLHEAD", articleRequirement.mTeacherSmallHead);
        startActivity(intent);
        TeacherArticleListInStudentActivity.this.finish();
        hideProgressDialog();
    }

    private void showProgressDialog() {
        try {
            if (pDialog != null) {
                pDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideProgressDialog() {
        try {
            if (pDialog != null) {
                pDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ErrorNetwork() {
        hideProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher_article_list_in_student, menu);
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
