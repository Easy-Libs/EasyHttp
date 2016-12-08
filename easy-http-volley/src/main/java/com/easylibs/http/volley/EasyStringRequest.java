package com.easylibs.http.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.easylibs.http.EasyHttp;

import java.util.Map;

/**
 * @author sachin.gupta
 */
class EasyStringRequest extends StringRequest {

    private Map<String, String> mPostParamsMap;
    private Map<String, String> mHttpHeadersMap;

    /**
     * @param pMethod
     * @param pUrl
     * @param pListener
     * @param pHeaders
     * @param pPostParams
     */
    EasyStringRequest(int pMethod, String pUrl, EasyStringListener pListener, Map<String, String> pHeaders, Map<String, String> pPostParams) {
        super(pMethod, pUrl, pListener, pListener);
        mPostParamsMap = pPostParams;
        mHttpHeadersMap = pHeaders;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mPostParamsMap;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return EasyHttp.mergeHeaders(mHttpHeadersMap, super.getHeaders());
    }
}