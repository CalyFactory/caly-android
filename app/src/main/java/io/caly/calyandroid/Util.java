package io.caly.calyandroid;

import calyfactory.io.caly.Model.HttpService;
import retrofit2.Retrofit;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project Caly
 * @since 17. 2. 11
 */

public class Util {

    //로그에 쓰일 tag
    private static final String TAG = Util.class.getSimpleName();

    private static HttpService httpService;

    // http service
    public static HttpService getHttpService() {
        if(httpService == null){
            Util.httpService =
                    new Retrofit.Builder()
                            .baseUrl("http://naver.com")
                            .build()
                            .create(HttpService.class);
        }

        return Util.httpService;
    }

}
