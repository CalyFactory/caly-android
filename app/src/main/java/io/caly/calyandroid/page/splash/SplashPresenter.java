package io.caly.calyandroid.page.splash;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import net.jspiner.prefer.Prefer;

import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;
import io.caly.calyandroid.page.base.BaseAppCompatActivity;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.DeviceType;
import io.caly.calyandroid.model.LoginPlatform;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.model.response.SessionResponse;
import io.caly.calyandroid.page.base.BasePresenter;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.ConfigClient;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 2
 */

public class SplashPresenter extends BasePresenter implements SplashContract.Presenter {


    private long startTimeMillisec;

    private SplashContract.View splashView;

    private GoogleApiClient mGoogleApiClient;

    public SplashPresenter(SplashContract.View splashView){
        this.splashView = splashView;

        splashView.setPresenter(this);
    }

    @Override
    public void setStartTime() {

        startTimeMillisec = System.currentTimeMillis();
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
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        splashView.showToast(
                                R.string.toast_msg_server_internal_error,
                                Toast.LENGTH_LONG
                        );
                }
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {
                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }

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
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        splashView.showToast(
                                R.string.toast_msg_server_internal_error,
                                Toast.LENGTH_LONG
                        );
                }
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {
                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }

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
    public void updateRemoteConfig(Context context) {

        long cacheTime = 0;
        if(BuildConfig.DEBUG){
            cacheTime = 0;
        }
        else{
            context.getResources().getInteger(R.integer.firebase_remoteconfig_cache_expiretime);
        }

        ConfigClient.getConfig()
                .fetch(cacheTime)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Logger.i(TAG, "remoteConfig fetch result : " + task.isSuccessful());
                                if(task.isSuccessful()){
                                    ConfigClient.getConfig().activateFetched();
                                }

                                long diffTimeMillisec = System.currentTimeMillis() - startTimeMillisec;
                                Logger.d(TAG, "isdidrun : " + Prefer.get("isDidRun", false));
                                if(Prefer.get("isDidRun", false)){
                                    timerHandler.sendEmptyMessageDelayed(0,diffTimeMillisec>1000?1000:1000-diffTimeMillisec);
                                }
                                else{
                                    timerHandler.sendEmptyMessageDelayed(1,diffTimeMillisec>2000?2000:2000-diffTimeMillisec);
                                }
                                Prefer.set("isDidRun", true);
                            }
                        }
        );
    }

    @Override
    public void initGoogleLogin(Activity activity) {

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestServerAuthCode(activity.getString(R.string.google_client_id), true)
                        .requestIdToken(activity.getString(R.string.google_client_id))
                        .requestScopes(
                                new Scope("https://www.googleapis.com/auth/calendar"),
                                new Scope("https://www.googleapis.com/auth/userinfo.email"),
                                new Scope("https://www.googleapis.com/auth/calendar.readonly")
                        ).build();

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(
                        (BaseAppCompatActivity)activity,
                        new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Logger.e(TAG, "onConnectionFailed : " + connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return this.mGoogleApiClient;
    }

    @Override
    public void trackingLoginButtonClick(Activity activity, String action) {
        Tracker t = ((CalyApplication)activity.getApplication()).getDefaultTracker();
        t.setScreenName(this.getClass().getName());
        t.send(
                new HitBuilders.EventBuilder()
                        .setCategory(activity.getString(R.string.ga_action_button_click))
                        .setAction(action)
                        .build()
        );
    }

    @Override
    public void procLoginCaldav(String userId, String userPw, String loginPlatform) {
        Logger.i(TAG, "procLoginCaldav");
        procLogin(userId, userPw, loginPlatform, "null", "null");
    }

    @Override
    public void procLoginGoogle(String subject, String authCode) {
        Logger.i(TAG, "procLoginGoogle");
        procLogin("null", "null", LoginPlatform.GOOGLE.value, subject, authCode);
    }

    private void procLogin(final String userId, final String userPw, final String loginPlatform, final String subject, final String authCode){
        Logger.i(TAG, "procLogin");

        splashView.changeProgressState(true);
        ApiClient.getService().loginCheck(
                userId,
                userPw,
                Util.getUUID(),
                "null", //session
                loginPlatform,
                subject,
                Util.getAppVersion()
        ).enqueue(new Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, Response<SessionResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());
                Logger.d(TAG,"req url : " + call.request().url().toString());

                SessionResponse body = response.body();

                TokenRecord tokenRecord = TokenRecord.getTokenRecord();

                signOutGoogle();
                splashView.changeProgressState(false);
                switch (response.code()){
                    case 200:
//                    case 205:
                        tokenRecord.setApiKey(body.payload.apiKey);
                        tokenRecord.setLoginPlatform(loginPlatform);
                        tokenRecord.setUserId(userId);
                        tokenRecord.save();
                        requestUpdatePushToken();
                        splashView.startEventActivity();
                        break;
                    case 202:
                        requestUpdatePushToken();
                        splashView.startSignupActivity(userId, userPw, loginPlatform, authCode);
                        break;
                    case 201:
                        tokenRecord.setApiKey(body.payload.apiKey);
                        tokenRecord.setLoginPlatform(loginPlatform);
                        tokenRecord.setUserId(userId);
                        tokenRecord.save();
                        requestRegisterDeviceInfo(body.payload.apiKey);
                        requestUpdatePushToken();
                        break;
                    case 400:
                    case 401:
                        splashView.showToast(
                                R.string.toast_msg_login_fail,
                                Toast.LENGTH_LONG
                        );
                        signOutGoogle();
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        splashView.showToast(
                                R.string.toast_msg_server_internal_error,
                                Toast.LENGTH_LONG
                        );
                        signOutGoogle();
                        break;
                }


            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {
                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                splashView.changeProgressState(false);

                splashView.showToast(
                        R.string.toast_msg_network_error,
                        Toast.LENGTH_LONG
                );
            }
        });
    }

    private void requestUpdatePushToken() {
        Logger.i(TAG, "sendRegistrationToServer");

        if(TokenRecord.getTokenRecord().getApiKey()!=null) {
            ApiClient.getService().updatePushToken(
                    FirebaseInstanceId.getInstance().getToken(),
                    TokenRecord.getTokenRecord().getApiKey()
            ).enqueue(new Callback<BasicResponse>() {
                @Override
                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                    Logger.d(TAG, "onResponse code : " + response.code());

                    if (response.code() == 200) {
                        BasicResponse body = response.body();
                        Logger.d(TAG, "push token update success");

                    } else {
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        Logger.d(TAG, "push token update fail");

                    }
                }

                @Override
                public void onFailure(Call<BasicResponse> call, Throwable t) {
                    if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                        Crashlytics.logException(new HttpResponseParsingException(call, t));
                    }

                    Logger.d(TAG, "onfail : " + t.getMessage());
                    Logger.d(TAG, "fail " + t.getClass().getName());

                }
            });
        }
    }

    void requestRegisterDeviceInfo(String sessionKey){
        Logger.i(TAG, "registerDeviceInfo");
        ApiClient.getService().registerDevice(
                sessionKey,
                FirebaseInstanceId.getInstance().getToken(),
                DeviceType.ANDROID.value,
                Util.getAppVersion(),
                Util.getDeviceInfo(),
                Util.getUUID(),
                Util.getSdkLevel()
        ).enqueue(new Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, Response<SessionResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());

                SessionResponse body = response.body();

                switch (response.code()){
                    case 200:
                        splashView.startEventActivity();
                        break;
                    case 400:
                        splashView.showToast(
                                R.string.toast_msg_login_fail,
                                Toast.LENGTH_LONG
                        );
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        splashView.showToast(
                                R.string.toast_msg_server_internal_error,
                                Toast.LENGTH_LONG
                        );
                        break;
                }

            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {
                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                splashView.showToast(
                        R.string.toast_msg_network_error,
                        Toast.LENGTH_LONG
                );
            }
        });
    }


    Handler timerHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            // 첫 실행일 경우
            if(msg.what == 1){
                splashView.startGuideActivity();
            }
            else{
                TokenRecord tokenRecord = TokenRecord.getTokenRecord();

                //로그인 정보가 없을 경우
                if(tokenRecord.getApiKey() == null){
                    Logger.d(TAG, "no login");
                    splashView.startLoginAnimation();
                }
                else{
                    Logger.d(TAG,"apikey : " + tokenRecord.getApiKey());

                    requestLoginCheck(tokenRecord.getApiKey());

                }

            }


            super.handleMessage(msg);
        }

    };

    void signOutGoogle(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //Toast.makeText(getBaseContext(),"logout status : " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
