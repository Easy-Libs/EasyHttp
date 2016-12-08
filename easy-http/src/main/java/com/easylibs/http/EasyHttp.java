package com.easylibs.http;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sachin.gupta on 03-11-2016.
 */
public class EasyHttp {

    private static final String LOG_TAG = EasyHttp.class.getSimpleName();

    private static EasyHttpExecutor sInstance;

    public static synchronized EasyHttpExecutor getExecutor(Context pContext) {
        if (sInstance == null) {
            try {
                Class<?> classObj = Class.forName("com.easylibs.http.volley.EasyHttpExecutorVolleyImpl");
                Constructor<?> constructor = classObj.getDeclaredConstructor(Context.class);
                constructor.setAccessible(true);
                sInstance = (EasyHttpExecutor) constructor.newInstance(pContext);
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
        if (pSource != null && !pSource.isEmpty()) {
            if (pDestination.isEmpty()) {
                pDestination.putAll(pSource);
            } else {
                for (String superHeaderName : pSource.keySet()) {
                    if (pDestination.containsKey(superHeaderName)) {
                        pDestination.put(superHeaderName, pSource.get(superHeaderName) + " " + pDestination.get(superHeaderName));
                    } else {
                        pDestination.put(superHeaderName, pSource.get(superHeaderName));
                    }
                }
            }
        }
        return pDestination;
    }
}