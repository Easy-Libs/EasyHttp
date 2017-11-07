package com.easylibs.utils;

/**
 * Created by easy.libs on 19/4/17.
 */
public class EasyUtils {

    public static boolean isBlank(String pText) {
        return pText == null || pText.trim().length() == 0;
    }

    /**
     * @param pStr1
     * @param pStr2
     * @return true if display of pStr1 and pStr2 will be same
     */
    public static boolean equals(String pStr1, String pStr2) {
        String temp1 = pStr1 == null ? "" : pStr1.trim();
        String temp2 = pStr2 == null ? "" : pStr2.trim();
        return temp1.equals(temp2);
    }
}
