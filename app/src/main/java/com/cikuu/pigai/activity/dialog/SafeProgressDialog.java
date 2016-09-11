package com.cikuu.pigai.activity.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-05-23
 * Time: 14:56
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class SafeProgressDialog extends ProgressDialog {
    Activity mParentActivity;

    public SafeProgressDialog(Context context) {
        super(context);
        mParentActivity = (Activity) context;
    }

    @Override
    public void show() {
        if (isValidContext(mParentActivity)) {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        if (isValidContext(mParentActivity)) {
            super.dismiss();
        }
    }

    private boolean isValidContext(Context c) {
        try {
            Activity activity = (Activity) c;
            if (activity != null && !activity.isFinishing()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
