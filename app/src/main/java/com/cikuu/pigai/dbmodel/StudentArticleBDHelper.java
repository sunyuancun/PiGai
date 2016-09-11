package com.cikuu.pigai.dbmodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.StudentArticle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhai on 15/1/4.
 */
public class StudentArticleBDHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "StudentArticleBDHelper";

    // Database Version
    private static final int DATABASE_VERSION = 19;

    // BD Names  studentArticle_uid
    private static int mUid = Student.GetInstance().mStudentDescription.mUid;
    public static String DATABASE_NAME = "cikuu_db_" + String.valueOf(mUid);

    private static final String TABLE_STUDENTARTICLE = "studentArticle";

    // StudentTable Table - column names
    //private static final String KEY_ID = "id";
    private static final String KEY_ESSAYID = "essayId";
    private static final String KEY_UID = "uid";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_ENDTIME = "endTime";
    private static final String KEY_STATUS = "status";
    private static final String KEY_REQUIREMENT = "requirement";
    private static final String KEY_COUNT = "count";
    private static final String KEY_SUBMITTEDTIME = "submittedTime";
    private static final String KEY_NETWORKORDATABASE = "networkOrDatabase";
    private static final String KEY_ARTICLEID = "articleId";
    private static final String KEY_TEACHERNAME = "teachername";
    // Table Create Statements
    // StudentArticle table create statement
    private static final String CREATE_TABLE_STUDENTARTICLE = "CREATE TABLE "
            + TABLE_STUDENTARTICLE + "(" +
//            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_ARTICLEID + " INTEGER ," +
            KEY_ESSAYID + " INTEGER," +
            KEY_UID + " INTEGER," +
            KEY_TITLE + " TEXT," +
            KEY_CONTENT + " TEXT," +
            KEY_ENDTIME + " TEXT," +
            KEY_STATUS + " INTEGER," +
            KEY_COUNT + " INTEGER," +
            KEY_SUBMITTEDTIME + " TEXT," +
            KEY_NETWORKORDATABASE + " INTEGER," +
            KEY_TEACHERNAME + " TEXT," +
            KEY_REQUIREMENT + " TEXT" + ")";

    public StudentArticleBDHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {

        //TODO strange, the database is not set to be enabled foreign_key
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        String sql = CREATE_TABLE_STUDENTARTICLE;
        db.execSQL(CREATE_TABLE_STUDENTARTICLE);
    }

    /**
     * 此方法调用时机 ： app版本升级时，检查该数据库版本，数据库版本升级（newVersion  > oldVersion ）,才会调用
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTARTICLE);
        // create new tables
        onCreate(db);

//      if (oldVersion == 19) {
//            db.execSQL("ALTER TABLE studentArticle RENAME TO studentArticle_temp");
//            db.execSQL(CREATE_TABLE_STUDENTARTICLE);
//            db.execSQL("insert into studentArticle( essayId, uid, title,content,endTime,status,requirement,count,submittedTime,networkOrDatabase,articleId,teachername ) "
//                    + "select essayId, uid, title,content,endTime,status,requirement,count,submittedTime,networkOrDatabase,articleId,teachername from studentArticle_temp");
//            db.execSQL("DROP TABLE IF EXISTS studentArticle_temp");
//        }
//
//        if (oldVersion == 19 && newVersion == 20) {
//            //todo 升级为20时需要加的逻辑
//        }

    }

    // ------------------------ "todos" table methods ----------------//

    /*
     * Creating a article
     */
    public long createArticle(StudentArticle article) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ESSAYID, article.getEssayId());
        values.put(KEY_ARTICLEID, article.getArticleId());
        values.put(KEY_UID, article.getUid());
        values.put(KEY_TITLE, article.getTitle());
        values.put(KEY_CONTENT, article.getContent());
        values.put(KEY_ENDTIME, article.getEndTime());
        values.put(KEY_STATUS, article.getStatus());
        values.put(KEY_REQUIREMENT, article.getRequirement());
        values.put(KEY_COUNT, article.getCount());
        values.put(KEY_SUBMITTEDTIME, article.getSubmittedTime());
        values.put(KEY_NETWORKORDATABASE, article.getNetworkOrDatabase());
        values.put(KEY_TEACHERNAME, article.getmTeacherName());
        // insert row
        long essay_id = db.insert(TABLE_STUDENTARTICLE, null, values);

        return essay_id;
    }

    /*
     * get single article
     */
    public StudentArticle getArticle(long articleId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_STUDENTARTICLE + " WHERE "
                + KEY_ARTICLEID + " = " + articleId;

        Log.e(LOG, selectQuery);
        StudentArticle article = null;
        Cursor c = db.rawQuery(selectQuery, null);

        try {
            if (c.moveToFirst()) {
                article = new StudentArticle();
                article.setEssayId(c.getInt(c.getColumnIndex(KEY_ESSAYID)));
                article.setArticleId(c.getInt(c.getColumnIndex(KEY_ARTICLEID)));
                article.setUid(c.getInt(c.getColumnIndex(KEY_UID)));
                article.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                article.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));
                article.setEndTime(c.getString(c.getColumnIndex(KEY_ENDTIME)));
                article.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                article.setRequirement(c.getString(c.getColumnIndex(KEY_REQUIREMENT)));
                article.setCount(c.getInt(c.getColumnIndex(KEY_COUNT)));
                article.setSubmittedTime(c.getString(c.getColumnIndex(KEY_SUBMITTEDTIME)));
                article.setNetworkOrDatabase(c.getInt(c.getColumnIndex(KEY_NETWORKORDATABASE)));
                article.setmTeacherName(c.getString(c.getColumnIndex(KEY_TEACHERNAME)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
            return article;
        }
    }

    /**
     * getting all articles
     */
    public List<StudentArticle> getAllArticles() {
        List<StudentArticle> articles = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_STUDENTARTICLE;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                StudentArticle article = new StudentArticle();
                article.setEssayId(c.getInt(c.getColumnIndex(KEY_ESSAYID)));
                article.setArticleId(c.getInt(c.getColumnIndex(KEY_ARTICLEID)));
                article.setUid(c.getInt(c.getColumnIndex(KEY_UID)));
                article.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                article.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));
                article.setEndTime(c.getString(c.getColumnIndex(KEY_ENDTIME)));
                article.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                article.setRequirement(c.getString(c.getColumnIndex(KEY_REQUIREMENT)));

                article.setCount(c.getInt(c.getColumnIndex(KEY_COUNT)));
                article.setSubmittedTime(c.getString(c.getColumnIndex(KEY_SUBMITTEDTIME)));
                article.setNetworkOrDatabase(c.getInt(c.getColumnIndex(KEY_NETWORKORDATABASE)));
                article.setmTeacherName(c.getString(c.getColumnIndex(KEY_TEACHERNAME)));
                articles.add(article);

            } while (c.moveToNext());
        }

        return articles;
    }

    /*
     * getting article count
     */
    public int getStudentArticlesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_STUDENTARTICLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /*
     * Updating a article
     */
    public int updateStudentArticle(StudentArticle article) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_UID, article.getUid());
        values.put(KEY_ESSAYID, article.getEssayId());
        values.put(KEY_TITLE, article.getTitle());
        values.put(KEY_CONTENT, article.getContent());
        values.put(KEY_ENDTIME, article.getEndTime());
        values.put(KEY_STATUS, article.getStatus());
        values.put(KEY_REQUIREMENT, article.getRequirement());

        values.put(KEY_COUNT, article.getCount());
        values.put(KEY_SUBMITTEDTIME, article.getSubmittedTime());
        values.put(KEY_NETWORKORDATABASE, article.getNetworkOrDatabase());
        values.put(KEY_TEACHERNAME, article.getmTeacherName());
        // updating row
        return db.update(TABLE_STUDENTARTICLE, values, KEY_ARTICLEID + " = ?",
                new String[]{String.valueOf(article.getArticleId())});
    }

    /*
     * Deleting a article
     */
    public void deleteStudentArticle(long articleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTARTICLE, KEY_ARTICLEID + " = ?",
                new String[]{String.valueOf(articleId)});
    }

    public void deleteAllStudentArticle() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_STUDENTARTICLE);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public int updateStudentArticleByEassayId(StudentArticle article) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_UID, article.getUid());
        values.put(KEY_ESSAYID, article.getEssayId());
        values.put(KEY_TITLE, article.getTitle());
        values.put(KEY_CONTENT, article.getContent());
        values.put(KEY_ENDTIME, article.getEndTime());
        values.put(KEY_STATUS, article.getStatus());
        values.put(KEY_REQUIREMENT, article.getRequirement());

        values.put(KEY_COUNT, article.getCount());
        values.put(KEY_SUBMITTEDTIME, article.getSubmittedTime());
        values.put(KEY_NETWORKORDATABASE, article.getNetworkOrDatabase());
        values.put(KEY_TEACHERNAME, article.getmTeacherName());
        // updating row
        return db.update(TABLE_STUDENTARTICLE, values, KEY_ESSAYID + " = ?",
                new String[]{String.valueOf(article.getEssayId())});
    }

    public StudentArticle getEssayIdArticle(long essayId) {
        StudentArticle article = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_STUDENTARTICLE + " WHERE "
                + KEY_ESSAYID + " = " + essayId;
        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        try {
            if (c.moveToFirst()) {
                article = new StudentArticle();
                article.setEssayId(c.getInt(c.getColumnIndex(KEY_ESSAYID)));
                article.setArticleId(c.getInt(c.getColumnIndex(KEY_ARTICLEID)));
                article.setUid(c.getInt(c.getColumnIndex(KEY_UID)));
                article.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                article.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));
                article.setEndTime(c.getString(c.getColumnIndex(KEY_ENDTIME)));
                article.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                article.setRequirement(c.getString(c.getColumnIndex(KEY_REQUIREMENT)));
                article.setCount(c.getInt(c.getColumnIndex(KEY_COUNT)));
                article.setSubmittedTime(c.getString(c.getColumnIndex(KEY_SUBMITTEDTIME)));
                article.setNetworkOrDatabase(c.getInt(c.getColumnIndex(KEY_NETWORKORDATABASE)));
                article.setmTeacherName(c.getString(c.getColumnIndex(KEY_TEACHERNAME)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
            return article;
        }
    }

    public void deleteStudentArticleByEidWhenSelfCreateRid(long essayId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENTARTICLE, KEY_ESSAYID + " = ?",
                new String[]{String.valueOf(essayId)});
    }
}
