package com.cikuu.pigai.activity.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2015-09-02
 * Time: 17:18
 * Protect: PiGai
 */
public class AppVisionTool {

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        String version = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            //// TODO:  when  update  version  ,  modify   it !
            version = "1.8";
        } finally {
            return "V" + version;
        }
    }


}