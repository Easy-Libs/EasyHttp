package com.easylibs.http.example.controller;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.MultipartEntity;
import com.android.internal.http.multipart.Part;
import com.android.internal.http.multipart.StringPart;
import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.http.example.Constants;
import com.easylibs.http.example.model.BaseResponse;
import com.easylibs.listener.EventListener;
import com.easylibs.utils.ContentType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

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
        request.setQueueBehaviour(EasyHttpRequest.QueueBehaviour.USE_NEW);

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
        request.setIgnoreCached(true);
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

    public static EasyHttpResponse<BaseResponse> postStream(Context pContext, EventListener pEventListener) {
        EasyHttpRequest<BaseResponse> httpRequest = new EasyHttpRequest<>();
        httpRequest.setUrl("http://203.122.38.219:8084/digi-safe/addDoc");
        httpRequest.setHttpMethod(EasyHttpRequest.Method.POST);

        HashMap<String, String> headers = new HashMap<>(3);
        headers.put("clientToken", "UFMtSU5URVJOQUwtUUE6OmQyN1M0NEAoeHdwIzRkdHMkZzFoIzIkUjQ2MEA2aGRS");
        headers.put("Content-Type", ContentType.JSON.toString());
        httpRequest.setHeaders(headers);

        final String charset = "utf-8";

        FilePart filePart = null;
        try {
            File file = new File(pContext.getFilesDir(), "google-doodle.jpg");
            Log.v(EasyHttp.LOG_TAG, file.exists() + ", " + file.getAbsolutePath());
            filePart = new FilePart("doc", "google-doodle.jpg", file, "image/jpeg", charset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Part token = new StringPart("accessToken", "25d834c8-2a21-418f-8f59-96b0e5d8a439", charset);
        Part docName = new StringPart("docName", "Google-Doodle", charset);
        StringPart deviceId = new StringPart("deviceId", "f878d1fa57618b22", charset);
        StringPart docExt = new StringPart("docExt", ".jpg", charset);
        StringPart userId = new StringPart("userId", "21", charset);

        Part[] parts = {token, docName, deviceId, docExt, userId, filePart};
        com.android.internal.http.multipart.MultipartEntity mpe = new MultipartEntity(parts);

        byte[] requestBody = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            mpe.writeTo(outputStream);
            requestBody = outputStream.toByteArray();
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(EasyHttp.LOG_TAG, new String(requestBody));

        ContentType contentType = new ContentType(mpe.getContentType().getValue());
        httpRequest.setRequestBody(requestBody, contentType);

        httpRequest.setResponseType(BaseResponse.class);
        return EasyHttp.getExecutor(pContext).executeSync(httpRequest);
    }
}
