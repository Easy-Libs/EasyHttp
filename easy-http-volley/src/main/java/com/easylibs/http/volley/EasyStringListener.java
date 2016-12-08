package com.easylibs.http.volley;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;

/**
 * connector between response handling of Volley and our network layer
 *
 * @author sachin.gupta
 */
class EasyStringListener implements Listener<String>, ErrorListener {

    private EasyHttpRequest mEasyHttpRequest;

    EasyStringListener(EasyHttpRequest pEasyHttpRequest) {
        mEasyHttpRequest = pEasyHttpRequest;
    }

    @Override
    public void onResponse(String pResponse) {
        EasyHttpResponse<String> easyHttpResponse = new EasyHttpResponse<>();
        easyHttpResponse.setEasyHttpRequest(mEasyHttpRequest);
        easyHttpResponse.setSuccess(true);
        easyHttpResponse.setData(pResponse);
        mEasyHttpRequest.onResponse(easyHttpResponse);
    }

    @Override
    public void onErrorResponse(VolleyError pResponseError) {
        mEasyHttpRequest.onError(pResponseError);
    }
}
