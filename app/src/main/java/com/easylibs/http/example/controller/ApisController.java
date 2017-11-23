package com.easylibs.http.example.controller;

import android.content.Context;
import android.text.format.DateUtils;

import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.http.example.Constants;
import com.easylibs.http.example.model.BaseResponse;
import com.easylibs.listener.EventListener;
import com.easylibs.utils.ContentType;

import java.io.InputStream;

/**
 * Created by easy.libs on 03-11-2016.
 */
public class ApisController {

    public static EasyHttpResponse<String> getResponseAsString(Context pContext, EventListener pEventListener) {

        EasyHttpRequest<String> request = new EasyHttpRequest<>();
        setGeoCodeServiceDetails(pContext, request);

        request.setResponseType(String.class);

        if (pEventListener == null) {
            // sync
            return EasyHttp.getExecutor(pContext).executeSync(request);
        }

        // async
        request.setEventCode(Constants.EVENT_GET_PLACES_AS_STRING);
        request.setEventListener(pEventListener);

        EasyHttp.getExecutor(pContext).executeAsync(request);
        return null;
    }

    public static EasyHttpResponse<BaseResponse> getParsedResponse(Context pContext, EventListener pEventListener) {

        EasyHttpRequest<BaseResponse> request = new EasyHttpRequest<>();
        setGeoCodeServiceDetails(pContext, request);

        request.setResponseType(BaseResponse.class);

        if (pEventListener == null) {
            // sync
            return EasyHttp.getExecutor(pContext).executeSync(request);
        }

        // async
        request.setEventCode(Constants.EVENT_GET_PLACES_PARSED);
        request.setEventListener(pEventListener);

        EasyHttp.getExecutor(pContext).executeAsync(request);
        return null;
    }

    private static <T> void setGeoCodeServiceDetails(Context pContext, EasyHttpRequest<T> request) {
        request.setContext(pContext);

        request.setHttpMethod(EasyHttpRequest.Method.GET);
        request.setUrl(Constants.URL_GEO_CODE);
        request.setResponseContentType(ContentType.JSON);

        // cache options
        // request.setCacheTtl(-1);
        // request.setIgnoreCached(true);
        request.setCacheTtl(DateUtils.MINUTE_IN_MILLIS);
        request.setCacheSoftTtl(DateUtils.MINUTE_IN_MILLIS / 2);
    }

    public static EasyHttpResponse<InputStream> getStream(Context pContext) {
        EasyHttpRequest<InputStream> httpRequest = new EasyHttpRequest<>();
        httpRequest.setUrl("https://www.google.com/logos/doodles/2017/indias-independence-day-2017-6586914957164544-2x.jpg");
        httpRequest.setHttpMethod(EasyHttpRequest.Method.GET);
        httpRequest.setResponseType(InputStream.class);
        return EasyHttp.getExecutor(pContext).executeSync(httpRequest);
    }
}
