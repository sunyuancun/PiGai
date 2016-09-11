package com.cikuu.pigai.businesslogic;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2015-10-12
 * Time: 10:53
 * Protect: PiGai
 */
public class SearchedTeacherInfoInStudent {

    public long teacherUid;
    public String teacherName;
    public String teacherSchool;
    public String teacherImage;
    public long articleCount;

    public long getTeacherUid() {
        return teacherUid;
    }

    public void setTeacherUid(long teacherUid) {
        this.teacherUid = teacherUid;
    }

    public long getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(long articleCount) {
        this.articleCount = articleCount;
    }

    public String getTeacherImage() {
        return teacherImage;
    }

    public void setTeacherImage(String teacherImage) {
        this.teacherImage = teacherImage;
    }

    public String getTeacherSchool() {
        return teacherSchool;
    }

    public void setTeacherSchool(String teacherSchool) {
        this.teacherSchool = teacherSchool;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
