package com.easylibs.http;

import android.content.Context;
import android.util.Log;

import com.easylibs.listener.EventListener;
import com.easylibs.utils.ContentType;
import com.easylibs.utils.EasyUtils;
import com.easylibs.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;

import static com.easylibs.http.EasyHttp.LOG_TAG;

/**
 * pojo class for data related to a service request which will be executed
 * by any network layer
 *
 * @author easy.libs
 */
public class EasyHttpRequest<T> {

    private WeakReference<Context> contextWeakRef;

    private int httpMethod = Method.DEPRECATED_GET_OR_POST;
    private String url;
    private HashMap<String, String> headers;

    private byte[] requestBody;
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

    private int queueBehaviour;

    private WeakReference<EventListener> eventListenerWeakRef;
    private int eventCode;

    public Context getContext() {
        return contextWeakRef == null ? null : contextWeakRef.get();
    }

    public void setContext(Context context) {
        this.contextWeakRef = new WeakReference<>(context);
    }

    public int getHttpMethod() {
        return httpMethod;
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

    public void setRequestBody(String requestBodyStr, ContentType requestBodyContentType) {
        if (EasyHttp.DEBUG) {
            Log.d(LOG_TAG, "EasyHttpRequest#setRequestBody: " + requestBodyStr);
        }
        byte[] requestBody = null;
        if (requestBodyStr != null) {
            if (requestBodyContentType == null || EasyUtils.isBlank(requestBodyContentType.getCharset())) {
                requestBody = requestBodyStr.getBytes();
            } else {
                try {
                    requestBody = requestBodyStr.getBytes(requestBodyContentType.getCharset());
                } catch (UnsupportedEncodingException uee) {
                    Log.e(LOG_TAG, "EasyHttpRequest#setRequestBody", uee);
                    requestBody = requestBodyStr.getBytes();
                }
            }
        }
        setRequestBody(requestBody, requestBodyContentType);
    }

    public void setRequestBody(byte[] requestBody, ContentType requestBodyContentType) {
        if (EasyHttp.DEBUG) {
            Log.d(LOG_TAG, "EasyHttpRequest#setRequestBody: " + (requestBodyContentType == null ? "null" : requestBodyContentType.toString()));
        }
        this.requestBody = requestBody;
        this.requestBodyContentType = requestBodyContentType;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    public ContentType getRequestBodyContentType() {
        return requestBodyContentType;
    }

    public ContentType getResponseContentType() {
        return responseContentType == null ? ContentType.JSON : responseContentType;
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

    public int getQueueBehaviour() {
        return queueBehaviour;
    }

    public void setQueueBehaviour(int queueBehaviour) {
        this.queueBehaviour = queueBehaviour;
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
     * Supported request methods.
     */
    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    /**
     * Supported request methods.
     */
    public interface QueueBehaviour {
        int DEFAULT = 0;
        int USE_NEW = 1;
    }
}