package com.cikuu.pigai.businesslogic;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2016-04-21
 * Time: 10:10
 * Protect: PiGai_v1.6(version1.6) _bug_fix
 */
public class TiKuArticleByOneCategory {

    private long rid;
    private String articleTitle;
    private String endTime;
    private int count;

    public long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
