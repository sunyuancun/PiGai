package com.cikuu.pigai.dbmodel.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-02-18
 * Time: 14:24
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class StudentArticleOrmLiteSqliteOpenHelper extends OrmLiteSqliteOpenHelper {
    private static int mUid = Student.GetInstance().mStudentDescription.mUid;
    public static String DATABASE_NAME = "cikuu_student_db_" + String.valueOf(mUid);
    private static final int DATABASE_VERSION = 1;
    private Dao<StudentArticle, Integer> StudentArticleDao = null;


    /**
     * 创建数据库  ：（注意：在这一定不能使用单例模式 ，为了加载不同的数据库DATABASE_NAME）
     *
     * @param context
     */
    public StudentArticleOrmLiteSqliteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 初始化数据库，创建表等数据
     *
     * @param sqLiteDatabase
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, StudentArticle.class);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(StudentArticleOrmLiteSqliteOpenHelper.class.getName(), "Can't create database", e);
        }
    }

    /**
     * 更新数据库  ：   调用时机为：   app版本升级时，数据库版本（DATABASE_VERSION）升级了才会调用 ，否则不会调用
     *
     * @param sqLiteDatabase
     * @param connectionSource
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
//        //数据库版本发生变化时的操作
//        int upgradeVersion = oldVersion;
//
//        if (1 == upgradeVersion) {
//            // Create table C
//            String sql = "CREATE TABLE ...";
//            sqLiteDatabase.execSQL(sql);
//            upgradeVersion = 2;
//        }
//
//        if (upgradeVersion != newVersion) {
//            // Drop tables
//            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS StudentArticle ");
//            // Create tables
//            onCreate(sqLiteDatabase);
//        }
    }

    public Dao<StudentArticle, Integer> getStudentAticleDao() throws SQLException {
        try {
            if (StudentArticleDao == null) {
                StudentArticleDao = getDao(StudentArticle.class);
            }
            return StudentArticleDao;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
