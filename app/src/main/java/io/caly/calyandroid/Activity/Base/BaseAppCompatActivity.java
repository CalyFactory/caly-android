package io.caly.calyandroid.Activity.Base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import io.caly.calyandroid.Activity.SplashActivity;
import io.caly.calyandroid.CalyApplication;

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
        super.onCreate(savedInstanceState);

/*
        Tracker t = ((CalyApplication)getApplication()).getDefaultTracker();
        t.setScreenName(this.getClass().getName());
        t.send(new HitBuilders.AppViewBuilder().build());*/

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
