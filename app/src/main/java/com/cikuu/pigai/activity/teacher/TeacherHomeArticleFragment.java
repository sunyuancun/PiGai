package com.cikuu.pigai.activity.teacher;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.adapter.TeacherArticleListAdapter;
import com.cikuu.pigai.activity.utils.SidTokenTool;
import com.cikuu.pigai.businesslogic.Teacher;
import com.cikuu.pigai.businesslogic.Article;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TeacherHomeArticleFragment extends Fragment implements VolleyRequest.TeacherArticleListCallback, VolleyRequest.DeleteNetworkArticleByTeacherCallback {

    private VolleyRequest mHttpRequest;
    private Teacher mTeacher;

    private ProgressDialog pDialog;
    private TextView emptyTextView;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int mCurrentArticlesNumber = 0;
    private int mStep = 10;
    private int articleListItemCount = -1;

    private List<Article> articleList = new ArrayList<Article>();
    private TeacherArticleListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_home_article, null);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        listView = (ListView) view.findViewById(R.id.teacherarticlelist);
        emptyTextView = (TextView) view.findViewById(R.id.framelayout);
        listView.setEmptyView(emptyTextView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("载入中...");
        pDialog.show();

        mTeacher = Teacher.GetInstance();
        mHttpRequest = new VolleyRequest();
        mHttpRequest.mTeacherArticleListCallback = this;
        mHttpRequest.mDeleteNetworkArticleByTeacherCallback = this;

       /* btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pDialog.show();
                mHttpRequest.GetTeacherArticleList(mTeacher.GetDescription().mUid, mCurrentArticlesNumber);
                mCurrentArticlesNumber += mStep;
            }
        });*/

        adapter = new TeacherArticleListAdapter(getActivity(), articleList);
        listView.setAdapter(adapter);
        mHttpRequest.GetTeacherArticleListInTeacher(mTeacher.GetDescription().mUid, mCurrentArticlesNumber);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:

                        if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1) {
                            //     pDialog.show();
                            if (articleListItemCount != 0) {
                                mCurrentArticlesNumber += mStep;
                                //    can't clear articleList
                                mHttpRequest.GetTeacherArticleListInTeacher(mTeacher.GetDescription().mUid, mCurrentArticlesNumber);
                            }
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

        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //must make   mStart = 0
                mCurrentArticlesNumber = 0;
                if (pDialog != null) {
                    pDialog.show();
                }

                // todo clear  all   list   but  not   draft         ====>  must  do  it
                articleList.clear();
                adapter.notifyDataSetChanged();
                mHttpRequest.GetTeacherArticleListInTeacher(mTeacher.GetDescription().mUid, mCurrentArticlesNumber);
            }
        });

        emptyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pDialog != null) {
                    pDialog.show();
                }
                mCurrentArticlesNumber = 0;
                // todo clear  all   list   but  not   draft         ====>  must  do  it
                articleList.clear();
                adapter.notifyDataSetChanged();
                mHttpRequest.GetTeacherArticleListInTeacher(mTeacher.GetDescription().mUid, mCurrentArticlesNumber);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View itemClicked, int
                    position, long id) {
                Article article = (Article) listView.getAdapter().getItem(position);
                long articleId = article.getArticleId();
                String artitleTitle = article.getTitle();
                String articleEndtime = article.getEndTime();
                int articleSubmitted = article.getCount();

                Intent intent = new Intent(getActivity(), StudentArticleListInTeacherArticleActivity.class);

                intent.putExtra("ARTICLE_ID", String.valueOf(articleId));
                intent.putExtra("TITLE", artitleTitle);
                intent.putExtra("COUNT", String.valueOf(articleSubmitted));
                intent.putExtra("END_TIME", articleEndtime);
                startActivity(intent);
            }

        });

        registerForContextMenu(listView);

    }


    public void TeacherArticles(List<Article> articleItems) {
        if (pDialog != null)
            pDialog.dismiss();

        articleListItemCount = articleItems.size();

        List<Article> oldList = new ArrayList<Article>(articleList);
        Collections.copy(oldList, articleList);

        articleList.clear();
        articleList.addAll(oldList);
        articleList.addAll(articleItems);

        if (articleList.size() > 10 && articleItems.size() == 0) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "文章加载完毕", Toast.LENGTH_SHORT).show();
            }
        }

        if (articleList.size() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    final static int CONTEXTMENU_OPTION1 = 1;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.teacherarticlelist) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Article article = (Article) lv.getItemAtPosition(acmi.position);
            menu.add(Menu.NONE, CONTEXTMENU_OPTION1, 0, "删除");
        }
    }

    private int mPosition;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        mPosition = menuInfo.position;
        if (mPosition < articleList.size()) {
            Article article = (Article) listView.getAdapter().getItem(mPosition);
            long rid = article.getArticleId();
            int uid = mTeacher.mDescription.mUid;
            String sid = SidTokenTool.getSidKeyOfTeacherDeleteNetworkArticle(uid, rid);
            mHttpRequest.DeleteNetworkArticleByTeacher(rid, uid, sid);
            try {
                if (pDialog != null) {
                    pDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void ArticleDeletedByTeacher(int error) {
        hidePDialog();

        if (error == 1) {
            if (mPosition < articleList.size()) {
                Article article = (Article) listView.getAdapter().getItem(mPosition);
                articleList.remove(article);
                adapter.notifyDataSetChanged();
            }
        } else {
            //error delete network article
        }

    }

    public void ErrorNetwork() {
        hidePDialog();
        if (getActivity() != null) {
            Toast.makeText(getActivity(), "网络错误！", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean NEW_ARTICLE_PUBLISHED = false;

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TeacherHomeArticleFragment"); //统计页面

        if (NEW_ARTICLE_PUBLISHED) {
            NEW_ARTICLE_PUBLISHED = false;
            articleList.clear();
            adapter.notifyDataSetChanged();
            mHttpRequest.GetTeacherArticleListInTeacher(mTeacher.GetDescription().mUid, 0);
            if (pDialog != null) {
                pDialog.show();
            }
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TeacherHomeArticleFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }


}
