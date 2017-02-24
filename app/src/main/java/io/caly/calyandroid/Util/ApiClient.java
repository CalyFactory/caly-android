package io.caly.calyandroid.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.Deserializer.EventInstanceCreator;
import io.caly.calyandroid.Model.EventModel;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Service.HttpService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 24
 */

public class ApiClient {

    //로그에 쓰일 tag
    private static final String TAG = ApiClient.class.getSimpleName();

    private static HttpService httpService;

    private static Gson gsonObject;

    //gson
    public static Gson getGson(){
        if(gsonObject == null){

            gsonObject = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .registerTypeAdapter(EventModel.class, new EventInstanceCreator())
                    .create();
        }

        return gsonObject;
    }


    // CodeReview : 싱글톤 좀 더 공부해보기 , HttpService.getInstance()등이 더 명확하다.
    // http service
    /*
    여러 패턴이 있지만, multi thread문제만 해결하면 될거라고봄
     */
    public static synchronized HttpService getService() {
        if(httpService == null){

            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.addInterceptor(new LoggingInterceptor());

            ApiClient.httpService =
                    new Retrofit.Builder()
                            .baseUrl(CalyApplication.getContext().getString(R.string.app_server) + CalyApplication.getContext().getString(R.string.app_server_version) + "/")
                            .addConverterFactory(GsonConverterFactory.create(getGson()))
                            .client(client.build())
                            .build()
                            .create(HttpService.class);

        }

        return ApiClient.httpService;
    }
}
