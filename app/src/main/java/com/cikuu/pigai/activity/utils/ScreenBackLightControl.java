package com.cikuu.pigai.activity.utils;

import android.app.Activity;
import android.view.WindowManager;

public class ScreenBackLightControl {

    private static boolean mBackLightOn;

    public static void SetBackLightOn(Activity activity, boolean onOrOff) {

        if (onOrOff) {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.screenBrightness = 1;
            activity.getWindow().setAttributes(params);
        } else {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            activity.getWindow().setAttributes(params);
        }

        mBackLightOn = onOrOff;
    }

    public boolean GetBackLightStatus() {
        return mBackLightOn;
    }

}
