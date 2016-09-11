package com.cikuu.pigai.activity.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2015-10-27
 * Time: 15:01
 * Protect: PiGai_v1.3
 */
public class TimeShowUtils {

    public static final String OUTTIME_OF_SUBMIT_ARTICLE = "已截止";

    public static String showTimeOfStudentHome(String endTime) {
        if (endTime == null) {
            endTime = "2030/1/1 12:30";
        }

        String show = "";
//        long currenttimeLong = System.currentTimeMillis();
        long currenttimeLong = ServerTime.GetInstance().mTimeStampInSeconds* 1000;
        long endtimeLong = getDateToSecondTime(endTime);
        long l = endtimeLong - currenttimeLong;

        if (l < 0) {
            show = OUTTIME_OF_SUBMIT_ARTICLE;
        } else {
            long day = l / (24 * 60 * 60 * 1000);
            if (day > 0) {
                show = "还剩" + day + "天截止";
            } else {
                long hour = (l / (60 * 60 * 1000) - day * 24);
                if (hour > 0) {
                    show = "还剩" + hour + "小时截止";
                } else {
                    long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                    if (min > 0) {
                        show = "还剩" + min + "分截止";
                    } else {
                        show = OUTTIME_OF_SUBMIT_ARTICLE;
                    }
                }
            }
            //    long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        }
        return show;
    }

    private static long getDateToSecondTime(String endtime) {
        long timeStamp;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());

        try {
            timeStamp = sdf.parse(endtime).getTime();//毫秒
        } catch (ParseException e) {
            e.printStackTrace();
            timeStamp = ServerTime.GetInstance().mTimeStampInSeconds* 1000 + 24*60*60*1000;
        }
        return timeStamp;
    }
}
