package io.caly.calyandroid.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.jspiner.prefer.Prefer;

import java.util.concurrent.TimeUnit;

import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.model.deserializer.EventDeserialize;
import io.caly.calyandroid.model.dataModel.EventModel;
import io.caly.calyandroid.model.deserializer.RecoStateDeserializer;
import io.caly.calyandroid.model.RecoState;
import io.caly.calyandroid.R;
import io.caly.calyandroid.service.HttpService;
import io.caly.calyandroid.util.eventListener.LoggingInterceptor;
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
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + ApiClient.class.getSimpleName();

    private static HttpService httpService;

    private static Gson gsonObject;

    //gson
    public static Gson getGson(){
        if(gsonObject == null){

            gsonObject = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .registerTypeAdapter(EventModel.class, new EventDeserialize())
                    .registerTypeAdapter(RecoState.class, new RecoStateDeserializer())
                    .create();
        }

        return gsonObject;
    }

    public static synchronized HttpService getService() {
        if(httpService == null){

            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.addInterceptor(new LoggingInterceptor());
            client.connectTimeout(60, TimeUnit.SECONDS);
            client.readTimeout(60, TimeUnit.SECONDS);
            client.writeTimeout(60, TimeUnit.SECONDS);


            ApiClient.httpService =
                    new Retrofit.Builder()
                            .baseUrl(getAppServerUrl())
                            .addConverterFactory(GsonConverterFactory.create(getGson()))
                            .client(client.build())
                            .build()
                            .create(HttpService.class);

        }

        return ApiClient.httpService;
    }

    public static synchronized void resetService(){
        httpService = null;
        getService();
    }

    private static String getAppServerUrl(){
        if(BuildConfig.DEBUG){
            return Prefer.get("app_server", CalyApplication.getContext().getString(R.string.app_server));
        }
        else{
            return CalyApplication.getContext().getString(R.string.app_server);
        }
    }
}
