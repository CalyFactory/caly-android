package io.caly.calyandroid.activity.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import io.caly.calyandroid.util.Logger;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.util.BusProvider;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 27
 */

public class BaseAppCompatActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    public final String TAG = CalyApplication.class.getSimpleName() + "/" + this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Logger.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);


        Tracker t = ((CalyApplication)getApplication()).getDefaultTracker();
        t.setScreenName(this.getClass().getName());
        t.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    protected void onStart() {
        Logger.i(TAG, "onStart : " + super.hashCode());
        super.onStart();

        BusProvider.getInstance().register(this);
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        Logger.i(TAG, "onStop : " + super.hashCode());
        super.onStop();

        BusProvider.getInstance().unregister(this);
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onPause() {
        Logger.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Logger.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Logger.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        Logger.i(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        Logger.i(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.i(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ")");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
