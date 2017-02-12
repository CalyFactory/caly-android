package io.caly.calyandroid.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.caly.calyandroid.Model.SessionRecord;
import io.caly.calyandroid.Model.SettingRecord;
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

        Util.setStatusBarColor(this, getColor(R.color.colorPrimaryDark));

        SettingRecord currentSetting = SettingRecord.getSettingRecord();

        if(currentSetting.isDidRun()){
            timerHandler.sendEmptyMessageDelayed(0,1000);
        }
        else{
            timerHandler.sendEmptyMessageDelayed(1,3000);
        }

        currentSetting.setDidRun(true);
        currentSetting.save();

    }

    void startLoginActivity(){
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    void startGuideActivity(){
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    Handler timerHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            // 첫 실행일 경우
            if(msg.what == 1){
                startGuideActivity();
            }
            else{
                SessionRecord sessionRecord = SessionRecord.getSessionRecord();
                //로그인 정보가 없을 경우
                if(sessionRecord.getSessionKey() == null){
                    startLoginActivity();
                }
                else{
                    //TODO : 로그인검증로직필요
                }

            }


            super.handleMessage(msg);
        }

    };

    @Override
    protected void onPause() {
        super.onPause();
    }

}
