package com.easylibs.http.example.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpRequest;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.http.example.BuildConfig;
import com.easylibs.http.example.Constants;
import com.easylibs.http.example.R;
import com.easylibs.http.example.controller.ApisController;
import com.easylibs.http.example.model.BaseResponse;
import com.easylibs.listener.EventListener;
import com.easylibs.utils.EasyUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity implements View.OnClickListener, EventListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RadioButton mRadioBtnStringAsync;
    private RadioButton mRadioBtnParsedObjAsync;
    private RadioButton mRadioBtnFileMpeAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EasyHttp.DEBUG = BuildConfig.DEBUG;

        mRadioBtnStringAsync = findViewById(R.id.radio_string_async);
        mRadioBtnParsedObjAsync = findViewById(R.id.radio_parsed_async);
        mRadioBtnFileMpeAsync = findViewById(R.id.radio_mpe_async);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_string_data: {
                if (mRadioBtnStringAsync.isChecked()) {
                    ApisController.getResponseAsString(this, this);
                } else {
                    new Thread(() -> {
                        EasyHttpResponse<String> response = ApisController.getResponseAsString(MainActivity.this, null);
                        onResponse(response, "Sync-String");
                    }).start();
                }
                break;
            }
            case R.id.btn_parsed_json: {
                if (mRadioBtnParsedObjAsync.isChecked()) {
                    ApisController.getParsedResponse(this, this);
                } else {
                    new Thread(() -> {
                        EasyHttpResponse<BaseResponse> response = ApisController.getParsedResponse(MainActivity.this, null);
                        onResponse(response, "Sync-Parsed");
                    }).start();
                }
                break;
            }
            case R.id.btn_post_stream: {
                if (mRadioBtnFileMpeAsync.isChecked()) {
                    ApisController.postStream(MainActivity.this, this);
                } else {
                    new Thread(() -> {
                        EasyHttpResponse<BaseResponse> response = ApisController.postStream(MainActivity.this, null);
                        if (response.getData() == null) {
                            Log.e(LOG_TAG, "Post File IN MPE (Sync): " + response.getStatusCode());
                            return;
                        }
                        Log.d(LOG_TAG, "Post File In MPE (Sync): " + response.getData());
                    }).start();
                }
                break;
            }
            case R.id.btn_get_stream: {
                new Thread(() -> {
                    EasyHttpResponse<InputStream> response = ApisController.getStream(MainActivity.this);
                    onResponse(response, "Sync-Stream");
                }).start();
                break;
            }
        }
    }

    @Override
    public void onEvent(int pEventCode, Object pEventData) {
        switch (pEventCode) {
            case Constants.EVENT_GET_PLACES_AS_STRING: {
                onResponse((EasyHttpResponse) pEventData, "Async-String");
                break;
            }
            case Constants.EVENT_GET_PLACES_PARSED: {
                onResponse((EasyHttpResponse) pEventData, "Async-Parsed");
                break;
            }
            case Constants.EVENT_POST_FILE_IN_MPE: {
                EasyHttpResponse<BaseResponse> response = (EasyHttpResponse) pEventData;
                if (response.getData() != null) {
                    Log.d(LOG_TAG, "Post File In MPE (Async): " + response.getData().getStatus());
                } else {
                    Log.e(LOG_TAG, "Post File In MPE (Async): " + response.getStatusCode());
                }
                break;
            }
        }
    }

    private <T> void onResponse(EasyHttpResponse<T> pResponse, String pRequestTypeTag) {
        String logMsgPrefix = "Response(" + pRequestTypeTag + "): ";
        if (pResponse.getData() == null) {
            Log.e(LOG_TAG, logMsgPrefix + pResponse.getStatusCode());
            return;
        }
        EasyHttpRequest<T> request = pResponse.getEasyHttpRequest();
        if (request.getResponseType() == String.class) {
            Log.d(LOG_TAG, logMsgPrefix + pResponse.getData());
            return;
        }
        if (request.getResponseType() == InputStream.class) {
            EasyHttpResponse<InputStream> response = (EasyHttpResponse<InputStream>) pResponse;
            boolean saved = createAppPrivateFile(MainActivity.this, "google-doodle.jpg", response.getData());
            Log.d(LOG_TAG, logMsgPrefix + (saved ? "Stream saved as file" : "Error in saving stream as file"));
            return;
        }
        EasyHttpResponse<BaseResponse> response = (EasyHttpResponse<BaseResponse>) pResponse;
        Log.d(LOG_TAG, logMsgPrefix + response.getData().getStatus());
    }

    /**
     * @param pContext
     * @param pAppPrivateFileName
     * @param pInputStream
     * @return
     */
    private static boolean createAppPrivateFile(Context pContext, String pAppPrivateFileName, InputStream pInputStream) {
        OutputStream outputStream = null;
        try {
            outputStream = pContext.openFileOutput(pAppPrivateFileName, Context.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = pInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG, "createAppPrivateFile() " + e.getMessage() + ", " + pAppPrivateFileName);
            return false;
        } finally {
            EasyUtils.closeSafely(pInputStream);
            EasyUtils.closeSafely(outputStream);
        }
    }
}
