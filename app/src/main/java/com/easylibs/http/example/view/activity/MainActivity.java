package com.easylibs.http.example.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.easylibs.http.EasyHttpResponse;
import com.easylibs.http.example.Constants;
import com.easylibs.http.example.R;
import com.easylibs.http.example.controller.ApisController;
import com.easylibs.http.example.model.TimeModel;
import com.easylibs.listener.EventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, EventListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_async_string).setOnClickListener(this);
        findViewById(R.id.btn_async_json).setOnClickListener(this);
        findViewById(R.id.btn_sync_json).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_async_string: {
                ApisController.getTimeString(this, this);
                break;
            }
            case R.id.btn_async_json: {
                ApisController.getTimeModel(this, this);
                break;
            }
            case R.id.btn_sync_json: {
                new Thread() {
                    @Override
                    public void run() {
                        EasyHttpResponse<TimeModel> response = ApisController.getTimeModelSync(MainActivity.this);
                        if (response.getData() != null) {
                            Log.d(LOG_TAG, "Response: " + response.getData().getDateString());
                        } else {
                            Log.d(LOG_TAG, "StatusCode: " + response.getStatusCode());
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
            case Constants.EVENT_CODE_GET_TIME: {
                EasyHttpResponse response = (EasyHttpResponse) pEventData;
                if (response.getData() != null) {
                    Log.d(LOG_TAG, "Response: " + response.getData().toString());
                } else {
                    Log.d(LOG_TAG, "StatusCode: " + response.getStatusCode());
                }
                break;
            }
            case Constants.EVENT_CODE_GET_TIME_JSON: {
                EasyHttpResponse<TimeModel> response = (EasyHttpResponse) pEventData;
                if (response.getData() != null) {
                    Log.d(LOG_TAG, "Response: " + response.getData().getDateString());
                } else {
                    Log.d(LOG_TAG, "StatusCode: " + response.getStatusCode());
                }
                break;
            }
        }
    }
}
