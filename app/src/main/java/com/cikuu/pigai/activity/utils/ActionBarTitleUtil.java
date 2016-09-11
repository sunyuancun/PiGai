package com.cikuu.pigai.activity.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cikuu.pigai.R;


/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-04-12
 * Time: 18:04
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class ActionBarTitleUtil {

    ActionBar mActionBar;
    TextView action_bar_title;
    Boolean mNeedBackMenu;

    public ActionBarTitleUtil(AppCompatActivity context, Boolean needBackMenu) {
        mActionBar = context.getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.actionbar_color)));
        mNeedBackMenu = needBackMenu;
        initView(context);
    }

    private void initView(Context context) {
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = inflater.inflate(R.layout.action_bar_title, null);
        action_bar_title = (TextView) titleView.findViewById(R.id.action_bar_title);
        configActionBar(titleView, layoutParams);

    }

    private void configActionBar(View titleView, ActionBar.LayoutParams layoutParams) {
        mActionBar.setCustomView(titleView, layoutParams);
        mActionBar.setDisplayShowHomeEnabled(false);//鍘绘帀瀵艰埅
        mActionBar.setDisplayShowTitleEnabled(true);
        if (mNeedBackMenu) {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        } else {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }
        //此行用于显示，必须在最后边
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    public void setTitleText(String text) {
        if (text != null)
            action_bar_title.setText(text);
    }

}
