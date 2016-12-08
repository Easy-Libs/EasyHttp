package com.easylibs.http;

import com.easylibs.listener.EventListener;

import java.util.HashMap;

/**
 * pojo class for data related to a service request which will be executed
 * by any network layer
 *
 * @author sachin.gupta
 */
public class EasyHttpRequest<T> {

    private int httpMethod;
    private String url;
    private HashMap<String, String> headers;
    private HashMap<String, String> postParams;
    private Object postObject;

    private Class<T> responseType;
    private int requestTimeOut;

    private int eventCode;
    private Object requestData;
    private EventListener eventListener;

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

    public HashMap<String, String> getPostParams() {
        return postParams;
    }

    public void setPostParams(HashMap<String, String> postParams) {
        this.postParams = postParams;
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

    public int getRequestTimeOut() {
        return requestTimeOut;
    }

    public void setRequestTimeOut(int requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public Object getRequestData() {
        return requestData;
    }

    public void setRequestData(Object requestData) {
        this.requestData = requestData;
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * @param pEasyHttpResponse
     */
    public <T> void onResponse(EasyHttpResponse<T> pEasyHttpResponse) {
        getEventListener().onEvent(getEventCode(), pEasyHttpResponse);
    }

    /**
     * @param pResponseError
     */
    public void onError(Exception pResponseError) {
        EasyHttpResponse easyHttpResponse = new EasyHttpResponse();
        easyHttpResponse.setEasyHttpRequest(this);
        easyHttpResponse.setSuccess(false);
        easyHttpResponse.setException(pResponseError);
        getEventListener().onEvent(getEventCode(), easyHttpResponse);
    }

    /**
     * @author sachin.gupta
     */
    public static class Method {

        public static final int GET = 1;
        public static final int POST = 2;
    }
}