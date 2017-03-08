package com.easylibs.http.volley;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.easylibs.http.EasyHttpRequest;

import org.apache.http.HttpStatus;

/**
 * Created by easy.libs on 24/1/17.
 */
class EasyRetryPolicy extends DefaultRetryPolicy {

    EasyRetryPolicy(EasyHttpRequest pEasyHttpRequest) {
        super(pEasyHttpRequest.getSocketTimeOutMs() > 0 ? pEasyHttpRequest.getSocketTimeOutMs() : DEFAULT_TIMEOUT_MS,
                pEasyHttpRequest.getRetryCount() <= 0 ? 0 : pEasyHttpRequest.getRetryCount(),
                DEFAULT_BACKOFF_MULT);
    }

    @Override
    public void retry(VolleyError error) throws VolleyError {
        if (error.networkResponse != null &&
                (error.networkResponse.statusCode == HttpStatus.SC_FORBIDDEN
                        || error.networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED)) {
            throw error;
        }
        super.retry(error);
    }
}
