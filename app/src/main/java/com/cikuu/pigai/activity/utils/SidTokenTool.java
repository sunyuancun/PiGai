package com.cikuu.pigai.activity.utils;

import com.cikuu.pigai.businesslogic.Student;

/**
 * User: Yuancun Sun@cikuu.com
 * Date: 2015-10-14
 * Time: 10:28
 * Protect: PiGai_v1.3
 */
public class SidTokenTool {

    /**
     * get sid   when   student  delete article
     *
     * @param uid
     * @param essayId
     * @return
     */
    public static String getSidKeyOfStudentDeleteNetworkArticle(int uid, long essayId) {
        String md5String = String.valueOf(uid) + String.valueOf(essayId) + "_q2w#e";
        String oldKey = md5(md5String);
        String newKey = oldKey.substring(2, 12);
        return newKey;
    }

    /**
     * get sid   when   teacher  delete article
     *
     * @param uid
     * @param rid
     * @return
     */
    public static String getSidKeyOfTeacherDeleteNetworkArticle(int uid, long rid) {
        String md5String = String.valueOf(uid) + String.valueOf(rid) + "_pgapi";
        String oldKey = md5(md5String);
        String newKey = oldKey.substring(2, 12);
        return newKey;
    }

    /**
     * md5
     *
     * @param s
     * @return
     */
    public static final String md5(final String s) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String getSidForResetPasswordByEmail(String email) {
        String md5String = email + "_#f!dn73z";
        String oldKey = md5(md5String);
        String newKey = oldKey.substring(2, 12);
        return newKey;
    }

    public static String getSidForResetPasswordByOldPassword(long uid, String oldPassword) {
        String md5String = String.valueOf(uid) + oldPassword + "_zy@dp7nu";
        String oldKey = md5(md5String);
        String newKey = oldKey.substring(2, 12);
        return newKey;
    }

    public static String getSidForGetStudentTeacher(long uid) {
        String md5String = String.valueOf(uid) + "_g2m#t&";
        String oldKey = md5(md5String);
        String newKey = oldKey.substring(2, 12);
        return newKey;
    }

    public static String getSidForGetStudentClass(long tid) {
        String md5String = String.valueOf(tid) + "_tcl#6&w";
        String oldKey = md5(md5String);
        String newKey = oldKey.substring(2, 12);
        return newKey;
    }

    public static String getSidForGetTikuArticleCate(int uid) {
        String md5String = String.valueOf(uid) + "_tiku#8231";
        String oldKey = md5(md5String);
        String newKey = oldKey.substring(2, 12);
        return newKey;
    }

    public static String getSidForGetTikuArticleListByOneCate(int uid, int cid) {
        String md5String = String.valueOf(uid) + String.valueOf(cid) + "_tiku&w!e";
        String oldKey = md5(md5String);
        String newKey = oldKey.substring(2, 12);
        return newKey;
    }

}
