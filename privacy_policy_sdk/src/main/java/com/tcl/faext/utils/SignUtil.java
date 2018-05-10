package com.tcl.faext.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class SignUtil {
    private static String KEY = "#87&$#1@90";
    private static String SPLIT = "&";

    public static String generateSign(List<String> signName) {
        StringBuilder builder = new StringBuilder();
        for (String name : signName) {
            builder.append(name);
            builder.append(SPLIT);
        }
        builder.append(KEY);
        return md5(builder.toString());
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
