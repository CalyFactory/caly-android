package io.caly.calyandroid.Service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.ORM.SessionRecord;
import io.caly.calyandroid.Util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 13
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    //로그에 쓰일 tag
    private static final String TAG = FirebaseInstanceIDService.class.getSimpleName();


    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        Util.getHttpService().updatePushToken(
                token,
                SessionRecord.getSessionRecord().getSessionKey()
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                if(response.code() == 200){
                    BasicResponse body = response.body();
                    Log.d(TAG, "push token update success");

                }
                else{
                    Log.d(TAG, "push token update fail");

                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {

                Log.d(TAG,"onfail : " + t.getMessage());
                Log.d(TAG, "fail " + t.getClass().getName());

            }
        });
    }



}
