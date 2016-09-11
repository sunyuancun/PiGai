package com.cikuu.pigai.activity.student;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
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
import com.cikuu.pigai.activity.adapter.StudentInformationAdapter;
import com.cikuu.pigai.activity.utils.ConstConfig;
import com.cikuu.pigai.activity.utils.ImageTools;
import com.cikuu.pigai.activity.utils.MobileTools;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class StudentInformationActivity extends ActionBarActivity implements VolleyRequest.UserInfoModifyCallback {
    private ListView mStudentInfoListView;
    private VolleyRequest mHttpRequest;
    private Student mStudent;

    private ArrayList<StudentInformationAdapter.Information> studentInfoListArray;
    private StudentInformationAdapter studentInfoAdapter;

    private String mNewInfo;  //new item value
    private int mStudentInfoListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_information);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mUserInfoModifyCallback = this;
        mStudent = Student.GetInstance();
        mStudentInfoListView = (ListView) findViewById(R.id.user_info);
        studentInfoListArray = new ArrayList<StudentInformationAdapter.Information>();
        StudentInformationAdapter.Information info1 = new StudentInformationAdapter.Information("姓名(必填)", mStudent.GetDescription().mName);
        StudentInformationAdapter.Information info2 = new StudentInformationAdapter.Information("性别", mStudent.GetDescription().mSex);
        StudentInformationAdapter.Information info3 = new StudentInformationAdapter.Information("学校(必填)", mStudent.GetDescription().mSchool);
        StudentInformationAdapter.Information info4 = new StudentInformationAdapter.Information("学号", mStudent.GetDescription().mStudent_number);
        StudentInformationAdapter.Information info5 = new StudentInformationAdapter.Information("班级(必填)", mStudent.GetDescription().mClass);
        StudentInformationAdapter.Information info6 = new StudentInformationAdapter.Information("手机", mStudent.GetDescription().mTel);
        studentInfoListArray.add(info1);
        studentInfoListArray.add(info2);
        studentInfoListArray.add(info3);
        studentInfoListArray.add(info4);
        studentInfoListArray.add(info5);
        studentInfoListArray.add(info6);
        studentInfoAdapter = new StudentInformationAdapter(this, studentInfoListArray);
        mStudentInfoListView.setAdapter(studentInfoAdapter);
        mStudentInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {
                    Dialog dialog = onCreateDialogSingleChoice(getUserSexInSP());
                    dialog.show();
                } else if (position == 2) {
                    Intent intent = new Intent(StudentInformationActivity.this, SearchSchoolActivity.class);
                    startActivityForResult(intent, 100);
                } else if (position == 4) {
                    Intent intent = new Intent(StudentInformationActivity.this, ChooseClassActivity.class);
                    startActivityForResult(intent, 101);
                } else if (position < 6) {
                    Dialog dialog = onCreateDialogInputText(position);
                    dialog.show();
                } else {
                    //TODO bind the phone number， send sms to server, then confirm
                    TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String mPhoneNumber = tMgr.getLine1Number();
                    if (mPhoneNumber == "") {
                    }
                    Toast.makeText(StudentInformationActivity.this, mPhoneNumber, Toast.LENGTH_SHORT).show();
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
                            sex = getUserSexInSP();
                        }
                        if (seletction == 0) {
                            sex = "1";
                        }
                        if (seletction == 1) {
                            sex = "2";
                        }
                        seletction = -1;

                        mNewInfo = (sex.equals("1")) ? "男" : "女";
                        ArrayList<StudentInformationAdapter.Information> tempArray = new ArrayList<StudentInformationAdapter.Information>(studentInfoListArray);
                        Collections.copy(tempArray, studentInfoListArray);
                        tempArray.get(1).value = sex;
                        mStudentInfoListPosition = 1;

                        mHttpRequest.UserInfoModify(mStudent.GetDescription().mUid,
                                tempArray.get(0).value,
                                tempArray.get(1).value,
                                tempArray.get(2).value,
                                tempArray.get(3).value,
                                tempArray.get(4).value,
                                tempArray.get(5).value);
//                       ModifySingleLine(1,  sex);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(StudentInformationActivity.this);

        final EditText input = new EditText(StudentInformationActivity.this);
        // input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        String titleArray[] = {"姓名", "性别", "学校", "学号", "班级", "手机"};
        builder.setTitle(titleArray[pos]);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pos == 5) {
                    //todo 验证手机hao
                    if (MobileTools.isMobile(input.getText().toString())) {
                        ModifySingleLine(pos, input.getText().toString());
                    } else {
                        Toast.makeText(StudentInformationActivity.this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
                    }
                }
                if (pos != 5) {
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

    private void ModifySingleLine(int pos, String value) {
        mNewInfo = value;
        mStudentInfoListPosition = pos;
        ArrayList<StudentInformationAdapter.Information> tempArray = new ArrayList<StudentInformationAdapter.Information>(studentInfoListArray);
        Collections.copy(tempArray, studentInfoListArray);
        tempArray.get(mStudentInfoListPosition).value = mNewInfo;

        mHttpRequest.UserInfoModify(mStudent.GetDescription().mUid,
                tempArray.get(0).value,
                tempArray.get(1).value,
                tempArray.get(2).value,
                tempArray.get(3).value,
                tempArray.get(4).value,
                tempArray.get(5).value);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("RESULT");
                    ModifySingleLine(2, result);
                }
                if (resultCode == RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;

            case 101:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("RESULT");
                    ModifySingleLine(4, result);
                }
                if (resultCode == RESULT_CANCELED) {
                    //Write your code if there's no result
                }
                break;

            default:
                break;

        }
    }


    private static final String SHAREDPREFERENCES_NAME = "user_name";

    private void setUserSexInSP(String sex) {
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserSex", sex);
        editor.apply();
    }

    private String getUserSexInSP() {
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, Activity.MODE_PRIVATE);
        return preferences.getString("UserSex", "1");
    }


    @Override
    public void UserInfoModify(int err) {
        String sexCheck = "1";
        if (err == 1) {

            if (TextUtils.isEmpty(mNewInfo) && mStudentInfoListPosition == 0) {
                mNewInfo = "请填写名字";
            }
            if (TextUtils.isEmpty(mNewInfo) && mStudentInfoListPosition == 2) {
                mNewInfo = "请填写学校";
            }

            if (TextUtils.isEmpty(studentInfoListArray.get(2).value) && mStudentInfoListPosition == 0) {
                studentInfoListArray.get(2).value = "请填写学校";
            }

            if (TextUtils.isEmpty(studentInfoListArray.get(0).value) && mStudentInfoListPosition == 2) {
                studentInfoListArray.get(0).value = "请填写名字";
            }

            studentInfoListArray.get(mStudentInfoListPosition).value = mNewInfo;
            mStudent.mStudentDescription.mName = studentInfoListArray.get(0).value;
            mStudent.mStudentDescription.mSex = studentInfoListArray.get(1).value;
            mStudent.mStudentDescription.mSchool = studentInfoListArray.get(2).value;
            mStudent.mStudentDescription.mStudent_number = studentInfoListArray.get(3).value;
            mStudent.mStudentDescription.mClass = studentInfoListArray.get(4).value;
            mStudent.mStudentDescription.mTel = studentInfoListArray.get(5).value;
            studentInfoAdapter.notifyDataSetChanged();
            if (studentInfoListArray.get(1).value.equals("男"))
                sexCheck = "1";
            if (studentInfoListArray.get(1).value.equals("女"))
                sexCheck = "2";
            setUserSexInSP(sexCheck);
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        }
        // 重新记录studentinfo sp
        mStudent.SetDescriptionAndWriteToSP(this, mStudent.mStudentDescription);
    }

    @Override
    public void ErrorNetwork() {
        Toast store = Toast.makeText(this, "网络或服务器错误！", Toast.LENGTH_LONG);
        store.show();
    }

    //友盟统计
    @Override
    protected void onResume() {
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
        getMenuInflater().inflate(R.menu.menu_student_information, menu);
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
