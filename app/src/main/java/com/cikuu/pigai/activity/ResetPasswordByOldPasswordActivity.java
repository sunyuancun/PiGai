package com.cikuu.pigai.activity;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.activity.utils.StringOP;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class ResetPasswordByOldPasswordActivity extends AppCompatActivity implements VolleyRequest.ResetPasswordByOldPasswordCallback {

    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private Button btnSubmit;

    private VolleyRequest mHttpRequest;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_by_old_password);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("修改中...");

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mResetPasswordByOldPasswordCallback = this;

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        oldPasswordEditText = (EditText) findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = (EditText) findViewById(R.id.newPasswordEditText);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();

                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();

                if (oldPassword.equals("") || newPassword.equals(""))
                    return;

                if (newPassword.length() < 6) {
                    Toast.makeText(ResetPasswordByOldPasswordActivity.this, "密码不能少于6位", Toast.LENGTH_SHORT).show();
                    return;
                }

                long student_uid = Student.GetInstance().mStudentDescription.mUid;
                long teacher_uid = Teacher.GetInstance().mDescription.mUid;
                long uid = 0;
                String savedUserType = SharedPreferenceUtil.getUserTypeInSP(ResetPasswordByOldPasswordActivity.this);
                if (savedUserType != null && savedUserType.equals(String.valueOf(VolleyRequest.User_Type_Student))) {
                    uid = student_uid;
                }

                if (savedUserType != null && savedUserType.equals(String.valueOf(VolleyRequest.User_Type_Teacher))) {
                    uid = teacher_uid;
                }
//                long uid = student_uid > 0 ? student_uid : teacher_uid;
                String oldPassword_half = StringOP.FulltoHalfString(oldPassword);
                String newPassword_half = StringOP.FulltoHalfString(newPassword);

                mHttpRequest.ResetPasswordByOldPassword(uid, newPassword_half, oldPassword_half);
            }
        });
    }

    public void ResetPasswordByOldPassword(int success, String errorMsg) {
        hidePDialog();
        if (success == 1) {
            Toast.makeText(ResetPasswordByOldPasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();

            String newPassword = newPasswordEditText.getText().toString().trim();
            ArrayList<String> userNamePasswordList = SharedPreferenceUtil.getUserNamePasswordInSP(this);

            if (userNamePasswordList.size() > 0) {
                SharedPreferenceUtil.setUserNamePasswordInSP(userNamePasswordList.get(0), newPassword, this);
            }
            finish();
        } else {
            Toast.makeText(ResetPasswordByOldPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
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
        MobclickAgent.onPageStart("ResetPasswordByOldPasswordActivity");
        MobclickAgent.onResume(this);
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ResetPasswordByOldPasswordActivity");
        MobclickAgent.onPause(this);
    }

}
