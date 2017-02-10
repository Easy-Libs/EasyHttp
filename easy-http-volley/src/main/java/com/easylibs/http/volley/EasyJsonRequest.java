package com.easylibs.http.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;

import java.util.Map;

/**
 * @author sachin.gupta
 */
class EasyJsonRequest<T> extends JsonRequest<EasyHttpResponse<T>> {

    private EasyHttpRequest<T> mEasyHttpRequest;

    /**
     * @param pMethod
     * @param pRequest
     * @param pListener
     */
    EasyJsonRequest(int pMethod, EasyHttpRequest<T> pRequest, Response.Listener<EasyHttpResponse<T>> pListener, Response.ErrorListener pErrorListener) {
        super(pMethod, pRequest.getUrl(), pRequest.getPostJson(), pListener, pErrorListener);
        mEasyHttpRequest = pRequest;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return EasyHttp.mergeHeaders(mEasyHttpRequest.getHeaders(), super.getHeaders());
    }

    @Override
    protected Response<EasyHttpResponse<T>> parseNetworkResponse(NetworkResponse pNetworkResponse) {
        EasyHttpResponse<T> easyHttpResponse = EasyVolleyUtils.createEasyHttpResponse(mEasyHttpRequest, pNetworkResponse);
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
}