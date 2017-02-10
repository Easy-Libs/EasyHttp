package com.easylibs.http.example.controller;

import android.content.Context;
import android.text.format.DateUtils;

import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.http.example.Constants;
import com.easylibs.http.example.model.ReverseGeoResponse;
import com.easylibs.listener.EventListener;

/**
 * Created by easy.libs on 03-11-2016.
 */
public class ApisController {

    public static void getPlaceAsync(Context pContext, EventListener pEventListener, int pEventCode) {

        EasyHttpRequest<ReverseGeoResponse> request = new EasyHttpRequest<>();

        request.setContext(pContext);
        request.setHttpMethod(EasyHttpRequest.Method.GET);
        request.setEventCode(pEventCode);
        request.setUrl(Constants.URL_GEO_CODE);
        request.setEventListener(pEventListener);
        request.setResponseType(ReverseGeoResponse.class);

        request.setCacheTtl(DateUtils.MINUTE_IN_MILLIS);
        request.setCacheSoftTtl(DateUtils.MINUTE_IN_MILLIS / 2);

        EasyHttp.getExecutor(pContext).executeAsync(request);
    }

    public static EasyHttpResponse<ReverseGeoResponse> getPlaceSync(Context pContext) {

        EasyHttpRequest<ReverseGeoResponse> request = new EasyHttpRequest<>();

        request.setContext(pContext);
        request.setHttpMethod(EasyHttpRequest.Method.GET);
        request.setUrl(Constants.URL_GEO_CODE);
        request.setResponseType(ReverseGeoResponse.class);
        request.setCacheTtl(-1);

        return EasyHttp.getExecutor(pContext).executeSync(request);
    }
}
