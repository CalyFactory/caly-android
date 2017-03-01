package io.caly.calyandroid.Util.EventListener;

import android.util.Log;

import java.io.IOException;

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
    private static final String TAG = LoggingInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Log.d(TAG, String.format("Sending \n%s \n%s",
                request.url(), Util.requestBodyToString(request.body())));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
//        Log.d(TAG, String.format("Received for %s in %.1fms%n%s",
//                response.request().url(), (t2 - t1) / 1e6d, response.headers()));


        final String responseString = new String(response.body().bytes());

        Log.d(TAG, "Response "+response.code()+"\n" + responseString);

        return  response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), responseString))
                .build();
    }

}
