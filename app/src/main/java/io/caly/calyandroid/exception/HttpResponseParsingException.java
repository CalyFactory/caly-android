package io.caly.calyandroid.exception;

import android.util.Log;

import java.io.IOException;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.util.Util;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jspiner on 2017. 5. 3..
 */

public class HttpResponseParsingException extends Exception {
    //로그에 쓰일 tag
    public final static String TAG = CalyApplication.class.getSimpleName() + "/" + HttpResponseParsingException.class.getSimpleName();

    public HttpResponseParsingException(Call httpCall, Throwable throwable){
        this(getLogMessage(httpCall, throwable));
        Logger.d(TAG, "HttpResponseParsingException(call, throwable)");
    }

    public HttpResponseParsingException(String msg){
        super(msg);
        Logger.d(TAG, "HttpResponseParsingException(msg)");
    }

    private static String getLogMessage(Call httpCall, Throwable throwable){
        Logger.d(TAG, "getLogMessage()");
        StringBuilder builder = new StringBuilder();

        Request request = httpCall.request();

        builder.append("request url : " + request.url());
        builder.append("\r\n");
        builder.append("request method : " + request.method());
        builder.append("\r\n");
        builder.append("request body : " + Util.requestBodyToString(request.body()));
        builder.append("\r\n");


        Logger.d(TAG, builder.toString());
        return builder.toString();
    }


}
