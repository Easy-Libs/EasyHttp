package com.easylibs.http.volley;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.utils.JsonUtils;

import java.util.Map;

/**
 * Created by easy.libs on 19/1/17.
 */
public class EasyVolleyUtils {

    /**
     * @param <T>
     * @param pEasyHttpRequest
     * @param pNetworkResponse
     * @return
     */
    static <T> EasyHttpResponse<T> createEasyHttpResponse(EasyHttpRequest<T> pEasyHttpRequest, NetworkResponse pNetworkResponse) {
        EasyHttpResponse<T> easyHttpResponse = createEasyHttpResponse(pNetworkResponse);
        if (pNetworkResponse != null && pNetworkResponse.data != null) {
            String dataStr;
            try {
                dataStr = new String(pNetworkResponse.data, HttpHeaderParser.parseCharset(pNetworkResponse.headers));
            } catch (Exception e) {
                dataStr = new String(pNetworkResponse.data);
            }
            if (EasyHttp.DEBUG) {
                Log.v(EasyHttp.LOG_TAG, "Response Data: " + dataStr);
            }
            T parsedData = JsonUtils.objectify(dataStr, pEasyHttpRequest.getResponseType());
            easyHttpResponse.setData(parsedData);
        }
        return easyHttpResponse;
    }

    /**
     * @param pNetworkResponse
     * @return
     */
    private static <T> EasyHttpResponse<T> createEasyHttpResponse(NetworkResponse pNetworkResponse) {
        EasyHttpResponse<T> easyHttpResponse = new EasyHttpResponse<>();
        if (pNetworkResponse != null) {
            easyHttpResponse.setStatusCode(pNetworkResponse.statusCode);
            easyHttpResponse.setHeaders(pNetworkResponse.headers);
        }
        if (EasyHttp.DEBUG) {
            Log.d(EasyHttp.LOG_TAG, "Response Status Code: " + easyHttpResponse.getStatusCode());
            Log.v(EasyHttp.LOG_TAG, "Response Headers: " + easyHttpResponse.getHeaders());
        }
        return easyHttpResponse;
    }

    /**
     * @param <T>
     * @param pVolleyError
     * @return
     */
    static <T> EasyHttpResponse<T> createEasyHttpResponse(VolleyError pVolleyError) {
        EasyHttpResponse easyHttpResponse;
        if (pVolleyError == null || pVolleyError.networkResponse == null) {
            easyHttpResponse = new EasyHttpResponse<>();
        } else {
            easyHttpResponse = createEasyHttpResponse(pVolleyError.networkResponse);
        }
        easyHttpResponse.setException(pVolleyError);
        if (pVolleyError instanceof EasyVolleyError) {
            EasyVolleyError evError = (EasyVolleyError) pVolleyError;
            easyHttpResponse.setData(evError.getParsedData());
        }
        return easyHttpResponse;
    }

    static Cache.Entry parseIgnoreCacheHeaders(EasyHttpRequest pEasyHttpRequest, NetworkResponse pResponse) {

        Cache.Entry entry = new Cache.Entry();
        entry.data = pResponse.data;

        long now = System.currentTimeMillis();
        entry.ttl = now + pEasyHttpRequest.getCacheTtl();

        if (pEasyHttpRequest.getCacheSoftTtl() <= 0) {
            entry.softTtl = now + pEasyHttpRequest.getCacheTtl();
        } else {
            entry.softTtl = now + pEasyHttpRequest.getCacheSoftTtl();
        }

        Map<String, String> headers = pResponse.headers;
        if (headers != null) {
            entry.responseHeaders = headers;
            entry.etag = headers.get("ETag");
            String dateHeaderValue = headers.get("Date");
            if (dateHeaderValue != null) {
                entry.serverDate = HttpHeaderParser.parseDateAsEpoch(dateHeaderValue);
            }
        }
        return entry;
    }
}
