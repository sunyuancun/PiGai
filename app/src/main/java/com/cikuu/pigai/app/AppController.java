package com.cikuu.pigai.app;

import com.android.volley.Cache;
import com.cikuu.pigai.R;
import com.cikuu.pigai.httprequest.LruBitmapCache;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.umeng.analytics.MobclickAgent;

public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private CacheImageLoader mCacheImageLoader;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //科大讯飞语音sdk初始化
        SpeechUtility.createUtility(AppController.this, SpeechConstant.APPID + getString(R.string.app_xunfei_id));
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public CacheImageLoader getCacheImageLoader() {
        getRequestQueue();
        if (mCacheImageLoader == null) {
            mCacheImageLoader = new CacheImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mCacheImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public class CacheImageLoader extends ImageLoader {

        private final LruBitmapCache mMemoryCache;
        private final Cache mDiskCache;

        public CacheImageLoader(RequestQueue queue, LruBitmapCache memoryCache) {
            super(queue, memoryCache);
            mMemoryCache = memoryCache;
            mDiskCache = queue.getCache();
        }

        public LruBitmapCache getMemoryCache() {
            return mMemoryCache;
        }

        public Cache getDiskCache() {
            return mDiskCache;
        }
    }
}
