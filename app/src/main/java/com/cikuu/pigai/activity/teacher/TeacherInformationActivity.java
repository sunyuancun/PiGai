package com.cikuu.pigai.activity.teacher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.SearchSchoolActivity;
import com.cikuu.pigai.activity.adapter.TeacherInformationAdapter;
import com.cikuu.pigai.activity.utils.ConstConfig;
import com.cikuu.pigai.activity.utils.ImageTools;
import com.cikuu.pigai.activity.utils.MobileTools;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class TeacherInformationActivity extends ActionBarActivity implements VolleyRequest.UserInfoModifyCallback {

    private VolleyRequest mHttpRequest;
    private Teacher mTeacher;
    TeacherInformationAdapter teacherInfoAdapter;
    ArrayList<TeacherInformationAdapter.Information> teacherInfoListArray;

    private ListView mTeacherInfoListView;
    private String mNewInfo;  //new item value
    private int mTeacherInfoListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_information);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mUserInfoModifyCallback = this;
        mTeacher = Teacher.GetInstance();

        teacherInfoListArray = new ArrayList<>();
        TeacherInformationAdapter.Information info1 = new TeacherInformationAdapter.Information("姓名", mTeacher.GetDescription().mName);
        TeacherInformationAdapter.Information info2 = new TeacherInformationAdapter.Information("性别", mTeacher.GetDescription().mSex);
        TeacherInformationAdapter.Information info3 = new TeacherInformationAdapter.Information("学校", mTeacher.GetDescription().mSchool);
        TeacherInformationAdapter.Information info4 = new TeacherInformationAdapter.Information("手机", mTeacher.GetDescription().mTel);
        teacherInfoListArray.add(info1);
        teacherInfoListArray.add(info2);
        teacherInfoListArray.add(info3);
        teacherInfoListArray.add(info4);
        teacherInfoAdapter = new TeacherInformationAdapter(TeacherInformationActivity.this, teacherInfoListArray);
        mTeacherInfoListView = (ListView) findViewById(R.id.user_info);
        mTeacherInfoListView.setAdapter(teacherInfoAdapter);
        mTeacherInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {
                    Dialog dialog = onCreateDialogSingleChoice(getTeacherSexInSP());
                    dialog.show();
                } else if (position == 2) {
                    Intent intent = new Intent(TeacherInformationActivity.this, SearchSchoolActivity.class);
                    startActivityForResult(intent, 200);
                } else if (position < 4) {
                    Dialog dialog = onCreateDialogInputText(position);
                    dialog.show();
                } else {
                    //TODO bind the phone number， send sms to server, then confirm
                    TelephonyManager tMgr = (TelephonyManager) TeacherInformationActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                    String mPhoneNumber = tMgr.getLine1Number();
                    if (mPhoneNumber == "") {
                    }
                    Toast.makeText(TeacherInformationActivity.this, mPhoneNumber, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    int seletction = -1;

    public Dialog onCreateDialogSingleChoice(String sex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] array = {"男", "女"};

        builder.setTitle("选择性别")
                .setSingleChoiceItems(array, Integer.parseInt(sex) - 1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seletction = which;

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String sex = "0";
                        if (seletction == -1) {
                            sex = getTeacherSexInSP();
                        }
                        if (seletction == 0) {
                            sex = "1";
                        }
                        if (seletction == 1) {
                            sex = "2";
                        }
                        seletction = -1;


                        mNewInfo = (sex.equals("1")) ? "男" : "女";
                        ArrayList<TeacherInformationAdapter.Information> tempArray = new ArrayList<>(teacherInfoListArray);
                        Collections.copy(tempArray, teacherInfoListArray);
                        tempArray.get(1).value = sex;
                        mTeacherInfoListPosition = 1;

                        mHttpRequest.UserInfoModify(mTeacher.GetDescription().mUid,
                                tempArray.get(0).value,
                                tempArray.get(1).value,
                                tempArray.get(2).value,
                                "",
                                "",
                                tempArray.get(3).value);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    private Dialog onCreateDialogInputText(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        final EditText input = new EditText(this);
        input.setBackgroundResource(R.drawable.login_input_edittext);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        String titleArray[] = {"姓名", "性别", "学校", "手机"};
        builder.setTitle(titleArray[pos]);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pos == 3) {
                    //todo 验证手机hao
                    if (MobileTools.isMobile(input.getText().toString())) {
                        ModifySingleLine(pos, input.getText().toString());
                    } else {
                        Toast.makeText(TeacherInformationActivity.this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
                    }
                } else if (pos != 3) {
                    ModifySingleLine(pos, input.getText().toString());
                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    String result = data.getStringExtra("RESULT");
                    mNewInfo = result;
                    mTeacherInfoListPosition = 2;
                    ArrayList<TeacherInformationAdapter.Information> tempArray = new ArrayList<>(teacherInfoListArray);
                    Collections.copy(tempArray, teacherInfoListArray);
                    tempArray.get(mTeacherInfoListPosition).value = mNewInfo;

                    mHttpRequest.UserInfoModify(mTeacher.GetDescription().mUid,
                            tempArray.get(0).value,
                            tempArray.get(1).value,
                            tempArray.get(2).value,
                            "",
                            "",
                            tempArray.get(3).value);
                    break;
                default:
                    break;
            }
        }

        if (resultCode == RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }

    private void ModifySingleLine(int pos, String value) {
        mNewInfo = value;
        ArrayList<TeacherInformationAdapter.Information> tempArray = new ArrayList<>(teacherInfoListArray);
        Collections.copy(tempArray, teacherInfoListArray);
        tempArray.get(pos).value = mNewInfo;
        mTeacherInfoListPosition = pos;

        mHttpRequest.UserInfoModify(mTeacher.GetDescription().mUid,
                tempArray.get(0).value,
                tempArray.get(1).value,
                tempArray.get(2).value,
                "",
                "",
                tempArray.get(3).value);
    }


    @Override
    public void UserInfoModify(int err) {
        if (err == 1) {
            String sexCheck = "1";

            if (TextUtils.isEmpty(mNewInfo) && mTeacherInfoListPosition == 0) {
                mNewInfo = "请填写名字";
            }
            if (TextUtils.isEmpty(mNewInfo) && mTeacherInfoListPosition == 2) {
                mNewInfo = "请填写学校";
            }

            if (TextUtils.isEmpty( teacherInfoListArray.get(2).value) && mTeacherInfoListPosition == 0 ){
                teacherInfoListArray.get(2).value = "请填写学校";
            }

            if (TextUtils.isEmpty( teacherInfoListArray.get(0).value) && mTeacherInfoListPosition == 2 ){
                teacherInfoListArray.get(0).value = "请填写名字";
            }

            teacherInfoListArray.get(mTeacherInfoListPosition).value = mNewInfo;
            mTeacher.mDescription.mName = teacherInfoListArray.get(0).value;
            mTeacher.mDescription.mSex = teacherInfoListArray.get(1).value;
            mTeacher.mDescription.mSchool = teacherInfoListArray.get(2).value;
            // mTeacher.mDescription.mTeacher_number = studentInfoListArray.get(3).value;
            //  mTeacher.mDescription.mClass = studentInfoListArray.get(4).value;
            mTeacher.mDescription.mTel = teacherInfoListArray.get(3).value;
            teacherInfoAdapter.notifyDataSetChanged();
            if (teacherInfoListArray.get(1).value.equals("男"))
                sexCheck = "1";
            if (teacherInfoListArray.get(1).value.equals("女"))
                sexCheck = "2";
            setTeacherSexInSP(sexCheck);
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        }
        // 重新记录studentinfo sp
        mTeacher.SetDescriptionAndWriteToSP(this, mTeacher.mDescription);
    }

    @Override
    public void ErrorNetwork() {
        Toast.makeText(this, "网络或服务器错误！", Toast.LENGTH_SHORT).show();
    }

    private static final String SHAREDPREFERENCES_NAME = "user_name";

    private void setTeacherSexInSP(String sex) {
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("TeacherSex", sex);
        editor.apply();
    }

    private String getTeacherSexInSP() {
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, Activity.MODE_PRIVATE);
        return preferences.getString("TeacherSex", "1");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher_information, menu);
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
