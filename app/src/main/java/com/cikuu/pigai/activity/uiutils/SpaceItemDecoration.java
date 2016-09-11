package com.cikuu.pigai.activity.uiutils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-05-11
 * Time: 15:59
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int headerCount;

    public SpaceItemDecoration(int space, int headerCount) {
        this.space = space;
        this.headerCount = headerCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //  todo  需要注意  RecyclerView  Header 的个数
        if (parent.getChildPosition(view) > headerCount - 1)
            outRect.top = space;
    }
}