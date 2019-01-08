package com.easylibs.http.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.utils.ContentType;
import com.easylibs.utils.EasyUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author sachin.gupta
 */
class EasyVolleyRequest<T> extends Request<EasyHttpResponse<T>> {

    private EasyHttpRequest<T> mEasyHttpRequest;
    private Response.Listener<EasyHttpResponse<T>> mListener;

    /**
     * @param pMethod
     * @param pRequest
     * @param pErrorListener
     */
    EasyVolleyRequest(int pMethod, EasyHttpRequest<T> pRequest, Response.Listener<EasyHttpResponse<T>> pListener, Response.ErrorListener pErrorListener) {
        super(pMethod, pRequest.getUrl(), pErrorListener);
        mEasyHttpRequest = pRequest;
        mListener = pListener;

        setTag(pRequest.getTag()); // TODO - check and complete it's usage
        setRetryPolicy(new EasyRetryPolicy(pRequest));
        setShouldCache(!pRequest.isIgnoreCached() && pRequest.getCacheTtl() >= 0);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return EasyHttp.mergeHeaders(mEasyHttpRequest.getHeaders(), super.getHeaders());
    }

    @Override
    protected Response<EasyHttpResponse<T>> parseNetworkResponse(NetworkResponse pNetworkResponse) {
        EasyHttpResponse<T> easyHttpResponse = EasyVolleyUtils.createEasyHttpResponse(mEasyHttpRequest, pNetworkResponse);
        if (mEasyHttpRequest.isIgnoreCached()) {
            setShouldCache(mEasyHttpRequest.getCacheTtl() >= 0);
        }
        if (mEasyHttpRequest.getCacheTtl() == 0) {
            return Response.success(easyHttpResponse, HttpHeaderParser.parseCacheHeaders(pNetworkResponse));
        } else if (mEasyHttpRequest.getCacheTtl() > 0) {
            return Response.success(easyHttpResponse, EasyVolleyUtils.parseIgnoreCacheHeaders(mEasyHttpRequest, pNetworkResponse));
        } else {
            return Response.success(easyHttpResponse, null);
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return EasyVolleyError.newInstance(mEasyHttpRequest, volleyError);
    }

    @Override
    protected void deliverResponse(EasyHttpResponse<T> response) {
        mListener.onResponse(response);
    }

    /**
     * @deprecated Use {@link #getBodyContentType()}.
     */
    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    /**
     * @deprecated Use {@link #getBody()}.
     */
    @Override
    public byte[] getPostBody() {
        return getBody();
    }

    @Override
    public String getBodyContentType() {
        ContentType requestBodyContentType = mEasyHttpRequest.getRequestBodyContentType();
        return requestBodyContentType == null ? null : requestBodyContentType.toString();
    }

    @Override
    public byte[] getBody() {
        String requestBody = mEasyHttpRequest.getRequestBody();
        if (requestBody == null) {
            return null;
        }
        ContentType requestBodyContentType = mEasyHttpRequest.getRequestBodyContentType();
        if (requestBodyContentType == null || EasyUtils.isBlank(requestBodyContentType.getCharset())) {
            return requestBody.getBytes();
        }
        try {
            return requestBody.getBytes(requestBodyContentType.getCharset());
        } catch (UnsupportedEncodingException uee) {
            Log.e(EasyHttp.LOG_TAG, "EasyVolleyRequest#getBody", uee);
            return requestBody.getBytes();
        }
    }
}