package com.cikuu.pigai.activity.teacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.Teacher;

import java.io.File;

public class TeacherInformationHtml5Activity extends ActionBarActivity {
    private ActionBar mActionBar;
    private ProgressDialog mProgressDialog;
    WebView teacherInfoWebView;
    Teacher  mTeacher;
    final Handler myHandler = new Handler();

    private static final String APP_CACAHE_DIRNAME = "/webcache";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_information_html5);

        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在加载...");
        mProgressDialog.show();

        mTeacher = Teacher.GetInstance();

        //计算 sid
        String name = mTeacher.GetDescription().mUser_Name;
        String uid = String.valueOf(mTeacher.GetDescription().mUid);
        String token = name + "__pigai" + uid;
        String oldKey = md5(token);
        String key = oldKey.substring(2, 10);

        teacherInfoWebView =(WebView) findViewById(R.id.teacherInfoWebView);
        teacherInfoWebView.clearHistory();
        teacherInfoWebView.clearFormData();
        teacherInfoWebView.clearCache(true);
        teacherInfoWebView.setWebViewClient(new WebViewClient(){


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        });

        teacherInfoWebView.getSettings().setJavaScriptEnabled(true);
        teacherInfoWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        final JavaScriptInterface myJavaScriptInterface
                = new JavaScriptInterface(this);
        teacherInfoWebView.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");
        teacherInfoWebView.getSettings().setAppCacheEnabled(false);
        teacherInfoWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //  http://www.pigai.org/?c=m&a=my&sid=3e6ad169&uid=83776&phone=phone    ;     tel字段用来是App
        String url = "http://www.pigai.org/?c=m&a=my&sid="+ key + "&uid=" + mTeacher.GetDescription().mUid +"&phone=phone";
        teacherInfoWebView.loadUrl(url);
    }

    //用于  Android 调用 JS 数据的 接口
    public class JavaScriptInterface {
        Context mContext;
        JavaScriptInterface(Context c) {
            mContext = c;
        }
        public void showToast(String webMessage){
            final String msgeToast = webMessage;
        final   String[] teacherData =  msgeToast.split("#");

            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    // This gets executed on the UI thread so it can safely modify Views
                    mTeacher.mDescription.mName = teacherData[0];
                    mTeacher.mDescription.mSchool = teacherData[1];

                }
            });

        //    Toast.makeText(mContext, webMessage, Toast.LENGTH_SHORT).show();
        }
    }


    public final String md5(final String s) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        CookieManager cookieManager =CookieManager. getInstance();
        cookieManager. removeAllCookie();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher_information_html5, menu);
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
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
