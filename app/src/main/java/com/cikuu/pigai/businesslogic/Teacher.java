package com.cikuu.pigai.businesslogic;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhai on 15/1/4.
 */
public class Teacher {

    private static Teacher mSingleInstance = null;
    public UserInformation mDescription = new UserInformation();
    private List<Article> mArticles = new ArrayList<Article>();


    public List<Article> GetArticles() {
        return mArticles;
    }

    public UserInformation GetDescription() {
        return mDescription;
    }

    private Teacher() {

    }

    public static Teacher GetInstance() {
        if (mSingleInstance == null) {
            mSingleInstance = new Teacher();
        }
        return mSingleInstance;
    }

    public void SetDescriptionAndWriteToDatabase(UserInformation description) {
        mDescription = description;
        //TODO store to database
    }

    public static final String SHAREDPREFERENCES_TEACHER = "teacher_description";

    public void SetDescriptionAndWriteToSP(Context context, UserInformation description) {
        mDescription = description;

        //store to sharedprefences
        SharedPreferences preferences = context.getSharedPreferences(SHAREDPREFERENCES_TEACHER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("UID", mDescription.mUid);
        editor.putString("NAME", mDescription.mName);
        editor.putString("SEX", mDescription.mSex);
        editor.putString("SCHOOL", mDescription.mSchool);
        editor.putString("EMAIL", mDescription.mEmail);
        editor.putString("USER_NAME", mDescription.mUser_Name);
        editor.putString("CLASS", mDescription.mClass);
        editor.putString("STUDENT_NUMBER", mDescription.mStudent_number);
        editor.putString("TEL", mDescription.mTel);
        editor.putInt("USER_TYPE", mDescription.type);
        editor.putString("BHEAD", mDescription.bHead);
        editor.putString("SHEAD", mDescription.sHead);
        editor.apply();
    }

    //TODO articles operation
    //First get articles from network, if network is not available then get from database
    public List<Article> GetArticlesFromNetworkOrDataBase() {
        GenerateArticlesFromNetwork(); //if false, then only get the old database value
        return GetArticlesFromDataBase();
    }

    //database operation
    private List<Article> GetArticlesFromDataBase() {
        return null;
    }

    //after generate articles from http , then store it to db by calling UpdateArticlesInDataBase()
    private boolean GenerateArticlesFromNetwork() {


        UpdateArticlesInDataBase();
        return true;
    }

    //store it to BD
    private void UpdateArticlesInDataBase() {

    }

    //TODO get student articles, this should be in the Article class
    public List<StudentArticle> GetStudentArticles(Article article) {
        //TODO always get from the network
        return null;
    }

}
