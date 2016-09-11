package com.cikuu.pigai.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

public class FindPasswordByEmailActivity extends AppCompatActivity implements VolleyRequest.ResetPasswordByEmailCallback {

    private Button btnSubmit;
    private EditText emailEditText;

    private VolleyRequest mHttpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password_by_email);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mResetPasswordByEmailCallback = this;

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        emailEditText = (EditText) findViewById(R.id.emailEditText);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if(email.equals(""))
                    return;

                mHttpRequest.ResetPasswordByEmail(email);
            }
        });

    }

    public void ResetPasswordByEmail(int success, String email, String errorMsg) {
        if(success == 1) {
            Toast.makeText(FindPasswordByEmailActivity.this, "邮件发送成功，请登录邮箱修改密码", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(FindPasswordByEmailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

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
        MobclickAgent.onPageStart("FindPasswordByEmailActivity");
        MobclickAgent.onResume(this);
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FindPasswordByEmailActivity");
        MobclickAgent.onPause(this);
    }
}
