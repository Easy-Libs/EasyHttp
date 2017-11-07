package com.easylibs.http;

import android.content.Context;

import com.easylibs.listener.EventListener;
import com.easylibs.utils.ContentType;
import com.easylibs.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
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

    private String requestBody;
    private ContentType requestBodyContentType;

    private ContentType responseContentType;
    private Type responseType;
    private int socketTimeOutMs;

    private boolean setRetryCountCalled;
    private int retryCount;

    private boolean ignoreCached;

    private long cacheTtl;
    private boolean setCacheTtlCalled;

    private long cacheSoftTtl;
    private boolean refreshedResponseDeliveryRequired;

    private Object tag;

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
                return requestBody == null ? Method.GET : Method.POST;
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

    /**
     * @param postObject
     * @deprecated use {@link EasyHttpRequest#setJsonBody(Object)}
     * OR {@link EasyHttpRequest#setRequestBody(String, ContentType)} instead
     */
    @Deprecated
    public void setPostObject(Object postObject) {
        setJsonBody(postObject);
    }

    public void setJsonBody(Object jsonBody) {
        String jsonString;
        if (jsonBody instanceof CharSequence || jsonBody instanceof JsonObject || jsonBody instanceof JSONObject
                || jsonBody instanceof JsonArray || jsonBody instanceof JSONArray) {
            jsonString = jsonBody.toString();
        } else {
            jsonString = JsonUtils.jsonify(jsonBody);
        }
        setRequestBody(jsonString, ContentType.JSON);
    }

    public void setRequestBody(String requestBody, ContentType requestBodyContentType) {
        this.requestBody = requestBody;
        this.requestBodyContentType = requestBodyContentType;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public ContentType getRequestBodyContentType() {
        return requestBodyContentType;
    }

    public ContentType getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(ContentType responseContentType) {
        this.responseContentType = responseContentType;
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

    public boolean isIgnoreCached() {
        return ignoreCached;
    }

    public void setIgnoreCached(boolean ignoreCached) {
        this.ignoreCached = ignoreCached;
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

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListenerWeakRef = new WeakReference<>(eventListener);
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