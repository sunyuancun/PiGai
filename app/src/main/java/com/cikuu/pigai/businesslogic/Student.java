package com.cikuu.pigai.businesslogic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.dbmodel.StudentArticleBDHelper;
import com.cikuu.pigai.dbmodel.ormlite.StudentArticleOrmLiteSqliteOpenHelper;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Student implements VolleyRequest.SyncStudentArticleCallback {

    private static Student mSingleInstance = null;
    public UserInformation mStudentDescription = new UserInformation();
    private List<StudentArticle> mStudentArticles = new ArrayList<>();

    VolleyRequest mHttpRequest = new VolleyRequest();

    public List<StudentArticle> GetArticles() {
        return mStudentArticles;
    }

    public UserInformation GetDescription() {
        return mStudentDescription;
    }

    private Student() {
        mHttpRequest.mSyncStudentArticleCallback = this;
    }

    public static Student GetInstance() {
        if (mSingleInstance == null) {
            mSingleInstance = new Student();
        }
        return mSingleInstance;
    }

    public void SetDescriptionAndWriteToDatabase(UserInformation description) {
        mStudentDescription = description;
        //TODO store to database
    }

    public static final String SHAREDPREFERENCES_STUDENT = "student_description";

    public void SetDescriptionAndWriteToSP(Context context, UserInformation description) {
        mStudentDescription = description;
        //store to sharedprefences
        SharedPreferences preferences = context.getSharedPreferences(SHAREDPREFERENCES_STUDENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("UID", mStudentDescription.mUid);
        editor.putString("NAME", mStudentDescription.mName);
        editor.putString("SEX", mStudentDescription.mSex);
        editor.putString("SCHOOL", mStudentDescription.mSchool);
        editor.putString("EMAIL", mStudentDescription.mEmail);
        editor.putString("USER_NAME", mStudentDescription.mUser_Name);
        editor.putString("CLASS", mStudentDescription.mClass);
        editor.putString("STUDENT_NUMBER", mStudentDescription.mStudent_number);
        editor.putString("TEL", mStudentDescription.mTel);
        editor.putInt("USER_TYPE", mStudentDescription.type);
        editor.putString("BHEAD", mStudentDescription.bHead);
        editor.putString("SHEAD", mStudentDescription.sHead);
        editor.apply();

    }


    //article list -------------------------------------------------------------------------------
    //First get articles from db, if network is not available then get from network
    public List<StudentArticle> GetArticleListFromDataBaseThenSyncFromNetwork() {
        mStudentArticles = GetArticleListFromDataBase();
        GenerateArticleListFromNetwork(mStudentDescription.mUid);
        return mStudentArticles;
    }

    public void GetArticleListFromNetwork() {
        GenerateArticleListFromNetwork(mStudentDescription.mUid);
    }

    //database operation
    private List<StudentArticle> GetArticleListFromDataBase() {
        return null;
    }

    //after generate articles from http , then store it to db by calling UpdateArticlesInDataBase()
    private void GenerateArticleListFromNetwork(int uid) {
        mHttpRequest.SyncStudentArticleList();
    }

    public void OnSyncArticleListFromNetwork(List<StudentArticle> articles) {
        mStudentArticles.clear();
        mStudentArticles.addAll(articles);
        //UpdateArticleListInDataBase();
        //TODO update UI
    }

    public void ErrorNetwork() {

    }

    private void UpdateArticleListInDataBase() {
        //TODO based on the articleID to update the DB

    }


    //single article detail  ---------------------------------------------------------------------
//    public StudentArticle GetOneArticleDetailFromDataBaseThenSyncFromNetwork(long articleId) {
//        StudentArticle articleDetail = GetOneArticleDetailFromDataBase(articleId);
//        GenerateOneArticleDetailFromNetwork(articleId);
//        return articleDetail;
//    }
//
//    public void GetOneArticleDetailFromNetwork(long articleId) {
//        GenerateOneArticleDetailFromNetwork(articleId);
//    }

//    private StudentArticle GetOneArticleDetailFromDataBase(long articleId) {
//        return null;
//    }
//
//    private void GenerateOneArticleDetailFromNetwork(long articleId) {
//        mHttpRequest.SyncOneStudentArticleDetail();
//    }

    public void OnSyncOneArticleDetailFromNetwork(StudentArticle article) {
        //UpdateOneArticleDetailInDataBase(article);
        //TODO update the UI
    }

    private void UpdateOneArticleDetailInDataBase(StudentArticle article) {
        //TODO based on the articleID to update the DB

    }


    //only store the draft article---------------------------------------------------------------
    public long StoreStudentArticleDraftInDataBase(StudentArticle article) {
        StudentArticleBDHelper db =
                new StudentArticleBDHelper(AppController.getInstance().getApplicationContext());

        long id = db.createArticle(article);
        Log.d("Article Count", "Article Count: " + db.getAllArticles().size());

        db.closeDB();
        return id;
    }

//    public long StoreStudentArticleDraftInDataBase(StudentArticle article) {
//        long id = 0;
//        try {
//            StudentArticleOrmLiteSqliteOpenHelper db = new StudentArticleOrmLiteSqliteOpenHelper(AppController.getInstance().getApplicationContext());
//            id = db.getStudentAticleDao().create(article);
//            db.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return id;
//    }

    public StudentArticle GetStudentArticleDraftFromDataBase(long articleId) {
        StudentArticleBDHelper db =
                new StudentArticleBDHelper(AppController.getInstance().getApplicationContext());

        StudentArticle article = db.getArticle(articleId);

        db.closeDB();
        return article;
    }

//    public StudentArticle GetStudentArticleDraftFromDataBase(long articleId) {
//
//        try {
//            StudentArticleOrmLiteSqliteOpenHelper db = new StudentArticleOrmLiteSqliteOpenHelper(AppController.getInstance().getApplicationContext());
//            QueryBuilder<StudentArticle, Integer> queryBuilder = db.getStudentAticleDao().queryBuilder();
//            queryBuilder.where().eq("mArticleId", articleId);
//            List<StudentArticle> studentArticles = queryBuilder.query();
//            db.close();
//
//            if (studentArticles != null && studentArticles.size() > 0) {
//                return studentArticles.get(0);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    private List<StudentArticle> mAllDraftStudentArticles = new ArrayList<>();

    public List<StudentArticle> GetAllDraftStudentArticlesFromDataBase() {

        try {
            StudentArticleBDHelper db =
                    new StudentArticleBDHelper(AppController.getInstance().getApplicationContext());

            mAllDraftStudentArticles = db.getAllArticles();

            db.closeDB();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mAllDraftStudentArticles;
    }

//    public List<StudentArticle> GetAllDraftStudentArticlesFromDataBase() {
//
//        try {
//            StudentArticleOrmLiteSqliteOpenHelper db = new StudentArticleOrmLiteSqliteOpenHelper(AppController.getInstance().getApplicationContext());
//            QueryBuilder<StudentArticle, Integer> queryBuilder = db.getStudentAticleDao().queryBuilder();
//            queryBuilder.where().eq("mNetworkOrDatabase", VolleyRequest.EDatabaseArticle);
//            mAllDraftStudentArticles = queryBuilder.query();
//            db.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return mAllDraftStudentArticles;
//    }

    public long UpdateStudentArticleInDataBase(StudentArticle article) {
        StudentArticleBDHelper db =
                new StudentArticleBDHelper(AppController.getInstance().getApplicationContext());

        int ret = db.updateStudentArticle(article);

        db.closeDB();

        return ret;
    }

//    public long UpdateStudentArticleInDataBase(StudentArticle article) {
//        long id = 0;
//        try {
//            StudentArticleOrmLiteSqliteOpenHelper db = new StudentArticleOrmLiteSqliteOpenHelper(AppController.getInstance().getApplicationContext());
//            UpdateBuilder updateBuilder = db.getStudentAticleDao().updateBuilder();
//            updateBuilder.where().eq("mArticleId", article.mArticleId);
//            updateBuilder.updateColumnValue("mTitle", article.mTitle);
//            updateBuilder.updateColumnValue("mContent", article.mContent);
//            updateBuilder.updateColumnValue("mEndtime", article.mEndtime);
//            updateBuilder.updateColumnValue("mSubmittedTime", article.mSubmittedTime);
//            updateBuilder.update();
//            db.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return id;
//    }

    public long UpdateStudentArticleByEassayIdInDataBase(StudentArticle article) {
        StudentArticleBDHelper db =
                new StudentArticleBDHelper(AppController.getInstance().getApplicationContext());
        int ret = db.updateStudentArticleByEassayId(article);

        db.closeDB();

        return ret;
    }

//    public long UpdateStudentArticleByEassayIdInDataBase(StudentArticle article) {
//        long id = 0;
//        try {
//            StudentArticleOrmLiteSqliteOpenHelper db = new StudentArticleOrmLiteSqliteOpenHelper(AppController.getInstance().getApplicationContext());
//            UpdateBuilder updateBuilder = db.getStudentAticleDao().updateBuilder();
//            updateBuilder.where().eq("mEssayId", article.mEssayId);
//            updateBuilder.updateColumnValue("mTitle", article.mTitle);
//            updateBuilder.updateColumnValue("mContent", article.mContent);
//            updateBuilder.updateColumnValue("mEndtime", article.mEndtime);
//            updateBuilder.updateColumnValue("mSubmittedTime", article.mSubmittedTime);
//            updateBuilder.update();
//            db.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return id;
//
//    }

    public void DeleteAllDraftStudentArticlesFromDataBase() {
        StudentArticleBDHelper db =
                new StudentArticleBDHelper(AppController.getInstance().getApplicationContext());

        db.deleteAllStudentArticle();

        db.closeDB();
    }


    public void DeleteStudentArticleDraftFromDataBase(long articleId) {
        StudentArticleBDHelper db =
                new StudentArticleBDHelper(AppController.getInstance().getApplicationContext());

        db.deleteStudentArticle(articleId);

        db.closeDB();
    }

//    public void DeleteStudentArticleDraftFromDataBase(long articleId) {
//        try {
//            StudentArticleOrmLiteSqliteOpenHelper db = new StudentArticleOrmLiteSqliteOpenHelper(AppController.getInstance().getApplicationContext());
//            DeleteBuilder<StudentArticle, Integer> deleteBuilder = db.getStudentAticleDao().deleteBuilder();
//            deleteBuilder.where().eq("mArticleId", articleId);
//            deleteBuilder.delete();
//            db.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//

    public StudentArticle GetStudentArticleDraftFromDataBaseByEssayIdWhenSelfCreateRid(long essayId) {
        StudentArticleBDHelper db =
                new StudentArticleBDHelper(AppController.getInstance().getApplicationContext());

        StudentArticle article = db.getEssayIdArticle(essayId);

        db.closeDB();
        return article;
    }

//    public StudentArticle GetStudentArticleDraftFromDataBaseByEssayIdWhenSelfCreateRid(long essayId) {
//        try {
//            StudentArticleOrmLiteSqliteOpenHelper db = new StudentArticleOrmLiteSqliteOpenHelper(AppController.getInstance().getApplicationContext());
//            QueryBuilder<StudentArticle, Integer> queryBuilder = db.getStudentAticleDao().queryBuilder();
//            queryBuilder.where().eq("mEssayId", essayId);
//            List<StudentArticle> studentArticles = queryBuilder.query();
//            db.close();
//
//            if (studentArticles != null && studentArticles.size() > 0) {
//                return studentArticles.get(0);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

        public void DeleteStudentArticleByEassayIdDraftFromDataBase(long essayId) {
        StudentArticleBDHelper db =
                new StudentArticleBDHelper(AppController.getInstance().getApplicationContext());

        db.deleteStudentArticleByEidWhenSelfCreateRid(essayId);

        db.closeDB();
    }
//    public void DeleteStudentArticleByEassayIdDraftFromDataBase(long essayId) {
//        try {
//            StudentArticleOrmLiteSqliteOpenHelper db = new StudentArticleOrmLiteSqliteOpenHelper(AppController.getInstance().getApplicationContext());
//            DeleteBuilder<StudentArticle, Integer> deleteBuilder = db.getStudentAticleDao().deleteBuilder();
//            deleteBuilder.where().eq("mEssayId", essayId);
//            deleteBuilder.delete();
//            db.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }
}
