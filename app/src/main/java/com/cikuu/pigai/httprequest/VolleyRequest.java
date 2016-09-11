package com.cikuu.pigai.httprequest;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cikuu.pigai.activity.adapter.StudentArticleListInTeacherArticleListAdapter;
import com.cikuu.pigai.activity.utils.SidTokenTool;
import com.cikuu.pigai.app.AppController;
import com.cikuu.pigai.businesslogic.Advertisement;
import com.cikuu.pigai.businesslogic.ArticleRequirement;
import com.cikuu.pigai.businesslogic.CommentBase;
import com.cikuu.pigai.businesslogic.SearchedTeacherInfoInStudent;
import com.cikuu.pigai.businesslogic.StudentArticle;
import com.cikuu.pigai.businesslogic.StudentArticleDetail;
import com.cikuu.pigai.businesslogic.TiKuArticleByOneCategory;
import com.cikuu.pigai.businesslogic.TiKuArticleCategory;
import com.cikuu.pigai.businesslogic.UserInformation;
import com.cikuu.pigai.businesslogic.Article;

public class VolleyRequest {

    public static final int User_Type_Teacher = 1;
    public static final int User_Type_Student = 2;
    public static String User_Token = "";

    public interface LogInCallback {
        void LoggedIn(UserInformation description, int type);

        void ErrorPassWord();
    }

    public interface TeacherArticleListCallback {
        void TeacherArticles(List<Article> articleItems);

        void ErrorNetwork();
    }

    public interface StudentArticleListCallback {
        void StudentArticles(List<StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle> articleItems);

        void ErrorNetwork();
    }

    public interface StudentArticleDetailCallback {
        void StudentArticleDetail(StudentArticleDetail studentArticleDetail);

        void ErrorNetwork();
    }

    public interface ArticleRequirementCallback {
        void ArticleRequirement(ArticleRequirement articleRequirement);

        void ErrorNetwork();
    }

    public interface SyncStudentArticleCallback {
        void OnSyncOneArticleDetailFromNetwork(StudentArticle article);

        void OnSyncArticleListFromNetwork(List<StudentArticle> articles);

        void ErrorNetwork();
    }

    public interface FindArticleCallback {
        void ArticleExist(boolean exist);
    }

    public LogInCallback mLogInCallback;
    public TeacherArticleListCallback mTeacherArticleListCallback;
    public StudentArticleListCallback mStudentArticleListCallback;
    public StudentArticleDetailCallback mStudentArticleDetailCallback;
    public ArticleRequirementCallback mArticleRequirementCallback;

    //student http request
    public SyncStudentArticleCallback mSyncStudentArticleCallback;
    public FindArticleCallback mFindArticleCallback;

    private static String tag_json_obj = "json_obj_req";
    private static String TAG = "Volley";

    public VolleyRequest() {

    }


    public interface VersionCallback {
        void Version(double version);
    }

    public VersionCallback mVersion;

    public void getVersion() {
        String url = "http://www.pigai.org/?c=api&a=version";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            double version = response.getDouble("android_version");
                            mVersion.Version(version);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mVersion.Version(0);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mVersion.Version(0);
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }


    public void LogIn(String userName, String passWord) {

        String LoginUrl = "http://www.pigai.org/?c=api&" +
                "a=sentbase&" +
                "action=userinfo&" +
                "email=" + userName + "&" +
                "psw=" + passWord + "&" +
                "key=SE9mpdwTPxyKGUvJb9vrVfYlwX803YcH&" +
                "type=json&" +
                "from=mobile";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                LoginUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {

//                            Map<String, String> responseHeaders = response.headers;
//                            String rawCookies = responseHeaders.get("Set-Cookie");//cookie值
                            int returnValue = response.getInt("re");
                            if (returnValue == 1) {
                                UserInformation description = new UserInformation();
                                JSONObject obj = response.getJSONObject("userinfo");
                                JSONObject objHead = obj.getJSONObject("head");
                                description.mName = obj.getString("name");
                                description.mSchool = obj.getString("school");
                                description.mEmail = obj.getString("email");
                                description.mUid = obj.getInt("uid");
                                description.mUser_Name = obj.getString("user_name");
                                description.bHead = objHead.getString("b");
                                description.sHead = objHead.getString("s");
                                description.type = obj.getInt("teacher_or_student");

                                JSONObject mininfoObj = response.getJSONObject("minfo");
                                description.pigai_token = mininfoObj.getString("pigai_token");
                                description.mStudent_number = mininfoObj.getString("student_number");
                                description.mClass = mininfoObj.getString("class");
                                description.mTel = mininfoObj.getString("tel");

                                if (mininfoObj.getInt("sex") == 2) {
                                    description.mSex = "女";
                                } else if (mininfoObj.getInt("sex") == 1) {
                                    description.mSex = "男";
                                } else
                                    description.mSex = "";

                                mLogInCallback.LoggedIn(description, description.type);
                            } else {
                                mLogInCallback.ErrorPassWord();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

//        JsonObjectCookieRequest jsonObjReq = new JsonObjectCookieRequest(LoginUrl,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG, response.toString());
//
//                        try {
//                            String login_cookie = response.optString("Cookie");
//                            int returnValue = response.getInt("re");
//                            if (returnValue == 1) {
//                                UserInformation description = new UserInformation();
//                                JSONObject obj = response.getJSONObject("userinfo");
//                                JSONObject objHead = obj.getJSONObject("head");
//                                description.mName = obj.getString("name");
//                                description.mSchool = obj.getString("school");
//                                description.mEmail = obj.getString("email");
//                                description.mUid = obj.getInt("uid");
//                                description.mUser_Name = obj.getString("user_name");
//                                description.bHead = objHead.getString("b");
//                                description.sHead = objHead.getString("s");
//                                description.type = obj.getInt("teacher_or_student");
//
//                                JSONObject mininfoObj = response.getJSONObject("minfo");
//                                description.pigai_token = mininfoObj.getString("pigai_token");
//                                description.mStudent_number = mininfoObj.getString("student_number");
//                                description.mClass = mininfoObj.getString("class");
//                                description.mTel = mininfoObj.getString("tel");
//
//                                if (mininfoObj.getInt("sex") == 2) {
//                                    description.mSex = "女";
//                                } else if (mininfoObj.getInt("sex") == 1) {
//                                    description.mSex = "男";
//                                } else
//                                    description.mSex = "";
//
//                                mLogInCallback.LoggedIn(description, description.type, login_cookie);
//                            } else {
//                                mLogInCallback.ErrorPassWord();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//            }
//        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "json_object");
    }

    public void GetTeacherArticleListInStudent(int uid, int next) {

        final String teacherarticleurl = "http://www.pigai.org/?c=api&a=requestList&uid=" +
                String.valueOf(uid) +
                "&limit=" + String.valueOf(10) +
                "&start=" + String.valueOf(next);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(teacherarticleurl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        List<Article> articleList = new ArrayList<>();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Article article = new Article();
                                article.setTitle(obj.getString("essay_title"));
                                article.setArticleId(obj.getLong("request_id"));
                                article.setCount(obj.getInt("essay_cnt"));
                                article.setEndTime(getDateToSecondTime(obj.getLong("end_time")));
                                articleList.add(article);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        mTeacherArticleListCallback.TeacherArticles(articleList);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTeacherArticleListCallback.ErrorNetwork();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }

    public void GetTeacherArticleListInTeacher(int uid, int next) {

        final String teacherarticleurl = "http://www.pigai.org/?c=api&a=requestList&uid=" +
                String.valueOf(uid) +
                "&token=" + User_Token +
                "&limit=" + String.valueOf(10) +
                "&start=" + String.valueOf(next) +
                "&type=teacher";

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(teacherarticleurl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        List<Article> articleList = new ArrayList<>();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Article article = new Article();
                                article.setTitle(obj.getString("essay_title"));
                                article.setArticleId(obj.getLong("request_id"));
                                article.setCount(obj.getInt("essay_cnt"));
                                article.setRequest_type(obj.getString("request_type"));
                                article.setEndTime(getDateToSecondTime(obj.getLong("end_time")));
                                article.setCreatedAt(getDateToSecondTime(obj.getLong("create_time")));
                                articleList.add(article);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        mTeacherArticleListCallback.TeacherArticles(articleList);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTeacherArticleListCallback.ErrorNetwork();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);

    }

    public void GetStudentArticleListInTeacher(long uid, long articleNumber, int start) {
        final String sutdentArticleListUrl = "http://www.pigai.org/index.php?c=api&a=essayList&rid=" + String.valueOf(articleNumber)
                + "&limit=" + String.valueOf(20)
                + "&start=" + String.valueOf(start)
                + "&uid=" + String.valueOf(uid)
                + "&token=" + User_Token;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                sutdentArticleListUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        List<StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle> articleList =
                                new ArrayList<>();

                        try {
                            JSONArray objArray = response.getJSONArray("essaylist");
                            // Parsing json
                            for (int i = 0; i < objArray.length(); i++) {
                                try {

                                    JSONObject obj = objArray.getJSONObject(i);
                                    StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle article =
                                            new StudentArticleListInTeacherArticleListAdapter.StudentArticleInTeacherArticle();

                                    article.mStudentHead = obj.getString("head");
                                    article.mEssayId = obj.getLong("essay_id");
                                    article.mTitle = obj.getString("title");
                                    article.mStudentName = obj.getString("stu_name");
                                    article.mDate = getDateToSecondTime(obj.getLong("ctime"));
                                    article.mScore = obj.getDouble("score");
                                    article.mType = obj.getInt("type");
                                    articleList.add(article);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            mStudentArticleListCallback.StudentArticles(articleList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mStudentArticleListCallback.ErrorNetwork();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "json_object");
    }

    public void GetStudentArticleDetail(long uid, long essayId) {

        String articleDetailUrl = "http://www.pigai.org/index.php?c=api&a=essayview&eid=" + String.valueOf(essayId) +
                "&token=" + User_Token +
                "&uid=" + String.valueOf(uid);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                articleDetailUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject articleDetail = response.optJSONObject("essay");
                            String articleComment = response.optString("pingyu");
                            if (articleDetail != null) {
                                StudentArticleDetail studentArticleDetail = new StudentArticleDetail();
                                studentArticleDetail.mUser_id = Long.parseLong(articleDetail.getString("user_id"));
                                studentArticleDetail.mName = articleDetail.getString("stu_name");
                                studentArticleDetail.mStudentClass = articleDetail.getString("stu_class");
                                studentArticleDetail.mTitle = articleDetail.getString("title");
                                studentArticleDetail.mSubmittedDate = getDateToSecondTime(articleDetail.getLong("ctime"));
                                studentArticleDetail.mModifiedTimes = articleDetail.getInt("version") - 1;
                                studentArticleDetail.mScore = articleDetail.getDouble("score");
                                studentArticleDetail.mEssay_rank = articleDetail.getString("essay_rank");
                                studentArticleDetail.mContent = articleDetail.getString("essay");
                                String word_num = articleDetail.getString("word_num");
                                if (word_num != "null") {
                                    int num = Integer.parseInt(word_num);
                                    studentArticleDetail.mWordsCount = num;
                                }
                                studentArticleDetail.mArticleId = articleDetail.getLong("request_id");
                                studentArticleDetail.mEssayId = articleDetail.getLong("essay_id");

                                try {
                                    JSONObject articleRequirement = response.getJSONObject("request");
                                    if (articleRequirement != null) {
                                        studentArticleDetail.mFullScore = articleRequirement.getInt("manfen");
                                        studentArticleDetail.mRequirement = articleRequirement.getString("essay_topic");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    studentArticleDetail.mFullScore = 0;
                                    studentArticleDetail.mRequirement = "";
                                }

                                studentArticleDetail.mComment = articleComment;

                                mStudentArticleDetailCallback.StudentArticleDetail(studentArticleDetail);
                            } else {
                                mStudentArticleDetailCallback.ErrorNetwork();
                            }
                        } catch (JSONException e) {
                            mStudentArticleDetailCallback.ErrorNetwork();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "json_object");
    }

    public interface StudentHeadCallback {
        void StudentHeadUrl(String url);

        void ErrorNetwork();
    }

    public StudentHeadCallback mStudentHeadCallback;

    public void GetUserHead(final long uid) {
        String userHeadUrl = "http://www.pigai.org/?c=api&a=getUserHead&uid=" + String.valueOf(uid);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                userHeadUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject request_user_head = response.getJSONObject(String.valueOf(uid));
                            String sHead = request_user_head.getString("s");
                            mStudentHeadCallback.StudentHeadUrl(sHead);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            mStudentHeadCallback.ErrorNetwork();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mStudentHeadCallback.ErrorNetwork();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "json_object");
    }

    public void GetArticleRequirement(final long articleId) {
        String requirementUrl = "http://www.pigai.org/index.php?c=api&a=requestView&rid=" + String.valueOf(articleId);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                requirementUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONArray objArray = response.getJSONArray("request");
                            JSONObject request_conf_obj = response.getJSONObject("request_conf");
                            JSONObject obj = objArray.getJSONObject(0);

                            if (obj != null) {
                                ArticleRequirement articleRequirement = new ArticleRequirement();
                                articleRequirement.mUid = obj.getLong("user_id");
                                articleRequirement.mTitle = obj.getString("essay_title");
                                articleRequirement.mCreatetime = getDateToSecondTime(obj.getLong("create_time"));
                                articleRequirement.mEndtime = getDateToSecondTime(obj.getLong("valid_end_time"));
                                articleRequirement.mRequirement = obj.getString("essay_topic");
                                articleRequirement.mCount = obj.getInt("essay_cnt");
                                articleRequirement.mArticleId = obj.getLong("request_id");
                                articleRequirement.mTeacher = obj.getString("teacher_name");
                                articleRequirement.mFullScore = obj.getInt("manfen");
                                articleRequirement.mTeacherSmallHead = obj.getString("teacher_small_head_img");
                                String image = obj.getString("img");
                                if (image != null) {
                                    articleRequirement.mImageRequirement = "http://img.pigai.org" + image;
                                }


                                JSONArray cnt_req_array = request_conf_obj.getJSONObject("word_cnt").getJSONArray("config");
                                articleRequirement.mRequireCount = String.valueOf(cnt_req_array.getInt(0)) +
                                        "~" +
                                        String.valueOf(cnt_req_array.getInt(1));
                                articleRequirement.mNo_paste = request_conf_obj.getJSONObject("no_paste").getInt("config");
                                mArticleRequirementCallback.ArticleRequirement(articleRequirement);
                            } else {
                                mArticleRequirementCallback.ErrorNetwork();
                            }
                        } catch (JSONException e) {
                            mArticleRequirementCallback.ErrorNetwork();
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "json_object");
    }


    public void SyncStudentArticleList() {

        mSyncStudentArticleCallback.OnSyncArticleListFromNetwork(null);
    }

    public void SyncOneStudentArticleDetail() {

        mSyncStudentArticleCallback.OnSyncOneArticleDetailFromNetwork(null);
    }


    public void FindArticleByArticleId(long articleId) {
        String requirementUrl = "http://www.pigai.org/?c=api&a=search&q=" + String.valueOf(articleId);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                requirementUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            String type = response.getString("type");

                            //article exist
                            if (type.equals("request_id")) {
                                //callback
                                mFindArticleCallback.ArticleExist(true);
                            } else {
                                mFindArticleCallback.ArticleExist(false);
                            }
                        } catch (JSONException e) {
                            mFindArticleCallback.ArticleExist(false);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "json_object");
    }


    public interface FindTeacherListByTeacherNameAndStudentSchoolCallback {
        void getTeacherList(List<SearchedTeacherInfoInStudent> teacherList);

        void ErrorNetwork();
    }

    public FindTeacherListByTeacherNameAndStudentSchoolCallback mFindTeacherListByTeacherNameAndStudentSchoolCallback;

    public void FindTeacherListByTeacherNameAndStudentSchool(String teacherName, String studentSchool, int next) {
        String findTeacherListUrl = "http://www.pigai.org/?c=api&a=searchTeacher&name=" + String.valueOf(teacherName) +
                "&schl=" + String.valueOf(studentSchool) +
                "&start=" + String.valueOf(next) +
                "&limit=" + String.valueOf(10);

        JsonObjectRequest jsonObjectReq_findTeacherList = new JsonObjectRequest(Method.GET,
                findTeacherListUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            List<SearchedTeacherInfoInStudent> teacherList = new ArrayList<>();
                            JSONArray jsonArray_teacherList = response.getJSONArray("teacherList");
                            System.out.println("++++++++++++++++++++++++++++++++++" + jsonArray_teacherList.length());
                            for (int i = 0; i < jsonArray_teacherList.length(); i++) {
                                JSONObject obj = jsonArray_teacherList.getJSONObject(i);
                                SearchedTeacherInfoInStudent searchedTeacherInfoInStudent = new SearchedTeacherInfoInStudent();

                                String user_id = obj.getString("user_id");
                                String img = obj.getString("img");
                                String name = obj.getString("name");
                                String school = obj.getString("school");
                                String request_cnt = obj.getString("request_cnt");
                                long uid = -1;
                                long count_request = -1;
                                try {
                                    uid = Long.parseLong(user_id);
                                    count_request = Long.parseLong(request_cnt);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                searchedTeacherInfoInStudent.setTeacherUid(uid);
                                searchedTeacherInfoInStudent.setArticleCount(count_request);
                                searchedTeacherInfoInStudent.setTeacherImage(img);
                                searchedTeacherInfoInStudent.setTeacherName(name);
                                searchedTeacherInfoInStudent.setTeacherSchool(school);

                                teacherList.add(searchedTeacherInfoInStudent);
                            }

                            mFindTeacherListByTeacherNameAndStudentSchoolCallback.getTeacherList(teacherList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mFindTeacherListByTeacherNameAndStudentSchoolCallback.ErrorNetwork();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mFindTeacherListByTeacherNameAndStudentSchoolCallback.ErrorNetwork();
                VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectReq_findTeacherList, "json_object");
    }


    public interface StudentSubmittedArticleListByUidCallback {
        void StudentArticleListByUid(List<StudentArticle> articleList);

        void ErrorNetwork();
    }

    public static final int ENetworkArticle = 0;
    public static final int EDatabaseArticle = 1;

    public StudentSubmittedArticleListByUidCallback mStudentSubmittedArticleListByUidCallback;

    public void GetStudentSubmittedArticeListByStudentUid(int uid, int start) {
        String requirementUrl = "http://www.pigai.org/?c=api&a=essaylistbyuid&start=" + String.valueOf(start) +
                "&limit=10" +
                "&token=" + User_Token +
                "&uid=" + String.valueOf(uid);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                requirementUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        List<StudentArticle> articleList = new ArrayList<>();

                        try {
                            JSONArray essayList = response.getJSONArray("essaylist");
                            if (essayList != null) {
                                for (int i = 0; i < essayList.length(); i++) {
                                    JSONObject obj = essayList.getJSONObject(i);
                                    StudentArticle article = new StudentArticle();
                                    article.mTitle = obj.getString("title");
                                    article.mSubmittedTime = getDateToSecondTime(obj.getLong("ctime"));
                                    article.mEssayId = obj.getLong("essay_id");
                                    article.mArticleId = obj.getLong("request_id");
                                    article.mScore = obj.getDouble("score");
                                    article.mSample_content = obj.getString("sample_content");
                                    article.mType = obj.getInt("type");
                                    article.mNetworkOrDatabase = ENetworkArticle;

                                    if (!obj.isNull("rq")) {
                                        JSONObject objInObj = obj.getJSONObject("rq");
                                        article.mTeacherName = objInObj.getString("teacher_name");
                                        article.mCount = objInObj.getInt("essay_cnt");
                                        article.mEndtime = getDateToSecondTime(objInObj.getLong("end_time"));
                                    } else {
                                        article.mCount = 0;
                                        article.mEndtime = "";
                                        article.mTeacherName = "默认";
                                    }

                                    articleList.add(article);
                                }
                                mStudentSubmittedArticleListByUidCallback.StudentArticleListByUid(articleList);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mStudentSubmittedArticleListByUidCallback.ErrorNetwork();
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "json_object");
    }

    public interface SubmittedArticleCallback {
        void ArticleSubmitted(long essayId);

        void ErrorNetwork();
    }

    public SubmittedArticleCallback mSubmittedArticleCallback;

    //    public final String test = "rid=402908&uid=160320&content=xu hai test";
    public void SubmitArticle(final long articleId, final int uid, final long essayId, final String content, final String title) {
        String submitUrl = "http://www.pigai.org/?c=api&a=essayadd";

        StringRequest sr = new StringRequest(Method.POST, submitUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    long essayId = obj.getLong("eid");
                    Log.e("----------", "" + essayId);
                    if (essayId > 0) {
                        mSubmittedArticleCallback.ArticleSubmitted(essayId);
                    } else {
                        mSubmittedArticleCallback.ErrorNetwork();
                    }

                } catch (JSONException e) {
                    mSubmittedArticleCallback.ErrorNetwork();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "" + error.getMessage() + "," + error.toString());
                mSubmittedArticleCallback.ErrorNetwork();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", String.valueOf(uid));
                params.put("rid", String.valueOf(articleId));
                params.put("eid", String.valueOf(essayId));
                params.put("content", content);
                params.put("title", title);
                params.put("token", User_Token);
                return params;
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "text/html");
//                return headers;
//            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(sr,
                tag_json_obj);
    }

    public interface DeleteNetworkArticleByEssayIdCallback {
        void ArticleDeleted(int error);

        void ErrorNetwork();
    }

    public DeleteNetworkArticleByEssayIdCallback mDeleteNetworkArticleByEssayIdCallback;

    public void DeleteNetworkArticleByEssayId(long essayId, int uid, String sid) {
        String delUrl = "http://www.pigai.org/?c=api&a=essayModifyType&eid=" + String.valueOf(essayId) +
                "&uid=" + String.valueOf(uid) +
                "&token=" + User_Token +
                "&sid=" + String.valueOf(sid) +
                "&type=-1";
        StringRequest delEssayReq = new StringRequest(Method.GET,
                delUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int error = obj.getInt("success");
                    mDeleteNetworkArticleByEssayIdCallback.ArticleDeleted(error);
                } catch (JSONException e) {
                    mDeleteNetworkArticleByEssayIdCallback.ErrorNetwork();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mDeleteNetworkArticleByEssayIdCallback.ErrorNetwork();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(delEssayReq, "delete_essay_request");

    }


    public interface DeleteNetworkArticleByTeacherCallback {
        void ArticleDeletedByTeacher(int error);

        void ErrorNetwork();
    }

    public DeleteNetworkArticleByTeacherCallback mDeleteNetworkArticleByTeacherCallback;

    public void DeleteNetworkArticleByTeacher(long rid, int uid, String sid) {
        String delUrl = "http://www.pigai.org/?c=api&a=delRidByTeacher&uid=" + String.valueOf(uid) +
                "&rid=" + String.valueOf(rid) +
                "&token=" + User_Token +
                "&sid=" + String.valueOf(sid);

        StringRequest delArticleReq = new StringRequest(Method.GET,
                delUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int error = obj.getInt("success");
                    mDeleteNetworkArticleByTeacherCallback.ArticleDeletedByTeacher(error);
                } catch (JSONException e) {
                    mDeleteNetworkArticleByTeacherCallback.ErrorNetwork();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mDeleteNetworkArticleByTeacherCallback.ErrorNetwork();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(delArticleReq, "delete_essay_request");
    }


    public interface TeacherModifyStudentScoreCallback {
        void ScoreModified(int error);

        void ErrorNetwork();
    }

    public TeacherModifyStudentScoreCallback mTeacherModifyStudentScoreCallback;

    public void TeacherModifyStudentScore(int uid, long essayId, double score) {
        String requirementUrl = "http://www.pigai.org/?c=api&a=essayModifyScore&eid=" + String.valueOf(essayId)
                + "&score=" + String.valueOf(score)
                + "&uid=" + String.valueOf(uid)
                + "&token=" + User_Token;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                requirementUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int err = response.getInt("re");
                            mTeacherModifyStudentScoreCallback.ScoreModified(err);
                        } catch (JSONException e) {
                            mTeacherModifyStudentScoreCallback.ErrorNetwork();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mTeacherModifyStudentScoreCallback.ErrorNetwork();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "modify_score");
    }

    public interface UserInfoModifyCallback {
        void UserInfoModify(int err);

        void ErrorNetwork();
    }

    public UserInfoModifyCallback mUserInfoModifyCallback;

    public void UserInfoModify(final int uid, final String name, final String student_sex, final String school,
                               final String student_number, final String classID, final String phone) {
        String modifyUrl = "http://www.pigai.org/?c=api&a=userModifyUinfo";
        StringRequest sr = new StringRequest(Method.POST, modifyUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                try {
                    JSONObject obj = new JSONObject(response);
                    int successId = obj.getInt("success");
                    mUserInfoModifyCallback.UserInfoModify(successId);

                } catch (JSONException e) {
                    mUserInfoModifyCallback.ErrorNetwork();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "" + error.getMessage() + "," + error.toString());
                mUserInfoModifyCallback.ErrorNetwork();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", String.valueOf(uid));
                if (TextUtils.isEmpty(name)) {
                    params.put("name", "请填写名字");
                } else {
                    params.put("name", name);
                }
                if (TextUtils.isEmpty(school)) {
                    params.put("school", "请填写学校");
                } else {
                    params.put("school", school);
                }
                params.put("student_number", student_number);
                params.put("class", classID);
                params.put("sex", student_sex);
                params.put("tel", phone);
                params.put("token", User_Token);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(sr, tag_json_obj);
    }

    public interface TeacherAddTextCommentCallback {
        void TextCommentAdded();

        void ErrorNetwork();
    }

    public TeacherAddTextCommentCallback mTeacherAddTextCommentCallback;

    public void TeacherAddTextComment(int uid, long essayId, String textComment) {
        String requirementUrl = "http://www.pigai.org/?c=api&a=essayPlun&uid=" + String.valueOf(uid) +
                "&token=" + User_Token +
                "&eid=" +
                String.valueOf(essayId) + "&txt=";// + textComment;

        String textCommentUtf;
        try {
            textCommentUtf = URLEncoder.encode(textComment, "UTF-8");
            requirementUrl = requirementUrl + textCommentUtf;
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                requirementUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int err = response.getInt("re");
                            if (err > 0) {
                                mTeacherAddTextCommentCallback.TextCommentAdded();
                            } else {
                                mTeacherAddTextCommentCallback.ErrorNetwork();
                            }
                        } catch (JSONException e) {
                            mTeacherAddTextCommentCallback.ErrorNetwork();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mTeacherModifyStudentScoreCallback.ErrorNetwork();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "modify_score");
    }


    public interface DataPostedCallback {
        void DataPosted();

        void ErrorNetwork();
    }

    public DataPostedCallback mDataPostedCallback;

    public void PostDataToServer(File filePart, int uid, long eid, int duration) {
        String postUrl = "http://www.pigai.org/?c=api&a=essayPlunMp3&uid=" + String.valueOf(uid) +
                "&token=" + User_Token +
                "&eid=" + String.valueOf(eid)
                + "&duration=" + String.valueOf(duration);
        MultipartRequest multipartRequest = new MultipartRequest("mp3", postUrl,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mDataPostedCallback.DataPosted();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mDataPostedCallback.ErrorNetwork();
            }
        }, filePart);

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(multipartRequest, "multi-part-post-request");
    }

    public interface GetCommentsByEssayIdCallback {
        void GetComments(List<CommentBase> commentsList);

        void ErrorNetwork();
    }

    public GetCommentsByEssayIdCallback mGetCommentsByEssayIdCallback;

    public void GetCommentsByEssayId(long eid) {

        final String teacherarticleurl = "http://www.pigai.org/?c=api&a=essayPlunList&eid=" + String.valueOf(eid);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(teacherarticleurl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        List<CommentBase> commentsList = new ArrayList<>();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                CommentBase comment = new CommentBase();
                                comment.mCommentId = obj.getLong("id");
                                comment.mUid = obj.getInt("user_id");
                                comment.mType = obj.getInt("type");
                                comment.mDate = getDateToSecondTime(obj.getLong("ctime"));
                                comment.mTextComments = obj.getString("txt");
                                comment.mSoundUrl = "http://pic.pigai.org/upload/audio/" + obj.getString("txt");
                                comment.mTeacherName = obj.getString("uname");
                                JSONObject objPic = obj.getJSONObject("pic");
                                comment.mPic = objPic.getString("s");
                                comment.mDuration = obj.getInt("duration");
                                commentsList.add(comment);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mGetCommentsByEssayIdCallback.GetComments(commentsList);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO should be error
//                mGetCommentsByEssayIdCallback.ErrorNetwork();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }


    public interface TeacherDeleteCommentCallback {
        void CommentDeleted();

        void ErrorNetwork();
    }

    public TeacherDeleteCommentCallback mTeacherDeleteCommentCallback;

    public void TeacherDeleteComment(int uid, long commentId) {
        String requirementUrl = "http://www.pigai.org/?c=api&a=essayPlunDel&uid=" + String.valueOf(uid) +
                "&token=" + User_Token +
                "&id=" + String.valueOf(commentId);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                requirementUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int err = response.getInt("re");
                            if (err > 0) {
                                mTeacherDeleteCommentCallback.CommentDeleted();
                            } else {
                                mTeacherDeleteCommentCallback.ErrorNetwork();
                            }
                        } catch (JSONException e) {
                            mTeacherDeleteCommentCallback.ErrorNetwork();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mTeacherDeleteCommentCallback.ErrorNetwork();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "delete_comment");
    }


    public interface UserPhotoModifyCallback {
        void UserPhotoModify(String sImage, String bImage);

        void ErrorNetwork();
    }

    public UserPhotoModifyCallback mUserPhotoModifyCallback;

    public void UserPhotoModify(int uid, File file) {
        String modifyUrl = "http://www.pigai.org/?c=api&a=head&uid=" + String.valueOf(uid) + "&token=" + User_Token;

        MultipartRequest photoRequest = new MultipartRequest("img", modifyUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                try {
                    JSONObject imageJson = new JSONObject(response);
                    String s = imageJson.getString("s");
                    String b = imageJson.getString("b");
                    mUserPhotoModifyCallback.UserPhotoModify(s, b);

                } catch (Exception e) {
                    Log.e(TAG, "Volley" + "find exception");
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.e(TAG, "Volley: " + volleyError.getMessage());
                Log.e(TAG, "Volley" + volleyError.getMessage() + "," + volleyError.toString());
                mUserPhotoModifyCallback.ErrorNetwork();
            }
        }, file);

        photoRequest.setShouldCache(false);
        photoRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(photoRequest);
    }

    public interface SubmittedBackInfoCallback {
        void BackInfoSubmitted(String message);

        void ErrorNetwork();
    }

    public SubmittedBackInfoCallback mSubmittedBackInfoCallback;

    public void SubmitBackInfo(final long uid, final String content, final String phone, final String mobileInfo) {
        String submitUrl = "http://www.pigai.org/api/api.php?act=feedback";

        StringRequest sr = new StringRequest(Method.POST, submitUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int error = obj.getInt("error");
                    String message = obj.getString("msg");
                    if (error == 0) {
                        mSubmittedBackInfoCallback.BackInfoSubmitted(message);
                    } else {
                        mSubmittedBackInfoCallback.ErrorNetwork();
                    }

                } catch (JSONException e) {
                    mSubmittedBackInfoCallback.ErrorNetwork();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "" + error.getMessage() + "," + error.toString());
                mSubmittedBackInfoCallback.ErrorNetwork();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", String.valueOf(uid));
                params.put("content", content);
                params.put("contact", phone);
                params.put("info", mobileInfo);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(sr,
                tag_json_obj);
    }

    public interface GetServerTimeCallback {
        void GetServerTime(long timeStamp);

        void FailGetServerTime();
    }

    public GetServerTimeCallback mGetServerTimeCallback;

    public void GetServerTime() {
        String serverTimeUrl = "http://www.pigai.org/?c=api&a=servertime";
        StringRequest serverTimeReq = new StringRequest(Method.GET,
                serverTimeUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    long timeStamp = obj.getLong("time");
                    mGetServerTimeCallback.GetServerTime(timeStamp);
                } catch (JSONException e) {
                    mGetServerTimeCallback.FailGetServerTime();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mGetServerTimeCallback.FailGetServerTime();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(serverTimeReq, "getverifycode_request");

    }

    public interface GetVerifyCodeCallback {
        void VerifyCode(String phone, int errorCode, String errorMsg);
    }

    public GetVerifyCodeCallback mGetVerifyCodeCallback;

    public void GetVerifyCode(final String phone, final String countryCode, int type) {   //3 find password, 1 user register
        String verifyCodeUrl = "http://www.pigai.org/?c=api&a=yzmsmssent&tel=" + phone +
                "&countrycode=" + countryCode +
                "&type=" + String.valueOf(type);
        StringRequest verifyCodeReq = new StringRequest(Method.GET,
                verifyCodeUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");  //1 is success
                    String msg = obj.getString("msg");
                    mGetVerifyCodeCallback.VerifyCode(phone, success, msg);
                } catch (JSONException e) {
                    mGetVerifyCodeCallback.VerifyCode(phone, -101, "服务器错误");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mGetVerifyCodeCallback.VerifyCode(phone, -100, "网络错误");
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(verifyCodeReq, "getverifycode_request");
    }


    public interface ResetPasswordByVerifyCodeCallback {
        void ResetPasswordByVerifyCode(int success, String phone, String errorMsg);
    }

    public ResetPasswordByVerifyCodeCallback mResetPasswordByVerifyCodeCallback;

    public void ResetPasswordByVerifyCode(final String phone, final String countryCode, final String verifyCode, final String newPassword) {
        String submitUrl = "http://www.pigai.org/?c=api&a=yzmSj";

        StringRequest sr = new StringRequest(Method.POST, submitUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int error = obj.getInt("success"); //1 is success
                    String msg = obj.getString("msg");
                    mResetPasswordByVerifyCodeCallback.ResetPasswordByVerifyCode(error, phone, msg);

                } catch (JSONException e) {
                    mResetPasswordByVerifyCodeCallback.ResetPasswordByVerifyCode(-101, phone, "服务器错误");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mResetPasswordByVerifyCodeCallback.ResetPasswordByVerifyCode(-100, phone, "网络错误");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("type", "3");
                params.put("countrycode", countryCode);
                params.put("tel", phone);
                params.put("pwd", newPassword);
                params.put("yzm_sj", verifyCode);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(sr,
                tag_json_obj);
    }

    public interface ResetPasswordByEmailCallback {
        void ResetPasswordByEmail(int success, String email, String errorMsg);
    }

    public ResetPasswordByEmailCallback mResetPasswordByEmailCallback;

    public void ResetPasswordByEmail(final String email) {

        String sid = SidTokenTool.getSidForResetPasswordByEmail(email);
        String resetPasswordByEmailUrl = "http://www.pigai.org/?c=api&a=sendEmail&email=" + email +
                "&sid=" + sid;
        StringRequest resetPasswordbyEmailReq = new StringRequest(Method.GET,
                resetPasswordByEmailUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success"); //1 is success
                    String msg = obj.getString("msg");
                    mResetPasswordByEmailCallback.ResetPasswordByEmail(success, email, msg);
                } catch (JSONException e) {
                    mResetPasswordByEmailCallback.ResetPasswordByEmail(-101, email, "服务器错误");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mResetPasswordByEmailCallback.ResetPasswordByEmail(-100, email, "网络错误");
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(resetPasswordbyEmailReq, "resetpasswordbyemail_request");
    }

    public interface ResetPasswordByOldPasswordCallback {
        void ResetPasswordByOldPassword(int success, String errorMsg);
    }

    public ResetPasswordByOldPasswordCallback mResetPasswordByOldPasswordCallback;

    public void ResetPasswordByOldPassword(final long uid, final String newPassword, final String oldPassword) {
        String submitUrl = "http://www.pigai.org/?c=api&a=modifyPassword";

        StringRequest sr = new StringRequest(Method.POST, submitUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int error = obj.getInt("success"); //1 is success
                    String msg = obj.getString("msg");
                    mResetPasswordByOldPasswordCallback.ResetPasswordByOldPassword(error, msg);

                } catch (JSONException e) {
                    mResetPasswordByOldPasswordCallback.ResetPasswordByOldPassword(-101, "服务器错误");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mResetPasswordByOldPasswordCallback.ResetPasswordByOldPassword(-100, "网络错误");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                String sid = SidTokenTool.getSidForResetPasswordByOldPassword(uid, oldPassword);

                Map<String, String> params = new HashMap<>();
                params.put("uid", String.valueOf(uid));
                params.put("oldpwd", oldPassword);
                params.put("newpwd", newPassword);
                params.put("sid", sid);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(sr,
                tag_json_obj);
    }

    public interface RegisterUserByVerifyCodeCallback {
        void RegisterUserByVerifyCode(int success, String phone, String errorMsg);
    }

    public RegisterUserByVerifyCodeCallback mRegisterUserByVerifyCodeCallback;

    public void RegisterUserByVerifyCode(final String phone, final String countryCode, final String verifyCode, final String newPassword, final String userType) {
        String submitUrl = "http://www.pigai.org/?c=api&a=yzmSj";

        StringRequest sr = new StringRequest(Method.POST, submitUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int error = obj.getInt("success"); //1 is success
                    String msg = obj.getString("msg");
                    mRegisterUserByVerifyCodeCallback.RegisterUserByVerifyCode(error, phone, msg);

                } catch (JSONException e) {
                    mRegisterUserByVerifyCodeCallback.RegisterUserByVerifyCode(-101, phone, "服务器错误");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mRegisterUserByVerifyCodeCallback.RegisterUserByVerifyCode(-100, phone, "网络错误");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("tel", phone);
                params.put("countrycode", countryCode);
                params.put("pwd", newPassword);
                params.put("yzm_sj", verifyCode);
                params.put("type", "1");
                params.put("st", userType);  //st  值为t时为老师，其他为学生
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(sr,
                tag_json_obj);
    }

    public interface GetStudentTeacherCallback {
        void StudentTeacher(int success, Map<Integer, String> teacherMap, String errorMsg);
    }

    public GetStudentTeacherCallback mGetStudentTeacherCallback;

    public void GetStudentTeacher(long uid) {
        String sid = SidTokenTool.getSidForGetStudentTeacher(uid);
        String studentTeacherlUrl = "http://www.pigai.org/?c=api&a=getMyTeacher&uid=" + String.valueOf(uid) +
                "&token=" + User_Token +
                "&sid=" + sid;

        JsonObjectRequest jsonObjectReq_findTeacherList = new JsonObjectRequest(Method.GET, studentTeacherlUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            Map<Integer, String> teacherUidAndNameMap = new HashMap<>();
                            JSONArray jsonArray = response.getJSONArray("msg");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String teacherName = obj.getString("teacher_name");
                                Integer uid = -1;
                                try {
                                    uid = Integer.parseInt(obj.getString("tid"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                teacherUidAndNameMap.put(uid, teacherName);
                            }
                            mGetStudentTeacherCallback.StudentTeacher(1, teacherUidAndNameMap, "成功");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mGetStudentTeacherCallback.StudentTeacher(-101, null, "服务器错误");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mGetStudentTeacherCallback.StudentTeacher(-100, null, "网络错误");
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectReq_findTeacherList, "json_object");
    }

    public interface GetStudentClassCallback {
        void StudentClass(int success, ArrayList<String> className, String errorMsg);
    }

    public GetStudentClassCallback mGetStudentClassCallback;

    public void GetStudentClass(long teacher_uid) {
        String sid = SidTokenTool.getSidForGetStudentClass(teacher_uid);
        String studentTeacherlUrl = "http://www.pigai.org/?c=api&a=teacherClassList&tid=" + String.valueOf(teacher_uid) +
                "&sid=" + sid;

        final ArrayList<String> classNameArray = new ArrayList<>();

        JsonObjectRequest jsonObjectReq_findTeacherList = new JsonObjectRequest(Method.GET, studentTeacherlUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            ArrayList<String> classNameArray = new ArrayList<>();
                            JSONArray jsonArray = response.getJSONArray("msg");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String className = jsonArray.getString(i);

                                classNameArray.add(className);
                            }
                            mGetStudentClassCallback.StudentClass(1, classNameArray, "成功");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mGetStudentClassCallback.StudentClass(-101, classNameArray, "服务器错误");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mGetStudentClassCallback.StudentClass(-100, classNameArray, "网络错误");
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectReq_findTeacherList, "json_object");
    }


    public interface GetTiKuArticleCateCallback {
        void getTiKuArticleCategories(ArrayList<TiKuArticleCategory> TiKuArticleCategoryList);

        void ErrorNetwork();
    }

    public GetTiKuArticleCateCallback mGetTiKuArticleCateCallback;


    public void getTiKuArticleCategories(int uid, String sid) {

        String tikuCategoriesUrl = "http://www.pigai.org/api-tikuRequestCate&uid=" + String.valueOf(uid) + "&sid=" + sid;

        JsonObjectRequest jsonObjectTiKuArticleCategories = new JsonObjectRequest(Method.GET, tikuCategoriesUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String status = response.optString("status");

                        if (status != null && status.equals("Success")) {
                            JSONArray categoriesJSONArray = response.optJSONArray("data");
                            ArrayList<TiKuArticleCategory> tiKuArticleCategoryArrayList = new ArrayList<>();

                            for (int i = 0; i < categoriesJSONArray.length(); i++) {
                                TiKuArticleCategory tiKuArticleCategory = new TiKuArticleCategory();
                                try {
                                    JSONObject categoriesJSON = (JSONObject) categoriesJSONArray.get(i);
                                    tiKuArticleCategory.setCate_id(categoriesJSON.optInt("cate_id"));
                                    tiKuArticleCategory.setCate_name(categoriesJSON.optString("cate_name"));
                                    tiKuArticleCategoryArrayList.add(tiKuArticleCategory);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            mGetTiKuArticleCateCallback.getTiKuArticleCategories(tiKuArticleCategoryArrayList);
                        } else {
                            mGetTiKuArticleCateCallback.ErrorNetwork();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mGetTiKuArticleCateCallback.ErrorNetwork();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectTiKuArticleCategories, "json_object");

    }

    public interface GetTiKuArticleListByOneCidCallback {
        void getTiKuArticleListByTiKuCid(ArrayList<TiKuArticleByOneCategory> TiKuArticleCategoryList);

        void ErrorNetwork();
    }

    public GetTiKuArticleListByOneCidCallback mGetTiKuArticleListByOneCidCallback;


    public void getTiKuArticleListByOneCid(int uid, int cid, int start, String sid) {

        String tikuArticleListByCategoryUrl = "http://www.pigai.org/api-tikuRequestList&uid="
                + String.valueOf(uid)
                + "&cid=" + String.valueOf(cid)
                + "&sid=" + sid
                + "&start=" + String.valueOf(start)
                + "&limit=" + String.valueOf(10);
        Log.d(TAG, tikuArticleListByCategoryUrl);
        JsonObjectRequest jsonObjectTiKuArticleListByOneCid = new JsonObjectRequest(Method.GET, tikuArticleListByCategoryUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String status = response.optString("status");

                        if (status != null && status.equals("Success")) {
                            try {
                                JSONArray jsonArray = response.optJSONArray("data");
                                ArrayList<TiKuArticleByOneCategory> tiKuArticleByOneCategoryList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                    TiKuArticleByOneCategory tiKuArticleByOneCategory = new TiKuArticleByOneCategory();

                                    tiKuArticleByOneCategory.setRid(jsonObject.optLong("request_id"));
                                    tiKuArticleByOneCategory.setArticleTitle(jsonObject.optString("essay_title"));
                                    tiKuArticleByOneCategory.setEndTime(getDateToSecondTime(jsonObject.optLong("valid_end_time")));
                                    tiKuArticleByOneCategory.setCount(jsonObject.optInt("essay_cnt"));

                                    tiKuArticleByOneCategoryList.add(tiKuArticleByOneCategory);
                                }
                                mGetTiKuArticleListByOneCidCallback.getTiKuArticleListByTiKuCid(tiKuArticleByOneCategoryList);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            mGetTiKuArticleListByOneCidCallback.ErrorNetwork();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mGetTiKuArticleListByOneCidCallback.ErrorNetwork();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectTiKuArticleListByOneCid, "json_object");

    }

    public interface GetPiGaiAdsCallback {
        void getPiGaiAdsResult(ArrayList<Advertisement> advertisementList);

        void ErrorNetwork();
    }

    public GetPiGaiAdsCallback mGetPiGaiAdsCallback;

    public void getPiGaiAds() {

        //// TODO: 2016/4/22    线上  open.pigai.org
//        String ads_url = "http://qq.pigai.org/?c=api&a=ads";
        String ads_url = "http://open.pigai.org/?c=api&a=ads";
        JsonObjectRequest jsonObjectPiGaiAds = new JsonObjectRequest(Method.GET, ads_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String status = response.optString("status");

                        if (status != null && status.equals("Success")) {
                            try {
                                JSONArray jsonArray = response.optJSONArray("data");
                                ArrayList<Advertisement> advertisementList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                    Advertisement advertisement = new Advertisement();
                                    advertisement.setImg_url(jsonObject.optString("img_url"));
                                    advertisement.setImg_desc(jsonObject.optString("img_desc"));
                                    advertisement.setLink(jsonObject.optString("link"));
                                    advertisementList.add(advertisement);
                                }
                                mGetPiGaiAdsCallback.getPiGaiAdsResult(advertisementList);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            mGetPiGaiAdsCallback.ErrorNetwork();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                mGetPiGaiAdsCallback.ErrorNetwork();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectPiGaiAds, "json_object");

    }


//--------------------------------------------------------------------------------------------------

    private String getDateToSecondTime(long timeStampInSecond) {
        Date date = new Date(timeStampInSecond * 1000);
        return DateFormat.format("yyyy/MM/dd HH:mm", date).toString();
    }

}
