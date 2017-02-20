package io.caly.calyandroid.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util;
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

    void test(){

        Util.getHttpService().test(
                "test"
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                if(response.code() == 200){
                    BasicResponse body = response.body();

                }
                else{
                    Log.e(TAG,"status code : " + response.code());
                    Toast.makeText(
                            getBaseContext(),
                            getString(R.string.toast_msg_server_internal_error),
                            Toast.LENGTH_LONG
                    ).show();
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
