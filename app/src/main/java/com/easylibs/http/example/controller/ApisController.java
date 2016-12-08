package com.easylibs.http.example.controller;

import android.content.Context;

import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.http.example.Constants;
import com.easylibs.http.example.model.TimeModel;
import com.easylibs.listener.EventListener;

/**
 * Created by sachin.gupta on 03-11-2016.
 */
public class ApisController {

    public static EasyHttpRequest<String> getTimeString(Context pContext, EventListener pEventListener) {

        EasyHttpRequest<String> request = new EasyHttpRequest<>();

        request.setHttpMethod(EasyHttpRequest.Method.GET);
        request.setEventCode(Constants.EVENT_CODE_GET_TIME);
        request.setUrl(Constants.URL_GET_TIME);
        request.setEventListener(pEventListener);
        request.setResponseType(String.class);

        EasyHttp.getExecutor(pContext).executeAsync(request);

        return request;
    }

    public static EasyHttpRequest<TimeModel> getTimeModel(Context pContext, EventListener pEventListener) {

        EasyHttpRequest<TimeModel> request = new EasyHttpRequest<>();

        request.setHttpMethod(EasyHttpRequest.Method.GET);
        request.setEventCode(Constants.EVENT_CODE_GET_TIME_JSON);
        request.setUrl(Constants.URL_GET_TIME_JSON);
        request.setEventListener(pEventListener);
        request.setResponseType(TimeModel.class);

        EasyHttp.getExecutor(pContext).executeAsync(request);

        return request;
    }

    public static EasyHttpResponse<TimeModel> getTimeModelSync(Context pContext) {

        EasyHttpRequest<TimeModel> request = new EasyHttpRequest<>();

        request.setHttpMethod(EasyHttpRequest.Method.GET);
        request.setUrl(Constants.URL_GET_TIME_JSON);
        request.setResponseType(TimeModel.class);

        return EasyHttp.getExecutor(pContext).executeSync(request);
    }
}
