package com.easylibs.utils;

import android.util.Log;

import com.easylibs.http.EasyHttp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;


public final class JsonUtils {

    private final static Gson M_GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static String jsonify(Object object) {
        return M_GSON.toJson(object);
    }

    /**
     * @param pJson
     * @param pType
     * @param <T>
     * @return
     */
    public static <T> T objectify(String pJson, Class<T> pType) {
        if (pJson == null || pJson.trim().length() == 0) {
            return null;
        }
        try {
            return M_GSON.fromJson(pJson, pType);
        } catch (Exception e) {
            Log.e(EasyHttp.LOG_TAG, "JsonUtils#objectify() Class " + pType + ", Json: " + pJson, e);
        }
        return null;
    }

    /**
     * @param pJson
     * @param pType
     * @param <T>
     * @return
     */
    public static <T> T objectify(String pJson, Type pType) {
        if (pJson == null || pJson.trim().length() == 0) {
            return null;
        }
        try {
            return M_GSON.fromJson(pJson, pType);
        } catch (Exception e) {
            Log.e(EasyHttp.LOG_TAG, "JsonUtils#objectify() Type: " + pType + ", Json: " + pJson, e);
        }
        return null;
    }
}
