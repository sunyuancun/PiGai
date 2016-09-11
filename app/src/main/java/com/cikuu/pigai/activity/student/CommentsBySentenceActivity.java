package com.cikuu.pigai.activity.student;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.dialog.SafeProgressDialog;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class CommentsBySentenceActivity extends AppCompatActivity {

    private WebView mCommentWebView;
    private ActionBar mActionBar;
    private SafeProgressDialog mProgressDialog;

    public static Boolean mNeedUpdateNativeDataInSubmmtedActivity = false;
    public static Boolean mNeedUpdateNativeDataInStudentHomeArticleFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_by_sentence);

        initActionBar();

        mProgressDialog = new SafeProgressDialog(CommentsBySentenceActivity.this);
        mProgressDialog.setMessage("正在加载...");
        mProgressDialog.show();

//        String login_cookie = SharedPreferenceUtil.getPigaiCookieInSP(CommentsBySentenceActivity.this);
        String pigai_token = SharedPreferenceUtil.getPigaiTokenInSP(this);
        String pigai_login_type = SharedPreferenceUtil.getUserTypeInSP(CommentsBySentenceActivity.this);

        int uid = 0;
        int student_uid = Student.GetInstance().mStudentDescription.mUid;
        int teacher_uid = Teacher.GetInstance().mDescription.mUid;

        mCommentWebView = (WebView) findViewById(R.id.commentWebView);
        mCommentWebView.getSettings().setJavaScriptEnabled(true);
        mCommentWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mCommentWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                mNeedUpdateNativeDataInSubmmtedActivity = true;
                mNeedUpdateNativeDataInStudentHomeArticleFragment = true;
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
            }
        });

        if (pigai_login_type.equals(String.valueOf(VolleyRequest.User_Type_Student)))
            uid = student_uid;
        else if (pigai_login_type.equals(String.valueOf(VolleyRequest.User_Type_Teacher)))
            uid = teacher_uid;

        String url = "http://open.pigai.org/pgmobile-analysisResult?eid=" + getIntent().getStringExtra("ESSAYID") + "&pigai_token=" + pigai_token +
                "&pigai_uid=" + uid;

        mCommentWebView.loadUrl(url);
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
    }

    public void webviewGoBack() {
        try {
            WebBackForwardList history = mCommentWebView.copyBackForwardList();
            int index = -1;
            index = history.getCurrentIndex();
            gobackBeforeUrlDiGui(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gobackBeforeUrlDiGui(int index) {
        if (index <= 0) {
            finish();
            overridePendingTransition(0, R.anim.slidedown);
            return;
        } else {
            int beforeIndex = index - 1;
            WebBackForwardList history = mCommentWebView.copyBackForwardList();
            String CurrentUrl = history.getItemAtIndex(index).getUrl();
            String BeforeUrl = history.getItemAtIndex(beforeIndex).getUrl();
            if (BeforeUrl.contains(CurrentUrl)) {
                //递归
                gobackBeforeUrlDiGui(beforeIndex);
            } else {
                //result
                backResult(beforeIndex);
            }
            return;
        }

    }

    private void backResult(int index) {
        if (mCommentWebView.canGoBackOrForward(index)) {
            mCommentWebView.goBackOrForward(index);
        } else {
            finish();
            overridePendingTransition(0, R.anim.slidedown);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            webviewGoBack();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //友盟统计 计数事件
        MobclickAgent.onEventBegin(CommentsBySentenceActivity.this, "CommentsBySentence");
        MobclickAgent.onEventEnd(CommentsBySentenceActivity.this, "CommentsBySentence");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comments_by_sentence, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            webviewGoBack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
