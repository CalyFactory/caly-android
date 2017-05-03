package io.caly.calyandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import io.caly.calyandroid.BuildConfig;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 23
 */

public class ActivityLauncher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    void init(){

        Intent intent = null;
        if(BuildConfig.DEBUG){
            intent = new Intent(this, DebugActivity.class);
        }
        else{
            intent = new Intent(this, SplashActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
