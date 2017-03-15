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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.File;
import java.io.InputStream;

class EasyHttpExecutorVolleyImpl implements EasyHttpExecutor {

    private static final String LOG_TAG = EasyHttpExecutorVolleyImpl.class.getSimpleName();

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
        int volleyHttpMethod = getVolleyHttpMethod(pRequest.getHttpMethod());
        EasyJsonListener<T> listener = new EasyJsonListener<>(pRequest);
        Request<EasyHttpResponse<T>> volleyRequest = new EasyJsonRequest<T>(volleyHttpMethod, pRequest, listener, listener);
        volleyRequest.setRetryPolicy(new EasyRetryPolicy(pRequest));
        volleyRequest.setShouldCache(pRequest.getCacheTtl() >= 0);

        getQueue(pRequest.getContext()).add(volleyRequest);
    }

    private int getVolleyHttpMethod(int httpMethod) {
        switch (httpMethod) {
            case EasyHttpRequest.Method.GET: {
                return Request.Method.GET;
            }
            case EasyHttpRequest.Method.POST: {
                return Request.Method.POST;
            }
            default: {
                return Request.Method.DEPRECATED_GET_OR_POST;
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

        int volleyHttpMethod = getVolleyHttpMethod(pRequest.getHttpMethod());
        RequestFuture<EasyHttpResponse<T>> future = RequestFuture.newFuture();
        Request<EasyHttpResponse<T>> volleyRequest = new EasyJsonRequest<T>(volleyHttpMethod, pRequest, future, future);
        volleyRequest.setShouldCache(pRequest.getCacheTtl() >= 0);
        volleyRequest.setRetryPolicy(new EasyRetryPolicy(pRequest));
        getQueue(pRequest.getContext()).add(volleyRequest);

        try {
            // TODO - is timeout to be provided here again?
            return future.get();
        } catch (Exception e) {
            Log.e(EasyHttp.LOG_TAG, "executeSync", e);
            EasyHttpResponse<T> response = new EasyHttpResponse<>();
            response.setStatusCode(500); // TODO
            response.setException(e);
            return response;
        }
    }

    /**
     * @param pRequest
     * @return
     */
    private EasyHttpResponse getStreamResponse(EasyHttpRequest pRequest) {
        EasyHttpResponse<InputStream> easyResponse = new EasyHttpResponse<>();

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
                    // TODO - entity
                    break;
                }
                default: {
                    request = new HttpGet(pRequest.getUrl());
                    break;
                }
            }
            // TODO - add request headers
            httpResponse = client.execute(request);
            easyResponse.setStatusCode(httpResponse.getStatusLine().getStatusCode());
            BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(httpResponse.getEntity());
            easyResponse.setData(bufferedEntity.getContent());
            // TODO - response headers
        } catch (Exception e) {
            easyResponse.setException(e);
            e.printStackTrace();
        }
        return easyResponse;
    }
}