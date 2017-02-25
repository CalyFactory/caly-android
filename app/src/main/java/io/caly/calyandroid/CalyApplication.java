package io.caly.calyandroid;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.orm.SugarApp;

import net.jspiner.prefer.Prefer;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class CalyApplication extends SugarApp {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        FirebaseApp.initializeApp(this);
        Prefer.init(context, "caly");
    }

    public static Context getContext(){
        return context;
    }
}
