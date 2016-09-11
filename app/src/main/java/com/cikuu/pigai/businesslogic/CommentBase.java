package com.cikuu.pigai.businesslogic;

/**
 * Created by xuhai on 14/12/31.
 */
public class CommentBase {

    public static final int ETEXTCOMMENT = 0;
    public static final int EVOICECOMMENT = 1;

    public long mCommentId;//used to delete the comment

    public int mType;

    public String mSoundUrl;
    public int mDuration;

    public String mTextComments;

    public String mDate;

    public int mUid;
    public String mTeacherName;

    public String mPic;

    public CommentBase() {

    }
}
