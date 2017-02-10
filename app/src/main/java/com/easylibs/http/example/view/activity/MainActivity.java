package com.easylibs.http.example.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.easylibs.http.EasyHttp;
import com.easylibs.http.EasyHttpResponse;
import com.easylibs.http.example.BuildConfig;
import com.easylibs.http.example.Constants;
import com.easylibs.http.example.R;
import com.easylibs.http.example.controller.ApisController;
import com.easylibs.http.example.model.ReverseGeoResponse;
import com.easylibs.listener.EventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, EventListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EasyHttp.DEBUG = BuildConfig.DEBUG;
        EasyHttp.LOG_TAG = getString(R.string.app_name);

        findViewById(R.id.btn_async_json).setOnClickListener(this);
        findViewById(R.id.btn_sync_json).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_async_json: {
                ApisController.getPlaceAsync(this, this, Constants.EVENT_CODE_GET_PLACES);
                break;
            }
            case R.id.btn_sync_json: {
                new Thread() {
                    @Override
                    public void run() {
                        EasyHttpResponse<ReverseGeoResponse> response = ApisController.getPlaceSync(MainActivity.this);
                        if (response.getData() != null) {
                            Log.d(LOG_TAG, "Response(Sync): " + response.getData().getStatus());
                        } else {
                            Log.e(LOG_TAG, "Response(Sync): " + response.getStatusCode());
                        }
                    }
                }.start();
                break;
            }

        }
    }

    @Override
    public void onEvent(int pEventCode, Object pEventData) {
        switch (pEventCode) {
            case Constants.EVENT_CODE_GET_PLACES: {
                EasyHttpResponse<ReverseGeoResponse> response = (EasyHttpResponse) pEventData;
                if (response.getData() != null) {
                    Log.d(LOG_TAG, "Response(" + pEventCode + "): " + response.getData().getStatus());
                } else {
                    Log.e(LOG_TAG, "Response(" + pEventCode + "): " + response.getStatusCode());
                }
                break;
            }
        }
    }
}
