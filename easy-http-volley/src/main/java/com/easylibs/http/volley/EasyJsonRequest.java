package com.easylibs.http.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.utils.JsonUtils;

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
        super(pMethod, pRequest.getUrl(), JsonUtils.jsonify(pRequest.getPostObject()), pListener, pErrorListener);
        mEasyHttpRequest = pRequest;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return EasyHttp.mergeHeaders(mEasyHttpRequest.getHeaders(), super.getHeaders());
    }

    @Override
    protected Response<EasyHttpResponse<T>> parseNetworkResponse(NetworkResponse pNetworkResponse) {
        EasyHttpResponse<T> easyHttpResponse = new EasyHttpResponse<>();
        easyHttpResponse.setEasyHttpRequest(mEasyHttpRequest);
        T parsedResponse = null;
        if (pNetworkResponse != null) {
            easyHttpResponse.setStatusCode(pNetworkResponse.statusCode);
            easyHttpResponse.setHeaders(pNetworkResponse.headers);
            if (pNetworkResponse.data != null) {
                String responseStr = new String(pNetworkResponse.data);
                parsedResponse = JsonUtils.objectify(responseStr, mEasyHttpRequest.getResponseType());
            }
        }
        if (parsedResponse != null) {
            easyHttpResponse.setSuccess(true);
            easyHttpResponse.setData(parsedResponse);
            return Response.success(easyHttpResponse, null);
        } else {
            return Response.error(new VolleyError(pNetworkResponse));
        }
    }
}