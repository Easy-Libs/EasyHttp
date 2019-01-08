package com.easylibs.http.volley;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.RequestFuture;
import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpExecutor;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.utils.ContentType;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

class EasyHttpExecutorVolleyImpl implements EasyHttpExecutor {

    private RequestQueue mBackgroundRequestsQueue;
    private RequestQueue mForegroundRequestsQueue;

    private EasyHttpExecutorVolleyImpl(Context pContext) {

        // TODO - same cache should be accessible by other network layers
        File cacheDir = new File(pContext.getCacheDir(), "networkCache");
        DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir);
        BasicNetwork network = new BasicNetwork(new HurlStack());

        mBackgroundRequestsQueue = new RequestQueue(diskBasedCache, network);
        mBackgroundRequestsQueue.start();

        mForegroundRequestsQueue = new RequestQueue(diskBasedCache, network);
        mForegroundRequestsQueue.start();
    }

    private RequestQueue getQueue(Context pContext) {
        if (pContext instanceof Activity) {
            return mForegroundRequestsQueue;
        }
        return mBackgroundRequestsQueue;
    }

    @Override
    public <T> void executeAsync(EasyHttpRequest<T> pRequest) {
        if (EasyHttp.DEBUG) {
            Log.d(EasyHttp.LOG_TAG, "executeAsync URL: " + pRequest.getUrl());
        }
        int volleyHttpMethod = getVolleyHttpMethod(pRequest);
        EasyJsonListener<T> listener = new EasyJsonListener<>(pRequest);
        Request<EasyHttpResponse<T>> volleyRequest = new EasyVolleyRequest<T>(volleyHttpMethod, pRequest, listener, listener);

        getQueue(pRequest.getContext()).add(volleyRequest);
    }

    private <T> int getVolleyHttpMethod(EasyHttpRequest<T> pRequest) {
        switch (pRequest.getHttpMethod()) {
            case EasyHttpRequest.Method.GET: {
                return Request.Method.GET;
            }
            case EasyHttpRequest.Method.POST: {
                return Request.Method.POST;
            }
            case EasyHttpRequest.Method.PUT: {
                return Request.Method.PUT;
            }
            case EasyHttpRequest.Method.DELETE: {
                return Request.Method.DELETE;
            }
            case EasyHttpRequest.Method.HEAD: {
                return Request.Method.HEAD;
            }
            case EasyHttpRequest.Method.OPTIONS: {
                return Request.Method.OPTIONS;
            }
            case EasyHttpRequest.Method.TRACE: {
                return Request.Method.TRACE;
            }
            case EasyHttpRequest.Method.PATCH: {
                return Request.Method.PATCH;
            }
            default: {
                if (pRequest.getRequestBody() == null) {
                    return Request.Method.GET;
                }
                return Request.Method.POST;
            }
        }
    }

    @Override
    public <T> EasyHttpResponse<T> executeSync(final EasyHttpRequest<T> pRequest) {
        if (EasyHttp.DEBUG) {
            Log.d(EasyHttp.LOG_TAG, "executeSync URL: " + pRequest.getUrl());
        }
        if (pRequest.getResponseType() == InputStream.class) {
            return getStreamResponse(pRequest);
        }

        int volleyHttpMethod = getVolleyHttpMethod(pRequest);
        RequestFuture<EasyHttpResponse<T>> future = RequestFuture.newFuture();
        Request<EasyHttpResponse<T>> volleyRequest = new EasyVolleyRequest<T>(volleyHttpMethod, pRequest, future, future);

        getQueue(pRequest.getContext()).add(volleyRequest);

        try {
            // TODO - is timeout to be provided here again?
            EasyHttpResponse<T> response = future.get();
            response.setEasyHttpRequest(pRequest);
            return response;
        } catch (Exception e) {
            Log.e(EasyHttp.LOG_TAG, "executeSync", e);
            Throwable temp = e;
            EasyVolleyError cause = null;
            while (temp.getCause() != null) {
                temp = e.getCause();
                if (temp instanceof EasyVolleyError) {
                    cause = (EasyVolleyError) temp;
                    break;
                }
            }
            if (cause != null) {
                return EasyVolleyUtils.createEasyHttpResponse(cause);
            } else {
                EasyHttpResponse<T> response = new EasyHttpResponse<>();
                response.setEasyHttpRequest(pRequest);
                response.setStatusCode(500); // TODO
                response.setException(e);
                return response;
            }
        }
    }

    /**
     * @param pRequest
     * @return
     */
    private EasyHttpResponse getStreamResponse(EasyHttpRequest pRequest) {
        EasyHttpResponse<InputStream> easyResponse = new EasyHttpResponse<>();
        easyResponse.setEasyHttpRequest(pRequest);

        HttpResponse httpResponse;
        try {
            HttpClient client = new DefaultHttpClient();
            if (pRequest.getSocketTimeOutMs() > 0) {
                HttpParams params = client.getParams();
                HttpConnectionParams.setConnectionTimeout(params, pRequest.getSocketTimeOutMs());
                HttpConnectionParams.setSoTimeout(params, pRequest.getSocketTimeOutMs());
            }
            HttpUriRequest request;
            switch (pRequest.getHttpMethod()) {
                case EasyHttpRequest.Method.POST: {
                    request = new HttpPost(pRequest.getUrl());
                    if (pRequest.getRequestBody() != null) {
                        HttpPost httpPost = (HttpPost) request;
                        httpPost.setEntity(new StringEntity(pRequest.getRequestBody()));
                        ContentType contentType = pRequest.getRequestBodyContentType();
                        if (contentType != null) {
                            httpPost.addHeader(ContentType.HEADER_ContentType, contentType.toString());
                        }
                    }
                    break;
                }
                default: {
                    request = new HttpGet(pRequest.getUrl());
                    break;
                }
            }
            HashMap<String, String> headers = pRequest.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                for (String headerName : headers.keySet()) {
                    request.addHeader(headerName, headers.get(headerName));
                }
            }
            httpResponse = client.execute(request);
            easyResponse.setStatusCode(httpResponse.getStatusLine().getStatusCode());

            // set data
            BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(httpResponse.getEntity());
            easyResponse.setData(bufferedEntity.getContent());

            // set headers
            Header[] responseHeaders = httpResponse.getAllHeaders();
            if (responseHeaders != null && responseHeaders.length != 0) {
                headers = new HashMap<>(responseHeaders.length);
                for (Header header : responseHeaders) {
                    request.addHeader(header.getName(), header.getValue());
                }
                easyResponse.setHeaders(headers);
            }
            // TODO - cache
        } catch (Exception e) {
            easyResponse.setException(e);
            Log.e(EasyHttp.LOG_TAG, "getStreamResponse", e);
            // TODO - retry
        }
        return easyResponse;
    }
}