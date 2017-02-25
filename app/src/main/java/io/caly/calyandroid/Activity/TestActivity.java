package io.caly.calyandroid.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Service.FirebaseMessagingService;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 18
 */

public class TestActivity extends Activity {

    //로그에 쓰일 tag
    private static final String TAG = TestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        Log.d(TAG, "uuid : " +Util.getUUID());

    }

    @OnClick(R.id.button2)
    void onButtonCLick(){

    }

    PushReceiver pushReceiver;

    @Override
    protected void onStart() {
        pushReceiver = new PushReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FirebaseMessagingService.INTENT_ACTION_SYNC_COMPLETE);
        registerReceiver(pushReceiver, intentFilter);

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(pushReceiver);
    }

    class PushReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received!");
            Log.d(TAG, "msg : " + intent.getStringExtra("message"));
        }
    }

    void test(){

        ApiClient.getService().test(
                "test"
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                BasicResponse body = response.body();
                switch (response.code()){
                    case 200:
                        break;
                    default:
                        Log.e(TAG,"status code : " + response.code());
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Log.e(TAG,"onfail : " + t.getMessage());
                Log.e(TAG, "fail " + t.getClass().getName());

                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }


}
