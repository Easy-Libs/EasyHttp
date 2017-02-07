package com.easylibs.http.volley;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;

/**
 * connector between response handling of Volley and our network layer
 *
 * @author easy.libs
 */
class EasyJsonListener<T> implements Listener<EasyHttpResponse<T>>, ErrorListener {

    private EasyHttpRequest<T> mEasyHttpRequest;

    EasyJsonListener(EasyHttpRequest<T> pEasyHttpRequest) {
        mEasyHttpRequest = pEasyHttpRequest;
    }

    @Override
    public void onResponse(EasyHttpResponse<T> pResponse) {
        mEasyHttpRequest.onResponse(pResponse);
    }

    @Override
    public void onErrorResponse(VolleyError pResponseError) {
        EasyHttpResponse<T> response = EasyVolleyUtils.createEasyHttpResponse(pResponseError);
        mEasyHttpRequest.onResponse(response);
    }
}
