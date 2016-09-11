package com.cikuu.pigai.dbmodel;


//TODO should be TeacherArticle, and move to businesslogic
public class Article {

    int id;
    String title;
    String content;
    long articleId;
    String author;
    int count;
    String endTime;
    String created_at;
    String request_type;


    // constructors
    public Article() {
    }

    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Article(String title, String content, int articleId, String author, int count, String endTime) {
        this.title = title;
        this.content = content;
        this.articleId = articleId;
        this.author = author;
        this.count = count;
        this.endTime = endTime;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCount(int status) {
        this.count = status;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }


    // getters
    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public long getArticleId() {
        return this.articleId;
    }

    public String getAuthor() {
        return this.author;
    }

    public int getCount() {
        return this.count;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getCreated_at() {
        return created_at;
    }

}
