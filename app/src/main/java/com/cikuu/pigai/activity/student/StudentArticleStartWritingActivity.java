package com.cikuu.pigai.activity.student;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Bundle;

import com.android.volley.toolbox.NetworkImageView;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.dialog.NoSubmitArticleDialog;
import com.cikuu.pigai.activity.utils.TimeShowUtils;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;


public class StudentArticleStartWritingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button start_write;
    private WebView article_require;
    private TextView mTitleTextView;


    private NetworkImageView article_requrementImage;

    private NetworkImageView mTeacherSmallHeadImageView;
    private TextView mTeacherNameTextView;
    private TextView mArticleIdTextView;
    private TextView mAnswerStudentCountsTextView;
    private TextView mEndTimeTextView;
    private TextView mManFenTextView;
    private TextView mZiShuTextView;

    private Student mStudent;

    String title;
    String require;
    String requireImage;
    String teacherSmallHead;
    String endtime;
    long essayId;
    long articleId;
    int count;
    int score;
    String reqCount;
    String teacherName;
    int teacherNo_paste;

    private ProgressDialog mProgressDialog;
    private CountDownTimer mTimer;
    AppController.CacheImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_article_start_writing2);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStudent = Student.GetInstance();
        InitGetIntentData();
        findViewById();

        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams lp = article_requrementImage.getLayoutParams();
        lp.width = 3 * screenWidth / 4;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        article_requrementImage.setLayoutParams(lp);
        article_requrementImage.setMaxWidth(screenWidth);
        article_requrementImage.setMaxHeight(screenWidth);

        imageLoader = AppController.getInstance().getCacheImageLoader();
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all


        mTeacherNameTextView.setText(teacherName);
        mArticleIdTextView.setText("作文号" + String.valueOf(articleId));
        mAnswerStudentCountsTextView.setText(String.valueOf(count));
        mEndTimeTextView.setText(TimeShowUtils.showTimeOfStudentHome(endtime));
        mManFenTextView.setText(String.valueOf(score) + "分");
        mZiShuTextView.setText(reqCount);
        mTitleTextView.setText(title);

//        article_require.setMovementMethod(ScrollingMovementMethod.getInstance());
//        article_require.setText(Html.fromHtml(require));

        article_require.loadDataWithBaseURL(null, require, "text/html", "utf-8", null);
        article_require.getSettings().setJavaScriptEnabled(true);
        article_require.setWebChromeClient(new WebChromeClient());


        if (requireImage != null)

        {
            article_requrementImage.setVisibility(View.VISIBLE);
            article_requrementImage.setImageUrl(requireImage, imageLoader);
        }

        if (teacherSmallHead != null)

        {
            mTeacherSmallHeadImageView.setVisibility(View.VISIBLE);
            mTeacherSmallHeadImageView.setImageUrl(teacherSmallHead, imageLoader);
        }

    }

    private void findViewById() {
        mTeacherSmallHeadImageView = (NetworkImageView) findViewById(R.id.teacher_small_head);
        mTeacherNameTextView = (TextView) findViewById(R.id.teacher_name);
        mArticleIdTextView = (TextView) findViewById(R.id.article_id);
        mAnswerStudentCountsTextView = (TextView) findViewById(R.id.answerstudent);

        mEndTimeTextView = (TextView) findViewById(R.id.endTime);
        mManFenTextView = (TextView) findViewById(R.id.manfen);
        mZiShuTextView = (TextView) findViewById(R.id.zishu);

        mTitleTextView = (TextView) findViewById(R.id.article_title);
        article_require = (WebView) findViewById(R.id.article_require);
        article_requrementImage = (NetworkImageView) findViewById(R.id.requrementImage);
        start_write = (Button) findViewById(R.id.start_write);
        start_write.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start_write) {

            String compareTime = TimeShowUtils.showTimeOfStudentHome(endtime);
            if (compareTime.equals(TimeShowUtils.OUTTIME_OF_SUBMIT_ARTICLE)) {
                showNoSubmitArticleDialog();
            } else {
                //write the article to database
                Intent intent = new Intent(StudentArticleStartWritingActivity.this, StudentArticleUnSubmittedActivity.class);
                //作文来源（在写作页面使用该字段）
                intent.putExtra("ArticleInNetWorkOrDataBase", String.valueOf(VolleyRequest.EDatabaseArticle));
                intent.putExtra("TEACHERNAME", teacherName);
                intent.putExtra("No_paste", teacherNo_paste);
                intent.putExtra("TITLE", title);
                intent.putExtra("REQ", require);
                intent.putExtra("END", endtime);
                intent.putExtra("ESSAY_ID", String.valueOf(essayId));
                intent.putExtra("ARTICLEID", String.valueOf(articleId));
                intent.putExtra("COUNT", String.valueOf(count));
                //TODO, get content from network first, then overwrite content from the DB if exist
                StudentArticle article = mStudent.GetStudentArticleDraftFromDataBase(articleId);
                if (article != null) {
                    intent.putExtra("CONTENT", article.mContent);
                }
                startActivity(intent);
                StudentArticleStartWritingActivity.this.finish();
            }

        }
    }

    public void showNoSubmitArticleDialog() {
        NoSubmitArticleDialog dialog;
        NoSubmitArticleDialog.Builder builder = new NoSubmitArticleDialog.Builder(StudentArticleStartWritingActivity.this);
        builder.setTitle("不能写作")
                .setMessage("温馨提示：已经过了截止日期")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    private void InitGetIntentData() {
        title = getIntent().getStringExtra("TITLE");
        require = getIntent().getStringExtra("REQ");
        requireImage = getIntent().getStringExtra("Image_requre");
        teacherSmallHead = getIntent().getStringExtra("TEACHERSMALLHEAD");
        endtime = getIntent().getStringExtra("END");
        essayId = Long.parseLong(getIntent().getStringExtra("ID"));
        articleId = Long.parseLong(getIntent().getStringExtra("ARTICLEID"));
        count = Integer.parseInt(getIntent().getStringExtra("COUNT"));
        score = Integer.parseInt(getIntent().getStringExtra("FULLSCORE"));
        reqCount = getIntent().getStringExtra("REQUIRECOUNT");
        teacherName = getIntent().getStringExtra("TEACHER");
        teacherNo_paste = getIntent().getIntExtra("No_paste", -1);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        if (StudentHomeArticleFragment.mNeedToRefreshArticleListFromNetwork) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setMessage("正在提交...");
//            mProgressDialog.show();
//
//            if (mTimer != null) {
//                mTimer.cancel();
//                mTimer = null;
//            }
//            mTimer = new CountDownTimer(3000, 1000) {
//                public void onTick(long millisUntilFinished) {
//                    //tick to do nothing
//                }
//
//                public void onFinish() {
//                    if (mProgressDialog != null) {
//                        mProgressDialog.dismiss();
//                        mProgressDialog = null;
//                    }
//                }
//            }.start();
//        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_article_requirement, menu);
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

