package io.caly.calyandroid.util.tracker;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 10
 */

public class AnalysisTracker {

    //로그에 쓰일 tag
    public final static String TAG = CalyApplication.class.getSimpleName() + "/" + AnalysisTracker.class.getSimpleName();

    private static AppSession session;
    private static Context context;

    public static void initTracker(Context context){
        AnalysisTracker.context = context;
        Logger.i(TAG, "session key : " + getAppSession().getSessionKey().toString());
    }

    public static AppSession getAppSession(){
        if(context == null){
            throw new NullPointerException("context is null");
        }

        if(session == null){
            session = new AppSession(context.getResources().getInteger(R.integer.appsession_expiretime));
        }
        return session;
    }

    //TODO log 수집 api 만들기
    public static void requestScreenLog(String screenName, int status){
        //apikey가 없다면

        ApiClient.getService().setScreenLog(
                TokenRecord.getTokenRecord().getApiKey(),
                getAppSession().getSessionKey().toString(),
                screenName,
                status
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());
                Logger.d(TAG, "param" + Util.requestBodyToString(call.request().body()));

                switch (response.code()){
                    case 200:
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

            }
        });

    }


}
