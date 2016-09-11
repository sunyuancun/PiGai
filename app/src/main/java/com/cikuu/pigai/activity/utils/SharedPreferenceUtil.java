package com.cikuu.pigai.activity.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cikuu.pigai.activity.UserRegisterActivity;

import java.util.ArrayList;


/**
 * Created by xuhai on 15/12/2.
 */
public class SharedPreferenceUtil {

    public static final String SHAREDPREFERENCES_NAME = "user_name";
    public static final String REMEBERPASSWORD = "REMEBERPASSWORD";
    public static final String STUDENTCLASS = "STUDENTCLASS";
    public static final String PIGAI_TOKEN = "PIGAI_TOKEN";
    public static final String PIGAI_COOKIE = "PIGAI_COOKIE";

    public static void setUserAutoLoginInSP(Boolean b, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("AutoLogin", b);
        editor.apply();
    }

    public static Boolean getUserAutoLoginInSP(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        Boolean savedUserAuto = preferences.getBoolean("AutoLogin", false);
        return savedUserAuto;
    }

    public static void setUserNamePasswordInSP(String userName, String password, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserName", userName);
        editor.putString("PassWord", password);
        editor.apply();
    }

    public static ArrayList<String> getUserNamePasswordInSP(Context context) {
        ArrayList<String> savedUser = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        savedUser.add(0, preferences.getString("UserName", ""));
        savedUser.add(1, preferences.getString("PassWord", ""));
        return savedUser;
    }

    public static void setPigaiCookieInSP(String loggedInCookie, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PIGAI_COOKIE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("COOKIE", loggedInCookie);
        editor.apply();
    }

    public static String getPigaiCookieInSP(Context context) {
        String loggedInCookie;
        SharedPreferences preferences = context.getSharedPreferences(
                PIGAI_COOKIE, Context.MODE_PRIVATE);
        loggedInCookie = preferences.getString("COOKIE", "");
        return loggedInCookie;
    }

    public static void setPigaiTokenInSP(String pigai_token, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PIGAI_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("TOKEN", pigai_token);
        editor.apply();
    }

    public static String getPigaiTokenInSP(Context context) {
        String pigai_token;
        SharedPreferences preferences = context.getSharedPreferences(
                PIGAI_TOKEN, Context.MODE_PRIVATE);
        pigai_token = preferences.getString("TOKEN", "");
        return pigai_token;
    }

    public static void setUserTypeInSP(String userType, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserType", userType);
        editor.apply();
    }

    public static String getUserTypeInSP(Context context) {
        String savedUserType;
        SharedPreferences preferences = context.getSharedPreferences(
                SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        savedUserType = preferences.getString("UserType", "");
        return savedUserType;
    }

    public static void setRemeberpasswordInSP(Boolean checked, Context context) {
        SharedPreferences remeberPasswordSP = context.getSharedPreferences(REMEBERPASSWORD, Context.MODE_PRIVATE);
        remeberPasswordSP.edit().putBoolean("ISCHECK", checked).commit();
    }

    public static Boolean getRememberpasswordInSP(Context context) {
        SharedPreferences remeberPasswordSP = context.getSharedPreferences(REMEBERPASSWORD, Context.MODE_PRIVATE);
        return remeberPasswordSP.getBoolean("ISCHECK", true);
    }

    public static boolean saveStudentClassArray(ArrayList<String> array, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(STUDENTCLASS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("MyClasses" + "_size", array.size());
        for (int i = 0; i < array.size(); i++)
            editor.putString("MyClasses" + "^" + i, array.get(i));
        return editor.commit();
    }

    public static ArrayList<String> loadStudentClassArray(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(STUDENTCLASS, Context.MODE_PRIVATE);
        int size = prefs.getInt("MyClasses" + "_size", 0);
        ArrayList<String> array = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            array.add(prefs.getString("MyClasses" + "^" + i, null));
        return array;
    }

}
