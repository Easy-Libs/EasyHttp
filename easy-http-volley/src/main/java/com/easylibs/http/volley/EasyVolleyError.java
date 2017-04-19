package com.easylibs.http.volley;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.utils.JsonUtils;

/**
 * Created by easy.libs on 6/2/17.
 */
class EasyVolleyError extends VolleyError {

    private Object parsedData;

    private EasyVolleyError() {
        super();
    }

    private EasyVolleyError(NetworkResponse response) {
        super(response);
    }

    private EasyVolleyError(String exceptionMessage) {
        super(exceptionMessage);
    }

    private EasyVolleyError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }

    private EasyVolleyError(Throwable cause) {
        super(cause);
    }

    static <T> EasyVolleyError newInstance(EasyHttpRequest<T> pEasyHttpRequest, VolleyError pVolleyError) {
        if (pVolleyError == null) {
            return new EasyVolleyError();
        }
        if (pVolleyError.networkResponse != null) {
            EasyVolleyError evError = new EasyVolleyError(pVolleyError.networkResponse);
            if (evError.networkResponse.data != null) {
                String dataStr;
                try {
                    dataStr = new String(evError.networkResponse.data, HttpHeaderParser.parseCharset(evError.networkResponse.headers));
                } catch (Exception e) {
                    dataStr = new String(evError.networkResponse.data);
                }
                if (EasyHttp.DEBUG) {
                    Log.e(EasyHttp.LOG_TAG, "Response Data: " + dataStr);
                }
                T parsedData = JsonUtils.objectify(dataStr, pEasyHttpRequest.getResponseType());
                evError.setParsedData(parsedData);
            }
            return evError;
        }
        if (pVolleyError.getCause() != null && pVolleyError.getMessage() != null) {
            return new EasyVolleyError(pVolleyError.getMessage(), pVolleyError.getCause());
        }
        if (pVolleyError.getCause() != null) {
            return new EasyVolleyError(pVolleyError.getCause());
        }
        if (pVolleyError.getMessage() != null) {
            return new EasyVolleyError(pVolleyError.getMessage());
        }
        return new EasyVolleyError();
    }

    private void setParsedData(Object parsedData) {
        this.parsedData = parsedData;
    }

    Object getParsedData() {
        return parsedData;
    }
}