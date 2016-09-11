package com.cikuu.pigai.businesslogic;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by xuhai on 14/12/31.
 */
@DatabaseTable(tableName = "StudentArticle")
public class StudentArticle {
    @DatabaseField
    public long mArticleId;
    @DatabaseField
    public long mEssayId;
    @DatabaseField
    public int mUid;
    @DatabaseField
    public String mTitle;
    @DatabaseField
    public String mContent;
    @DatabaseField
    public String mEndtime;
    @DatabaseField
    public int mStatus;
    @DatabaseField//0-unsubmitted, 1-submitted
    public String mRequirement;
    @DatabaseField
    public int mCount;
    @DatabaseField
    public String mSubmittedTime;
    @DatabaseField
    public int mNetworkOrDatabase;   //ENetworkArticle = 0;  EDatabaseArticle = 1
    @DatabaseField
    public double mScore;
    @DatabaseField
    public int mType;
    @DatabaseField
    public int mNo_paste;  //0  可以粘贴  1 不可以粘贴
    @DatabaseField
    public String mTeacherName = ""; //结合mArticleId，  用于 判断自测  题库  老师布置
    @DatabaseField
    public String mSample_content = "";

    public StudentArticle() {

    }

    public StudentArticle(long id, int uid, String title, String content,
                          String endtime, int status, String requirement,
                          int count, String submittime, int networkOrDatabase) {
        this.mEssayId = id;
        this.mUid = uid;
        this.mTitle = title;
        this.mContent = content;
        this.mEndtime = endtime;
        this.mStatus = status;
        this.mRequirement = requirement;
        this.mCount = count;
        this.mSubmittedTime = submittime;
        this.mNetworkOrDatabase = networkOrDatabase;
    }


    // setters
    public void setEssayId(long essayId) {
        this.mEssayId = essayId;
    }

    public void setArticleId(long articleID) {
        this.mArticleId = articleID;
    }

    public void setUid(int uid) {
        this.mUid = uid;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public void setEndTime(String endTime) {
        this.mEndtime = endTime;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public void setRequirement(String requirement) {
        this.mRequirement = requirement;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public void setSubmittedTime(String submittedTime) {
        this.mSubmittedTime = submittedTime;
    }

    public void setNetworkOrDatabase(int networkOrDatabase) {
        this.mNetworkOrDatabase = networkOrDatabase;
    }


    // getters
    public long getEssayId() {
        return this.mEssayId;
    }

    public long getArticleId() {
        return this.mArticleId;
    }

    public long getUid() {
        return this.mUid;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getContent() {
        return this.mContent;
    }

    public String getEndTime() {
        return this.mEndtime;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public String getRequirement() {
        return this.mRequirement;
    }

    public int getCount() {
        return this.mCount;
    }

    public String getSubmittedTime() {
        return this.mSubmittedTime;
    }

    public int getNetworkOrDatabase() {
        return this.mNetworkOrDatabase;
    }


    public String getmTeacherName() {
        return mTeacherName;
    }

    public void setmTeacherName(String mTeacherName) {
        this.mTeacherName = mTeacherName;
    }

    public String getmSample_content() {
        return mSample_content;
    }

    public void setmSample_content(String mSample_content) {
        this.mSample_content = mSample_content;
    }
}
