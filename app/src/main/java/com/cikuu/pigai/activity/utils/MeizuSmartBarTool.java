package com.cikuu.pigai.activity.utils;

import android.os.Build;

import java.lang.reflect.Method;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2015-11-11
 * Time: 16:43
 * Protect: PiGai_develop_Article
 */
public class MeizuSmartBarTool {

    public static boolean hasSmartBar() {
        Method method = null;
        try {
            method = Class.forName("android.os.Build").getMethod("hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.DEVICE.equals("mx2")) {
            return true;
        } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
            return false;
        }
        return false;
    }

}
