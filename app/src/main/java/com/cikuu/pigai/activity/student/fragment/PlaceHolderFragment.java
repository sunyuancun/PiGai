package com.cikuu.pigai.activity.student.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.adapter.TiKuArticleListByOneCateInStudentAdapter;
import com.cikuu.pigai.activity.student.StudentArticleStartWritingActivity;
import com.cikuu.pigai.activity.utils.SidTokenTool;
import com.cikuu.pigai.businesslogic.ArticleRequirement;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.businesslogic.TiKuArticleByOneCategory;
import com.cikuu.pigai.businesslogic.TiKuArticleCategory;
import com.cikuu.pigai.dbmodel.Article_No_Paste_DatabaseHelper;
import com.cikuu.pigai.httprequest.VolleyRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PlaceHolderFragment extends BaseFragment implements VolleyRequest.GetTiKuArticleListByOneCidCallback, VolleyRequest.ArticleRequirementCallback {


    private static final String CID_CODE = "cid";
    private int mStart = 0;

    /**
     * 每次取10条
     */
    private int mStep = 10;

    /**
     * 下拉刷新标志位
     */
    private Boolean downToRefresh = false;

    /**
     * 双击事件记录最近一次点击的时间
     */
    private long lastClickTime = 0;

    SwipeRefreshLayout swipeRefreshLayout;
    ListView mListView;

    ProgressDialog pDialog;
    VolleyRequest mHttpRequest;
    ArrayList<TiKuArticleByOneCategory> mTiKuArticleCategoryList = new ArrayList<>();
    TiKuArticleListByOneCateInStudentAdapter mTiKuArticleListByOneCateInStudentAdapter;

    public PlaceHolderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceHolderFragment newInstance(int sectionNumber) {
        PlaceHolderFragment fragment = new PlaceHolderFragment();
        Bundle args = new Bundle();
        args.putInt(CID_CODE, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        return rootView;
    }

    @Override
    protected void initData() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("载入中...");
        pDialog.setCanceledOnTouchOutside(false);
        showProgressDialog();

        int tikuArticleCid = getArguments().getInt(CID_CODE);
        int uid = Student.GetInstance().mStudentDescription.mUid;
        String sid = SidTokenTool.getSidForGetTikuArticleListByOneCate(uid, tikuArticleCid);

        mTiKuArticleListByOneCateInStudentAdapter = new TiKuArticleListByOneCateInStudentAdapter(getActivity(), mTiKuArticleCategoryList);
        mListView.setAdapter(mTiKuArticleListByOneCateInStudentAdapter);
        onItemClick();
        setUpAndDownToRefresh(uid, tikuArticleCid, sid);

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mArticleRequirementCallback = this;
        mHttpRequest.mGetTiKuArticleListByOneCidCallback = this;
        mHttpRequest.getTiKuArticleListByOneCid(uid, tikuArticleCid, mStart, sid);

    }

    private void setUpAndDownToRefresh(final int uid, final int tikuArticleCid, final String sid) {
        swipeRefreshLayout.setColorSchemeColors(R.color.fresh_blue, R.color.fresh_white, R.color.fresh_red);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mStart = 0;
                downToRefresh = true;
                mHttpRequest.getTiKuArticleListByOneCid(uid, tikuArticleCid, mStart, sid);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:

                        if (mListView.getLastVisiblePosition() == mListView.getAdapter().getCount() - 1) {
                            mStart += mStep;
                            //    can't clear articleList
                            mHttpRequest.getTiKuArticleListByOneCid(uid, tikuArticleCid, mStart, sid);
                        }
                        break;
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void onItemClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TiKuArticleByOneCategory article = (TiKuArticleByOneCategory) mListView.getAdapter().getItem(position);
                // 如果是双击,1秒内连续点击判断为双击
                //  article.getArticleId() == lastClickId
                if ((Math.abs(lastClickTime - System.currentTimeMillis()) < 1000)) {
                    // lastClickId = 0;
                    lastClickTime = 0;
                    return;
                } else {
                    // lastClickId = article.getArticleId();
                    lastClickTime = System.currentTimeMillis();
                    long articleId = article.getRid();
                    if (articleId != 0) {
                        showProgressDialog();
                        mHttpRequest.GetArticleRequirement(articleId);
                    }
                }
            }
        });
    }

    @Override
    protected void setDefaultFragmentTitle(String title) {

    }


    @Override
    public void getTiKuArticleListByTiKuCid(ArrayList<TiKuArticleByOneCategory> tiKuArticleCategoryListItems) {
        hideProgressDialog();
        if (downToRefresh) {
            mTiKuArticleCategoryList.clear();
            mTiKuArticleListByOneCateInStudentAdapter.notifyDataSetChanged();
            downToRefresh = false;
        }


        List<TiKuArticleByOneCategory> oldList = new ArrayList<TiKuArticleByOneCategory>(mTiKuArticleCategoryList);
        Collections.copy(oldList, mTiKuArticleCategoryList);

        mTiKuArticleCategoryList.clear();
        mTiKuArticleCategoryList.addAll(oldList);
        mTiKuArticleCategoryList.addAll(tiKuArticleCategoryListItems);

        swipeRefreshLayout.setRefreshing(false);
        mTiKuArticleListByOneCateInStudentAdapter.notifyDataSetChanged();

    }

    @Override
    public void ArticleRequirement(ArticleRequirement articleRequirement) {
        hideProgressDialog();

        StudentArticle article = new StudentArticle();
        article.mArticleId = articleRequirement.mArticleId;
        article.mNo_paste = articleRequirement.mNo_paste;
        //保存该作文的粘贴选项
        Article_No_Paste_DatabaseHelper.getInstance(getActivity()).createArticleId(article);

        Intent intent = new Intent(getActivity(), StudentArticleStartWritingActivity.class);
        intent.putExtra("TITLE", articleRequirement.mTitle);
        intent.putExtra("REQ", articleRequirement.mRequirement);
        intent.putExtra("END", articleRequirement.mEndtime);
        //mEssayId  =  0  here
        intent.putExtra("ID", String.valueOf(0));
        intent.putExtra("ARTICLEID", String.valueOf(articleRequirement.mArticleId));
        intent.putExtra("COUNT", String.valueOf(articleRequirement.mCount));
        intent.putExtra("FULLSCORE", String.valueOf(articleRequirement.mFullScore));
        intent.putExtra("REQUIRECOUNT", articleRequirement.mRequireCount);
        intent.putExtra("TEACHER", articleRequirement.mTeacher);
        intent.putExtra("No_paste", articleRequirement.mNo_paste);
        intent.putExtra("Image_requre", articleRequirement.mImageRequirement);
        intent.putExtra("TEACHERSMALLHEAD", articleRequirement.mTeacherSmallHead);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void ErrorNetwork() {

    }

    private void showProgressDialog() {
        try {
            if (pDialog != null) {
                pDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideProgressDialog() {
        try {
            if (pDialog != null) {
                pDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
