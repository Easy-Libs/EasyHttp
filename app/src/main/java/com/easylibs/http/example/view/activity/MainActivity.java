package com.easylibs.http.example.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.http.example.BuildConfig;
import com.easylibs.http.example.Constants;
import com.easylibs.http.example.R;
import com.easylibs.http.example.controller.ApisController;
import com.easylibs.http.example.model.BaseResponse;
import com.easylibs.listener.EventListener;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity implements View.OnClickListener, EventListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RadioButton mRadioBtnStringAsync;
    private RadioButton mRadioBtnParsedObjASync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EasyHttp.DEBUG = BuildConfig.DEBUG;

        mRadioBtnStringAsync = findViewById(R.id.radio_string_async);
        mRadioBtnParsedObjASync = findViewById(R.id.radio_parsed_async);
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
                        if (response.getData() == null) {
                            Log.e(LOG_TAG, "String Response(Sync): " + response.getStatusCode());
                            return;
                        }
                        Log.d(LOG_TAG, "String Response(Sync): " + response.getData());
                    }).start();
                }
                break;
            }
            case R.id.btn_parsed_json: {
                if (mRadioBtnParsedObjASync.isChecked()) {
                    ApisController.getParsedResponse(this, this);
                } else {
                    new Thread(() -> {
                        EasyHttpResponse<BaseResponse> response = ApisController.getParsedResponse(MainActivity.this, null);
                        if (response.getData() == null) {
                            Log.e(LOG_TAG, "Parsed Response(Sync): " + response.getStatusCode());
                            return;
                        }
                        Log.d(LOG_TAG, "Parsed Response(Sync): " + response.getData());
                    }).start();
                }
                break;
            }
            case R.id.btn_get_stream: {
                new Thread(() -> {
                    EasyHttpResponse<InputStream> response = ApisController.getStream(MainActivity.this);
                    if (response.getData() == null) {
                        Log.e(LOG_TAG, "Stream Response(Sync): " + response.getStatusCode());
                        return;
                    }
                    boolean saved = createAppPrivateFile(MainActivity.this, "google-doodle.jpg", response.getData());
                    Log.d(LOG_TAG, "Stream Response(Sync): " + (saved ? "Stream saved as file" : "Error in saving stream as file"));
                }).start();
                break;
            }
        }
    }

    @Override
    public void onEvent(int pEventCode, Object pEventData) {
        switch (pEventCode) {
            case Constants.EVENT_GET_PLACES_AS_STRING: {
                EasyHttpResponse<String> response = (EasyHttpResponse) pEventData;
                if (response.getData() != null) {
                    Log.d(LOG_TAG, "String Response(Async): " + response.getData());
                } else {
                    Log.e(LOG_TAG, "String Response(Async): " + response.getStatusCode());
                }
                break;
            }
            case Constants.EVENT_GET_PLACES_PARSED: {
                EasyHttpResponse<BaseResponse> response = (EasyHttpResponse) pEventData;
                if (response.getData() != null) {
                    Log.d(LOG_TAG, "Parsed Response(Async): " + response.getData().getStatus());
                } else {
                    Log.e(LOG_TAG, "Parsed Response(Async): " + response.getStatusCode());
                }
                break;
            }
        }
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
            closeSafely(pInputStream);
            closeSafely(outputStream);
        }
    }

    /**
     * @param pCloseable
     */
    private static void closeSafely(Closeable pCloseable) {
        if (pCloseable != null) {
            try {
                pCloseable.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "closeSafely() " + e.getMessage());
            }
        }
    }
}
