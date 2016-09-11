package com.cikuu.pigai.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.student.StudentHomeActivity;
import com.cikuu.pigai.activity.teacher.TeacherHomeActivity;
import com.cikuu.pigai.activity.utils.MobileTools;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.activity.utils.StringOP;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.businesslogic.UserInformation;
import com.cikuu.pigai.dbmodel.StudentArticleBDHelper;
import com.cikuu.pigai.dbmodel.ormlite.StudentArticleOrmLiteSqliteOpenHelper;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

public class UserRegisterActivity extends ActionBarActivity implements VolleyRequest.GetVerifyCodeCallback, VolleyRequest.RegisterUserByVerifyCodeCallback, VolleyRequest.LogInCallback {

    public static String userType;

    private Button btnGetVerifyCode;
    private Button btnSubmit;

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
                mHttpRequest.GetVerifyCode(phone, countryCode, 1);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                logIn();

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
                    Toast.makeText(UserRegisterActivity.this, "密码至少6位", Toast.LENGTH_SHORT).show();
                    return;
                }

                String password_half = StringOP.FulltoHalfString(password);

                mHttpRequest.RegisterUserByVerifyCode(phone, countryCode, verifyCode, password_half, userType);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        //actionBar.hide();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mGetVerifyCodeCallback = this;
        mHttpRequest.mRegisterUserByVerifyCodeCallback = this;
        mHttpRequest.mLogInCallback = this;

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
            Toast.makeText(UserRegisterActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(UserRegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            btnGetVerifyCode.setEnabled(true);
        }
    }

    public void RegisterUserByVerifyCode(int success, String phone, String errorMsg) {
        btnSubmit.setEnabled(true);
        if (success == 1) {
            Toast.makeText(UserRegisterActivity.this, "用户注册成功，正在登录...", Toast.LENGTH_SHORT).show();
            logIn();
        } else {
            resetButton();
            Toast.makeText(UserRegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    void logIn() {
        String userName = phoneEditText.getText().toString().trim();
        String password = newPasswordEditText.getText().toString().trim();

        mHttpRequest.LogIn(userName, password);
        SharedPreferenceUtil.setUserNamePasswordInSP(userName, password, this);
    }

    public void LoggedIn(UserInformation description, int type) {
        Teacher teacher;
        Student student;
        VolleyRequest.User_Token = description.pigai_token;
        SharedPreferenceUtil.setUserAutoLoginInSP(true, this);
        SharedPreferenceUtil.setPigaiTokenInSP(description.pigai_token, this);
        if (type == VolleyRequest.User_Type_Teacher) {
            teacher = Teacher.GetInstance();
            teacher.SetDescriptionAndWriteToDatabase(description);
            teacher.SetDescriptionAndWriteToSP(this, description);
            teacher.mDescription.type = type;

            Intent intent = new Intent(this, TeacherHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        if (type == VolleyRequest.User_Type_Student) {
            student = Student.GetInstance();
            student.SetDescriptionAndWriteToDatabase(description);
            student.SetDescriptionAndWriteToSP(this, description);
            student.mStudentDescription.type = type;

            StudentArticleBDHelper.DATABASE_NAME = "cikuu_db_" + String.valueOf(description.mUid);
//            StudentArticleOrmLiteSqliteOpenHelper.DATABASE_NAME = "cikuu_student_db_" + String.valueOf(description.mUid);
            Intent intent = new Intent(this, StudentHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    //only used for login error
    public void ErrorPassWord() {
        resetButton();
        Toast.makeText(UserRegisterActivity.this, "注册已成功，网络错误，请重新登录！", Toast.LENGTH_SHORT).show();
        SharedPreferenceUtil.setUserAutoLoginInSP(false, this);
    }

    void resetButton() {
        btnGetVerifyCode.setEnabled(true);
        btnSubmit.setEnabled(true);
    }

    class TimerHit extends TimerTask {
        public void run() {
            UserRegisterActivity.this.runOnUiThread(Timer_Tick);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //友盟统计
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("UserRegisterActivity");
        MobclickAgent.onResume(this);
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("UserRegisterActivity");
        MobclickAgent.onPause(this);
    }
}
