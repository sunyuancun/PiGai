package com.cikuu.pigai.dbmodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.cikuu.pigai.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/1/22.
 */
public class SchoolDBManager {
    private static SQLiteDatabase database;
    public static final String TAG = "SchoolDBManager";
    public static final String DATABASE_FILENAME = "school.s3db"; // 这个是DB文件名字
    public static final String PACKAGE_NAME = "com.cikuu.pigai"; // 这个是自己项目包路径
    public static final String DATABASE_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME; // 获取存储位置地址

    public static SQLiteDatabase openDatabase(Context context) {
        try {
            String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (!(new File(databaseFilename)).exists()) {
                InputStream is = context.getClass().getClassLoader().getResourceAsStream("assets/school.s3db");
//                InputStream is = context.getResources().openRawResource(R.raw.school);
                FileOutputStream fos = new FileOutputStream(databaseFilename);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            database = SQLiteDatabase.openOrCreateDatabase(
                    databaseFilename, null);
            return database;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "database is null and not open");
        }
        return null;
    }
}
