package com.cikuu.pigai.activity.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2015-11-02
 * Time: 10:48
 * Protect: PiGai_v1.3
 */
public class KeyBoardUtil {

    public static void hideSoftInputFromWindow(Activity activity) {
        try {
            View view = activity.getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
