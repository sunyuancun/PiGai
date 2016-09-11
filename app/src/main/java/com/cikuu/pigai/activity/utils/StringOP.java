package com.cikuu.pigai.activity.utils;

/**
 * Created by xuhai on 15/12/17.
 */
public class StringOP {

    public static String FulltoHalfString(String src) {
         /*全角空格为12288，半角空格为32
         * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
         * 将字符串中的全角字符转为半角
         * @param src 要转换的包含全角的任意字符串
         * @return  转换之后的字符串
         */
        char[]c=src.toCharArray();
        for(int index=0;index<c.length;index++){
            if (c[index]==12288){   //全角空格
                c[index]=(char)32;
            }
            else if (c[index]>65280 && c[index]<65375){   //其他全角字符
                c[index]=(char)(c[index]-65248);
            }
        }
        return String.valueOf(c);
    }
}
