package com.easylibs.http.example.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.easylibs.http.EasyHttpResponse;
import com.easylibs.http.example.Constants;
import com.easylibs.http.example.R;
import com.easylibs.http.example.controller.ApisController;
import com.easylibs.http.example.model.TimeModel;
import com.easylibs.listener.EventListener;

public class MainActivity extends AppCompatActivity implements EventListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApisController.getTimeString(this, this);

        ApisController.getTimeModel(this, this);

        new Thread() {
            @Override
            public void run() {
                EasyHttpResponse<TimeModel> response = ApisController.getTimeModelSync(MainActivity.this);
                if (response.isSuccess()) {
                    Log.d(LOG_TAG, response.getData().getDateString());
                } else {
                    Log.d(LOG_TAG, "StatusCode: " + response.getStatusCode());
                }
            }
        }.start();

    }

    @Override
    public void onEvent(int pEventCode, Object pEventData) {
        switch (pEventCode) {
            case Constants.EVENT_CODE_GET_TIME: {
                EasyHttpResponse response = (EasyHttpResponse) pEventData;
                if (response.isSuccess()) {
                    Log.d(LOG_TAG, response.getData().toString());
                } else {
                    Log.d(LOG_TAG, "StatusCode: " + response.getStatusCode());
                }
                break;
            }
            case Constants.EVENT_CODE_GET_TIME_JSON: {
                EasyHttpResponse response = (EasyHttpResponse) pEventData;
                TimeModel model = (TimeModel) response.getData();
                if (response.isSuccess()) {
                    Log.d(LOG_TAG, model.getDateString());
                } else {
                    Log.d(LOG_TAG, "StatusCode: " + response.getStatusCode());
                }
                break;
            }
        }
    }
}
