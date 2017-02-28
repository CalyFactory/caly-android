package io.caly.calyandroid.Util.EventListener;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public class AppLifecycleListener implements Application.ActivityLifecycleCallbacks{

    //로그에 쓰일 tag
    private static final String TAG = AppLifecycleListener.class.getSimpleName();

    private static int activeActivities = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, "onActivityStarted");
        addActivity();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "onActivityResumed");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "onActivityPaused");
        delActivity();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        Log.d(TAG, "onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "onActivityDestroyed");
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
