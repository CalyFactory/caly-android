package io.caly.calyandroid;

import android.content.Context;

import io.caly.calyandroid.util.tracker.AnalysisTracker;
import io.caly.calyandroid.util.Logger;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;
import com.orm.SugarApp;

import io.caly.calyandroid.util.eventListener.AppLifecycleListener;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import net.jspiner.prefer.Prefer;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class CalyApplication extends SugarApp {

    private final String TAG = CalyApplication.class.getSimpleName();

    private Tracker mTracker;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
        Logger.i(TAG, "onCreate");
    }

    void init(){

        //set basic context
        context = this;

        //init fabric
        Fabric.with(this, new Crashlytics());

        //init firebase
        FirebaseApp.initializeApp(this);

        //init prefer lib
        Prefer.init(context, "caly");

        //add activitylifecycle listener
        registerActivityLifecycleCallbacks(new AppLifecycleListener());


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NanumSquareOTFBold.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //init analysis
        AnalysisTracker.initTracker(this);
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
    public static Context getContext(){
        return context;
    }
}
