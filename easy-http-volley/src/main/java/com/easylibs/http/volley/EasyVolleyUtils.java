package com.easylibs.http.volley;

import com.android.volley.NetworkResponse;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.utils.JsonUtils;

/**
 * Created by root on 19/1/17.
 */

public class EasyVolleyUtils {

    /**
     * @param pEasyHttpRequest
     * @param pNetworkResponse
     * @param <T>
     * @return
     */
    static <T> EasyHttpResponse<T> createEasyHttpResponse(EasyHttpRequest<T> pEasyHttpRequest, NetworkResponse pNetworkResponse) {
        EasyHttpResponse<T> easyHttpResponse = new EasyHttpResponse<>();
        easyHttpResponse.setEasyHttpRequest(pEasyHttpRequest);
        if (pNetworkResponse == null) {
            return easyHttpResponse;
        }
        easyHttpResponse.setStatusCode(pNetworkResponse.statusCode);
        easyHttpResponse.setHeaders(pNetworkResponse.headers);
        if (pNetworkResponse.data != null) {
            String responseStr = new String(pNetworkResponse.data);
            T parsedResponse = JsonUtils.objectify(responseStr, pEasyHttpRequest.getResponseType());
            easyHttpResponse.setData(parsedResponse);
        }
        return easyHttpResponse;
    }
}
