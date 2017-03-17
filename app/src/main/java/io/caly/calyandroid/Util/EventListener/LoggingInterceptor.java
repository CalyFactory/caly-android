package io.caly.calyandroid.Util.EventListener;

import android.util.Log;

import java.io.IOException;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Util.Util;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 20
 */

public class LoggingInterceptor implements Interceptor {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + LoggingInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Log.i(TAG, "Send to " + request.url() + "\nbody\n" + Util.requestBodyToString(request.body()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();

        String responseString = new String(response.body().bytes());

        Log.i(TAG, "Response in " + (t2 - t1) + "\ncode : " + response.code() + "\n" + responseString);

        return  response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), responseString))
                .build();
    }

}
