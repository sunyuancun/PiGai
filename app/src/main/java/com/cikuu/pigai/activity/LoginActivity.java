package com.cikuu.pigai.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.dialog.UpdateApplication;
import com.cikuu.pigai.activity.student.StudentHomeActivity;
import com.cikuu.pigai.activity.teacher.TeacherHomeActivity;
import com.cikuu.pigai.activity.utils.KeyBoardUtil;
import com.cikuu.pigai.activity.utils.ServerTime;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.activity.utils.StringOP;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.businesslogic.UserInformation;
import com.cikuu.pigai.dbmodel.StudentArticleBDHelper;
import com.cikuu.pigai.dbmodel.ormlite.StudentArticleOrmLiteSqliteOpenHelper;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class LoginActivity extends ActionBarActivity implements VolleyRequest.LogInCallback {

    private Button btnLogin;
    private TextView btRegister;
    private TextView btnFindPassword;
    private EditText userNameEditText;
    private EditText passWordEditText;
    private TextView mErrorPassWordTextView;
    private ProgressDialog mProgressDialog;

    ArrayList<String> userNamePasswordList;
    private VolleyRequest mHttpRequest;

    private Teacher mTeacher;
    private Student mStudent;

    private UpdateApplication updateApp;

    ServerTime mServerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The Action Bar is a window feature. The feature must be requested
        // before setting a content view. Normally this is set automatically
        // by your Activity's theme in your manifest. The provided system
        // theme Theme.WithActionBar enables this for you. Use it as you would
        // use Theme.NoTitleBar. You can add an Action Bar to your own themes
        // by adding the element <item name="android:windowActionBar">true</item>
        // to your style definition.
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        bar.setDisplayShowTitleEnabled(false);
        bar.setDisplayShowTitleEnabled(true);

        updateApp = new UpdateApplication(this);
        updateApp.Update();

        //get server time
        mServerTime = ServerTime.GetInstance();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("登录中...");

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mLogInCallback = this;

        userNamePasswordList = new ArrayList<>();

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btRegister = (TextView) findViewById(R.id.btRegister);
        btnFindPassword = (TextView) findViewById(R.id.btnFindPassword);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        passWordEditText = (EditText) findViewById(R.id.passWordEditText);

        mErrorPassWordTextView = (TextView) findViewById(R.id.errorPassWord);
        mErrorPassWordTextView.setVisibility(View.INVISIBLE);

        //store the remember password in SP
        SharedPreferenceUtil.setRemeberpasswordInSP(true, LoginActivity.this);

        //get user name and password from SP
        userNamePasswordList = SharedPreferenceUtil.getUserNamePasswordInSP(LoginActivity.this);
        if (userNamePasswordList.size() > 0) {
            userNameEditText.setText(userNamePasswordList.get(0));
            //get remember password in SP
            if (SharedPreferenceUtil.getRememberpasswordInSP(LoginActivity.this)) {
                passWordEditText.setText(userNamePasswordList.get(1));
            }
        }

        //teacher
//        userNameEditText.setText("1066361531@qq.com");
//        passWordEditText.setText("821627");
//        userNameEditText.setText("990385465@qq.com");
//        passWordEditText.setText("15236270233");
////        //student
//        userNameEditText.setText("1101228253@qq.com");
//        passWordEditText.setText("821627");
//        userNameEditText.setText("student@jukuu.com");
//        passWordEditText.setText("258369");
//        userNameEditText.setText("15236271029");
//        passWordEditText.setText("877035");
        // button click listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showProgressDialog();
                userLogin();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
//                LoginActivity.this.finish();
            }
        });

        btnFindPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(registerIntent);
//                LoginActivity.this.finish();
            }
        });

    }

    private void userLogin() {
        String mUserName = userNameEditText.getText().toString();
        String mPassWord = passWordEditText.getText().toString();
        if ((!TextUtils.isEmpty(mUserName)) && (!TextUtils.isEmpty(mPassWord))) {

            String halfUsername = StringOP.FulltoHalfString(mUserName);
            String halfPassword = StringOP.FulltoHalfString(mPassWord);

            SharedPreferenceUtil.setUserNamePasswordInSP(halfUsername, halfPassword, LoginActivity.this);
            try {
                halfUsername = URLEncoder.encode(halfUsername, "UTF-8");
                halfPassword = URLEncoder.encode(halfPassword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mHttpRequest.LogIn(halfUsername, halfPassword);
            KeyBoardUtil.hideSoftInputFromWindow(LoginActivity.this);
        } else {
            Toast.makeText(LoginActivity.this, "账号密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    public void LoggedIn(UserInformation description, int type) {
        VolleyRequest.User_Token = description.pigai_token;
        SharedPreferenceUtil.setUserAutoLoginInSP(true, this);
        SharedPreferenceUtil.setPigaiTokenInSP(description.pigai_token, this);
        if (type == VolleyRequest.User_Type_Teacher) {
            mTeacher = Teacher.GetInstance();
            mTeacher.SetDescriptionAndWriteToDatabase(description);
            mTeacher.SetDescriptionAndWriteToSP(this, description);
            mTeacher.mDescription.type = type;
            SharedPreferenceUtil.setUserTypeInSP(String.valueOf(VolleyRequest.User_Type_Teacher), this);
            hidePDialog();

            Intent intent = new Intent(LoginActivity.this, TeacherHomeActivity.class);
            LoginActivity.this.startActivity(intent);
            LoginActivity.this.finish();
        }

        if (type == VolleyRequest.User_Type_Student) {
            mStudent = Student.GetInstance();
            mStudent.SetDescriptionAndWriteToDatabase(description);
            mStudent.SetDescriptionAndWriteToSP(this, description);
            mStudent.mStudentDescription.type = type;
            SharedPreferenceUtil.setUserTypeInSP(String.valueOf(VolleyRequest.User_Type_Student), this);

            //TODO we need to set a unique db for one user
            StudentArticleBDHelper.DATABASE_NAME = "cikuu_db_" + String.valueOf(description.mUid);
//            StudentArticleOrmLiteSqliteOpenHelper.DATABASE_NAME = "cikuu_student_db_" + String.valueOf(description.mUid);
            hidePDialog();
            Intent intent = new Intent(LoginActivity.this, StudentHomeActivity.class);
            LoginActivity.this.startActivity(intent);
            LoginActivity.this.finish();
        }
    }

    public void ErrorPassWord() {
        hidePDialog();
        mErrorPassWordTextView.setVisibility(View.VISIBLE);
        SharedPreferenceUtil.setUserAutoLoginInSP(false, this);
    }

    private void showProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onDestroy() {
        super.onDestroy();
//        try {
//            if (mProgressDialog != null) {
//                mProgressDialog.dismiss();
//                mProgressDialog = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    //友盟统计
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LoginActivity");
        MobclickAgent.onResume(this);

        userNamePasswordList = SharedPreferenceUtil.getUserNamePasswordInSP(LoginActivity.this);
        if (userNamePasswordList.size() > 0) {
            userNameEditText.setText(userNamePasswordList.get(0));
            //get remember password in SP
            if (SharedPreferenceUtil.getRememberpasswordInSP(LoginActivity.this)) {
                passWordEditText.setText(userNamePasswordList.get(1));
            }
        }

        Boolean isAutoLogin = SharedPreferenceUtil.getUserAutoLoginInSP(this);

        if (isAutoLogin) {
            showProgressDialog();
            userLogin();
        }


    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LoginActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
