package io.caly.calyandroid.page.splash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

import butterknife.ButterKnife;
import io.caly.calyandroid.util.Logger;

import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;

import io.caly.calyandroid.activity.base.BaseAppCompatActivity;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.Util;
import io.caly.calyandroid.util.tracker.AnalysisTracker;


/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project Caly
 * @since 17. 2. 11
 */

public class SplashActivity extends BaseAppCompatActivity {

    private SplashPresenter presenter;
    private SplashFragment splashView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_splash);


        ButterKnife.bind(this);

        init();
    }

    void init(){

        AnalysisTracker.getAppSession().refreshKey();

        Util.setStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDark));

        splashView = SplashFragment.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_splash_container, splashView);
        transaction.commit();

        presenter = new SplashPresenter(
                splashView
        );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "onActivityResult : " + requestCode);

        switch (requestCode){
            case Util.RC_PERMISSION_PHONE_STATE:
                if(presenter.isPermissionGranted(this)){
                    presenter.requestVersionCheck();
                }
                else{
                    presenter.requestPermission(this);
                }
                break;
            case Util.RC_INTENT_GOOGLE_SIGNIN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                Logger.d(TAG, "handleSignInResult:" + result.isSuccess());
                Logger.d(TAG, "handleSignInResult:" + result.getStatus().getStatus());

                if (result.isSuccess()) {
                    GoogleSignInAccount acct = result.getSignInAccount();
                    Logger.d(TAG, acct.getDisplayName());
                    Logger.i(TAG, "id token : " + acct.getIdToken());
                    Logger.i(TAG, "serverauthcode : " + acct.getServerAuthCode());
                    Logger.i(TAG, "id : " + acct.getId());
                    Logger.d(TAG, "email : " + acct.getEmail());

                    presenter.procLoginGoogle(acct.getId(), acct.getServerAuthCode());

                } else {
                    switch (result.getStatus().getStatusCode()){
                        case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                            Toast.makeText(
                                    getBaseContext(),
                                    getString(R.string.toast_msg_login_canceled),
                                    Toast.LENGTH_LONG
                            ).show();
                            break;
                        case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                            Toast.makeText(
                                    getBaseContext(),
                                    getString(R.string.toast_msg_login_fail),
                                    Toast.LENGTH_LONG
                            ).show();
                            break;
                        default:
                            Toast.makeText(
                                    getBaseContext(),
                                    getString(R.string.toast_msg_unknown_error),
                                    Toast.LENGTH_LONG
                            ).show();
                            break;
                    }

                }
                break;
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
                presenter.requestVersionCheck();

            } else{
                //set to never ask again
                Logger.i(TAG,"never ask");

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
