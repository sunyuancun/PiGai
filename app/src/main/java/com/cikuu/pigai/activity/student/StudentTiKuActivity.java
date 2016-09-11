package com.cikuu.pigai.activity.student;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.adapter.FragmentAdapter;
import com.cikuu.pigai.activity.utils.SidTokenTool;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.TiKuArticleCategory;
import com.cikuu.pigai.httprequest.VolleyRequest;

import java.util.ArrayList;

public class StudentTiKuActivity extends AppCompatActivity implements VolleyRequest.GetTiKuArticleCateCallback {

    private ArrayList<TiKuArticleCategory> mTiKuArticleCategoryList = new ArrayList<>();
    FragmentAdapter mTitleFragmentAdapter;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_ti_ku);
        setupActionBar();
        initView();
        getArticleCategories();
    }

    private void getArticleCategories() {
        VolleyRequest volleyRequest = new VolleyRequest();
        volleyRequest.mGetTiKuArticleCateCallback = this;
        int uid = Student.GetInstance().mStudentDescription.mUid;
        //uid与 _tiku#8231 进行md5加密，截取的2-11共10位字母
        String sid = SidTokenTool.getSidForGetTikuArticleCate(uid);
        volleyRequest.getTiKuArticleCategories(uid, sid);
    }

    ViewPager pager;

    private void initView() {
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(15);
        mTitleFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), mTiKuArticleCategoryList);
        pager.setAdapter(mTitleFragmentAdapter);
        tabs = (TabLayout) findViewById(R.id.tabLayout);
    }

    private void setupActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setTitle(getResources().getString(R.string.tiku_article));
    }

    @Override
    public void getTiKuArticleCategories(ArrayList<TiKuArticleCategory> tiKuArticleCategoryList) {
        try {
            mTiKuArticleCategoryList.clear();
            mTiKuArticleCategoryList.addAll(tiKuArticleCategoryList);
            mTitleFragmentAdapter.notifyDataSetChanged();
            tabs.setupWithViewPager(pager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ErrorNetwork() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_student_tiku_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
