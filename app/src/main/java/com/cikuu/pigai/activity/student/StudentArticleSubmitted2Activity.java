package com.cikuu.pigai.activity.student;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.adapter.CommentsAdapter;
import com.cikuu.pigai.activity.dialog.NoSubmitArticleDialog;
import com.cikuu.pigai.activity.dialog.SafeProgressDialog;
import com.cikuu.pigai.activity.utils.ActionBarTitleUtil;
import com.cikuu.pigai.activity.utils.AudioRecorder;
import com.cikuu.pigai.activity.utils.ScoreTools;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.activity.utils.TimeShowUtils;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.ArticleRequirement;
import com.cikuu.pigai.businesslogic.CommentBase;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.businesslogic.StudentArticleDetail;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentArticleSubmitted2Activity extends AppCompatActivity implements View.OnClickListener,
        VolleyRequest.StudentArticleDetailCallback,
        VolleyRequest.ArticleRequirementCallback,
        VolleyRequest.GetCommentsByEssayIdCallback,
        VolleyRequest.DataPostedCallback,
        AudioRecorder.AudioStateCallback, AdapterView.OnItemClickListener {

    ScrollView mScrollView;
    TextView articleModifiedTimesTextView;
    TextView articleManFenTextView;
    TextView articleZiShuTextView;
    TextView articleScoreTextView;
    TextView essay_Rank_in_Rid_TextView;
    TextView articleTitleTextView;
    TextView articleBodyTextView;

    RadioButton zidong_RadioButton;
    RadioButton rengong_RadioButton;
    RadioButton requrement_RadioButton;
    FrameLayout zidong_framelayout;
    FrameLayout rengong_framelayout;
    FrameLayout requrement_framelayout;
    //Variables of item 1
    TextView mArticleCommentTextView;
    Button commentOnEachSentenceButton;
    //Variables of item 3
    private TextView mRequirementTextView;
    private TextView mRequireTitle;
    private TextView mRequireArticleId;
    private TextView mRequireEndTime;
    private TextView mRequireSubmittedNumbers;
    private TextView mRequireCount;
    private TextView mRequireFullScore;
    private TextView mRequireTeacher;
    private NetworkImageView mArticle_requrementImage;


    private ListView mCommentsListView;
    private List<CommentBase> mCommentsList;
    private CommentsAdapter mCommentAdapter;


    private long mEssayId;
    private long mArticleId;
    private int mNo_paste;
    private String mTeacherName = "";
    private StudentArticleDetail mStudentArticleDetail = null;
    private ArticleRequirement mArticleRequirement = null;

    private VolleyRequest mHttpRequest;
    private Student mStudent;
    private SafeProgressDialog mArticleProgressDialog;
    private AppController.CacheImageLoader imageLoader;
    private AudioRecorder mRecorder;
    String isDownload = " ";
    String compareTime = "";
    long mUid = 0;
    public static boolean mNeedToRefreshArticleDetailFromNetwork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_article_submitted2);

        mEssayId = Long.parseLong(getIntent().getStringExtra("ESSAYID"));
        mArticleId = Long.parseLong(getIntent().getStringExtra("ARTICLEID"));
        mNo_paste = getIntent().getIntExtra("No_paste", -1);
        mTeacherName = getIntent().getStringExtra("TEACHERNAME");

        setupActionBar("作文(" + String.valueOf(mArticleId) + ")");

        mArticleProgressDialog = new SafeProgressDialog(StudentArticleSubmitted2Activity.this);
        mArticleProgressDialog.setMessage("载入作文中...");
        mArticleProgressDialog.show();


        InitFindViewById();
        InitCommentListViewAndAdapter();

        mStudent = Student.GetInstance();
        mHttpRequest = new VolleyRequest();
        mHttpRequest.mStudentArticleDetailCallback = this;
        mHttpRequest.mArticleRequirementCallback = this;
        mHttpRequest.mGetCommentsByEssayIdCallback = this;

        mUid = getLoginUid();
        mHttpRequest.GetStudentArticleDetail(mUid, mEssayId);
        mHttpRequest.GetArticleRequirement(mArticleId);
        mHttpRequest.GetCommentsByEssayId(mEssayId);

        imageLoader = AppController.getInstance().getCacheImageLoader();
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all
    }


    private void setupActionBar(String text) {
        ActionBarTitleUtil actionBarTitleUtil = new ActionBarTitleUtil(this, true);
        actionBarTitleUtil.setTitleText(text);
    }


    private void InitFindViewById() {
        articleModifiedTimesTextView = (TextView) findViewById(R.id.articleModifiedTimesTextView);
        articleManFenTextView = (TextView) findViewById(R.id.manfen);
        articleZiShuTextView = (TextView) findViewById(R.id.zishu);
        articleScoreTextView = (TextView) findViewById(R.id.score);
        essay_Rank_in_Rid_TextView = (TextView) findViewById(R.id.essay_rank);
        articleTitleTextView = (TextView) findViewById(R.id.article_title);
        articleBodyTextView = (TextView) findViewById(R.id.articleBodyTextView);
        mScrollView = (ScrollView) findViewById(R.id.mScrollView);

        zidong_RadioButton = (RadioButton) findViewById(R.id.radioButton1);
        zidong_RadioButton.setOnClickListener(this);
        rengong_RadioButton = (RadioButton) findViewById(R.id.radioButton2);
        rengong_RadioButton.setOnClickListener(this);
        requrement_RadioButton = (RadioButton) findViewById(R.id.radioButton3);
        requrement_RadioButton.setOnClickListener(this);
        zidong_framelayout = (FrameLayout) findViewById(R.id.zidong_framelayout);
        rengong_framelayout = (FrameLayout) findViewById(R.id.rengong_framelayout);
        requrement_framelayout = (FrameLayout) findViewById(R.id.zuowenyaoqiu_framelayout);

        //  in  zidong_framelayout
        mArticleCommentTextView = (TextView) findViewById(R.id.jukudianpingTextView);
        commentOnEachSentenceButton = (Button) findViewById(R.id.commentOnEachSentence);
        commentOnEachSentenceButton.setOnClickListener(this);
        //  in  requrement_framelayout
        mRequirementTextView = (TextView) findViewById(R.id.requirementTextView);
        mRequireTitle = (TextView) findViewById(R.id.requireTitle);
        mRequireArticleId = (TextView) findViewById(R.id.requireArticleId);
        mRequireEndTime = (TextView) findViewById(R.id.requireEndtime);
        //   mRequireSubmittedNumbers = (TextView) findViewById(R.id.requireSubmittedNumbers);
        mRequireCount = (TextView) findViewById(R.id.requireCount);
        mRequireFullScore = (TextView) findViewById(R.id.requireFullScore);
        mRequireTeacher = (TextView) findViewById(R.id.requireTeacher);
        mArticle_requrementImage = (NetworkImageView) findViewById(R.id.requrementImage);
    }

    private void InitCommentListViewAndAdapter() {
        mRecorder = AudioRecorder.GetInstance(StudentArticleSubmitted2Activity.this);
        mRecorder.mCallback = this;
        mCommentsListView = (ListView) findViewById(R.id.commentsList);
        mCommentsList = new ArrayList<>();
        mCommentAdapter = new CommentsAdapter(this, mCommentsList);
        mCommentAdapter.HideDeleteButton();
        mCommentsListView.setAdapter(mCommentAdapter);
        mCommentsListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        //第一次  播放  获取网络  并下载到本地MP3  ，以后从本地MP3获取，不在请求网络
        CommentBase comment = (CommentBase) mCommentAdapter.getItem(position);
        if (comment.mType == CommentBase.EVOICECOMMENT) {
            if (mRecorder == null) {
                mRecorder = AudioRecorder.GetInstance(StudentArticleSubmitted2Activity.this);
            }
            if (!mRecorder.isPlaying()) {
                mCommentAdapter.mPlayOrPause = true;
                mCommentAdapter.mClickPos = position;
                mCommentAdapter.notifyDataSetInvalidated();
                final String playingUrl = comment.mSoundUrl; // mp3  url
                final String saveUrl = Environment.getExternalStorageDirectory() +
                        "/PiGai/student/" + comment.mCommentId + ".mp3";    //// mp3  save loacl url
                SharedPreferences sp = getSharedPreferences("STUDENTSAVEMP3", MODE_PRIVATE);
                String readString = sp.getString("isDownload", "");
                String[] commentIds = readString.split(" ");
                Boolean commentIdIsExist = false;
                for (int i = 0; i < commentIds.length; i++) {
                    if (String.valueOf(comment.mCommentId).equals(commentIds[i])) {
                        commentIdIsExist = true;
                    }
                }
                if (commentIdIsExist) {
                    File localMp3 = new File(saveUrl);   //file 验证本地文件是否存在 ，不存在则网络获取并下载到本地
                    if (localMp3.exists()) {
                        mRecorder.onPlay(true, saveUrl);
                    } else {
                        downloadMp3AndStartLocalAudio(playingUrl, saveUrl);
                    }

                } else {
                    downloadMp3AndStartLocalAudio(playingUrl, saveUrl);
                }

                //  TODO   需优化  1 isDownload字符串去重    2.没有限制文件个数
                isDownload = getSharedPreferences("STUDENTSAVEMP3", MODE_PRIVATE).getString("isDownload", "") + " " + String.valueOf(comment.mCommentId);
                SharedPreferences commentsp = getSharedPreferences("STUDENTSAVEMP3", MODE_PRIVATE);
                SharedPreferences.Editor editor = commentsp.edit();
                editor.putString("isDownload", isDownload);
                editor.apply();

            }
        } else {
            mCommentAdapter.mPlayOrPause = false;
            mCommentAdapter.mClickPos = position;
            mCommentAdapter.notifyDataSetInvalidated();
            mRecorder.onPlay(false, "");
        }
    }

    private void downloadMp3AndStartLocalAudio(String playingUrl, final String saveUrl) {
        HttpUtils http = new HttpUtils();
        HttpHandler handler = http.download(playingUrl, saveUrl, true, false, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> fileResponseInfo) {
                mRecorder.onPlay(true, saveUrl);
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.radioButton1:
                radioButtonSetTextBackGround("#ffffff", "#3ab3ff", "#3ab3ff");
                setFramelayoutVisibility(View.VISIBLE, View.GONE, View.GONE);
                break;
            case R.id.radioButton2:
                radioButtonSetTextBackGround("#3ab3ff", "#ffffff", "#3ab3ff");
                setFramelayoutVisibility(View.GONE, View.VISIBLE, View.GONE);
                break;
            case R.id.radioButton3:
                radioButtonSetTextBackGround("#3ab3ff", "#3ab3ff", "#ffffff");
                setFramelayoutVisibility(View.GONE, View.GONE, View.VISIBLE);
                break;
            case R.id.commentOnEachSentence:
                Intent intent = new Intent(StudentArticleSubmitted2Activity.this, CommentsBySentenceActivity.class);
                intent.putExtra("ESSAYID", String.valueOf(mEssayId));
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void setFramelayoutVisibility(int zidong, int rengong, int requrement) {
        zidong_framelayout.setVisibility(zidong);
        rengong_framelayout.setVisibility(rengong);
        requrement_framelayout.setVisibility(requrement);
    }


    private void radioButtonSetTextBackGround(String zidong, String rengong, String requrement) {
        zidong_RadioButton.setTextColor(Color.parseColor(zidong));
        rengong_RadioButton.setTextColor(Color.parseColor(rengong));
        requrement_RadioButton.setTextColor(Color.parseColor(requrement));
    }

    private long getLoginUid() {
        long uid = 0;
        try {
            long student_uid = Student.GetInstance().mStudentDescription.mUid;
            long teacher_uid = Teacher.GetInstance().mDescription.mUid;
            String savedUserType = SharedPreferenceUtil.getUserTypeInSP(StudentArticleSubmitted2Activity.this);
            if (savedUserType != null && savedUserType.equals(String.valueOf(VolleyRequest.User_Type_Student))) {
                uid = student_uid;
            }

            if (savedUserType != null && savedUserType.equals(String.valueOf(VolleyRequest.User_Type_Teacher))) {
                uid = teacher_uid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uid;
    }


    @Override
    public void StudentArticleDetail(StudentArticleDetail studentArticleDetail) {
        //友盟统计 计数事件
        MobclickAgent.onEventBegin(StudentArticleSubmitted2Activity.this, "StudentLookArticle");
        MobclickAgent.onEventEnd(StudentArticleSubmitted2Activity.this, "StudentLookArticle");

        if (studentArticleDetail != null) {
            mStudentArticleDetail = studentArticleDetail;
            articleTitleTextView.setText(studentArticleDetail.mTitle);
            articleBodyTextView.setText(studentArticleDetail.mContent);
            mRequirementTextView.setText(Html.fromHtml(studentArticleDetail.mRequirement));
            mArticleCommentTextView.setText(studentArticleDetail.mComment);
            articleModifiedTimesTextView.setText(String.valueOf(studentArticleDetail.mModifiedTimes) + "次");
            articleManFenTextView.setText(String.valueOf(studentArticleDetail.mFullScore) + "分");
            articleZiShuTextView.setText(String.valueOf(studentArticleDetail.mWordsCount));
            ScoreTextViewSetData(studentArticleDetail.mScore);
            if (mArticleId == 10) {
                essay_Rank_in_Rid_TextView.setText("无");
            } else {
                essay_Rank_in_Rid_TextView.setText("第" + studentArticleDetail.mEssay_rank + "名");
            }
        }

        dismissArticleProgressDialog();
    }


    private void ScoreTextViewSetData(double mScore) {

        double RealScore = ScoreTools.NumberChange(mScore);
        String highValue = String.valueOf((int) RealScore);
        int higgValueLen = highValue.length();
        String lowValue = String.valueOf((int) (RealScore * 10) % 10);

        if (!lowValue.equals("0")) {
            SpannableString builder = new SpannableString(highValue + "." + lowValue);
            builder.setSpan(new RelativeSizeSpan(0.5f), higgValueLen, higgValueLen + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            articleScoreTextView.setText(builder);
        } else {
            articleScoreTextView.setText(highValue);
        }

        if (mScore >= 60) {
//            articleScoreTextView.setTextColor(0xff5bb630);
            articleScoreTextView.setTextColor(0xffd90c16);
        } else if (mScore > 0) {
            articleScoreTextView.setTextColor(0xffd90c16);
        } else if (mScore < 1 && lowValue.equals("0")) {
            articleScoreTextView.setText("0");
            articleScoreTextView.setTextColor(0xffd90c16);
        }

    }

    @Override
    public void ArticleRequirement(ArticleRequirement articleRequirement) {
        if (articleRequirement != null) {

            mArticleRequirement = articleRequirement;

            compareTime = TimeShowUtils.showTimeOfStudentHome(articleRequirement.mEndtime);
            mRequireTitle.setText(articleRequirement.mTitle);
            mRequireArticleId.setText("作文号:" + String.valueOf(articleRequirement.mArticleId));
            mRequireEndTime.setText(TimeShowUtils.showTimeOfStudentHome(articleRequirement.mEndtime));
            //    mRequireSubmittedNumbers.setText(String.valueOf(articleRequirement.mCount) + "人答题");
            mRequireCount.setText(String.valueOf(articleRequirement.mRequireCount));
            mRequireFullScore.setText(String.valueOf(articleRequirement.mFullScore) + "分");
            mRequireTeacher.setText("出题人:" + articleRequirement.mTeacher);
            if (articleRequirement.mImageRequirement != null) {
                mArticle_requrementImage.setVisibility(View.VISIBLE);
                mArticle_requrementImage.setImageUrl(articleRequirement.mImageRequirement, imageLoader);
            }
        }
        dismissArticleProgressDialog();
    }


    @Override
    public void GetComments(List<CommentBase> commentsList) {
        mCommentsList.clear();
        mCommentsList.addAll(commentsList);
        mCommentAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(mCommentsListView);
        dismissArticleProgressDialog();
    }


    @Override
    public void AudioStoppedPlaying() {
        mCommentAdapter.mPlayOrPause = false;
        mCommentAdapter.notifyDataSetInvalidated();
    }


    @Override
    public void AudioStoppedRecording(File file) {
        //do nothing
    }

    @Override
    public void DataPosted() {
        //do nothing
    }

    @Override
    public void ErrorNetwork() {
        Toast store = Toast.makeText(getApplicationContext(), "作文号可能已被删除，获取文章失败！", Toast.LENGTH_LONG);
        store.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null)
            mRecorder.onPlay(false, "");
    }

    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mHttpRequest.GetStudentArticleDetail(mUid,mEssayId);
                mNeedToRefreshArticleDetailFromNetwork = false;
                CommentsBySentenceActivity.mNeedUpdateNativeDataInSubmmtedActivity = false;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("StudentArticleSubmittedActivity");
        if (mNeedToRefreshArticleDetailFromNetwork || CommentsBySentenceActivity.mNeedUpdateNativeDataInSubmmtedActivity) {
            mMainHandler.sendEmptyMessageDelayed(0, 3000);
            if (mArticleProgressDialog != null)
                mArticleProgressDialog.show();
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("StudentArticleSubmittedActivity");
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void showNoSubmitArticleDialog() {
        NoSubmitArticleDialog dialog;
        NoSubmitArticleDialog.Builder builder = new NoSubmitArticleDialog.Builder(StudentArticleSubmitted2Activity.this);
        builder.setTitle("不能修改")
                .setMessage("温馨提示：已经过了截止日期")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissArticleProgressDialog();
    }


    private void dismissArticleProgressDialog() {
        try {
            if (mArticleProgressDialog != null) {
                if (mArticleProgressDialog.isShowing()) {
                    mArticleProgressDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_student_article_submitted, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Modify) {

            if (compareTime.equals(TimeShowUtils.OUTTIME_OF_SUBMIT_ARTICLE)) {
                showNoSubmitArticleDialog();
                return true;
            }

            if (mStudentArticleDetail == null || mArticleRequirement == null) {
                Toast.makeText(StudentArticleSubmitted2Activity.this, "数据加载不全，请稍等...", Toast.LENGTH_SHORT).show();
                return true;
            }

            Intent intent = new Intent(StudentArticleSubmitted2Activity.this, StudentArticleUnSubmittedActivity.class);
            intent.putExtra("ARTICLEID", String.valueOf(mStudentArticleDetail.mArticleId));
            intent.putExtra("ESSAY_ID", String.valueOf(mStudentArticleDetail.mEssayId));
            intent.putExtra("TITLE", mStudentArticleDetail.mTitle);
            intent.putExtra("REQ", mStudentArticleDetail.mRequirement);
            if (mArticleRequirement.mEndtime != null) {
                intent.putExtra("END", mArticleRequirement.mEndtime);
            } else {
                intent.putExtra("END", "");
            }
            intent.putExtra("CONTENT", mStudentArticleDetail.mContent);
            intent.putExtra("COUNT", String.valueOf(mStudentArticleDetail.mWordsCount));
            //粘贴选项
            intent.putExtra("No_paste", mNo_paste);
            //teacherName
            intent.putExtra("TEACHERNAME", mTeacherName);
            //作文来源（在写作页面使用该字段）
            intent.putExtra("ArticleInNetWorkOrDataBase", String.valueOf(VolleyRequest.ENetworkArticle));
            //从草稿里取 title  content
            StudentArticle dbArticle = null;
            if (mStudentArticleDetail.mArticleId == 10) {
                dbArticle = mStudent.GetStudentArticleDraftFromDataBaseByEssayIdWhenSelfCreateRid(mStudentArticleDetail.mEssayId);
            } else {
                dbArticle = mStudent.GetStudentArticleDraftFromDataBase(mStudentArticleDetail.mArticleId);
            }

            if (dbArticle != null) {
                intent.putExtra("CONTENT", dbArticle.mContent);
                intent.putExtra("TITLE", dbArticle.mTitle);
            }
            startActivity(intent);
//            this.finish();
            return true;
        }


        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
