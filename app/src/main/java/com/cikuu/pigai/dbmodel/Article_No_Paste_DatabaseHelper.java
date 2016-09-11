package com.cikuu.pigai.dbmodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cikuu.pigai.businesslogic.StudentArticle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/28.
 */
public class Article_No_Paste_DatabaseHelper extends SQLiteOpenHelper {

    private static Article_No_Paste_DatabaseHelper mInstance = null;

    /**
     * 数据库名称
     **/

    public static final String DATABASE_NAME = "Article_No_Paste.db";

    /**
     * 数据库版本号
     **/
    private static final int DATABASE_VERSION = 1;


    /**
     * 数据库SQL语句 添加一个表
     **/
    private static final String NAME_TABLE_CREATE = "create table article_nopaste ("
            + "id integer primary key autoincrement, "
            + "articleId integer, "
            + "nopaste integer)";

    public Article_No_Paste_DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static synchronized Article_No_Paste_DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Article_No_Paste_DatabaseHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**向数据中添加表**/
        db.execSQL(NAME_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /*
    * Creating a article
    */
    public void createArticleId(StudentArticle article) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("articleId", article.getArticleId());
            values.put("nopaste", article.mNo_paste);

            String selectQuery = "select  nopaste from  article_nopaste where articleId = " + article.getArticleId();
            Cursor c = db.rawQuery(selectQuery, null);
            if (c != null && c.getCount() > 0) {
                //update row
                db.update("article_nopaste", values, " articleId = ?", new String[]{String.valueOf(article.getArticleId())});
            } else {
                // insert row
                db.insert("article_nopaste", null, values);
            }

            if (c != null) {
                c.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getting all todos
     */
    public int getArticleNoPaste(StudentArticle article) {
        String selectQuery = "select  nopaste from  article_nopaste where articleId = " + article.getArticleId();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        int nopaste = -1;
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            int columIndex = c.getColumnIndex("nopaste");
            nopaste = c.getInt(columIndex);
        }
        if (c != null) {
            c.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }

        return nopaste;
    }

}
