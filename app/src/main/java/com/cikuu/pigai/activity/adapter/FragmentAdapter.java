package com.cikuu.pigai.activity.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cikuu.pigai.activity.student.fragment.PlaceHolderFragment;
import com.cikuu.pigai.businesslogic.TiKuArticleCategory;

import java.util.ArrayList;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-04-19
 * Time: 11:53
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    ArrayList<TiKuArticleCategory> mTitles;

    public FragmentAdapter(FragmentManager fm, ArrayList<TiKuArticleCategory> titles) {
        super(fm);
        mTitles = titles;
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position).getCate_name();
    }

    @Override
    public Fragment getItem(int position) {

        return PlaceHolderFragment.newInstance(mTitles.get(position).getCate_id());

    }


}
