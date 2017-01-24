package com.easylibs.http;

import java.util.Map;

/**
 * POJO class to pass response back from network layer to UI layer
 */
public class EasyHttpResponse<T> {

    private EasyHttpRequest easyHttpRequest;

    /**
     * response after parsing, may be of any custom class
     */
    private T data;
    /**
     * response headers
     */
    private int statusCode;
    /**
     * response headers
     */
    private Map<String, String> headers;
    /**
     * exception, if occurred while fetching the response
     */
    private Exception exception;

    public EasyHttpRequest getEasyHttpRequest() {
        return easyHttpRequest;
    }

    public void setEasyHttpRequest(EasyHttpRequest easyHttpRequest) {
        this.easyHttpRequest = easyHttpRequest;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
}