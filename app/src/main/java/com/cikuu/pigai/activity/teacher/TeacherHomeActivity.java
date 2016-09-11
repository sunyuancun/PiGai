package com.cikuu.pigai.activity.teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.dialog.ActionSheet;
import com.cikuu.pigai.activity.student.StudentHomeActivity;
import com.cikuu.pigai.activity.student.StudentInformationHtml5Activity;
import com.cikuu.pigai.businesslogic.Teacher;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class TeacherHomeActivity extends ActionBarActivity implements ActionSheet.MenuItemClickListener, View.OnClickListener {
    public static final String TAG = TeacherHomeActivity.class
            .getSimpleName();
    FragmentManager fMgr;
    LinearLayout teacherarticleline;
    LinearLayout teachermeline;
    private ImageView teacherarticle, textView2, textView3, teacher_me;
    private TextView center_button, articleText, meText;

    private Teacher mTeacher;

    private ActionBar mActionBar;
    SharedPreferences preferences;

    int id1 = R.drawable.home_article_press;
    int id11 = R.drawable.home_article_default;
    int id4 = R.drawable.home_me_press;
    int id41 = R.drawable.home_me_default;
    String colorBlue = "#3ab3ff";
    String colordefault = "#66000000";

    /**
     * 解决  ：java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState（）
     * 使用commit()方法提交Fragment事务时的问题
     */
    boolean mCheckStateSaved = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        mTeacher = Teacher.GetInstance();
        fMgr = getSupportFragmentManager();
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mTeacher = Teacher.GetInstance();
            preferences = getSharedPreferences(Teacher.SHAREDPREFERENCES_TEACHER, Context.MODE_PRIVATE);
            mTeacher.mDescription.mUid = preferences.getInt("UID", 0);
            mTeacher.mDescription.mName = preferences.getString("NAME", "");
            mTeacher.mDescription.mSex = preferences.getString("SEX", "");
            mTeacher.mDescription.mSchool = preferences.getString("SCHOOL", "");
            mTeacher.mDescription.mStudent_number = preferences.getString("STUDENT_NUMBER", "");
            mTeacher.mDescription.mClass = preferences.getString("CLASS", "");
            mTeacher.mDescription.mTel = preferences.getString("TEL", "");
            mTeacher.mDescription.bHead = preferences.getString("SHEAD", "");
            mTeacher.mDescription.bHead = preferences.getString("BHEAD", "");
        }

        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(true);

        InitFragment();
        BottomButtonsClickEvent();

    }

    TeacherHomeArticleFragment mTeacherHomeArticleFragment;
    TeacherHomeMeFragment mTeacherHomeMeFragment;

    private void InitFragment() {
        FragmentTransaction fragmentTransaction = fMgr.beginTransaction();
        mTeacherHomeArticleFragment = new TeacherHomeArticleFragment();
        fragmentTransaction.add(R.id.vPager, mTeacherHomeArticleFragment, "TeacherHomeArticleFragment");

        mTeacherHomeMeFragment = new TeacherHomeMeFragment();
        fragmentTransaction.add(R.id.vPager, mTeacherHomeMeFragment, "TeacherHomeMeFragment");
        fragmentTransaction.hide(mTeacherHomeMeFragment);

        fragmentTransaction.commit();

        //    进行提示 完善个人资料
        if (TextUtils.isEmpty(mTeacher.mDescription.mName) || TextUtils.isEmpty(mTeacher.mDescription.mSchool) ||
                mTeacher.mDescription.mName.equals("请填写名字") || mTeacher.mDescription.mSchool.equals("请填写学校")
                || mTeacher.mDescription.mSchool.equals("pigai")) {
            setTheme(R.style.ActionSheetStyleIOS7);
            showActionSheet();
        }
    }

    private void BottomButtonsClickEvent() {
        teacherarticleline = (LinearLayout) findViewById(R.id.teacherarticleline);
        teachermeline = (LinearLayout) findViewById(R.id.teachermeline);
        teacherarticle = (ImageView) findViewById(R.id.teacherarticle);
        teacher_me = (ImageView) findViewById(R.id.teacher_me);
        articleText = (TextView) findViewById(R.id.articleText);
        meText = (TextView) findViewById(R.id.meText);
        center_button = (TextView) findViewById(R.id.center_button);

        teacherarticleline.setOnClickListener(this);
        teachermeline.setOnClickListener(this);
        center_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.teacherarticleline:

                if (mTeacherHomeArticleFragment.isVisible())
                    return;

                if (!mCheckStateSaved) {
                    mActionBar.setTitle("浏览作文");
                    setTextViewBackground(id1, colorBlue, id41, colordefault);
                    showFragment(mTeacherHomeArticleFragment);
                    hideFragment(mTeacherHomeMeFragment);
                }

                break;
            case R.id.teachermeline:

                if (mTeacherHomeMeFragment.isVisible())
                    return;

                if (!mCheckStateSaved) {
                    mActionBar.setTitle("关于我");
                    setTextViewBackground(id11, colordefault, id4, colorBlue);
                    showFragment(mTeacherHomeMeFragment);
                    hideFragment(mTeacherHomeArticleFragment);
                }

                break;
            case R.id.center_button:
                showPopupWindow();
                break;
            default:
                break;
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fMgr.beginTransaction();
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    private void hideFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fMgr.beginTransaction();
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    public void showActionSheet() {
        ActionSheet menuView = new ActionSheet(this);
        menuView.setCancelButtonTitle("确定");// before add items
        menuView.addItems("老师，请完善个人稀料", "温馨提示：必须填写“名字”和“学校”");
        menuView.setItemClickListener(TeacherHomeActivity.this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    @Override
    public void onCancelClick() {
        //设置你的操作事项
        Intent intent = new Intent(TeacherHomeActivity.this, TeacherInformationActivity.class);
        startActivity(intent);
    }

    private void showPopupWindow() {
        boolean offline = isNetworkOnline();
        if (!offline) {
            Toast store = Toast.makeText(getApplicationContext(), "没有网络", Toast.LENGTH_LONG);
            store.show();
        }
        Intent intent = new Intent(TeacherHomeActivity.this, TeacherPublishNewArticleActivity.class);
        startActivity(intent);
        //注意，切换方法overridePendingTransition只能在startActivity和finish方法之后调用。
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }


    private void setTextViewBackground(int id1, String color1, int id4, String color4) {
        teacherarticle.setBackgroundResource(id1);
        articleText.setTextColor(Color.parseColor(color1));
        teacher_me.setBackgroundResource(id4);
        meText.setTextColor(Color.parseColor(color4));
    }

    //点击返回键2次退出程序
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            TeacherHomeActivity.this.finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "再按一次退出批改网", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mCheckStateSaved = true;
        Log.e(TAG, "---------->onSaveInstanceState");
    }

    @Override
    public void onResume() {
        super.onResume();
        mCheckStateSaved = false;
        MobclickAgent.onResume(this);
        Log.e(TAG, "---------->onResume");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        Log.e(TAG, "---------->onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "---------->onStop");
    }
}
