package com.easylibs.http;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.easylibs.utils.EasyUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by easy.libs on 03-11-2016.
 */
public class EasyHttp {

    /**
     * max length for a valid LOG_TAG is 23 characters.
     */
    public static String LOG_TAG = EasyHttp.class.getSimpleName();

    public static boolean DEBUG = BuildConfig.DEBUG;

    private static EasyHttpExecutor sInstance;

    public static synchronized EasyHttpExecutor getExecutor(Context pContext) {
        if (sInstance == null) {
            try {
                Class<?> classObj = Class.forName("com.easylibs.http.volley.EasyHttpExecutorVolleyImpl");
                Constructor<?> constructor = classObj.getDeclaredConstructor(Application.class);
                constructor.setAccessible(true);
                sInstance = (EasyHttpExecutor) constructor.newInstance((Application) pContext.getApplicationContext());
                constructor.setAccessible(false);
            } catch (Exception e) {
                Log.e(LOG_TAG, "getExecutor", e);
            }
        }
        return sInstance;
    }

    /**
     * @param pDestination
     * @param pSource
     * @return
     */
    public static Map<String, String> mergeHeaders(Map<String, String> pDestination, Map<String, String> pSource) {
        if (pDestination == null) {
            pDestination = new HashMap<>();
        }
        if (pSource == null || pSource.isEmpty()) {
            return pDestination;
        }
        if (pDestination.isEmpty()) {
            pDestination.putAll(pSource);
            return pDestination;
        }
        for (String headerName : pSource.keySet()) {
            String sourceValue = pSource.get(headerName);
            String destValue = pDestination.get(headerName);
            if (EasyUtils.isBlank(destValue)) {
                // sourceValue = any, null or blank, destValue = null or blank
                pDestination.put(headerName, sourceValue);
            } else if (!EasyUtils.isBlank(sourceValue)) {
                if (destValue.contains(sourceValue)) {
                    // nothing to do, destValue already contains or equals sourceValue
                } else if (sourceValue.contains(destValue)) {
                    pDestination.put(headerName, sourceValue);
                } else {
                    pDestination.put(headerName, destValue + " " + sourceValue);
                }
            }
            if (EasyHttp.DEBUG) {
                Log.v(EasyHttp.LOG_TAG, headerName + " : " + destValue + " changed to " + pDestination.get(headerName));
            }
        }
        return pDestination;
    }
}