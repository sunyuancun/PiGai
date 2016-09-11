package com.cikuu.pigai.activity.student;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cikuu.pigai.R;

public class HuoDongActivity extends AppCompatActivity {

    WebView mwebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huo_dong);
        initActionBar();
        mwebview = (WebView) findViewById(R.id.webview);

        mwebview = (WebView) findViewById(R.id.webview);
        mwebview.getSettings().setJavaScriptEnabled(true);
        mwebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mwebview.setWebViewClient(new WebViewClient() {
            //shouldOverrideUrlLoading：对网页中超链接按钮的响应。当按下某个连接时WebViewClient会调用这个方法
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {

            }

        });

        String url = getIntent().getStringExtra("URL");

        if (null != url) {
            mwebview.loadUrl(url);
        }

    }

    private void initActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setTitle("活动页面");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_comments_by_sentence, menu);
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

        if (mwebview.canGoBack()) {
            mwebview.goBack();
        } else {
            HuoDongActivity.this.finish();
            //注意，切换方法overridePendingTransition只能在startActivity和finish方法之后调用。
            overridePendingTransition(0, R.anim.slidedown);
        }
    }
}
