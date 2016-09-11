package com.cikuu.pigai.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.toolbox.NetworkImageView;
import com.cikuu.pigai.R;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.Teacher;
import com.umeng.analytics.MobclickAgent;

public class BigUserPhotoActivity extends AppCompatActivity {

    private NetworkImageView bigUserPhoto;
    private Student mStudent;
    private Teacher mTeacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_student_photo);
        Intent intent = getIntent();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStudent = Student.GetInstance();
        mTeacher = Teacher.GetInstance();
        bigUserPhoto = (NetworkImageView) findViewById(R.id.bigImage);
        AppController.CacheImageLoader imageLoader = AppController.getInstance().getCacheImageLoader();
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all

        if (intent.getIntExtra("flag", 0) == 101)
            bigUserPhoto.setImageUrl(mStudent.mStudentDescription.bHead, imageLoader);
        else if (intent.getIntExtra("flag", 0) == 102)
            bigUserPhoto.setImageUrl(mTeacher.mDescription.bHead, imageLoader);
    }

    //友盟统计
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BigUserPhotoActivity");
        MobclickAgent.onResume(this);
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("BigUserPhotoActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_big_student_photo, menu);
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
