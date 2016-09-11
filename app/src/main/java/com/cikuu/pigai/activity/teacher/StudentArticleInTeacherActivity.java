package com.cikuu.pigai.activity.teacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.adapter.CommentsAdapter;
import com.cikuu.pigai.activity.dialog.InputScoreDialog;
import com.cikuu.pigai.activity.dialog.RecordingVoiceDialog;
import com.cikuu.pigai.activity.student.CommentsBySentenceActivity;
import com.cikuu.pigai.activity.utils.AudioRecorder;
import com.cikuu.pigai.activity.utils.ScoreTools;
import com.cikuu.pigai.activity.utils.ScreenBackLightControl;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.cikuu.pigai.activity.utils.TimeShowUtils;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.ArticleRequirement;
import com.cikuu.pigai.businesslogic.CommentBase;
import com.cikuu.pigai.businesslogic.Student;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class StudentArticleInTeacherActivity extends AppCompatActivity
        implements VolleyRequest.StudentArticleDetailCallback,
        VolleyRequest.StudentHeadCallback,
        VolleyRequest.TeacherModifyStudentScoreCallback,
        VolleyRequest.TeacherAddTextCommentCallback,
        VolleyRequest.GetCommentsByEssayIdCallback,
        VolleyRequest.TeacherDeleteCommentCallback,
        VolleyRequest.DataPostedCallback,
        AudioRecorder.AudioStateCallback,
        VolleyRequest.ArticleRequirementCallback,
        InputScoreDialog.NoticeDialogListener, View.OnClickListener {

    @InjectView(R.id.student_image)
    NetworkImageView studentImage;
    @InjectView(R.id.studentName)
    TextView studentName;
    @InjectView(R.id.date)
    TextView date;
    @InjectView(R.id.eassayrank)
    TextView eassayrank;
    @InjectView(R.id.articleModifiedTimesTextView)
    TextView articleModifiedTimesTextView;
    @InjectView(R.id.manfen)
    TextView manfen;
    @InjectView(R.id.zishu)
    TextView zishu;
    @InjectView(R.id.score)
    TextView score;
    @InjectView(R.id.article_title)
    TextView articleTitle;
    @InjectView(R.id.articleBodyTextView)
    TextView articleBodyTextView;
    @InjectView(R.id.radioButton1)
    RadioButton radioButton1;
    @InjectView(R.id.radioButton2)
    RadioButton radioButton2;
    @InjectView(R.id.radioButton3)
    RadioButton radioButton3;
    @InjectView(R.id.jukudianpingTextView)
    TextView jukudianpingTextView;
    @InjectView(R.id.commentOnEachSentence)
    Button commentOnEachSentence;
    @InjectView(R.id.requireEndtime)
    TextView requireEndtime;
    @InjectView(R.id.requireFullScore)
    TextView requireFullScore;
    @InjectView(R.id.requireCount)
    TextView requireCount;
    @InjectView(R.id.requireTitle)
    TextView requireTitle;
    @InjectView(R.id.requireArticleId)
    TextView requireArticleId;
    @InjectView(R.id.requireTeacher)
    TextView requireTeacher;
    @InjectView(R.id.requrementImage)
    NetworkImageView requrementImage;
    @InjectView(R.id.requirementTextView)
    TextView requirementTextView;
    @InjectView(R.id.zuowenyaoqiu_framelayout)
    FrameLayout zuowenyaoqiuFramelayout;
    @InjectView(R.id.zidong_framelayout)
    FrameLayout zidongFramelayout;
    @InjectView(R.id.rengong_framelayout)
    FrameLayout rengongFramelayout;
    @InjectView(R.id.input_view)
    LinearLayout input_view_layout;

    private VolleyRequest mHttpRequest;
    private ProgressDialog mProgressDialog;


    Button mBtnText;
    Button mBtnVoice;
    EditText mInputTextCommentEditText;
    Button mInputVoiceCommentButton;
    Button mBtnSendTextComment;

    InputScoreDialog mSocreDialog;

    long mEssayId;
    long mArticleId;

    Teacher mTeacher;
    AudioRecorder mRecorder;
    RecordingVoiceDialog mRecordingDialog;
    CountDownTimer mTimer;
    private List<CommentBase> mCommentsList = new ArrayList<CommentBase>();
    private ListView mCommentsListView;
    private CommentsAdapter mCommentAdapter;
    private int mDeleteCommentPos;
    private int mDuration = 0;

    AppController.CacheImageLoader imageLoader;

    String isDownload = " ";
    //-------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_article_in_teacher2);
        ButterKnife.inject(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.show();

        mEssayId = Long.parseLong(this.getIntent().getStringExtra("ESSAY_ID"));
        mArticleId = Long.parseLong(this.getIntent().getStringExtra("ARTICLE_ID"));

        mTeacher = Teacher.GetInstance();

        imageLoader = AppController.getInstance().getCacheImageLoader();
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mStudentArticleDetailCallback = this;
        mHttpRequest.mStudentHeadCallback = this;
        mHttpRequest.mArticleRequirementCallback = this;
        mHttpRequest.mTeacherModifyStudentScoreCallback = this;
        mHttpRequest.mTeacherAddTextCommentCallback = this;
        mHttpRequest.mGetCommentsByEssayIdCallback = this;
        mHttpRequest.mTeacherDeleteCommentCallback = this;
        mHttpRequest.mDataPostedCallback = this;

        long uid = getLoginUid();
        mHttpRequest.GetStudentArticleDetail(uid, mEssayId);
        mHttpRequest.GetCommentsByEssayId(mEssayId);
        mHttpRequest.GetArticleRequirement(mArticleId);

        final FragmentManager fm = getSupportFragmentManager();
        mRecordingDialog = RecordingVoiceDialog.newInstance();
        mRecorder = AudioRecorder.GetInstance(StudentArticleInTeacherActivity.this);
        mRecorder.mCallback = this;

        //  用于  小米  华为  等手机   授权  崩掉  的 bug  提前授权设置
        try {
            mRecorder.startRecording();
            mRecorder.stopRecording();
        } catch (Exception E) {
            E.printStackTrace();
        }


        score.setOnClickListener(this);

        radioButton1.setOnClickListener(this);
        radioButton2.setOnClickListener(this);
        radioButton3.setOnClickListener(this);
        //fragment   1
        commentOnEachSentence.setOnClickListener(this);
        //bottom UI elements, then enable user to input text or voice comment
        mBtnText = (Button) findViewById(R.id.btnText);
        mBtnVoice = (Button) findViewById(R.id.btnVoice);
        mInputTextCommentEditText = (EditText) findViewById(R.id.editTextInputText);
        mInputVoiceCommentButton = (Button) findViewById(R.id.btnInputVoice);
        mBtnSendTextComment = (Button) findViewById(R.id.btnSendTextComment);

        mBtnSendTextComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String comment = mInputTextCommentEditText.getText().toString();
                try {
                    comment = URLEncoder.encode(comment, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mHttpRequest.TeacherAddTextComment(mTeacher.mDescription.mUid, mEssayId, comment);
                //友盟统计 计数事件
                MobclickAgent.onEventBegin(StudentArticleInTeacherActivity.this, "TeacherTextComment");
                MobclickAgent.onEventEnd(StudentArticleInTeacherActivity.this, "TeacherTextComment");
            }
        });

        mBtnText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mInputTextCommentEditText.setVisibility(View.VISIBLE);
                mInputVoiceCommentButton.setVisibility(View.INVISIBLE);
                mBtnVoice.setVisibility(View.VISIBLE);
                mBtnText.setVisibility(View.INVISIBLE);
                mBtnSendTextComment.setVisibility(View.VISIBLE);
            }
        });

        mBtnVoice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mInputTextCommentEditText.setVisibility(View.INVISIBLE);
                mInputVoiceCommentButton.setVisibility(View.VISIBLE);
                mBtnVoice.setVisibility(View.INVISIBLE);
                mBtnText.setVisibility(View.VISIBLE);
                mBtnSendTextComment.setVisibility(View.INVISIBLE);
            }
        });


        //-------------------------------------------------------

        mInputVoiceCommentButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        ScreenBackLightControl.SetBackLightOn(StudentArticleInTeacherActivity.this, true);

                        mInputVoiceCommentButton.setBackgroundResource(R.drawable.button_voice_comment_pressed);
                        mRecorder = AudioRecorder.GetInstance(StudentArticleInTeacherActivity.this);
                        mRecorder.onRecord(true);
                        mRecordingDialog.show(fm, "recording");

                        if (mTimer != null) {
                            mTimer.cancel();
                            mTimer = null;
                        }
                        mDuration = 1;
                        mTimer = new CountDownTimer(60000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                if (mDuration < 10) {
                                    mRecordingDialog.mCountDownTextView.setText("00:0" + String.valueOf(mDuration++));
                                } else {
                                    mRecordingDialog.mCountDownTextView.setText("00:" + String.valueOf(mDuration++));
                                }
                            }

                            public void onFinish() {
                                mRecorder.onRecord(false);
                                mRecordingDialog.dismiss();
                                mTimer.cancel();
                                mTimer = null;
                            }
                        }.start();
                        return true;

                    case MotionEvent.ACTION_UP:

                        ScreenBackLightControl.SetBackLightOn(StudentArticleInTeacherActivity.this, false);

                        if (mTimer != null) {
                            mTimer.cancel();
                            mTimer = null;
                        }

                        mInputVoiceCommentButton.setBackgroundResource(R.drawable.button_voice_comment_default);
                        mRecorder = AudioRecorder.GetInstance(StudentArticleInTeacherActivity.this);
                        mRecorder.onRecord(false);
                        mRecordingDialog.dismiss();
                        //友盟统计 计数事件
                        MobclickAgent.onEventBegin(StudentArticleInTeacherActivity.this, "TeacherVoiceComment");
                        MobclickAgent.onEventEnd(StudentArticleInTeacherActivity.this, "TeacherVoiceComment");
                        return true;
                }
                return false;
            }
        });


        mCommentsListView = (ListView) findViewById(R.id.commentsList);
        mCommentAdapter = new CommentsAdapter(this, mCommentsList);
        mCommentsListView.setAdapter(mCommentAdapter);


        mCommentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                final CommentBase comment = (CommentBase) mCommentAdapter.getItem(position);
                if (comment.mType == CommentBase.EVOICECOMMENT) {
                    mRecorder = AudioRecorder.GetInstance(StudentArticleInTeacherActivity.this);
                    if (!mRecorder.isPlaying()) {
                        mCommentAdapter.mPlayOrPause = true;
                        mCommentAdapter.mClickPos = position;
                        mCommentAdapter.notifyDataSetInvalidated();
                        //MP3 网络获取路径
                        final String playingUrl = comment.mSoundUrl;
                        //MP3文件 保存的本地路径
                        final String saveUrl = Environment.getExternalStorageDirectory() + "/PiGai/teacher/" + comment.mCommentId + ".mp3";
                        //第一次  播放  获取网络  并下载到本地MP3  ，以后从本地MP3获取，不在请求网络
                        SharedPreferences sp = getSharedPreferences("TEACHERSAVEMP3", MODE_PRIVATE);
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
                                        //        Toast.makeText(StudentArticleInTeacherActivity.this, "保存本地成功", Toast.LENGTH_SHORT).show();
                                        mRecorder.onPlay(true, saveUrl);
                                    }

                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        //        Toast.makeText(StudentArticleInTeacherActivity.this, "保存本地失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //-----------------------------------------------------------------------------
                            }

                        } else {

                            HttpUtils http = new HttpUtils();
                            HttpHandler handler = http.download(playingUrl, saveUrl, true, false, new RequestCallBack<File>() {
                                @Override
                                public void onSuccess(ResponseInfo<File> fileResponseInfo) {
                                    //    Toast.makeText(StudentArticleInTeacherActivity.this, "保存本地成功", Toast.LENGTH_SHORT).show();
                                    mRecorder.onPlay(true, saveUrl);
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    //    Toast.makeText(StudentArticleInTeacherActivity.this, "保存本地失败", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        //  TODO   需优化  1 isDownload字符串去重    2.没有限制文件个数
                        isDownload = getSharedPreferences("TEACHERSAVEMP3", MODE_PRIVATE).getString("isDownload", "") + " " + String.valueOf(comment.mCommentId);
                        SharedPreferences commentsp = getSharedPreferences("TEACHERSAVEMP3", MODE_PRIVATE);
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


        setListViewHeightBasedOnChildren(mCommentsListView);


    }

    private long getLoginUid() {
        long uid = 0;
        try {
            long student_uid = Student.GetInstance().mStudentDescription.mUid;
            long teacher_uid = Teacher.GetInstance().mDescription.mUid;
            String savedUserType = SharedPreferenceUtil.getUserTypeInSP(StudentArticleInTeacherActivity.this);
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
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.radioButton1:
                radioButtonSetTextBackGround("#ffffff", "#3ab3ff", "#3ab3ff");
                setFramelayoutVisibility(View.VISIBLE, View.GONE, View.GONE);
                input_view_layout.setVisibility(View.GONE);
                break;
            case R.id.radioButton2:
                radioButtonSetTextBackGround("#3ab3ff", "#ffffff", "#3ab3ff");
                setFramelayoutVisibility(View.GONE, View.VISIBLE, View.GONE);
                input_view_layout.setVisibility(View.VISIBLE);
                break;
            case R.id.radioButton3:
                radioButtonSetTextBackGround("#3ab3ff", "#3ab3ff", "#ffffff");
                setFramelayoutVisibility(View.GONE, View.GONE, View.VISIBLE);
                input_view_layout.setVisibility(View.GONE);
                break;

            case R.id.score:
                FragmentManager fm = getSupportFragmentManager();
                mSocreDialog = InputScoreDialog.newInstance(85);
                mSocreDialog.show(fm, "score");
                //友盟统计 计数事件
                MobclickAgent.onEventBegin(StudentArticleInTeacherActivity.this, "TeacherModifyScore");
                MobclickAgent.onEventEnd(StudentArticleInTeacherActivity.this, "TeacherModifyScore");
                break;

            case R.id.commentOnEachSentence:
                Intent intent = new Intent(StudentArticleInTeacherActivity.this, CommentsBySentenceActivity.class);
                intent.putExtra("ESSAYID", String.valueOf(mEssayId));
                startActivity(intent);
                break;


        }

    }

    private void setFramelayoutVisibility(int zidong, int rengong, int requrement) {
        zidongFramelayout.setVisibility(zidong);
        rengongFramelayout.setVisibility(rengong);
        zuowenyaoqiuFramelayout.setVisibility(requrement);
    }

    private void radioButtonSetTextBackGround(String zidong, String rengong, String requrement) {
        radioButton1.setTextColor(Color.parseColor(zidong));
        radioButton2.setTextColor(Color.parseColor(rengong));
        radioButton3.setTextColor(Color.parseColor(requrement));
    }


    public void AudioStoppedPlaying() {
        mCommentAdapter.mPlayOrPause = false;
        mCommentAdapter.notifyDataSetInvalidated();
    }

    public void AudioStoppedRecording(File file) {
        mRecorder.CreateAndShowProgressDialog();
        mHttpRequest.PostDataToServer(file, mTeacher.mDescription.mUid, mEssayId, mDuration);
    }

    public void DataPosted() {
        mRecorder.DismissProgressDialog();
        VoiceCommentAdded();
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

    private void hidePDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void DeleteComment(int pos) {
        mDeleteCommentPos = pos;
        CommentBase comment = mCommentsList.get(pos);
        mHttpRequest.TeacherDeleteComment(comment.mUid, comment.mCommentId);
    }

    public void CommentDeleted() {
        if (mDeleteCommentPos < mCommentsList.size()) {
            mCommentsList.remove(mDeleteCommentPos);
            mCommentAdapter.notifyDataSetChanged();
        }
        setListViewHeightBasedOnChildren(mCommentsListView);
    }

    public void TextCommentAdded() {

        CommentBase comment = new CommentBase();
        comment.mType = CommentBase.ETEXTCOMMENT;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        comment.mDate = formattedDate;
        comment.mTextComments = mInputTextCommentEditText.getText().toString();

        mCommentsList.add(comment);
        mCommentAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(mCommentsListView);

        mInputTextCommentEditText.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mInputTextCommentEditText.getWindowToken(), 0);
        mHttpRequest.GetCommentsByEssayId(mEssayId);
    }

    public void VoiceCommentAdded() {
        CommentBase comment = new CommentBase();
        comment.mType = CommentBase.EVOICECOMMENT;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        comment.mDate = formattedDate;
        String localUrl = mRecorder.getAudioFilePath();
        comment.mTextComments = localUrl;

        mCommentsList.add(comment);
        mCommentAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(mCommentsListView);

        mHttpRequest.GetCommentsByEssayId(mEssayId);
    }

    public void GetComments(List<CommentBase> commentsList) {
        mCommentsList.clear();
        mCommentsList.addAll(commentsList);
        mCommentAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(mCommentsListView);
    }

    //callback from the network
    public void StudentArticleDetail(StudentArticleDetail studentArticleDetail) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        SetUIData(studentArticleDetail);
        //友盟统计 计数事件
        MobclickAgent.onEventBegin(StudentArticleInTeacherActivity.this, "TeacherLookArticle");
        MobclickAgent.onEventEnd(StudentArticleInTeacherActivity.this, "TeacherLookArticle");
    }

    private void SetUIData(StudentArticleDetail studentArticleDetail) {
        jukudianpingTextView.setText(studentArticleDetail.mComment);
        articleTitle.setText(studentArticleDetail.mTitle);
        articleBodyTextView.setText(studentArticleDetail.mContent);
        studentName.setText(studentArticleDetail.mName);
//        mClassTextView.setText(studentArticleDetail.mStudentClass);
        date.setText(studentArticleDetail.mSubmittedDate);
        articleModifiedTimesTextView.setText(String.valueOf(studentArticleDetail.mModifiedTimes));
        zishu.setText(String.valueOf(studentArticleDetail.mWordsCount));
        manfen.setText(String.valueOf(studentArticleDetail.mFullScore));
        eassayrank.setText("第" + studentArticleDetail.mEssay_rank + "名");
        double RealScore = ScoreTools.NumberChange(studentArticleDetail.mScore);
        String highValue = String.valueOf((int) RealScore);
        int higgValueLen = highValue.length();
        String lowValue = String.valueOf((int) (RealScore * 10) % 10);

        if (!lowValue.equals("0")) {
            SpannableString builder = new SpannableString(highValue + "." + lowValue);
            builder.setSpan(new RelativeSizeSpan(0.5f), higgValueLen, higgValueLen + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            score.setText(builder);
        } else
            score.setText(highValue);

        if (studentArticleDetail.mScore < 1) {
            score.setText("");
        }
        mHttpRequest.GetUserHead(studentArticleDetail.mUser_id);
    }

    @Override
    public void StudentHeadUrl(String url) {
        studentImage.setImageUrl(url, imageLoader);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String fullScore = manfen.getText().toString().trim();
        if (mSocreDialog.mScore >= Integer.parseInt(fullScore)) {
            Toast.makeText(this, "您设置分数的范围为0--" + fullScore, Toast.LENGTH_SHORT).show();
            return;
        } else {
            mHttpRequest.TeacherModifyStudentScore(mTeacher.mDescription.mUid, mEssayId, mSocreDialog.mScore);
            mSocreDialog.dismiss();
        }
    }

    public void ScoreModified(int error) {
        if (error > 0) {

            String highValue = String.valueOf((int) mSocreDialog.mScore);
            int highValueLen = highValue.length();
            String lowValue = String.valueOf((int) (mSocreDialog.mScore * 10) % 10);


            if (!lowValue.equals("0")) {
                SpannableString builder = new SpannableString(highValue + "." + lowValue);
                builder.setSpan(new RelativeSizeSpan(0.5f), highValueLen, highValueLen + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                score.setText(builder);
            } else
                score.setText(highValue);

            StudentArticleListInTeacherArticleActivity.SCORE_CHANGED = true;
            StudentArticleListInTeacherArticleActivity.SCORE = mSocreDialog.mScore;
//            if (mSocreDialog.mScore < 60) {
//                mRelativeLayout.setBackgroundResource(R.drawable.scored_unqualified);
//                mScoreTextView.setTextColor(Color.parseColor("#d80d17"));
//            } else {
//                mRelativeLayout.setBackgroundResource(R.drawable.scored);
//                mScoreTextView.setTextColor(Color.parseColor("#5db633"));
//            }
        } else {
            Toast store = Toast.makeText(getApplicationContext(), "没有权限更改其他老师布置作文的分数，", Toast.LENGTH_LONG);
            store.show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if (mSocreDialog != null)
            mSocreDialog.dismiss();
    }

    @Override
    public void ArticleRequirement(ArticleRequirement articleRequirement) {

        requireTitle.setText(articleRequirement.mTitle);
        requireArticleId.setText("作文号:" + String.valueOf(articleRequirement.mArticleId));
        requireEndtime.setText(TimeShowUtils.showTimeOfStudentHome(articleRequirement.mEndtime));
        //    mRequireSubmittedNumbers.setText(String.valueOf(articleRequirement.mCount) + "人答题");
        requireCount.setText(String.valueOf(articleRequirement.mRequireCount));
        requireFullScore.setText(String.valueOf(articleRequirement.mFullScore) + "分");
        requireTeacher.setText("出题人:" + articleRequirement.mTeacher);
        requirementTextView.setText(Html.fromHtml(articleRequirement.mRequirement));

        if (articleRequirement.mImageRequirement != null) {
            requrementImage.setVisibility(View.VISIBLE);
            requrementImage.setImageUrl(articleRequirement.mImageRequirement, imageLoader);
        }
    }


    public void ErrorNetwork() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        Toast.makeText(getApplicationContext(), "不能加载文章！", Toast.LENGTH_LONG).show();
    }

    //友盟统计
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null)
            mRecorder.onPlay(false, "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecorder != null)
            mRecorder.onPlay(false, "");
        hidePDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_article_in_teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
