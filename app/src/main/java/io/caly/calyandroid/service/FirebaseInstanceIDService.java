package io.caly.calyandroid.service;

import io.caly.calyandroid.util.Logger;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.util.ApiClient;
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
        Logger.i(TAG, "onTokenRefresh");
        String token = FirebaseInstanceId.getInstance().getToken();
        Logger.d(TAG, "Refreshed token: " + token);

        requestUpdatePushToken(token);
    }

    private void requestUpdatePushToken(String token) {
        Logger.i(TAG, "sendRegistrationToServer");

        if(TokenRecord.getTokenRecord().getApiKey()!=null) {
            ApiClient.getService().updatePushToken(
                    token,
                    TokenRecord.getTokenRecord().getApiKey()
            ).enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                    Logger.d(TAG, "onResponse code : " + response.code());

                    if (response.code() == 200) {
                        BasicResponse body = response.body();
                        Logger.d(TAG, "push token update success");

                    } else {
                        Logger.d(TAG, "push token update fail");

                    }
                }

                @Override
                public void onFailure(Call<BasicResponse> call, Throwable t) {

                    Logger.d(TAG, "onfail : " + t.getMessage());
                    Logger.d(TAG, "fail " + t.getClass().getName());

                }
            });
        }
    }



}
