package com.cikuu.pigai.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.MobileTools;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.activity.utils.StringOP;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

public class FindPasswordActivity extends AppCompatActivity implements VolleyRequest.GetVerifyCodeCallback, VolleyRequest.ResetPasswordByVerifyCodeCallback {

    private Button btnGetVerifyCode;
    private Button btnSubmit;
    private Button btnResetPasswordByEmail;

    private EditText countryCodeEditText;
    private EditText phoneEditText;
    private EditText verifyCodeEditText;
    private EditText newPasswordEditText;

    private VolleyRequest mHttpRequest;

    private Timer countDownTimer;
    private int remainingTicks;


    void initUI() {
        btnGetVerifyCode = (Button) findViewById(R.id.btnGetVerifyCode);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnResetPasswordByEmail = (Button) findViewById(R.id.btnFindPasswordByEmail);

        countryCodeEditText = (EditText) findViewById(R.id.countryCodeEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        verifyCodeEditText = (EditText) findViewById(R.id.verifyCodeEditText);
        newPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
    }

    void setupEventListener() {
        btnGetVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //type 3 find password, 1 user register
                String phone = phoneEditText.getText().toString().trim();
                String countryCode = countryCodeEditText.getText().toString().trim();

                if (phone.equals("") || countryCode.equals("") || !MobileTools.isAllDigits(phone))
                    return;

                btnGetVerifyCode.setEnabled(false);
                mHttpRequest.GetVerifyCode(phone, countryCode, 3);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmit.setEnabled(false);
                String phone = phoneEditText.getText().toString().trim();
                String countryCode = countryCodeEditText.getText().toString().trim();
                String verifyCode = verifyCodeEditText.getText().toString().trim();
                String password = newPasswordEditText.getText().toString().trim();

                if (verifyCode.equals("") || password.equals("")) {
                    btnSubmit.setEnabled(true);
                    return;
                }

                if (password.length() < 6) {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(FindPasswordActivity.this, "密码至少6位", Toast.LENGTH_SHORT).show();
                    return;
                }

                String password_half = StringOP.FulltoHalfString(password);

                mHttpRequest.ResetPasswordByVerifyCode(phone, countryCode, verifyCode, password_half);
            }
        });

        btnResetPasswordByEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mailIntent = new Intent(FindPasswordActivity.this, FindPasswordByEmailActivity.class);
                startActivity(mailIntent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mGetVerifyCodeCallback = this;
        mHttpRequest.mResetPasswordByVerifyCodeCallback = this;

        initUI();
        setupEventListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void VerifyCode(String phone, int errorCode, String errorMsg) {
        if (errorCode == 1) {
            Toast.makeText(FindPasswordActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
            verifyCodeEditText.requestFocus();
            btnGetVerifyCode.setEnabled(false);
            remainingTicks = 60;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            countDownTimer = new Timer();
            TimerHit updateTimer = new TimerHit();
            countDownTimer.scheduleAtFixedRate(updateTimer, 0, 1000);

        } else {
            Toast.makeText(FindPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            btnGetVerifyCode.setEnabled(true);
        }
    }

    public void ResetPasswordByVerifyCode(int success, String phone, String errorMsg) {
        btnSubmit.setEnabled(true);
        if (success == 1) {
            Toast.makeText(FindPasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();

            String phoneNumber = phoneEditText.getText().toString().trim();
            String password = newPasswordEditText.getText().toString().trim();

            SharedPreferenceUtil.setUserNamePasswordInSP(phoneNumber, password, this);

            finish();
        } else {
            Toast.makeText(FindPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    }


    class TimerHit extends TimerTask {
        public void run() {
            FindPasswordActivity.this.runOnUiThread(Timer_Tick);
        }
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            remainingTicks--;
            btnGetVerifyCode.setText(String.valueOf(remainingTicks));

            if (remainingTicks <= 0) {
                cancelTimer();
            }
        }
    };

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        btnGetVerifyCode.setEnabled(true);
        btnGetVerifyCode.setText("请求验证码");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_find_password, menu);
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    //友盟统计
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FindPasswordActivity");
        MobclickAgent.onResume(this);
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FindPasswordActivity");
        MobclickAgent.onPause(this);
    }

}
