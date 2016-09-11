package com.cikuu.pigai.activity.student;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.dialog.ActionSheet;
import com.cikuu.pigai.activity.utils.ActionBarTitleUtil;
import com.cikuu.pigai.activity.utils.ConstConfig;
import com.cikuu.pigai.activity.utils.MeizuSmartBarTool;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StudentHomeActivity extends AppCompatActivity implements ActionSheet.MenuItemClickListener, View.OnClickListener {
    public static final String TAG = StudentHomeActivity.class
            .getSimpleName();
    private FragmentManager mFragmentManager;
    private LinearLayout mStudentArticleLinearLayout;
    private LinearLayout mStudentMelLinearLayout;
    private ImageView studentArticle, studentMe;
    private TextView center_button, articleText, meText;
    private ActionBar mActionBar;

    private Student mStudent;

    private PopupWindow popWindow;
    LinearLayout zice_item;
    LinearLayout sousuo_item;
    LinearLayout tiku_item;
    RelativeLayout back_line;

    int tabBar_item1 = R.drawable.home_article_press;
    int tabBar_item1_unselected = R.drawable.home_article_default;
    int tabBar_item2 = R.drawable.home_me_press;
    int tabBar_item2_unselected = R.drawable.home_me_default;

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
        setContentView(R.layout.activity_student_home);
        Log.e(TAG, "---------->onCreate");
        restoreFromSaveInstanceState(savedInstanceState);
        mStudent = Student.GetInstance();

        setupUI();
    }

    private void setupUI() {
        setupActionBar();
        setOverflowShowingAlways();
        InitFragment();
        BottomLayout();
    }

    ActionBarTitleUtil actionBarTitleUtil;

    private void setupActionBar() {
        actionBarTitleUtil = new ActionBarTitleUtil(this, false);
        actionBarTitleUtil.setTitleText(getResources().getString(R.string.liu_lan_zuo_wen));
    }

    private void restoreFromSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.e(TAG, "-------CREATE--->restoreFromSaveInstanceState");
            mStudent = Student.GetInstance();
            SharedPreferences preferences = getSharedPreferences(Student.SHAREDPREFERENCES_STUDENT, Context.MODE_PRIVATE);
            mStudent.mStudentDescription.mUid = preferences.getInt("UID", 0);
            mStudent.mStudentDescription.mName = preferences.getString("NAME", "");
            mStudent.mStudentDescription.mSex = preferences.getString("SEX", "");
            mStudent.mStudentDescription.mSchool = preferences.getString("SCHOOL", "");
            mStudent.mStudentDescription.mStudent_number = preferences.getString("STUDENT_NUMBER", "");
            mStudent.mStudentDescription.mClass = preferences.getString("CLASS", "");
            mStudent.mStudentDescription.mTel = preferences.getString("TEL", "");
            mStudent.mStudentDescription.bHead = preferences.getString("SHEAD", "");
            mStudent.mStudentDescription.bHead = preferences.getString("BHEAD", "");
        }
    }

    StudentHomeArticleFragment mStudentHomeArticleFragment;
    StudentHomeMeFragment mStudentHomeMeFragment;

    private void InitFragment() {
        mFragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mStudentHomeArticleFragment = new StudentHomeArticleFragment();
        fragmentTransaction.add(R.id.vPager, mStudentHomeArticleFragment, "StudentHomeArticleFragment");

        mStudentHomeMeFragment = new StudentHomeMeFragment();
        fragmentTransaction.add(R.id.vPager, mStudentHomeMeFragment, "StudentHomeMeFragment");
        fragmentTransaction.hide(mStudentHomeMeFragment);

        fragmentTransaction.commit();

        if (TextUtils.isEmpty(mStudent.mStudentDescription.mName) || TextUtils.isEmpty(mStudent.mStudentDescription.mSchool) ||
                mStudent.mStudentDescription.mName.equals("请填写名字") || mStudent.mStudentDescription.mSchool.equals("请填写学校") ||
                mStudent.mStudentDescription.mSchool.equals("pigai")) {
            setTheme(R.style.ActionSheetStyleIOS7);
            showActionSheet();
        }
    }

    private void BottomLayout() {
        mStudentArticleLinearLayout = (LinearLayout) findViewById(R.id.studentarticleline);
        mStudentMelLinearLayout = (LinearLayout) findViewById(R.id.studentmeline);
        studentArticle = (ImageView) findViewById(R.id.studentarticle);
        studentMe = (ImageView) findViewById(R.id.student_me);
        articleText = (TextView) findViewById(R.id.articleText);
        meText = (TextView) findViewById(R.id.meText);
        center_button = (TextView) findViewById(R.id.center_button);

        mStudentArticleLinearLayout.setOnClickListener(this);
        mStudentMelLinearLayout.setOnClickListener(this);
        center_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.studentarticleline:
                if (mStudentHomeArticleFragment != null && mStudentHomeArticleFragment.isVisible())
                    return;
                if (!mCheckStateSaved) {
                    actionBarTitleUtil.setTitleText(getResources().getString(R.string.liu_lan_zuo_wen));
                    setTextViewBackground(tabBar_item1, colorBlue, tabBar_item2_unselected, colordefault);
                    showFragment(mStudentHomeArticleFragment);
                    hideFragment(mStudentHomeMeFragment);
                }
                break;
            case R.id.studentmeline:
                if (mStudentHomeMeFragment != null && mStudentHomeMeFragment.isVisible())
                    return;
                if (!mCheckStateSaved) {
                    actionBarTitleUtil.setTitleText(getResources().getString(R.string.guan_yu_wo));
                    setTextViewBackground(tabBar_item1_unselected, colordefault, tabBar_item2, colorBlue);
                    showFragment(mStudentHomeMeFragment);
                    hideFragment(mStudentHomeArticleFragment);
                }
                break;
            case R.id.center_button:
                showPopupWindow(center_button);
                break;
            default:
                break;
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.show(fragment);
        // fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.commit();
    }

    private void hideFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment);
        // fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.commit();
    }

    public void showActionSheet() {
        ActionSheet menuView = new ActionSheet(this);
        menuView.setCancelButtonTitle("确定");// before add items
        menuView.addItems("同学，请完善个人资料", "温馨提示：必须填写“名字”和“学校”");
        menuView.setItemClickListener(StudentHomeActivity.this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    @Override
    public void onCancelClick() {
        Intent intent = new Intent(StudentHomeActivity.this, StudentInformationActivity.class);
        startActivity(intent);
    }

    private void showPopupWindow(View parent) {
        if (popWindow == null) {
            View view = null;
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (MeizuSmartBarTool.hasSmartBar()) {
                view = layoutInflater.inflate(R.layout.popuwindow_of_student_home_for_meizu, null);
            } else {
                view = layoutInflater.inflate(R.layout.popuwindow_of_student_home, null);
            }

            zice_item = (LinearLayout) view.findViewById(R.id.zice);
            sousuo_item = (LinearLayout) view.findViewById(R.id.sousuo);
            tiku_item = (LinearLayout) view.findViewById(R.id.tiku);
            back_line = (RelativeLayout) view.findViewById(R.id.back_line);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            popWindow = new PopupWindow(view, dm.widthPixels, dm.heightPixels);
        }

        Resources resources = getBaseContext().getResources();
        Drawable drawable = resources.getDrawable(R.color.home_student_background_color);
        popWindow.setBackgroundDrawable(drawable);
        // PopupWindow的显示及位置设置
        popWindow.setAnimationStyle(R.style.anim_student_home_popuwindow_style);
        popWindow.showAtLocation(parent, Gravity.FILL, 0, 0);

        zice_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentHomeActivity.this, StudentArticleUnSubmittedActivity.class);
                intent.putExtra("ARTICLEID", String.valueOf(10));
                intent.putExtra("ESSAY_ID", String.valueOf(ConstConfig.CREATE_NEW_SELF_ARTICLE));  //eid<0 , create  new SelfArticle
                intent.putExtra("TITLE", "");
                intent.putExtra("REQ", "");
                intent.putExtra("END", "");
                intent.putExtra("CONTENT", "");
                intent.putExtra("COUNT", "0");
                intent.putExtra("No_paste", -1);
                //teacherName
                intent.putExtra("TEACHERNAME", "批改网");
                //作文来源（在写作页面使用该字段）
                intent.putExtra("ArticleInNetWorkOrDataBase", String.valueOf(VolleyRequest.EDatabaseArticle));
                startActivity(intent);
                popWindow.dismiss();
            }
        });

        sousuo_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sousuo_intent = new Intent(StudentHomeActivity.this, StudentSearchActivity.class);
                startActivity(sousuo_intent);
                popWindow.dismiss();

            }
        });

        tiku_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tiku_intent = new Intent(StudentHomeActivity.this, StudentTiKuActivity.class);
                startActivity(tiku_intent);
                popWindow.dismiss();
            }
        });

        back_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
            }
        });
    }

    private void setTextViewBackground(int id1, String color1, int id4, String color4) {
        studentArticle.setBackgroundResource(id1);
        articleText.setTextColor(Color.parseColor(color1));
        studentMe.setBackgroundResource(id4);
        meText.setTextColor(Color.parseColor(color4));
    }

    //点击返回键2次退出程序
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            StudentHomeActivity.this.finish();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //MENU button
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    //show app:showAsAction="never" menu item of this property
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {

                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    //block menu key,
    //https://wenchaojames.wordpress.com/2013/01/20/split-action-bar-on-android-device-with-physical-menu-button/
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_student_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mCheckStateSaved = true;
        Log.e(TAG, "---------->onSaveInstanceState");
    }

    //友盟统计
    @Override
    protected void onResume() {
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
