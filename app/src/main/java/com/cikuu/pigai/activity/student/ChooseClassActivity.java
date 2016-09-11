package com.cikuu.pigai.activity.student;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.SharedPreferenceUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class ChooseClassActivity extends AppCompatActivity {

    EditText mClassNameEditText;
    ListView mClassListView;
    ArrayAdapter<String> mAdapter;
    ArrayList<String> mItemArray = new ArrayList<>();

    public static String SELECTED_CLASS = "其他";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_class);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mClassNameEditText = (EditText) findViewById(R.id.classEditText);
        mClassListView = (ListView) findViewById(R.id.classNameList);
        mItemArray = SharedPreferenceUtil.loadStudentClassArray(this);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, mItemArray);

        if (mItemArray.size() > 0) {
            mClassNameEditText.setText(mItemArray.get(0));
        }

        mClassListView.setAdapter(mAdapter);
        mClassListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mClassListView.setItemChecked(0, true);

        mClassListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position,
                                    long id) {
                SELECTED_CLASS = mItemArray.get(position);
                mClassNameEditText.setText(SELECTED_CLASS);
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_class, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_chooseclass) {
            SELECTED_CLASS = mClassNameEditText.getText().toString();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", SELECTED_CLASS);
            setResult(RESULT_OK, returnIntent);

            finish();
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

}
