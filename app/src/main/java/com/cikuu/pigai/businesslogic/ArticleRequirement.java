package com.cikuu.pigai.businesslogic;

/**
 * Created by xuhai on 15/1/8.
 */
public class ArticleRequirement {

    public long mArticleId;
    public int mCount;
    public long mUid;
    public String mTitle;
    public String mContent;
    public String mEndtime;
    public String mSubmittedTime;
    public int mStatus;  //0-unsubmitted, 1-submitted

    public String mRequirement;
    public String mTeacher;
    public int mFullScore;
    public String mRequireCount;

    public int mNo_paste;   //0  可以粘贴  1 不可以粘贴

    public int mNetworkOrDatabase;

    public String mImageRequirement;
    public String mTeacherSmallHead;
    public String mCreatetime;

    public ArticleRequirement() {

    }


}
