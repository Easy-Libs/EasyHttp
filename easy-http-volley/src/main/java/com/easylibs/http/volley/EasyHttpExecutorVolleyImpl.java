package com.easylibs.http.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.RequestFuture;
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

import java.io.File;
import java.io.InputStream;

class EasyHttpExecutorVolleyImpl implements EasyHttpExecutor {

    private static final String LOG_TAG = EasyHttpExecutorVolleyImpl.class.getSimpleName();

    private String mDefaultRequestTag;
    private RequestQueue mRequestQueue;

    private EasyHttpExecutorVolleyImpl(Context pContext) {

        mDefaultRequestTag = pContext.getPackageName();

        // TODO - same cache should be accessible by other network layers
        File cacheDir = new File(pContext.getCacheDir(), "networkCache");
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        mRequestQueue.start();
    }

    @Override
    public <T> void executeAsync(EasyHttpRequest<T> pRequest) {
        if (BuildConfig.DEBUG) {
            Log.v(LOG_TAG, "executeAsync URL: " + pRequest.getUrl());
        }
        int volleyHttpMethod = getVolleyHttpMethod(pRequest.getHttpMethod());
        Request volleyRequest;

        if (pRequest.getResponseType() == String.class) {
            EasyStringListener listener = new EasyStringListener(pRequest);
            volleyRequest = new EasyStringRequest(volleyHttpMethod, pRequest.getUrl(), listener, pRequest.getHeaders(), pRequest.getPostParams());
        } else {
            EasyJsonListener<T> listener = new EasyJsonListener<>(pRequest);
            volleyRequest = new EasyJsonRequest<T>(volleyHttpMethod, pRequest, listener, listener);
        }
        // TODO - get request specific tag
        volleyRequest.setTag(mDefaultRequestTag);

        mRequestQueue.add(volleyRequest);
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
        if (BuildConfig.DEBUG) {
            Log.v(LOG_TAG, "executeSync URL: " + pRequest.getUrl());
        }
        if (pRequest.getResponseType() == InputStream.class) {
            return getStreamResponse(pRequest);
        }

        int volleyHttpMethod = getVolleyHttpMethod(pRequest.getHttpMethod());
        RequestFuture<EasyHttpResponse<T>> future = RequestFuture.newFuture();
        Request volleyRq = new EasyJsonRequest<T>(volleyHttpMethod, pRequest, future, future);
        mRequestQueue.add(volleyRq);

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            EasyHttpResponse<T> response = new EasyHttpResponse<>();
            response.setException(e);
            return response;
        }
    }

    /**
     * Cancels all pending requests by the specified pRequestTag, it is important to
     * specify a pRequestTag so that the pending/ongoing requests can be cancelled.
     *
     * @param pRequestTag
     */
    public synchronized void cancelPendingRequests(Object pRequestTag) {
        if (pRequestTag == null) {
            pRequestTag = mDefaultRequestTag;
        }
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(pRequestTag);
        }
    }

    /**
     * @param pRequest
     * @return
     */
    private EasyHttpResponse getStreamResponse(EasyHttpRequest pRequest) {
        EasyHttpResponse easyResponse = new EasyHttpResponse();
        easyResponse.setEasyHttpRequest(pRequest);

        HttpResponse httpResponse = null;
        try {
            HttpClient client = new DefaultHttpClient();
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
            // TODO - headers
            httpResponse = client.execute(request);
            easyResponse.setStatusCode(httpResponse.getStatusLine().getStatusCode());
            BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(httpResponse.getEntity());
            easyResponse.setData(bufferedEntity.getContent());
            easyResponse.setSuccess(true);
        } catch (Exception e) {
            easyResponse.setException(e);
            e.printStackTrace();
        }
        return easyResponse;
    }
}
