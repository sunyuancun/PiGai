package com.cikuu.pigai.activity.teacher;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.businesslogic.Teacher;

public class StudentArticleSummaryInTeacherActivity extends AppCompatActivity {

    private WebView mWebView;
    private ActionBar mActionBar;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_summary_in_teacher);

        initActionBar();
        mProgressDialog = new ProgressDialog(StudentArticleSummaryInTeacherActivity.this);
        mProgressDialog.setMessage("正在加载...");
        mProgressDialog.show();

        String rid = getIntent().getStringExtra("rid");
        String pigai_token = SharedPreferenceUtil.getPigaiTokenInSP(this);
        int teacher_uid = Teacher.GetInstance().mDescription.mUid;

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
            }
        });

        String url = "http://open.pigai.org/pgmobile-analysisScore?rid=" + rid + "&pigai_token=" + pigai_token +
                "&pigai_uid=" + teacher_uid;

        mWebView.loadUrl(url);
    }

    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();//返回上一页面
            } else {
                finish();//退出webview
                overridePendingTransition(0, R.anim.slidedown);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //todo  Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();//返回上一页面
            } else {
                finish();//退出webview
                overridePendingTransition(0, R.anim.slidedown);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
