package io.caly.calyandroid.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import io.caly.calyandroid.Model.Response.SessionResponse;
import io.caly.calyandroid.Model.ORM.SessionRecord;
import io.caly.calyandroid.Model.ORM.SettingRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.Util;
import retrofit2.Call;
import retrofit2.Response;


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

    private final int PERMISSION_CODE = 1111;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();
    }

    void init(){

        Util.setStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDark));


        //firebase init
        Log.d(TAG, "push token : " + FirebaseInstanceId.getInstance().getToken());
        FirebaseMessaging.getInstance().subscribeToTopic("noti");

        if(isPermissionGranted()){
            startSplash();
        }
        else{
            requestPermission();
        }
    }

    void startSplash(){

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

    void requestPermission(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.READ_PHONE_STATE
                },
                PERMISSION_CODE
        );
    }

    boolean isPermissionGranted(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        return true;
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

    void startEventActivity(){
        Intent intent = new Intent(SplashActivity.this, EventListActivity.class);
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
                    Log.d(TAG, "no login");
                    startLoginActivity();
                }
                else{
                    Log.d(TAG,"session : " + sessionRecord.getSessionKey());

                    Util.getHttpService().loginCheck(
                            "null",
                            "null",
                            Util.getUUID(),
                            sessionRecord.getSessionKey(),
                            "null",
                            "null",
                            Util.getAppVersion()
                    ).enqueue(new retrofit2.Callback<SessionResponse>() {
                        @Override
                        public void onResponse(Call<SessionResponse> call, Response<SessionResponse> response) {
                            Log.d(TAG,"onResponse code : " + response.code());

                            if(response.code() == 200){
                                SessionResponse body = response.body();
                                switch (body.code){
                                    case 200:
                                        startEventActivity();
                                        break;
                                    case  401:
                                        Toast.makeText(
                                                getBaseContext(),
                                                getString(R.string.toast_msg_session_invalid),
                                                Toast.LENGTH_LONG
                                        ).show();
                                        finish();
                                        break;
                                    default:
                                        Toast.makeText(
                                                getBaseContext(),
                                                getString(R.string.toast_msg_session_invalid),
                                                Toast.LENGTH_LONG
                                        ).show();
                                        SessionRecord.destorySession();
                                        startLoginActivity();
                                }
                            }
                            else{
                                Toast.makeText(
                                        getBaseContext(),
                                        getString(R.string.toast_msg_server_internal_error),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SessionResponse> call, Throwable t) {

                            Log.e(TAG,"onfail : " + t.getMessage());
                            Log.e(TAG, "fail " + t.getClass().getName());

                            Toast.makeText(
                                    getBaseContext(),
                                    getString(R.string.toast_msg_network_error),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });

                }

            }


            super.handleMessage(msg);
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == PERMISSION_CODE){

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startSplash();
            }
            else{
                finish();
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
