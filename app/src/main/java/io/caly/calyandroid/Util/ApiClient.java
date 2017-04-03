package io.caly.calyandroid.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.jspiner.prefer.Prefer;

import java.util.concurrent.TimeUnit;

import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.Deserializer.EventDeserialize;
import io.caly.calyandroid.Model.DataModel.EventModel;
import io.caly.calyandroid.Model.Deserializer.RecoStateDeserializer;
import io.caly.calyandroid.Model.RecoState;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Service.HttpService;
import io.caly.calyandroid.Util.EventListener.LoggingInterceptor;
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
            client.connectTimeout(20, TimeUnit.SECONDS);
            client.readTimeout(20, TimeUnit.SECONDS);
            client.writeTimeout(20, TimeUnit.SECONDS);


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
