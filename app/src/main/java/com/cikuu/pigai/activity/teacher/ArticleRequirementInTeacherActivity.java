package com.cikuu.pigai.activity.teacher;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cikuu.pigai.R;
import com.cikuu.pigai.activity.utils.TimeShowUtils;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.ArticleRequirement;
import com.cikuu.pigai.httprequest.VolleyRequest;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ArticleRequirementInTeacherActivity extends ActionBarActivity implements VolleyRequest.ArticleRequirementCallback {

    @InjectView(R.id.requireArticleId)
    TextView requireArticleId;
    @InjectView(R.id.requireCreateTime)
    TextView requireCreateTime;
    @InjectView(R.id.requireEndtime_show)
    TextView requireEndtimeShow;
    @InjectView(R.id.requireFullScore)
    TextView requireFullScore;
    @InjectView(R.id.requireTitle)
    TextView requireTitle;
    @InjectView(R.id.requrementImage)
    NetworkImageView requrementImage;
    @InjectView(R.id.requirementTextView)
    TextView requirementTextView;
    @InjectView(R.id.requireCount)
    TextView requireCount;


    private AppController.CacheImageLoader imageLoader;
    VolleyRequest mHttpRequest;
    private long mArticleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_requirement_in_teacher);
        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("作文要求");

        mArticleID = this.getIntent().getLongExtra("ARTICLEID", 0);

        imageLoader = AppController.getInstance().getCacheImageLoader();
        imageLoader.getMemoryCache().evictAll(); // remove all
        imageLoader.getDiskCache().clear(); // remove all

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mArticleRequirementCallback = this;
        mHttpRequest.GetArticleRequirement(mArticleID);


    }

    @Override
    public void ArticleRequirement(ArticleRequirement articleRequirement) {

        requireTitle.setText(articleRequirement.mTitle);
        requireArticleId.setText("作文号:" + String.valueOf(articleRequirement.mArticleId));
        requireCount.setText(articleRequirement.mRequireCount);
        requireFullScore.setText(String.valueOf(articleRequirement.mFullScore) + "分");
        requirementTextView.setText(Html.fromHtml(articleRequirement.mRequirement));
        String endtime = articleRequirement.mEndtime;
        if (endtime != null) {
            requireEndtimeShow.setText(TimeShowUtils.showTimeOfStudentHome(endtime));
            requireCreateTime.setText(articleRequirement.mCreatetime);
        }

        if (articleRequirement.mImageRequirement != null) {
            requrementImage.setVisibility(View.VISIBLE);
            requrementImage.setImageUrl(articleRequirement.mImageRequirement, imageLoader);
        }

    }

    @Override
    public void ErrorNetwork() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article_requirement_in_teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
