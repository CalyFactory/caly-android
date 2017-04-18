package io.caly.calyandroid.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import net.jspiner.prefer.Prefer;

import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Model.LoginPlatform;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.Response.SessionResponse;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.ConfigClient;
import io.caly.calyandroid.Util.StringFormmater;
import io.caly.calyandroid.Util.Util;
import io.caly.calyandroid.View.LoginDialog;
import io.caly.calyandroid.View.PasswordChangeDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project Caly
 * @since 17. 2. 11
 */

public class SplashActivity extends BaseAppCompatActivity {

    long startTimeMillisec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();
    }

    void init(){

        startTimeMillisec = System.currentTimeMillis();

        Util.setStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDark));

        //firebase init
        Log.i(TAG, "pushToken : " + FirebaseInstanceId.getInstance().getToken());
        FirebaseMessaging.getInstance().subscribeToTopic("noti");

        if(isPermissionGranted()){
            startSplash();
        }
        else{
            requestPermission();
        }
    }

    void startSplash(){
        //update remote config
        ConfigClient.getConfig()
                .fetch(0/*
                        getResources()
                                .getInteger(
                                        R.integer.firebase_remoteconfig_cache_expiretime
                                )*/
                ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.i(TAG, "remoteConfig fetch result : " + task.isSuccessful());
                    if(task.isSuccessful()){
                        ConfigClient.getConfig().activateFetched();
                    }

                    long diffTimeMillisec = System.currentTimeMillis() - startTimeMillisec;
                    Log.d(TAG, "isdidrun : " + Prefer.get("isDidRun", false));
                    if(Prefer.get("isDidRun", false)){
                        timerHandler.sendEmptyMessageDelayed(0,diffTimeMillisec>1000?1000:1000-diffTimeMillisec);
                    }
                    else{
                        timerHandler.sendEmptyMessageDelayed(1,diffTimeMillisec>1500?1500:1500-diffTimeMillisec);
                    }
                    Prefer.set("isDidRun", true);
                }
            }
        );
    }

    void requestPermission(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.READ_PHONE_STATE
                },
                Util.RC_PERMISSION_PHONE_STATE
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

    void requestUpdate(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(intent);
    }

    Handler timerHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            // 첫 실행일 경우
            if(msg.what == 1){
                startGuideActivity();
            }
            else{
                TokenRecord tokenRecord = TokenRecord.getTokenRecord();

                //로그인 정보가 없을 경우
                if(tokenRecord.getApiKey() == null){
                    Log.d(TAG, "no login");
                    startLoginActivity();
                }
                else{
                    Log.d(TAG,"session : " + tokenRecord.getApiKey());

                    requestLoginCheck(tokenRecord.getApiKey());

                }

            }


            super.handleMessage(msg);
        }

    };

    void requestLoginCheck(String apiKey){
        Log.i(TAG, "requestLoginCheck");

        ApiClient.getService().loginCheck(
                "null",
                "null",
                Util.getUUID(),
                apiKey,
                "null",
                "null",
                Util.getAppVersion()
        ).enqueue(new retrofit2.Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, Response<SessionResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                SessionResponse body = response.body();
                switch (response.code()){
                    case 200:
                        startEventActivity();
                        break;
                    case 400: //세션이 없거나 만료됬음.
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_session_invalid),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                    case 401: //비밀번호가 변경되어있음.
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_password_changed),
                                Toast.LENGTH_LONG
                        ).show();
                        showChangePasswordDialog();
                        break;
                    case 403:
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_app_version_not_latest),
                                Toast.LENGTH_LONG
                        ).show();
                        requestUpdate();
                        finish();
                        break;
                    default:
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

    void showChangePasswordDialog(){
        PasswordChangeDialog dialog = new PasswordChangeDialog(SplashActivity.this,
                TokenRecord.getTokenRecord().getLoginPlatform() + " 계정 비밀번호 변경",
                TokenRecord.getTokenRecord().getUserId(),
                new PasswordChangeDialog.LoginDialogCallback() {
            @Override
            public void onPositive(PasswordChangeDialog dialog, String userId, String userPw) {
                dialog.dismiss();

                ApiClient.getService().updateAccount(
                        userId,
                        userPw,
                        TokenRecord.getTokenRecord().getLoginPlatform()
                ).enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        Log.d(TAG,"onResponse code : " + response.code());

                        BasicResponse body = response.body();
                        switch (response.code()) {
                            case 200:
                                requestLoginCheck(TokenRecord.getTokenRecord().getApiKey());
                                break;
                            case 401:
                                Toast.makeText(getBaseContext(), getString(R.string.toast_msg_login_fail), Toast.LENGTH_LONG).show();
                                showChangePasswordDialog();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {

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

            @Override
            public void onNegative(PasswordChangeDialog dialog) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(isPermissionGranted()){
            startSplash();
        }
        else{
            requestPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){
            //denied
            finish();

        }else{
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                //allowed
                startSplash();

            } else{
                //set to never ask again
                Log.i(TAG,"never ask");

                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setMessage("캘리 서비스를 이용하시려면 권한 설정이 필요합니다. 설정 번튼을 눌러 [어플리케이션 정보 > 권한] 에서 권한을 허용해주세요.");
                builder.setTitle("권한 필요");
                builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }
                );
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.show();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
