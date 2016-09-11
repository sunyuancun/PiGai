package com.cikuu.pigai.activity.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.LoginActivity;
import com.umeng.analytics.MobclickAgent;

public class StudentRegisterActivity extends ActionBarActivity {

    private ActionBar mActionBar;
    private ProgressDialog mProgressDialog;
    WebView studentregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在加载...");
        mProgressDialog.show();

        studentregister = (WebView) findViewById(R.id.studentregister);
        studentregister.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("http://www.pigai.org/?c=m")) {
                    Intent intent = new Intent(StudentRegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    StudentRegisterActivity.this.finish();
                    Toast.makeText(StudentRegisterActivity.this, "注册成功，跳转至登陆页面", Toast.LENGTH_SHORT).show();
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mProgressDialog != null)
                    mProgressDialog.dismiss();
            }

        });

        studentregister.getSettings().setJavaScriptEnabled(true);
        studentregister.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        String url = "http://www.pigai.org/?c=m&a=reg&st=s&sid=1";
        studentregister.loadUrl(url);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (studentregister.canGoBack()) {
                studentregister.goBack();//返回上一页面
            } else {
                finish();//退出程序
            }
        }
        return true;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if (studentregister.canGoBack()) {
                studentregister.goBack();//返回上一页面
            } else {
                finish();//退出程序
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
