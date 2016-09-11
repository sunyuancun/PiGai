package com.cikuu.pigai.activity.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.adapter.CommentsAdapter;
import com.cikuu.pigai.activity.utils.AudioRecorder;
import com.cikuu.pigai.activity.utils.ScoreTools;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
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

/**
 * Created by Administrator on 2015/1/26.
 */
public class StudentArticleSubmittedActivity extends ActionBarActivity implements
        VolleyRequest.StudentArticleDetailCallback,
        VolleyRequest.ArticleRequirementCallback,
        VolleyRequest.GetCommentsByEssayIdCallback,
        VolleyRequest.DataPostedCallback,
        AudioRecorder.AudioStateCallback {

    private ViewPager viewPager;
    private List<View> views;
    private View view1, view2;
    //Variables of item 1
    private Button mBtnComment;
    private ImageView mScoreCircle;
    private TextView mRequirementTextView;
    private TextView mArticleTextView;
    private TextView mArticleTitleTextView;
    private TextView mArticleCommentTextView;
    private TextView mScoreTextView;
    private TextView mArticleModifiedTimesTextView;
    private TextView mArticleWordsCountTextView;
    private TextView mArticleFullScoreTextView;
    //Variables of item 2
    private TextView mRequireTitle;
    private TextView mRequireArticleId;
    private TextView mRequireEndTime;
    private TextView mRequireSubmittedNumbers;
    private TextView mRequireCount;
    private TextView mRequireFullScore;
    private TextView mRequireTeacher;
    NetworkImageView mArticle_requrementImage;

    private VolleyRequest mHttpRequest;
    private Student mStudent;
    private StudentArticleDetail mStudentArticleDetail = null;

    private long mEssayId;
    private long mArticleId;
    private int mNo_paste;
    private String mTeacherName = "";
    private long mUid = 0;

    private ProgressDialog mProgressDialog;
    private ProgressDialog mArticleProgressDialog;
    AudioRecorder mRecorder;
    private List<CommentBase> mCommentsList = new ArrayList<CommentBase>();
    private ListView mCommentsListView;
    private CommentsAdapter mCommentAdapter;

    private CountDownTimer mTimer;
    AppController.CacheImageLoader imageLoader;
    String isDownload = " ";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_article_submitted);
        mEssayId = Long.parseLong(getIntent().getStringExtra("ESSAYID"));
        mArticleId = Long.parseLong(getIntent().getStringExtra("ARTICLEID"));
        mNo_paste = getIntent().getIntExtra("No_paste", -1);
        mTeacherName = getIntent().getStringExtra("TEACHERNAME");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("作文(" + String.valueOf(mArticleId) + ")");

        mArticleProgressDialog = new ProgressDialog(StudentArticleSubmittedActivity.this);
        mArticleProgressDialog.setMessage("载入作文中...");
        mArticleProgressDialog.show();

        mUid = getLoginUid();

        InitViewPager();

        mStudent = Student.GetInstance();
        mHttpRequest = new VolleyRequest();
        mHttpRequest.mStudentArticleDetailCallback = this;
        mHttpRequest.mArticleRequirementCallback = this;
        mHttpRequest.GetStudentArticleDetail(mUid, mEssayId);
        mHttpRequest.GetArticleRequirement(mArticleId);
        mHttpRequest.mGetCommentsByEssayIdCallback = this;
        mHttpRequest.GetCommentsByEssayId(mEssayId);

        imageLoader = AppController.getInstance().getCacheImageLoader();
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all
    }

    private long getLoginUid() {
        long uid = 0;
        try {
            long student_uid = Student.GetInstance().mStudentDescription.mUid;
            long teacher_uid = Teacher.GetInstance().mDescription.mUid;
            String savedUserType = SharedPreferenceUtil.getUserTypeInSP(StudentArticleSubmittedActivity.this);
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

    private void InitViewPager() {
        viewPager = (ViewPager) findViewById(R.id.vPager);
        views = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.student_submitted_my_article, null);
        view2 = inflater.inflate(R.layout.student_submitted_article_requment, null);
        views.add(view1);
        views.add(view2);
        viewPager.setAdapter(new MyViewPagerAdapter(views));
        viewPager.setCurrentItem(0);

        mScoreCircle = (ImageView) view1.findViewById(R.id.score_circle);
        mArticleTextView = (TextView) view1.findViewById(R.id.articleBodyTextView);
        mArticleTitleTextView = (TextView) view1.findViewById(R.id.articleTitleTextView);
        mArticleCommentTextView = (TextView) view1.findViewById(R.id.commentTextView);
        mArticleModifiedTimesTextView = (TextView) view1.findViewById(R.id.articleModifiedTimesTextView);
        mArticleWordsCountTextView = (TextView) view1.findViewById(R.id.articleWordsCountTextView);
        mArticleFullScoreTextView = (TextView) view1.findViewById(R.id.articleFullScoreTextView);
        mScoreTextView = (TextView) view1.findViewById(R.id.score);
        mBtnComment = (Button) view1.findViewById(R.id.commentOnEachSentence);

        mBtnComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(StudentArticleSubmittedActivity.this, CommentsBySentenceActivity.class);
                intent.putExtra("ESSAYID", String.valueOf(mEssayId));
                startActivity(intent);

            }
        });
        mRequirementTextView = (TextView) view2.findViewById(R.id.requirementTextView);
        mRequireTitle = (TextView) view2.findViewById(R.id.requireTitle);
        mRequireArticleId = (TextView) view2.findViewById(R.id.requireArticleId);
        mRequireEndTime = (TextView) view2.findViewById(R.id.requireEndtime);
        mRequireSubmittedNumbers = (TextView) view2.findViewById(R.id.requireSubmittedNumbers);
        mRequireCount = (TextView) view2.findViewById(R.id.requireCount);
        mRequireFullScore = (TextView) view2.findViewById(R.id.requireFullScore);
        mRequireTeacher = (TextView) view2.findViewById(R.id.requireTeacher);
        mArticle_requrementImage = (NetworkImageView) view2.findViewById(R.id.requrementImage);

        mRecorder = AudioRecorder.GetInstance(StudentArticleSubmittedActivity.this);
        mRecorder.mCallback = this;
        mCommentsListView = (ListView) view1.findViewById(R.id.commentsList);
        mCommentAdapter = new CommentsAdapter(this, mCommentsList);
        mCommentAdapter.HideDeleteButton();
        mCommentsListView.setAdapter(mCommentAdapter);
        mCommentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                CommentBase comment = (CommentBase) mCommentAdapter.getItem(position);

                if (comment.mType == CommentBase.EVOICECOMMENT) {
                    mRecorder = AudioRecorder.GetInstance(StudentArticleSubmittedActivity.this);
                    if (!mRecorder.isPlaying()) {
                        mCommentAdapter.mPlayOrPause = true;
                        mCommentAdapter.mClickPos = position;
                        mCommentAdapter.notifyDataSetInvalidated();
                        //MP3 网络获取路径
                        final String playingUrl = comment.mSoundUrl;
                        //MP3文件 保存的本地路径
                        final String saveUrl = Environment.getExternalStorageDirectory() + "/PiGai/student/" + comment.mCommentId + ".mp3";
                        //第一次  播放  获取网络  并下载到本地MP3  ，以后从本地MP3获取，不在请求网络
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
                            //file 验证本地文件是否存在 ，不存在则网络获取并下载到本地
                            File mp3 = new File(saveUrl);
                            if (mp3.exists()) {
                                mRecorder.onPlay(true, saveUrl);
                            } else {
                                HttpUtils http = new HttpUtils();
                                HttpHandler handler = http.download(playingUrl, saveUrl, true, false, new RequestCallBack<File>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<File> fileResponseInfo) {
                                        //   Toast.makeText(StudentArticleSubmittedActivity.this, "保存本地成功", Toast.LENGTH_SHORT).show();
                                        mRecorder.onPlay(true, saveUrl);
                                    }

                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        //   Toast.makeText(StudentArticleSubmittedActivity.this, "保存本地失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //-----------------------------------------------------------------------------
                            }

                        } else {

                            HttpUtils http = new HttpUtils();
                            HttpHandler handler = http.download(playingUrl, saveUrl, true, false, new RequestCallBack<File>() {
                                @Override
                                public void onSuccess(ResponseInfo<File> fileResponseInfo) {
                                    //    Toast.makeText(StudentArticleSubmittedActivity.this, "保存本地成功", Toast.LENGTH_SHORT).show();
                                    mRecorder.onPlay(true, saveUrl);
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    //   Toast.makeText(StudentArticleSubmittedActivity.this, "保存本地失败", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        //  TODO   需优化  1 isDownload字符串去重    2.没有限制文件个数
                        isDownload = getSharedPreferences("STUDENTSAVEMP3", MODE_PRIVATE).getString("isDownload", "") + " " + String.valueOf(comment.mCommentId);
                        SharedPreferences commentsp = getSharedPreferences("STUDENTSAVEMP3", MODE_PRIVATE);
                        SharedPreferences.Editor editor = commentsp.edit();
                        editor.putString("isDownload", isDownload);
                        editor.apply();
                    } else {
                        mCommentAdapter.mPlayOrPause = false;
                        mCommentAdapter.mClickPos = position;
                        mCommentAdapter.notifyDataSetInvalidated();
                        mRecorder.onPlay(false, "");
                    }
                }
            }
        });


    }

    public void AudioStoppedRecording(File file) {
        //do nothing
    }

    public void DataPosted() {
        //do nothing
    }

    public void AudioStoppedPlaying() {
        mCommentAdapter.mPlayOrPause = false;
        mCommentAdapter.notifyDataSetInvalidated();
    }

    public void GetComments(List<CommentBase> commentsList) {
        mCommentsList.clear();
        mCommentsList.addAll(commentsList);
        mCommentAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(mCommentsListView);
        dismissArticleProgressDialog();
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

    @Override
    public void StudentArticleDetail(StudentArticleDetail studentArticleDetail) {
        //友盟统计 计数事件
        MobclickAgent.onEventBegin(StudentArticleSubmittedActivity.this, "StudentLookArticle");
        MobclickAgent.onEventEnd(StudentArticleSubmittedActivity.this, "StudentLookArticle");

        if (studentArticleDetail != null) {
            mStudentArticleDetail = studentArticleDetail;
            mArticleTextView.setText(studentArticleDetail.mContent);
            mArticleTitleTextView.setText(studentArticleDetail.mTitle);
            mArticleModifiedTimesTextView.setText("次数：" + String.valueOf(studentArticleDetail.mModifiedTimes));
            mArticleWordsCountTextView.setText("字数：" + String.valueOf(studentArticleDetail.mWordsCount));
            mArticleFullScoreTextView.setText(" 满分：" + String.valueOf(studentArticleDetail.mFullScore));
            mArticleCommentTextView.setText("评语：" + studentArticleDetail.mComment);
//        mRequirementTextView.setText(studentArticleDetail.mRequirement);
            mRequirementTextView.setText(Html.fromHtml(studentArticleDetail.mRequirement));
            double RealScore = ScoreTools.NumberChange(studentArticleDetail.mScore);
            String highValue = String.valueOf((int) RealScore);
            int higgValueLen = highValue.length();
            String lowValue = String.valueOf((int) (RealScore * 10) % 10);

            if (!lowValue.equals("0")) {
                SpannableString builder = new SpannableString(highValue + "." + lowValue);
                builder.setSpan(new RelativeSizeSpan(0.5f), higgValueLen, higgValueLen + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mScoreTextView.setText(builder);
            } else
                mScoreTextView.setText(highValue);

            if (studentArticleDetail.mScore >= 60) {
                mScoreCircle.setImageResource(R.drawable.scored);
//            mScoreTextView.setText(String.valueOf(studentArticleDetail.mScore));
                mScoreTextView.setTextColor(0xff5bb630);
            } else if (studentArticleDetail.mScore > 0) {
                mScoreCircle.setImageResource(R.drawable.scored_unqualified);
//            mScoreTextView.setText(String.valueOf(studentArticleDetail.mScore));
                mScoreTextView.setTextColor(0xffd90c16);
            }
            if (highValue.equals("0")) {
                mScoreTextView.setText("");
                mScoreCircle.setImageDrawable(null);
            }
        }

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        dismissArticleProgressDialog();
    }

    @Override
    public void ArticleRequirement(ArticleRequirement articleRequirement) {

        mRequireTitle.setText(articleRequirement.mTitle);
        mRequireArticleId.setText("作文号:" + String.valueOf(articleRequirement.mArticleId));
        mRequireEndTime.setText("截止:" + articleRequirement.mEndtime);
        mRequireSubmittedNumbers.setText(String.valueOf(articleRequirement.mCount) + "人答题");
        mRequireCount.setText("字数:" + String.valueOf(articleRequirement.mRequireCount));
        mRequireFullScore.setText("满分:" + String.valueOf(articleRequirement.mFullScore));
        mRequireTeacher.setText("出题人:" + articleRequirement.mTeacher);
        if (articleRequirement.mImageRequirement != null) {
            mArticle_requrementImage.setVisibility(View.VISIBLE);
            mArticle_requrementImage.setImageUrl(articleRequirement.mImageRequirement, imageLoader);
        }

        dismissArticleProgressDialog();
    }

    public void ErrorNetwork() {
        Toast store = Toast.makeText(getApplicationContext(), "作文号可能已被删除，获取文章失败！", Toast.LENGTH_LONG);
        store.show();
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
    public void onStop() {
        super.onStop();
        if (mRecorder != null)
            mRecorder.onPlay(false, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //     MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("StudentArticleSubmittedActivity");

        //   if(StudentArticleHomeViewPagerActivity.mNeedToRefreshArticleListFromNetwork) {
        if (StudentHomeArticleFragment.mNeedToRefreshArticleListFromNetwork) {
            //TODO check the score just submitted, callback to issue the request again.
            //TODO currently this is temp solution, get data 5 seconds later
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("正在提交...");
            mProgressDialog.show();

            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            mTimer = new CountDownTimer(3000, 1000) {
                int i = 1;

                public void onTick(long millisUntilFinished) {
                    //tick to do nothing
                }

                public void onFinish() {
                    mHttpRequest.GetStudentArticleDetail(mUid, mEssayId);
                }
            }.start();
        }
    }

    private void dismissArticleProgressDialog() {
        if (mArticleProgressDialog != null) {
            mArticleProgressDialog.dismiss();
        }
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        //      MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("StudentArticleSubmittedActivity");
    }

}
