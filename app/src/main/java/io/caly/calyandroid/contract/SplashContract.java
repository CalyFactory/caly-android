package io.caly.calyandroid.contract;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;

import io.caly.calyandroid.contract.base.BaseView;
import io.caly.calyandroid.presenter.base.BasePresenterModel;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 2
 */

public class SplashContract {

    public interface View extends BaseView<Presenter> {
        void startSplash();
        void startLoginActivity();
        void startGuideActivity();
        void startEventActivity();
        void startUpdateMarketPage();
        void showChangePasswordDialog();
        void startLoginAnimation();
        void changeProgressState(boolean isLoading);
        void startSignupActivity(String userId, String userPw, String loginPlatform, String authCode);
    }

    public interface Presenter extends BasePresenterModel {
        void setStartTime();
        boolean isPermissionGranted(Context context);
        void requestVersionCheck();
        void requestPermission(Activity activity);
        void requestLoginCheck(String apiKey);
        void updateRemoteConfig(Context context);
        void initGoogleLogin(Activity activity);
        GoogleApiClient getGoogleApiClient();
        void trackingLoginButtonClick(Activity activity, String action);
        void procLoginCaldav(String userId, String userPw, String loginPlatform);
        void procLoginGoogle(String subject, String authCode);
    }

}
