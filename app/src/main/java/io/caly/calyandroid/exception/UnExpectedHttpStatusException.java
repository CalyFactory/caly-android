package io.caly.calyandroid.exception;

import android.util.Log;

import java.io.IOException;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Util;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jspiner on 2017. 5. 3..
 */

public class UnExpectedHttpStatusException extends Exception {

    //로그에 쓰일 tag
    public final static String TAG = CalyApplication.class.getSimpleName() + "/" + UnExpectedHttpStatusException.class.getSimpleName();

    public UnExpectedHttpStatusException(Call httpCall, Response httpResponse){
        this(getLogMessage(httpCall, httpResponse));
        Logger.d(TAG, "UnExpectedHttpStatusException(call, response)");
    }

    public UnExpectedHttpStatusException(String msg){
        super(msg);
        Logger.d(TAG, "UnExpectedHttpStatusException(msg)");
    }

    private static String getLogMessage(Call httpCall, Response httpResponse){
        Logger.d(TAG, "getLogMessage()");
        StringBuilder builder = new StringBuilder();

        Request request = httpCall.request();

        builder.append("request url : " + request.url());
        builder.append("\r\n");
        builder.append("request method : " + request.method());
        builder.append("\r\n");
        builder.append("request body : " + Util.requestBodyToString(request.body()));
        builder.append("\r\n");
        builder.append("response code : " + httpResponse.code());
        builder.append("\r\n");
        try {
            builder.append("response body : " + httpResponse.errorBody().string());
        } catch (IOException e) {
            e.printStackTrace();
            builder.append("response body : read error("+e.getMessage());
        }

        Logger.d(TAG, builder.toString());
        return builder.toString();
    }


}
