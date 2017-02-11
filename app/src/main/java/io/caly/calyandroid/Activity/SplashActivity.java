package io.caly.calyandroid.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.caly.calyandroid.R;
import io.caly.calyandroid.Util;


/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project Caly
 * @since 17. 2. 11
 */

public class SplashActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();
    }

    void init(){

        getSupportActionBar().hide();
        Util.setStatusBarColor(this, getColor(R.color.colorPrimaryDark));


        timerHandler.sendEmptyMessageDelayed(0,3000);
    }

    Handler timerHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();

            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            super.handleMessage(msg);
        }

    };

    @Override
    protected void onPause() {
        super.onPause();
    }

}
