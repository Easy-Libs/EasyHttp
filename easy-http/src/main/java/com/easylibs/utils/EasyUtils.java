package com.easylibs.utils;

/**
 * Created by easy.libs on 19/4/17.
 */
public class EasyUtils {

    public static boolean isBlank(String pText) {
        return pText == null || pText.trim().length() == 0;
    }
}
