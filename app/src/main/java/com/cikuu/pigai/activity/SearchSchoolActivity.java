package com.cikuu.pigai.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.adapter.SchoolAdapter;
import com.cikuu.pigai.businesslogic.School;
import com.cikuu.pigai.dbmodel.SchoolDBManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class SearchSchoolActivity extends AppCompatActivity {
    private EditText searchSchool;
    private ListView mListView;
    private SchoolAdapter mAdapter;
    private SQLiteDatabase db;
    List<School> mSchools = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_school);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setTitle("选择学校");

        searchSchool = (EditText) findViewById(R.id.et_search);
        View.OnKeyListener enterKey = new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    Toast.makeText(SearchSchoolActivity.this, "亲，这里不让使用Enter键", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        };
        searchSchool.setOnKeyListener(enterKey);
        mListView = (ListView) findViewById(R.id.listView);
        db = SchoolDBManager.openDatabase(this);
        mSchools = findAllSchool();
        mAdapter = new SchoolAdapter(SearchSchoolActivity.this, mSchools);
        mListView.setAdapter(mAdapter);

        searchSchool.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListView.setVisibility(View.VISIBLE);
                String inputText = s.toString().trim();
                mSchools.clear();
                if (inputText.equals("")) {
                    mSchools = findAllSchool();
                } else {
                    mSchools = findMatchedSchool(inputText);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.notifyDataSetChanged();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                School school = (School) (mListView.getAdapter().getItem(position));
                String text = school.getSchool();
                searchSchool.setText(text);
                mListView.setVisibility(View.INVISIBLE);
            }
        });
    }

    //match SQLIte
    private List<School> findMatchedSchool(String inputText) {


        //todo    // TODO: 2015/11/4
        //  把   inputText   包含的“'”   换为“''”
        if (inputText.contains("'")) {
            inputText = inputText.replaceAll("'", "''");
        }

        String sql = "select * from yourSchool where school like '%" + inputText + "%' ;";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            School school = new School();
            school.setSchool(cursor.getString(cursor.getColumnIndex("school")));
            mSchools.add(school);
        }
        if (cursor != null)
            cursor.close();

        return mSchools;
    }

    //find all school limit 20
    public List<School> findAllSchool() {
        String sql = "select * from yourSchool limit 20 ";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            School school = new School();
            school.setSchool(cursor.getString(cursor.getColumnIndex("school")));
            mSchools.add(school);
        }

        if (cursor != null)
            cursor.close();

        return mSchools;
    }

    //友盟统计
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SearchSchoolActivity");
        MobclickAgent.onResume(this);
    }

    //友盟统计
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SearchSchoolActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_school, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.action_complete) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", searchSchool.getText().toString());
            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
