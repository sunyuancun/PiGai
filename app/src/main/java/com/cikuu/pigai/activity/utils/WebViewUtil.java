package com.cikuu.pigai.activity.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-03-30
 * Time: 17:16
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class WebViewUtil {

    public static void setCookieToWebView(Context context,String url, String login_cookie) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, login_cookie);
        Log.e("---cookie---", login_cookie);
        CookieSyncManager.getInstance().sync();
    }
}
