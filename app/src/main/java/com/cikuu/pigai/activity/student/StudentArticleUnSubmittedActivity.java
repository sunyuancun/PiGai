package com.cikuu.pigai.activity.student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.dialog.ActionSheet;
import com.cikuu.pigai.activity.dialog.SafeProgressDialog;
import com.cikuu.pigai.activity.dialog.SpeechInputDialog;
import com.cikuu.pigai.activity.uiutils.NoPasteMenuEditText;
import com.cikuu.pigai.activity.utils.ActionBarTitleUtil;
import com.cikuu.pigai.activity.utils.ConstConfig;
import com.cikuu.pigai.activity.utils.KeyBoardUtil;
import com.cikuu.pigai.activity.utils.ServerTime;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.activity.utils.TimeShowUtils;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.ArticleRequirement;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.httprequest.VolleyRequest;

import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class StudentArticleUnSubmittedActivity extends AppCompatActivity implements VolleyRequest.ArticleRequirementCallback,
        VolleyRequest.SubmittedArticleCallback, ActionSheet.MenuItemClickListener, VolleyRequest.GetStudentClassCallback,
        View.OnClickListener, SpeechInputDialog.GetSpeechTextDataCallBack {

    //item1
    private NoPasteMenuEditText mContentEditText;
    private EditText mTitleEditText;
    private TextView mWordCountTextView;

    private TextView mArticleRequirementTextView;
    //item2
    private TextView mRequireTitle;
    private TextView mRequireArticleId;
    private TextView mRequireEndTime;
    private TextView mRequireSubmittedNumbers;
    private TextView mRequireCount;
    private TextView mRequireFullScore;
    private TextView mRequireTeacher;
    private NetworkImageView articleRequirementImageView;
    private long mTeacher_uid;

    private VolleyRequest mHttpRequest;
    private Student mStudent;

    private String title;
    private String require;
    private String endtime;
    private String content;
    private int uid;
    private int count;
    private long mArticleId;
    private long mEssayId;
    private int no_paste;
    private String teachername;
    //判断 提交作文后 需要刷新的页面
    private String articleInNetWorkOrDataBase;

    private SpeechInputDialog mSpeechInputDialog;
    private AppController.CacheImageLoader imageLoader;
    private Timer timer = new Timer();
    private TimerTask task;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //要做的事情  3s保存草稿
            if (msg.what == 1)
                saveDraft();
            if (msg.what == 2) {
                try {
                    String speechText = (String) msg.obj;
                    int positionSelectionStart = mContentEditText.getSelectionStart();
                    int positionSelectionEnd = mContentEditText.getSelectionEnd();
                    mContentEditText.getText().delete(positionSelectionStart, positionSelectionEnd);
                    mContentEditText.getText().insert(positionSelectionStart, speechText);
                    int positionSelection = positionSelectionStart + speechText.length();
                    mContentEditText.setSelection(positionSelection);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_article_un_submitted);
        setOverflowShowingAlways();
        InitGetIntentDataAndSetActionBarData(savedInstanceState);
        InitViewPager();
        InitTimeTask();
        InitSpeechDialog();


        mTeacher_uid = 0;
        mStudent = Student.GetInstance();
        mHttpRequest = new VolleyRequest();
        mHttpRequest.mArticleRequirementCallback = this;
        mHttpRequest.mSubmittedArticleCallback = this;
        mHttpRequest.mGetStudentClassCallback = this;


        if (mArticleId != 10) {
            mHttpRequest.GetArticleRequirement(mArticleId);
        } else {
            mArticleRequirementTextView.setText("题目自拟，开放性作文");
            mRequireTitle.setText("自测作文");
            mRequireArticleId.setText("作文号:10");
            mRequireEndTime.setText("");
            mRequireSubmittedNumbers.setText("");
            mRequireCount.setText("字数:100~500");
            mRequireFullScore.setText("满分:100");
            mRequireTeacher.setText("出题人:批改网");
            mTeacher_uid = 0;
        }
        /**
         * 初始化新建自测作文的eid：   rid = 10 && mEssayId = -1；
         */
        if (mEssayId == ConstConfig.CREATE_NEW_SELF_ARTICLE && mArticleId == 10) {
            mEssayId = NegativeRandomInt();
        }
    }

    private void InitSpeechDialog() {
        mSpeechInputDialog = new SpeechInputDialog(this);
        mSpeechInputDialog.mGetSpeechTextDataCallBack = this;
    }

    private void InitGetIntentDataAndSetActionBarData(Bundle savedInstanceState) {
        mStudent = Student.GetInstance();
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            title = savedInstanceState.getString(STATE_TITLE);
            require = savedInstanceState.getString(STATE_REQUIRE);
            endtime = savedInstanceState.getString(STATE_ENDTIME);
            content = savedInstanceState.getString(STATE_CONTENT);
            mArticleId = savedInstanceState.getLong(STATE_ARTICLEID);
            count = savedInstanceState.getInt(STATE_COUNT);
            uid = savedInstanceState.getInt(STATE_UID);
            mEssayId = savedInstanceState.getLong(STATE_EID);
        } else {
            title = getIntent().getStringExtra("TITLE");
            require = getIntent().getStringExtra("REQ");
            endtime = getIntent().getStringExtra("END");
            content = getIntent().getStringExtra("CONTENT");
            mArticleId = Long.parseLong(getIntent().getStringExtra("ARTICLEID"));
            count = Integer.parseInt(getIntent().getStringExtra("COUNT"));
            uid = mStudent.mStudentDescription.mUid;
            mEssayId = Long.valueOf(getIntent().getStringExtra("ESSAY_ID"));
        }
        no_paste = getIntent().getIntExtra("No_paste", -1);
        teachername = getIntent().getStringExtra("TEACHERNAME");
        articleInNetWorkOrDataBase = getIntent().getStringExtra("ArticleInNetWorkOrDataBase");

        setupActionBar("写作(" + String.valueOf(mArticleId) + ")");

        imageLoader = AppController.getInstance().getCacheImageLoader();
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all
    }

    private void setupActionBar(String text) {
        ActionBarTitleUtil actionBarTitleUtil = new ActionBarTitleUtil(this, true);
        actionBarTitleUtil.setTitleText(text);
    }

    private void InitTimeTask() {
        task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
        timer.schedule(task, 0, 3000);
    }

    private void InitViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.vPager);
        List<View> views = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.un_finish_home_startarticle, null);
        View view2 = inflater.inflate(R.layout.un_finish_home_articlerequment, null);
        views.add(view1);
        views.add(view2);
        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);

        mContentEditText = (NoPasteMenuEditText) view1.findViewById(R.id.contentEditText);
        mTitleEditText = (EditText) view1.findViewById(R.id.titleTextView);
        mWordCountTextView = (TextView) view1.findViewById(R.id.wordCountTextView);
        RelativeLayout yuYinRelativeLayout = (RelativeLayout) view1.findViewById(R.id.yuyin_relativeLayout);
        yuYinRelativeLayout.setOnClickListener(this);

        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String trimmed = s.toString().trim();
                int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
                mWordCountTextView.setText(String.valueOf(words));
            }

            public void afterTextChanged(Editable s) {
            }
        };
        mContentEditText.addTextChangedListener(mTextEditorWatcher);


        if (no_paste == 1) {
            //   禁止粘贴内容
            mContentEditText.setPasted(Boolean.FALSE);
            Toast.makeText(StudentArticleUnSubmittedActivity.this, "本篇文章禁止粘贴", Toast.LENGTH_SHORT).show();
        } else {
            mContentEditText.setPasted(Boolean.TRUE);
        }

        mContentEditText.setText(content);
        mContentEditText.setSelection(mContentEditText.length());
        mTitleEditText.setText(title);

        mArticleRequirementTextView = (TextView) view2.findViewById(R.id.articleRequirementTextView);
        mRequireTitle = (TextView) view2.findViewById(R.id.requireTitle);
        mRequireArticleId = (TextView) view2.findViewById(R.id.requireArticleId);
        mRequireEndTime = (TextView) view2.findViewById(R.id.requireEndtime);
        mRequireSubmittedNumbers = (TextView) view2.findViewById(R.id.requireSubmittedNumbers);
        mRequireCount = (TextView) view2.findViewById(R.id.requireCount);
        mRequireFullScore = (TextView) view2.findViewById(R.id.requireFullScore);
        mRequireTeacher = (TextView) view2.findViewById(R.id.requireTeacher);
        articleRequirementImageView = (NetworkImageView) view2.findViewById(R.id.articleRequirementImageView);

    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yuyin_relativeLayout:
                if (mSpeechInputDialog != null)
                    mSpeechInputDialog.showRecognizerDaialog();
                break;
        }
    }

    @Override
    public void GetSpeechTextData(String string) {
        if (string != null) {
            Message message = Message.obtain();
            message.what = 2;
            message.obj = string;
            handler.sendMessage(message);
        }
    }

    public void ArticleSubmitted(long essayId) {
        //友盟统计 计数事件
        MobclickAgent.onEventBegin(StudentArticleUnSubmittedActivity.this, "CommitArticle");
        MobclickAgent.onEventEnd(StudentArticleUnSubmittedActivity.this, "CommitArticle");

        //stop the timer   and make sure to remove the Draft
        DestoryTimeTask();
        hideProgressDialog();

        if (mArticleId == 10) {
            mStudent.DeleteStudentArticleByEassayIdDraftFromDataBase(mEssayId);
        } else {
            mStudent.DeleteStudentArticleDraftFromDataBase(mArticleId);
        }

        StudentHomeArticleFragment.mNeedToRefreshArticleListFromNetwork = true;
        StudentArticleSubmitted2Activity.mNeedToRefreshArticleDetailFromNetwork = true;

        if (articleInNetWorkOrDataBase != null && articleInNetWorkOrDataBase.equals(String.valueOf(VolleyRequest.ENetworkArticle))) {
            this.finish();
        } else {
            Intent intent = new Intent(StudentArticleUnSubmittedActivity.this, StudentArticleSubmitted2Activity.class);
            intent.putExtra("ESSAYID", String.valueOf(essayId));
            intent.putExtra("ARTICLEID", String.valueOf(mArticleId));
            intent.putExtra("No_paste", no_paste);
            intent.putExtra("TEACHERNAME", teachername);
            startActivity(intent);
            this.finish();
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Toast store = Toast.makeText(getApplicationContext(), "文章提交成功", Toast.LENGTH_SHORT);
                store.show();
            }

        }, 3000);

    }

    public void StudentClass(int success, ArrayList<String> className, String errorMsg) {
        if (className != null) {
            SharedPreferenceUtil.saveStudentClassArray(className, this);
        }
    }

    public void ArticleRequirement(ArticleRequirement articleRequirement) {
//        mArticleRequirementTextView.setText(articleRequirement.mRequirement);
        mArticleRequirementTextView.setText(Html.fromHtml(articleRequirement.mRequirement));
        mRequireTitle.setText(articleRequirement.mTitle);
        mRequireArticleId.setText("作文号：" + String.valueOf(articleRequirement.mArticleId));
        mRequireEndTime.setText(TimeShowUtils.showTimeOfStudentHome(articleRequirement.mEndtime));
        //提交作文时进行比对
        mRequireSubmittedNumbers.setText(String.valueOf(articleRequirement.mCount) + "人答题");
        mRequireCount.setText("字数:" + String.valueOf(articleRequirement.mRequireCount));
        mRequireFullScore.setText("满分:" + String.valueOf(articleRequirement.mFullScore));
        mRequireTeacher.setText("出题人:" + articleRequirement.mTeacher);

        mTeacher_uid = articleRequirement.mUid;
        mHttpRequest.GetStudentClass(mTeacher_uid);

        //// TODO: 2015/11/6
        String mImageRequirement = articleRequirement.mImageRequirement;
        if (mImageRequirement != null && !mImageRequirement.equals("http://img.pigai.org")) {

            articleRequirementImageView.setVisibility(View.VISIBLE);
            articleRequirementImageView.setImageUrl(mImageRequirement, imageLoader);
        } else {
            articleRequirementImageView.setVisibility(View.GONE);
        }
    }

    public void ErrorNetwork() {
        Toast.makeText(getApplicationContext(), "服务器或网络错误！", Toast.LENGTH_LONG).show();
    }

    static final String STATE_TITLE = "STATE_TITLE";
    static final String STATE_REQUIRE = "STATE_REQUIRE";
    static final String STATE_ENDTIME = "STATE_ENDTIME";
    static final String STATE_CONTENT = "STATE_CONTENT";
    static final String STATE_ARTICLEID = "STATE_ARTICLEID";
    static final String STATE_COUNT = "STATE_COUNT";
    static final String STATE_UID = "STATE_UID";
    static final String STATE_EID = "STATE_EID";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_TITLE, title);
        savedInstanceState.putString(STATE_REQUIRE, require);
        savedInstanceState.putString(STATE_ENDTIME, endtime);
        content = mContentEditText.getText().toString();
        savedInstanceState.putString(STATE_CONTENT, content);
        savedInstanceState.putLong(STATE_ARTICLEID, mArticleId);
        savedInstanceState.putLong(STATE_EID, mEssayId);
        savedInstanceState.putInt(STATE_COUNT, count);
        savedInstanceState.putInt(STATE_UID, uid);

        super.onSaveInstanceState(savedInstanceState);
    }

    //monitor and intercept the event
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                    Log.d("Menu", "show menu item");
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    //block menu key
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
        getMenuInflater().inflate(R.menu.menu_student_article_un_submitted, menu);
        return true;
    }

    public void showActionSheetForCompleteInformation() {
        ActionSheet menuView = new ActionSheet(this);
        menuView.setCancelButtonTitle("确定");// before add items
        menuView.addItems("作文已保存为草稿", "请完善个人信息再提交(姓名，学校，班级必填)");
        menuView.setItemClickListener(this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    public void showActionSheetForCompleteClass() {
        new AlertDialog.Builder(this)
                .setTitle("作文已保存为草稿")
                .setMessage("您的班级不在老师设定的班级中，建议修改")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(StudentArticleUnSubmittedActivity.this, StudentInformationActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("继续提交", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        submitArticle();
                    }
                })
                .show();
    }

    @Override
    public void onCancelClick() {
        Intent intent = new Intent(this, StudentInformationActivity.class);
        startActivity(intent);
    }

    boolean isCurrentClassInTeacherClass(String currentClass, ArrayList<String> teacherClass) {

        if (teacherClass == null || teacherClass.size() == 0)
            return true;
        for (String item : teacherClass) {
            if (item.equals(currentClass))
                return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_submit) {

            if (TextUtils.isEmpty(mStudent.mStudentDescription.mName) || TextUtils.isEmpty(mStudent.mStudentDescription.mSchool) ||
                    mStudent.mStudentDescription.mName.equals("请填写名字") || mStudent.mStudentDescription.mSchool.equals("请填写学校") ||
                    mStudent.mStudentDescription.mSchool.equals("pigai") || mStudent.mStudentDescription.mClass.equals("")) {
                setTheme(R.style.ActionSheetStyleIOS7);
                showActionSheetForCompleteInformation();
                return true;
            }

            String currentClass = Student.GetInstance().mStudentDescription.mClass;
            ArrayList<String> teacherClass = SharedPreferenceUtil.loadStudentClassArray(this);
            if (!isCurrentClassInTeacherClass(currentClass, teacherClass) && mArticleId != 10) {
                showActionSheetForCompleteClass();
                return true;
            }
            submitArticle();

            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    SafeProgressDialog mProgressDialog;

    private void showProgressDialog() {
        try {
            mProgressDialog = new SafeProgressDialog(StudentArticleUnSubmittedActivity.this);
            mProgressDialog.setMessage("正在提交...");
            mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitArticle() {

        String content = mContentEditText.getText().toString();
        String title = mTitleEditText.getText().toString();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content) && content.length() > 5) {
            showProgressDialog();
            mHttpRequest.SubmitArticle(mArticleId, uid, mEssayId, content, title);
        } else {
            Toast.makeText(this, "请填写完整题目和内容,并且内容字数大于5", Toast.LENGTH_SHORT).show();
        }

        //隐藏键盘
        KeyBoardUtil.hideSoftInputFromWindow(StudentArticleUnSubmittedActivity.this);
    }

    private void saveDraft() {
        try {
            String content = mContentEditText.getText().toString();
            String title = mTitleEditText.getText().toString();
            // 必须有内容才能保存草稿   // TODO: 2015/10/31
            if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(content)) {

                if (mArticleId == 10) {
                    // todo 查询  rid == 10(zi_ce Article)  看  eid 的文章是否存在
                    StudentArticle article = mStudent.GetStudentArticleDraftFromDataBaseByEssayIdWhenSelfCreateRid(mEssayId);
                    if (article != null) {
                        article.mContent = content;
                        article.mTitle = title;
                        article.mSubmittedTime = getDateToSecondTime(ServerTime.GetInstance().mTimeStampInSeconds * 1000);
                        article.mEndtime = endtime;
                        mStudent.UpdateStudentArticleByEassayIdInDataBase(article);
                    } else {
                        article = new StudentArticle();
                        article.mArticleId = mArticleId;
                        article.mEssayId = mEssayId;
                        article.mTitle = title;
                        article.mContent = content;
                        article.mUid = uid;
                        article.mRequirement = require;
                        article.mSubmittedTime = getDateToSecondTime(ServerTime.GetInstance().mTimeStampInSeconds * 1000);
                        article.mEndtime = endtime;
                        article.mCount = count;
                        article.mTeacherName = "自测";
                        article.mNetworkOrDatabase = VolleyRequest.EDatabaseArticle;
                        mStudent.StoreStudentArticleDraftInDataBase(article);
                    }
                } else {
                    StudentArticle article = mStudent.GetStudentArticleDraftFromDataBase(mArticleId);
                    if (article != null) {
                        article.mContent = content;
                        article.mTitle = title;
                        article.mEndtime = endtime;
                        article.mSubmittedTime = getDateToSecondTime(ServerTime.GetInstance().mTimeStampInSeconds * 1000);
                        mStudent.UpdateStudentArticleInDataBase(article);
                    } else {
                        article = new StudentArticle();
                        article.mArticleId = mArticleId;
                        article.mEssayId = mEssayId;
                        article.mTitle = title;
                        article.mContent = content;
                        article.mUid = uid;
                        article.mTeacherName = teachername;
                        article.mRequirement = require;
                        article.mEndtime = endtime;
                        article.mSubmittedTime = getDateToSecondTime(ServerTime.GetInstance().mTimeStampInSeconds * 1000);
                        article.mCount = count;
                        article.mNetworkOrDatabase = VolleyRequest.EDatabaseArticle;
                        mStudent.StoreStudentArticleDraftInDataBase(article);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDateToSecondTime(long timeStampInSecond) {
        Date date = new Date(timeStampInSecond);
        return android.text.format.DateFormat.format("yyyy/MM/dd HH:mm", date).toString();
    }

    //生成不重复随机数
    private long NegativeRandomInt() {
        Set<Integer> m = new HashSet<>();
        int a = 0;
        for (int i = 0; i < 100; i++) {
            do {
                a = (int) (Math.random() * 100000 + 1);
            } while (m.contains(a));
            m.add(a);
        }
        return -a;
    }

    private void DestoryTimeTask() {
        try {
            if (task != null) {
                task.cancel();
                task = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //友盟统计
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //友盟统计
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        DestoryTimeTask();
        hideProgressDialog();
        super.onDestroy();

    }
}
