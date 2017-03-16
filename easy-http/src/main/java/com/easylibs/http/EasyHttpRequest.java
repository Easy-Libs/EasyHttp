package com.easylibs.http;

import android.content.Context;
import android.util.Log;

import com.easylibs.listener.EventListener;
import com.easylibs.utils.JsonUtils;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * pojo class for data related to a service request which will be executed
 * by any network layer
 *
 * @author easy.libs
 */
public class EasyHttpRequest<T> {

    private WeakReference<Context> contextWeakRef;

    private int httpMethod;
    private String url;
    private HashMap<String, String> headers;
    private Object postObject;

    private Type responseType;
    private int socketTimeOutMs;

    private boolean setRetryCountCalled;
    private int retryCount;

    private boolean setCacheTtlCalled;
    private long cacheTtl;
    private long cacheSoftTtl;
    private boolean refreshedResponseDeliveryRequired;

    private WeakReference<EventListener> eventListenerWeakRef;
    private int eventCode;

    public Context getContext() {
        return contextWeakRef == null ? null : contextWeakRef.get();
    }

    public void setContext(Context context) {
        this.contextWeakRef = new WeakReference<>(context);
    }

    public int getHttpMethod() {
        switch (httpMethod) {
            case Method.GET:
            case Method.POST: {
                return httpMethod;
            }
            default: {
                return postObject == null ? Method.GET : Method.POST;
            }
        }
    }

    public void setHttpMethod(int httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public Object getPostObject() {
        return postObject;
    }

    public void setPostObject(Object postObject) {
        this.postObject = postObject;
    }

    public Type getResponseType() {
        return responseType;
    }

    public void setResponseType(Type responseType) {
        this.responseType = responseType;
    }

    public void setResponseType(Class<T> responseClassType) {
        this.responseType = responseClassType;
    }

    public int getSocketTimeOutMs() {
        return socketTimeOutMs;
    }

    public void setSocketTimeOutMs(int socketTimeOutMs) {
        this.socketTimeOutMs = socketTimeOutMs;
    }

    public int getRetryCount() {
        if (!setRetryCountCalled) {
            // by default retry once and only for GET requests
            return getHttpMethod() == Method.GET ? 1 : 0;
        }
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.setRetryCountCalled = true;
        this.retryCount = retryCount;
    }

    public long getCacheTtl() {
        if (!setCacheTtlCalled) {
            // by default use cache only for GET requests
            return getHttpMethod() == Method.GET ? 0 : -1;
        }
        return cacheTtl;
    }

    public void setCacheTtl(long cacheTtl) {
        this.setCacheTtlCalled = true;
        this.cacheTtl = cacheTtl;
    }

    public long getCacheSoftTtl() {
        return cacheSoftTtl;
    }

    public void setCacheSoftTtl(long cacheSoftTtl) {
        setCacheSoftTtl(cacheSoftTtl, false);
    }

    public void setCacheSoftTtl(long cacheSoftTtl, boolean refreshedResponseDeliveryRequired) {
        this.cacheSoftTtl = cacheSoftTtl;
        this.refreshedResponseDeliveryRequired = refreshedResponseDeliveryRequired;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListenerWeakRef = new WeakReference<>(eventListener);
    }

    public String getPostJson() {
        String postJson = null;
        if (postObject != null) {
            if (postObject instanceof String) {
                postJson = (String) postObject;
            } else if (postObject instanceof JsonObject || postObject instanceof JSONObject) {
                postJson = postObject.toString();
            } else {
                postJson = JsonUtils.jsonify(postObject);
            }
        }
        if (EasyHttp.DEBUG) {
            Log.v(EasyHttp.LOG_TAG, "Post Body: " + postJson);
        }
        return postJson;
    }

    /**
     * @param pEasyHttpResponse
     */
    public void onResponse(EasyHttpResponse<T> pEasyHttpResponse) {
        if (eventListenerWeakRef == null) {
            return;
        }
        EventListener listener = eventListenerWeakRef.get();
        if (listener != null) {
            pEasyHttpResponse.setEasyHttpRequest(this);
            listener.onEvent(eventCode, pEasyHttpResponse);
            if (!refreshedResponseDeliveryRequired) {
                eventListenerWeakRef = null;
            }
        }
    }

    /**
     * @author sachin.gupta
     */
    public static class Method {

        public static final int GET = 1;
        public static final int POST = 2;
    }
}