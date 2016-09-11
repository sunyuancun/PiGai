package com.cikuu.pigai.activity.utils;

import com.cikuu.pigai.httprequest.VolleyRequest;

import java.util.Timer;
import java.util.TimerTask;

public class ServerTime implements VolleyRequest.GetServerTimeCallback {

    private static ServerTime mSingleInstance = null;

    public long mTimeStampInSeconds;
    private Timer increaseTimer;

    VolleyRequest mHttpRequest;

    public static ServerTime GetInstance() {
        if (mSingleInstance == null) {
            mSingleInstance = new ServerTime();
        }
        return mSingleInstance;
    }

    private ServerTime() {
        mTimeStampInSeconds = System.currentTimeMillis() / 1000;

        if(increaseTimer!= null) {
            increaseTimer.cancel();
            increaseTimer = null;
        }
        increaseTimer = new Timer();
        TimerHit updateTimer = new TimerHit();
        increaseTimer.scheduleAtFixedRate(updateTimer, 0, 1000);

        mHttpRequest = new VolleyRequest();
        mHttpRequest.mGetServerTimeCallback = this;
        mHttpRequest.GetServerTime();
    }

    public void GetServerTime(long timeStamp) {
        mTimeStampInSeconds = timeStamp;
    }

    public void FailGetServerTime() {
        mTimeStampInSeconds = System.currentTimeMillis() / 1000;
    }

    class TimerHit extends TimerTask {
        public void run() {
            mTimeStampInSeconds++;
        }
    }

    private void cancelTimer() {
        if(increaseTimer!= null) {
            increaseTimer.cancel();
            increaseTimer = null;
        }
    }

}
