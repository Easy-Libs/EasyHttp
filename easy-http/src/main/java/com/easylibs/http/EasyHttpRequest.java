package com.easylibs.http;

import android.content.Context;

import com.easylibs.listener.EventListener;

import java.lang.ref.WeakReference;
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

    private Class<T> responseType;
    private int socketTimeOutMs;

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

    public Object getPostObject() {
        return postObject;
    }

    public void setPostObject(Object postObject) {
        this.postObject = postObject;
    }

    public Class<T> getResponseType() {
        return responseType;
    }

    public void setResponseType(Class<T> responseType) {
        this.responseType = responseType;
    }

    public int getSocketTimeOutMs() {
        return socketTimeOutMs;
    }

    public void setSocketTimeOutMs(int socketTimeOutMs) {
        this.socketTimeOutMs = socketTimeOutMs;
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
    public <T> void onResponse(EasyHttpResponse<T> pEasyHttpResponse) {
        if (eventListenerWeakRef == null) {
            return;
        }
        EventListener listener = eventListenerWeakRef.get();
        if (listener != null) {
            pEasyHttpResponse.setEasyHttpRequest(this);
            listener.onEvent(eventCode, pEasyHttpResponse);
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