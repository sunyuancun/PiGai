package com.cikuu.pigai.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.AppVisionTool;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

public class SendFeedbackActivity extends AppCompatActivity implements VolleyRequest.SubmittedBackInfoCallback {

    private VolleyRequest mHttpRequest;

    private EditText content;
    private EditText phone;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("提交中...");


        mHttpRequest = new VolleyRequest();
        mHttpRequest.mSubmittedBackInfoCallback = this;

        content = (EditText) findViewById(R.id.content);
        phone = (EditText) findViewById(R.id.phone);
        Button submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentInfo = content.getText().toString().trim();
                String contactInfo = phone.getText().toString().trim();
                String mobileInfo = PhoneStatus();
                String appVersionInfo = AppVisionTool.getVersion(SendFeedbackActivity.this);
                if ((!TextUtils.isEmpty(contentInfo)) & (!TextUtils.isEmpty(contactInfo))) {
                    if (contentInfo.length() > 10) {
                        mProgressDialog.show();

                        long student_uid = Student.GetInstance().mStudentDescription.mUid;
                        long teacher_uid = Teacher.GetInstance().mDescription.mUid;
                        long uid = 0;
                        String savedUserType = SharedPreferenceUtil.getUserTypeInSP(SendFeedbackActivity.this);
                        if (savedUserType != null && savedUserType.equals(String.valueOf(VolleyRequest.User_Type_Student))) {
                            uid = student_uid;
                        }

                        if (savedUserType != null && savedUserType.equals(String.valueOf(VolleyRequest.User_Type_Teacher))) {
                            uid = teacher_uid;
                        }

                        Log.e("-----uid---", "" + uid);

                        mHttpRequest.SubmitBackInfo(uid, contentInfo, contactInfo, appVersionInfo + " || " + mobileInfo);
                    } else {
                        Toast.makeText(SendFeedbackActivity.this, "您好，请至少填写10个字", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SendFeedbackActivity.this, "您好，请填写完整信息再提交", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //mobile  info of version and type
    private String PhoneStatus() {
        String info = Build.BRAND + " " + Build.MODEL + "  " + Build.VERSION.RELEASE + "  " + Build.VERSION.SDK;
        return info;
    }

    @Override
    public void BackInfoSubmitted(String message) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void ErrorNetwork() {
        Toast.makeText(this, "提交失败", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    //友盟统计
    public void onResume() {
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
        getMenuInflater().inflate(R.menu.menu_student_back_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
