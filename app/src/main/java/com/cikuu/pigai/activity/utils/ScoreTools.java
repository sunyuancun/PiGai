package com.cikuu.pigai.activity.utils;

/**
 * Created by Administrator on 2015/2/4.
 */
public class ScoreTools {
    public static double FM = 2;


    /**
     * 修改不规则double值
     * eg：
     * 2--2.25    -------->   2.0
     * 2.25--2.75 -------->  2.5
     * 2.75--3.25 --------> 3.0
     *
     * @param num
     * @return double  number
     */
    public static double NumberChange(double num) {
        double number = ((int) (num * 2 + 0.5)) / FM;
        return number;
    }

}
