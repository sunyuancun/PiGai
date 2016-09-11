package com.cikuu.pigai.activity.student;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.KeyBoardUtil;
import com.cikuu.pigai.businesslogic.ArticleRequirement;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.dbmodel.Article_No_Paste_DatabaseHelper;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.ns.developer.tagview.entity.Tag;
import com.ns.developer.tagview.widget.TagCloudLinkView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentSearchActivity extends ActionBarActivity
        implements VolleyRequest.FindArticleCallback,
        VolleyRequest.ArticleRequirementCallback,
        VolleyRequest.GetStudentTeacherCallback {

    private TextView noneArticle;
    private ListView listView;
    private Button search;
    private LinearLayout search_result;
    private EditText mSearchEditText;

    private VolleyRequest mHttpRequest;
    private ProgressDialog mProgressDialog;
    private Student mStudent;
    private long mArticleId;
    private TagCloudLinkView mTeacherNameTagView;

    List<StudentArticle> articleList;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_student_search);
        search_result = (LinearLayout) findViewById(R.id.search_message);
        mProgressDialog = new ProgressDialog(this);
        mStudent = Student.GetInstance();

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mFindArticleCallback = this;
        mHttpRequest.mArticleRequirementCallback = this;
        mHttpRequest.mGetStudentTeacherCallback = this;

        noneArticle = (TextView) findViewById(R.id.noneArticle);

        listView = (ListView) findViewById(R.id.listview_myArticles);
        articleList = new ArrayList<StudentArticle>();
        adapter = new MyAdapter(StudentSearchActivity.this, articleList);
        listView.setAdapter(adapter);

        mSearchEditText = (EditText) findViewById(R.id.articleId);
        search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                articleList.clear();
                search_result.setVisibility(View.INVISIBLE);
                noneArticle.setVisibility(View.INVISIBLE);
                KeyBoardUtil.hideSoftInputFromWindow(StudentSearchActivity.this);
                String searchText = mSearchEditText.getText().toString().trim();
                Boolean isArticleNumber = false;

                if (!TextUtils.isEmpty(searchText)) {
                    try {
                        mArticleId = Long.parseLong(searchText);
                        isArticleNumber = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isArticleNumber) {
                        //搜文章
                        showPDialog();
                        mHttpRequest.FindArticleByArticleId(mArticleId);

                    } else {
                        //搜老师
                        if (TextUtils.isEmpty(mStudent.GetDescription().mSchool)) {
                            mStudent.GetDescription().mSchool = "jukuu";
                        }
                        Intent searchTeacherNameIntent = new Intent(StudentSearchActivity.this, SearcherTeacherActivity.class);
                        searchTeacherNameIntent.putExtra("TEACHERNAME", searchText);
                        searchTeacherNameIntent.putExtra("STUDENTSCHOOL", mStudent.GetDescription().mSchool);
                        startActivity(searchTeacherNameIntent);
                        StudentSearchActivity.this.finish();
                    }
                }

            }
        });

        mTeacherNameTagView = (TagCloudLinkView) findViewById(R.id.teacherNameTagView);
        mTeacherNameTagView.setOnTagSelectListener(new TagCloudLinkView.OnTagSelectListener() {
            @Override
            public void onTagSelected(Tag tag, int i) {
                long teacherUid = tag.getId();
                Intent teacherArticleListIntent = new Intent(StudentSearchActivity.this, TeacherArticleListInStudentActivity.class);
                teacherArticleListIntent.putExtra("TEACHERUID", teacherUid);
                startActivity(teacherArticleListIntent);
                StudentSearchActivity.this.finish();
            }
        });
        mHttpRequest.GetStudentTeacher(mStudent.mStudentDescription.mUid);

    }


    public void StudentTeacher(int success, Map<Integer, String> teacherMap, String errorMsg) {
        if (success == 1) {
            for (Map.Entry<Integer, String> entry : teacherMap.entrySet()) {
                System.out.println(entry.getKey() + "/" + entry.getValue());
                mTeacherNameTagView.add(new Tag(entry.getKey(), entry.getValue()));
            }
            mTeacherNameTagView.drawTags();
        }
    }

    public void ArticleExist(boolean exist) {
        if (exist) {
            mHttpRequest.GetArticleRequirement(mArticleId);
        } else {
            hidePDialog();
            search_result.setVisibility(View.VISIBLE);
            noneArticle.setVisibility(View.VISIBLE);
            //    adapter.notifyDataSetChanged();
        }
    }


    public void ArticleRequirement(final ArticleRequirement studentArticle) {
        hidePDialog();
        search_result.setVisibility(View.VISIBLE);
        noneArticle.setVisibility(View.GONE);

        StudentArticle article = new StudentArticle();
        article.mTitle = studentArticle.mTitle;
        article.mRequirement = studentArticle.mRequirement;
        article.mEndtime = studentArticle.mEndtime;
        article.mArticleId = studentArticle.mArticleId;
        article.mCount = studentArticle.mCount;
        article.mNo_paste = studentArticle.mNo_paste;
        if (article.mEndtime == null) {
            article.mEndtime = "";
        }
        articleList.add(article);
        adapter.notifyDataSetChanged();

        //   NO_PASTE  存数据库  mArticleId v  NO_PASTE
        // todo“没有考虑  rid=  10”
        Article_No_Paste_DatabaseHelper.getInstance(StudentSearchActivity.this).createArticleId(article);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StudentSearchActivity.this, StudentArticleStartWritingActivity.class);
                StudentArticle article = articleList.get(0);
                intent.putExtra("TITLE", article.mTitle);
                intent.putExtra("REQ", article.mRequirement);
                intent.putExtra("END", article.mEndtime);
                intent.putExtra("ID", String.valueOf(article.mEssayId));
                intent.putExtra("ARTICLEID", String.valueOf(article.mArticleId));
                intent.putExtra("COUNT", String.valueOf(article.mCount));
                //studentArticle   come from   ArticleRequirement(final ArticleRequirement studentArticle)
                intent.putExtra("FULLSCORE", String.valueOf(studentArticle.mFullScore));
                intent.putExtra("REQUIRECOUNT", studentArticle.mRequireCount);
                intent.putExtra("TEACHER", studentArticle.mTeacher);
                intent.putExtra("No_paste", studentArticle.mNo_paste);
                intent.putExtra("Image_requre", studentArticle.mImageRequirement);
                intent.putExtra("TEACHERSMALLHEAD", studentArticle.mTeacherSmallHead);
                startActivity(intent);
                StudentSearchActivity.this.finish();
            }
        });
    }

    public void ErrorNetwork() {
        Toast store = Toast.makeText(getApplicationContext(), "网络或服务器错误，找不到文章！", Toast.LENGTH_LONG);
        store.show();
    }

    private void showPDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.setMessage("刷新列表...");
                mProgressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hidePDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //友盟统计
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //----------------------------------------------------------------------------
    public class MyAdapter extends BaseAdapter {
        Activity mContext;
        LayoutInflater inflater;
        private List<StudentArticle> articleItems;

        public MyAdapter(Activity activity, List<StudentArticle> articleItems) {
            this.mContext = activity;
            this.articleItems = articleItems;
            inflater = LayoutInflater.from(mContext);
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
            int type = article.getNetworkOrDatabase();
            return type;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.student_searched_article_in_student, parent, false);
            }

            int type = getItemViewType(position);
            if (type == VolleyRequest.ENetworkArticle) {
                StudentArticle m = articleItems.get(position);
                TextView submittedNumber = (TextView) convertView.findViewById(R.id.student_count);
                TextView title = (TextView) convertView.findViewById(R.id.articleTitle);
                TextView endtime = (TextView) convertView.findViewById(R.id.endtime);
                TextView submitTime = (TextView) convertView.findViewById(R.id.submit_time);
                TextView icon = (TextView) convertView.findViewById(R.id.submitted);

                icon.setText("答题");
                icon.setTextSize(18);
                submittedNumber.setText(String.valueOf(m.getCount()) + "人提交");
                title.setText(m.mTitle);
                endtime.setText("截止:" + m.mEndtime);
                return convertView;
            }

            if (type == VolleyRequest.EDatabaseArticle) {
                StudentArticle m = articleItems.get(position);
                TextView submittedNumber = (TextView) convertView.findViewById(R.id.student_count);
                TextView title = (TextView) convertView.findViewById(R.id.articleTitle);
                TextView endtime = (TextView) convertView.findViewById(R.id.endtime);

                submittedNumber.setText(String.valueOf(m.getCount()));
                title.setText(m.mTitle);
                endtime.setText(m.mEndtime);
                return convertView;
            }
            return null;
        }
    }
}

