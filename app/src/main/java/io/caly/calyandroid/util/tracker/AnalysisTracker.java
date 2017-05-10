package io.caly.calyandroid.util.tracker;

import android.content.Context;

import java.util.UUID;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.Logger;

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


}
