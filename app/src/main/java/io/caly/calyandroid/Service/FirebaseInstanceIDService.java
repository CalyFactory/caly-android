package io.caly.calyandroid.Service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Util.ApiClient;
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
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + FirebaseInstanceIDService.class.getSimpleName();


    @Override
    public void onTokenRefresh() {
        Log.i(TAG, "onTokenRefresh");
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        requestUpdatePushToken(token);
    }

    private void requestUpdatePushToken(String token) {
        Log.i(TAG, "sendRegistrationToServer");

        if(TokenRecord.getTokenRecord().getApiKey()!=null) {
            ApiClient.getService().updatePushToken(
                    token,
                    TokenRecord.getTokenRecord().getApiKey()
            ).enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                    Log.d(TAG, "onResponse code : " + response.code());

                    if (response.code() == 200) {
                        BasicResponse body = response.body();
                        Log.d(TAG, "push token update success");

                    } else {
                        Log.d(TAG, "push token update fail");

                    }
                }

                @Override
                public void onFailure(Call<BasicResponse> call, Throwable t) {

                    Log.d(TAG, "onfail : " + t.getMessage());
                    Log.d(TAG, "fail " + t.getClass().getName());

                }
            });
        }
    }



}
