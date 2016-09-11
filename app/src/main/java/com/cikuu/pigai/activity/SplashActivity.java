package com.cikuu.pigai.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.cikuu.pigai.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by xuhai on 15/1/4.
 */
public class SplashActivity extends Activity {

    //delay 3 seconds
    private static final long SPLASH_DELAY_MILLIS = 3000;
    private static final String SHAREDPREFERENCES_NAME = "first_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
        //    AnalyticsConfig.enableEncrypt(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        //check OS version
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            // API 11
            init();
        } else {
            finishActivity();
        }
    }

    private void init() {
        Handler mHandler = new Handler();
        // use sharedpreference to store the first time launch data
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        // get the value, if null then first time launch
        boolean isFirstIn = preferences.getBoolean("isFirstIn", true);
        // check if the app is first time launch
        if (!isFirstIn) {
            // delay 3 seconds, then goto the corresponding activity
            mHandler.postDelayed(new goHomeRunnable(), SPLASH_DELAY_MILLIS);
        } else {
            mHandler.postDelayed(new goGuideRunnable(), SPLASH_DELAY_MILLIS);
        }

        setGuided();

    }

    private void finishActivity() {
        new AlertDialog.Builder(this)
                .setTitle("系统版本太低")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }).show();
    }

    private void setGuided() {
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        //store the date
        editor.putBoolean("isFirstIn", false);
        //commit the data
        editor.apply();
    }

    public class goHomeRunnable implements Runnable {
        @Override
        public void run() {
            goHome();
        }
    }

    public class goGuideRunnable implements Runnable {

        @Override
        public void run() {
            goGuide();
        }
    }

    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void goGuide() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    //友盟统计
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SplashActivity");
        MobclickAgent.onResume(this);
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashActivity");
        MobclickAgent.onPause(this);
    }

}
