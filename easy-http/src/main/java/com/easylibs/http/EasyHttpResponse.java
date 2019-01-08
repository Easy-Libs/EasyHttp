package com.easylibs.http;

import java.util.Map;

/**
 * POJO class to pass response back from network layer to UI layer
 */
public class EasyHttpResponse<T> {

    /**
     * HTTP Status Code
     */
    private int statusCode;

    /**
     * HTTP response headers
     */
    private Map<String, String> headers;

    /**
     * response after parsing, may be of any custom class
     */
    private T data;

    /**
     * exception, if occurred while fetching the response
     */
    private Exception exception;

    /**
     * request for which this response is, will be null in case of sync requests
     */
    private EasyHttpRequest<T> easyHttpRequest;

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
     * @return parsed data
     */
    public T getData() {
        return data;
    }

    /**
     * @param data parsed data to set
     */
    public void setData(T data) {
        this.data = data;
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

    /**
     * @param easyHttpRequest the easyHttpRequest to set
     */
    public void setEasyHttpRequest(EasyHttpRequest<T> easyHttpRequest) {
        this.easyHttpRequest = easyHttpRequest;
    }

    /**
     * @return request for which this response is, will be null in case of sync requests
     */
    public EasyHttpRequest<T> getEasyHttpRequest() {
        return easyHttpRequest;
    }
}