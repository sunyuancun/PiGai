package com.cikuu.pigai.activity.student;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adorkable.iosdialog.ActionSheetDialog;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.adapter.StudentArticleListInStudentRecyclerViewAdapter;
import com.cikuu.pigai.activity.uiutils.NetworkImageHolderView;
import com.cikuu.pigai.activity.uiutils.SpaceItemDecoration;
import com.cikuu.pigai.activity.utils.SidTokenTool;
import com.cikuu.pigai.businesslogic.Advertisement;
import com.cikuu.pigai.businesslogic.Student;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.dbmodel.Article_No_Paste_DatabaseHelper;
import com.cikuu.pigai.httprequest.VolleyRequest;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.analytics.MobclickAgent;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StudentHomeArticleFragment extends Fragment implements VolleyRequest.StudentSubmittedArticleListByUidCallback,
        VolleyRequest.DeleteNetworkArticleByEssayIdCallback, VolleyRequest.GetPiGaiAdsCallback, NetworkImageHolderView.OnClickCallBack,
        StudentArticleListInStudentRecyclerViewAdapter.MyItemClickListener,
        StudentArticleListInStudentRecyclerViewAdapter.MyItemLongClickListener {

    Context mStudentHomeActivityContext;

    private XRecyclerView mStudentArticleListView;
    private ConvenientBanner mConvenientBanner;
    private TextView emptyTextView;

    private ProgressDialog mProgressDialog;

    private VolleyRequest mHttpRequest;
    private Student mStudent;

    private List<StudentArticle> mStudentArticleList = new ArrayList<>();
    private List<StudentArticle> mStudentArticleListWithoutDB = new ArrayList<>();
    private ArrayList<Advertisement> mAdvertisementList = new ArrayList<>();
    private List<String> networkImages = new ArrayList<>();

    public StudentArticleListInStudentRecyclerViewAdapter mArticleListAdapter;

    private int mStart = 0;
    private int articleListItemCount = -1;
    private int mPosition;
    //下拉刷新 判断
    private Boolean RefreshFromUpToDown = false;

    //新提交作文刷新判断
    public static boolean mNeedToRefreshArticleListFromNetwork = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home_article, null);
        mStudentArticleListView = (XRecyclerView) view.findViewById(R.id.listview_myArticles);
        mStudentArticleListView.setEmptyView(view.findViewById(R.id.empty));
        emptyTextView = (TextView) view.findViewById(R.id.empty);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mStudentHomeActivityContext = getActivity();
        mStudent = Student.GetInstance();
        mHttpRequest = new VolleyRequest();

        mHttpRequest.mStudentSubmittedArticleListByUidCallback = this;
        mHttpRequest.mDeleteNetworkArticleByEssayIdCallback = this;
        mHttpRequest.mGetPiGaiAdsCallback = this;
        NetworkImageHolderView.mOnClickCallBack = this;

        showProgressDialog();
        //加载图片前初始化imageLoader
        initImageLoader();
        initRecyclerView();
        setRefreshUpAndDown();
        setEmptyTextViewOnClick();

        mHttpRequest.GetStudentSubmittedArticeListByStudentUid(Student.GetInstance().mStudentDescription.mUid, mStart);
        mHttpRequest.getPiGaiAds();

    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mStudentHomeActivityContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mStudentArticleListView.setLayoutManager(layoutManager);

        mStudentArticleListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mStudentArticleListView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mStudentArticleListView.setArrowImageView(R.drawable.iconfont_downgrey);


        View header = LayoutInflater.from(getActivity()).inflate(R.layout.recyclerview_header, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        mConvenientBanner = (ConvenientBanner) header.findViewById(R.id.convenientBanner);
        mStudentArticleListView.addHeaderView(mConvenientBanner);

        // TODO 设置  item 间距 需要在 addHeaderView 之后
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_space);
        mStudentArticleListView.addItemDecoration(new SpaceItemDecoration(spacingInPixels, mStudentArticleListView.getHeaderViewCount()));

        mArticleListAdapter = new StudentArticleListInStudentRecyclerViewAdapter(mStudentHomeActivityContext, mStudentArticleList);
        this.mArticleListAdapter.setOnItemClickListener(this);
        this.mArticleListAdapter.setOnItemLongClickListener(this);
        mStudentArticleListView.setAdapter(mArticleListAdapter);
    }

    private void initImageLoader() {
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.drawable.ic_default_adimage)
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    private void setRefreshUpAndDown() {

        mStudentArticleListView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                // 下拉刷新一次
                mStart = 0;
                RefreshFromUpToDown = true;
                mHttpRequest.GetStudentSubmittedArticeListByStudentUid(Student.GetInstance().mStudentDescription.mUid, mStart);
            }

            @Override
            public void onLoadMore() {
                //加载更多
                if (articleListItemCount != 0) {
                    mStart += 10;
                    mHttpRequest.GetStudentSubmittedArticeListByStudentUid(Student.GetInstance().mStudentDescription.mUid, mStart);
                } else {
                    mStudentArticleListView.loadMoreComplete();
                    Toast.makeText(mStudentHomeActivityContext, "全部文章加载完成", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void setEmptyTextViewOnClick() {
        emptyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStart = 0;
                RefreshFromUpToDown = true;
                mHttpRequest.GetStudentSubmittedArticeListByStudentUid(Student.GetInstance().mStudentDescription.mUid, mStart);
            }
        });
    }

    @Override
    public void onItemClick(View view, int postion) {
        postion = postion - mStudentArticleListView.getHeaderViewCount();

        if (postion > mStudentArticleList.size() - 1) {
            return;
        }

        StudentArticle article = (StudentArticle) mArticleListAdapter.getItem(postion);

        long essayId = article.mEssayId;
        long articleId = article.mArticleId;
        int no_paste = Article_No_Paste_DatabaseHelper.getInstance(mStudentHomeActivityContext).getArticleNoPaste(article);
        int type = article.mNetworkOrDatabase;
        String teacherName = article.mTeacherName;

        if (type == VolleyRequest.ENetworkArticle) {
            Intent intent = new Intent(mStudentHomeActivityContext, StudentArticleSubmitted2Activity.class);
            intent.putExtra("ESSAYID", String.valueOf(essayId));
            intent.putExtra("ARTICLEID", String.valueOf(articleId));
            intent.putExtra("No_paste", no_paste);
            intent.putExtra("TEACHERNAME", teacherName);
            startActivity(intent);
        }

        if (type == VolleyRequest.EDatabaseArticle) {
            Intent intent = new Intent(mStudentHomeActivityContext, StudentArticleUnSubmittedActivity.class);
            intent.putExtra("ARTICLEID", String.valueOf(article.mArticleId));
            intent.putExtra("ESSAY_ID", String.valueOf(article.mEssayId));
            intent.putExtra("TITLE", article.mTitle);
            intent.putExtra("REQ", article.mRequirement);
            if (article.mEndtime == null) {
                article.mEndtime = "";
            }
            intent.putExtra("END", article.mEndtime);
            intent.putExtra("CONTENT", article.mContent);
            intent.putExtra("COUNT", String.valueOf(article.mCount));
            //粘贴选项
            intent.putExtra("No_paste", no_paste);
            intent.putExtra("TEACHERNAME", teacherName);
            //作文来源（在写作页面使用该字段）
            intent.putExtra("ArticleInNetWorkOrDataBase", String.valueOf(VolleyRequest.EDatabaseArticle));
            startActivity(intent);

        }

    }

    @Override
    public void onItemLongClick(View view, int postion) {
        postion = postion - mStudentArticleListView.getHeaderViewCount();
        StudentArticle article = (StudentArticle) mArticleListAdapter.getItem(postion);
        mPosition = postion;
        showActionSheet(article.getTitle());
    }

    private void deleteArticle() {
        StudentArticle article = (StudentArticle) mArticleListAdapter.getItem(mPosition);
        int networkOrDb = article.mNetworkOrDatabase;

        if (networkOrDb == VolleyRequest.ENetworkArticle) {

            long essayId = article.mEssayId;
            int uid = mStudent.GetDescription().mUid;
            String sid = SidTokenTool.getSidKeyOfStudentDeleteNetworkArticle(uid, essayId);
            mHttpRequest.DeleteNetworkArticleByEssayId(essayId, uid, sid);
        }

        if (networkOrDb == VolleyRequest.EDatabaseArticle) {
            //   long articleId = article.mArticleId;
            long essayId = article.mEssayId;
            mStudentArticleList.remove(article);
            mStudent.DeleteStudentArticleByEassayIdDraftFromDataBase(essayId);
            mArticleListAdapter.notifyDataSetChanged();
        }

    }

    private void showActionSheet(String title) {
        try {
            new ActionSheetDialog(mStudentHomeActivityContext)
                    .builder()
                    .setTitle("删除作文:" + title + "？")
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(false)
                    .addSheetItem("删除作文", ActionSheetDialog.SheetItemColor.Red,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    deleteArticle();
                                }
                            }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void StudentArticleListByUid(List<StudentArticle> articleList) {
        hidePDialog();

        if (RefreshFromUpToDown) {
            mStudentArticleList.clear();
            mStudentArticleListWithoutDB.clear();
            mArticleListAdapter.notifyDataSetChanged();
            RefreshFromUpToDown = false;
            mStudentArticleListView.smoothScrollToPosition(0);
        }

        articleListItemCount = articleList.size();

        Student stu = Student.GetInstance();
        List<StudentArticle> dbList = stu.GetAllDraftStudentArticlesFromDataBase();

        List<StudentArticle> oldList = new ArrayList<>(mStudentArticleListWithoutDB);
        Collections.copy(oldList, mStudentArticleListWithoutDB);

        List<StudentArticle> newList = new ArrayList<>(articleList);
        Collections.copy(newList, articleList);

        mStudentArticleListWithoutDB.clear();
        mStudentArticleListWithoutDB.addAll(oldList);
        mStudentArticleListWithoutDB.addAll(newList);

        mStudentArticleList.clear();
        mStudentArticleList.addAll(dbList);
        mStudentArticleList.addAll(mStudentArticleListWithoutDB);

        if (mStudentArticleList.size() > 10 && articleList.size() == 0) {
            Toast.makeText(mStudentHomeActivityContext, "文章加载完毕", Toast.LENGTH_SHORT).show();
        }

        if (mStudentArticleList.size() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
        }

        mArticleListAdapter.notifyDataSetChanged();
        mStudentArticleListView.refreshComplete();
        mStudentArticleListView.loadMoreComplete();
    }

    @Override
    public void ArticleDeleted(final int error) {
        if (error == 1) {
            if (mPosition < mStudentArticleList.size()) {
                StudentArticle article = (StudentArticle) mArticleListAdapter.getItem(mPosition);
                mStudentArticleListWithoutDB.remove(article);
                mStudentArticleList.remove(article);
                mArticleListAdapter.notifyDataSetChanged();
            }
        }
        hidePDialog();
    }

    @Override
    public void getPiGaiAdsResult(ArrayList<Advertisement> advertisementList) {
        try {
            if (advertisementList.size() == 0) {
                return;
            }

            mAdvertisementList.clear();
            mAdvertisementList.addAll(advertisementList);

            String[] images = new String[advertisementList.size()];
            for (int i = 0; i < advertisementList.size(); i++) {
                images[i] = advertisementList.get(i).getImg_url();
            }

            getPiGaiAdsResultDataToView(images);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPiGaiAdsResultDataToView(String[] images) {
        networkImages.clear();
        networkImages = Arrays.asList(images);
        mConvenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, networkImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.dot_blur, R.drawable.dot_focus})
                //设置翻页的效果，不需要翻页效果可用不设
                .setPageTransformer(ConvenientBanner.Transformer.DefaultTransformer);
    }

    @Override
    public void onViewPagerItemClick(int position) {
        if (mAdvertisementList.size() - 1 >= position) {
            Intent intent = new Intent(getActivity(), HuoDongActivity.class);
            intent.putExtra("URL", mAdvertisementList.get(position).getLink());
            startActivity(intent);
        }
    }

    @Override
    public void ErrorNetwork() {
        if (mStudentArticleListView != null) {
            mStudentArticleListView.refreshComplete();
            mStudentArticleListView.loadMoreComplete();
        }
        hidePDialog();
    }

    public void ReloadStudentArticleFromDatabase() {
        Student stu = Student.GetInstance();
        List<StudentArticle> dbList = stu.GetAllDraftStudentArticlesFromDataBase();

        List<StudentArticle> oldList = new ArrayList<>(mStudentArticleListWithoutDB);
        Collections.copy(oldList, mStudentArticleListWithoutDB);

        mStudentArticleList.clear();
        mStudentArticleList.addAll(dbList);
        mStudentArticleList.addAll(mStudentArticleListWithoutDB);
        mArticleListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        MobclickAgent.onPageStart("StudenttudentHomeArticleFragment"); //统计页面
        MobclickAgent.onResume(mStudentHomeActivityContext);
        ReloadStudentArticleFromDatabase();

        if (mNeedToRefreshArticleListFromNetwork || CommentsBySentenceActivity.mNeedUpdateNativeDataInStudentHomeArticleFragment) {
            mStart = 0;
            //相当于 下拉刷新一次
            RefreshFromUpToDown = true;
            mNeedToRefreshArticleListFromNetwork = false;
            CommentsBySentenceActivity.mNeedUpdateNativeDataInStudentHomeArticleFragment = false;
            mHttpRequest.GetStudentSubmittedArticeListByStudentUid(Student.GetInstance().mStudentDescription.mUid, mStart);
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("StudenttudentHomeArticleFragment");// 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(mStudentHomeActivityContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        try {
            mProgressDialog = new ProgressDialog(mStudentHomeActivityContext);
            mProgressDialog.setMessage("刷新列表...");
            mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hidePDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
