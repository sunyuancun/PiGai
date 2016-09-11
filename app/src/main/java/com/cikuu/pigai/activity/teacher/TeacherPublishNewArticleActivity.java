package com.cikuu.pigai.activity.teacher;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.activity.utils.WebViewUtil;
import com.cikuu.pigai.businesslogic.Teacher;
import com.umeng.analytics.MobclickAgent;

public class TeacherPublishNewArticleActivity extends AppCompatActivity {

    private WebView mPublishNewArticleWebView;
    private ProgressDialog mProgressDialog;
    //根据lasturl，判断是否需要退出
    private String lastUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_publish_new_article);

        initActionBar();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在加载...");
        mProgressDialog.show();

        Teacher mTeacher = Teacher.GetInstance();
        String uid = String.valueOf(mTeacher.mDescription.mUid);
        String mPiGaiToken = SharedPreferenceUtil.getPigaiTokenInSP(this);

        mPublishNewArticleWebView = (WebView) findViewById(R.id.publishArticleWebView);
        mPublishNewArticleWebView.getSettings().setJavaScriptEnabled(true);
        mPublishNewArticleWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mPublishNewArticleWebView.setWebViewClient(new WebViewClient() {
            //shouldOverrideUrlLoading：对网页中超链接按钮的响应。当按下某个连接时WebViewClient会调用这个方法
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("c=m&a=viewlist&rid=") && (lastUrl.contains("a=t_create") || lastUrl.contains("a=t_modify&rid="))) {
                    //友盟统计 计数事件
                    MobclickAgent.onEventBegin(TeacherPublishNewArticleActivity.this, "PublishNewArticle");
                    MobclickAgent.onEventEnd(TeacherPublishNewArticleActivity.this, "PublishNewArticle");
                    TeacherHomeArticleFragment.NEW_ARTICLE_PUBLISHED = true;
                    TeacherPublishNewArticleActivity.this.finish();
                } else {
                    view.loadUrl(url);
                    lastUrl = url;
                }
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                hidePDialog();
            }

        });

        String url = "http://www.pigai.org/?c=m&a=t_create&fr=cl" + "&pigai_uid=" + uid + "&pigai_token=" + mPiGaiToken;
        lastUrl = url;
        mPublishNewArticleWebView.loadUrl(url);

    }

    private void initActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(true);
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

    @Override
    public void onResume() {
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
        getMenuInflater().inflate(R.menu.menu_teacher_publish_new_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            goBack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
        }
        return true;
    }

    private void goBack() {
//        if (mPublishNewArticleWebView.canGoBack()) {
//            mPublishNewArticleWebView.goBack();//返回上一页面
//        } else {
//            TeacherPublishNewArticleActivity.this.finish();
//            //注意，切换方法overridePendingTransition只能在startActivity和finish方法之后调用。
//            overridePendingTransition(0, R.anim.slidedown);
//        }

        TeacherPublishNewArticleActivity.this.finish();
        //注意，切换方法overridePendingTransition只能在startActivity和finish方法之后调用。
        overridePendingTransition(0, R.anim.slidedown);
    }
}
