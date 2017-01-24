package com.easylibs.http.volley;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.easylibs.http.EasyHttpRequest;

import org.apache.http.HttpStatus;

/**
 * Created by easy.libs on 24/1/17.
 */
public class EasyRetryPolicy extends DefaultRetryPolicy {

    public EasyRetryPolicy(EasyHttpRequest pEasyHttpRequest) {
        super(pEasyHttpRequest.getSocketTimeOutMs() > 0 ? pEasyHttpRequest.getSocketTimeOutMs() : DEFAULT_TIMEOUT_MS,
                DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT);
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
