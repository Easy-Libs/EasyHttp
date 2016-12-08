package com.easylibs.utils;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;


public final class JsonUtils {

    private final static String LOG_TAG = JsonUtils.class.getSimpleName();

    private final static Gson M_GSON = new Gson();

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
            Log.e(LOG_TAG, "objectify() Class " + pType + ", Json: " + pJson, e);
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
            Log.e(LOG_TAG, "objectify() Type: " + pType + ", Json: " + pJson, e);
        }
        return null;
    }
}
