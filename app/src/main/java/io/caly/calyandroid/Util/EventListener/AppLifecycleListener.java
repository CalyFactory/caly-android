package io.caly.calyandroid.Util.EventListener;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import io.caly.calyandroid.Util.Logger;

import io.caly.calyandroid.CalyApplication;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public class AppLifecycleListener implements Application.   ActivityLifecycleCallbacks{

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + AppLifecycleListener.class.getSimpleName();

    private static int activeActivities = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Logger.i(TAG, "onActivityCreated : " + activity.getLocalClassName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Logger.i(TAG, "onActivityStarted : " + activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Logger.d(TAG, "onActivityResumed : " + activity.getLocalClassName());
        addActivity();
        Logger.i(TAG, "activity count : " + activeActivities);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Logger.d(TAG, "onActivityPaused : " + activity.getLocalClassName());
        delActivity();
        Logger.i(TAG, "activity count : " + activeActivities);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Logger.d(TAG, "onActivityStopped : " + activity.getLocalClassName());

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public void addActivity(){
        activeActivities++;
    }

    public void delActivity(){
        activeActivities--;
    }

    public static int getActiveActivityCount(){
        return activeActivities;
    }

}
