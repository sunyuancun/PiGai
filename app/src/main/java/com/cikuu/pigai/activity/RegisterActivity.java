package com.cikuu.pigai.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.student.StudentRegisterActivity;
import com.cikuu.pigai.activity.teacher.TeacherRegisterActivity;
import com.umeng.analytics.MobclickAgent;

public class RegisterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        bar.setDisplayShowTitleEnabled(false);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
    }

    //teacher 注册
    public void teacherRegister(View view) {
//        Intent teacherIntent = new Intent(RegisterActivity.this, TeacherRegisterActivity.class);
//        startActivity(teacherIntent);

        Intent teacherIntent = new Intent(RegisterActivity.this, UserRegisterActivity.class);
        UserRegisterActivity.userType = "t";
        startActivity(teacherIntent);
//        RegisterActivity.this.finish();
    }

    //teacher 注册
    public void studentRegister(View view) {
//        Intent studentIntent = new Intent(RegisterActivity.this, StudentRegisterActivity.class);
//        startActivity(studentIntent);
        Intent studentIntent = new Intent(RegisterActivity.this, UserRegisterActivity.class);
        UserRegisterActivity.userType = "s";
        startActivity(studentIntent);

//        RegisterActivity.this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    //友盟统计
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("RegisterActivity");
        MobclickAgent.onResume(this);
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RegisterActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            RegisterActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
