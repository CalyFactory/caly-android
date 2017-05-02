package io.caly.calyandroid.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import net.jspiner.prefer.Prefer;

import io.caly.calyandroid.R;
import io.caly.calyandroid.contract.SplashContract;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.SessionResponse;
import io.caly.calyandroid.presenter.base.BasePresenter;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.Util;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 2
 */

public class SplashPresenter extends BasePresenter implements SplashContract.Presenter {

    SplashContract.View splashView;

    public SplashPresenter(SplashContract.View splashView){
        this.splashView = splashView;

        splashView.setPresenter(this);
    }

    @Override
    public boolean isPermissionGranted(Context context) {
        if(
                ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
        ){
            return false;
        }
        return true;
    }

    @Override
    public void requestVersionCheck() {

        Logger.i(TAG, "requestLoginCheck");

        ApiClient.getService().loginCheck(
                "null",
                "null",
                Util.getUUID(),
                "empty",
                "null",
                "null",
                Util.getAppVersion()
        ).enqueue(new retrofit2.Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, Response<SessionResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());

                SessionResponse body = response.body();
                switch (response.code()){
                    case 200:
                        splashView.startSplash();
                        break;
                    case 400: //세션이 없거나 만료됬음.
                        splashView.startSplash();
                        break;
                    case 401: //비밀번호가 변경되어있음.
                        splashView.startSplash();
                        break;
                    case 403:
                        splashView.showToast(
                                R.string.toast_msg_app_version_not_latest,
                                Toast.LENGTH_LONG
                        );
                        splashView.startUpdateMarketPage();
                        break;
                    default:
                        splashView.showToast(
                                R.string.toast_msg_server_internal_error,
                                Toast.LENGTH_LONG
                        );
                }
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {

                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                splashView.showToast(
                        R.string.toast_msg_network_error,
                        Toast.LENGTH_LONG
                );
            }
        });
    }

    @Override
    public void requestPermission(Activity activity) {

        ActivityCompat.requestPermissions(
                activity,
                new String[]{
                        Manifest.permission.READ_PHONE_STATE
                },
                Util.RC_PERMISSION_PHONE_STATE
        );
    }

    @Override
    public void requestLoginCheck(String apiKey){
        Logger.i(TAG, "requestLoginCheck");

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
                Logger.d(TAG,"onResponse code : " + response.code());

                SessionResponse body = response.body();
                switch (response.code()){
                    case 200:
                        splashView.startEventActivity();
                        break;
                    case 400: //세션이 없거나 만료됬음.
                        splashView.showToast(
                                R.string.toast_msg_session_invalid,
                                Toast.LENGTH_LONG
                        );

                        TokenRecord.destoryToken();
                        Prefer.getSharedPreferences().edit().clear().commit();
                        Prefer.set("isDidRun", true);

                        splashView.finishActivity();
                        break;
                    case 401: //비밀번호가 변경되어있음.
                        splashView.showToast(
                                R.string.toast_msg_password_changed,
                                Toast.LENGTH_LONG
                        );
                        splashView.showChangePasswordDialog();
                        break;
                    case 403:
                        splashView.showToast(
                                R.string.toast_msg_app_version_not_latest,
                                Toast.LENGTH_LONG
                        );
                        splashView.startUpdateMarketPage();
                        break;
                    default:
                        splashView.showToast(
                                R.string.toast_msg_server_internal_error,
                                Toast.LENGTH_LONG
                        );
                }
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {

                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                splashView.showToast(
                        R.string.toast_msg_network_error,
                        Toast.LENGTH_LONG
                );
            }
        });
    }

}
