package io.caly.calyandroid;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.orm.SugarApp;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class CalyApplication extends SugarApp {
    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseApp.initializeApp(this);
    }
}
