package io.caly.calyandroid.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Model.DeviceType;
import io.caly.calyandroid.Model.LoginPlatform;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.Response.SessionResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.Util;
import io.caly.calyandroid.View.LoginDialog;
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

public class LoginActivity extends BaseAppCompatActivity {

    GoogleApiClient mGoogleApiClient;

    @Bind(R.id.btn_login_google)
    SignInButton btnLoginGoogle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

    }

    private void init(){

        ButterKnife.bind(this);


        //set toolbar
        Util.setStatusBarColor(this, Color.BLACK);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestServerAuthCode(getString(R.string.google_client_id))
                        .requestIdToken(getString(R.string.google_client_id))
                        .requestScopes(
                                new Scope("https://www.googleapis.com/auth/calendar"),
                                new Scope("https://www.googleapis.com/auth/userinfo.email"),
                                new Scope("https://www.googleapis.com/auth/calendar.readonly"),
                                new Scope(Scopes.PLUS_ME),
                                new Scope(Scopes.PLUS_LOGIN)
                        ).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , onGoogleConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    OnConnectionFailedListener onGoogleConnectionFailedListener = new OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed : " + connectionResult.getErrorMessage());
        }
    };

    @OnClick(R.id.btn_login_google)
    void onGoogleLoginClick(){
        Log.d(TAG,"onclick");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Util.RC_INTENT_GOOGLE_SIGNIN);
    }

    @OnClick(R.id.btn_login_naver)
    void onNaverLoginClick(){
        LoginDialog dialog = new LoginDialog(this, "Naver로 로그인", new LoginDialog.LoginDialogCallback() {
            @Override
            public void onPositive(LoginDialog dialog, String userId, String userPw) {
                dialog.dismiss();

                procLoginCaldav(userId, userPw, LoginPlatform.CALDAV_NAVER.value);
            }

            @Override
            public void onNegative(LoginDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @OnClick(R.id.btn_login_apple)
    void onAppleLoginClick(){
        LoginDialog dialog = new LoginDialog(this, "Apple로 로그인", new LoginDialog.LoginDialogCallback() {
            @Override
            public void onPositive(LoginDialog dialog, String userId, String userPw) {
                dialog.dismiss();

                procLoginCaldav(userId, userPw, LoginPlatform.CALDAV_ICAL.value);
            }

            @Override
            public void onNegative(LoginDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void startEventActivity(){
        Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
        startActivity(intent);
        finish();
    }

    void startSignupActivity(String userId, String userPw, String loginPlatform, String authCode){
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userPw", userPw);
        intent.putExtra("loginPlatform", loginPlatform);
        intent.putExtra("authCode", authCode);
        startActivity(intent);
        finish();
    }

    void signOutGoogle(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(getBaseContext(),"logout status : " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    void registerDeviceInfo(String sessionKey){
        Log.i(TAG, "registerDeviceInfo");
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
                Log.d(TAG,"onResponse code : " + response.code());

                SessionResponse body = response.body();

                switch (response.code()){
                    case 200:
                        TokenRecord session = TokenRecord.getTokenRecord();
                        session.setApiKey(body.payload.apiKey);
                        session.save();
                        startEventActivity();
                        break;
                    case 400:
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_login_fail),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                    default:
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
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

    void procLogin(final String userId, final String userPw, final String loginPlatform, final String subject, final String authCode){
        Log.i(TAG, "procLogin");
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
                Log.d(TAG,"onResponse code : " + response.code());
                Log.d(TAG,"req url : " + call.request().url().toString());

                SessionResponse body = response.body();

                TokenRecord tokenRecord = TokenRecord.getTokenRecord();
                switch (response.code()){
                    case 200:
//                    case 205:
                        tokenRecord.setApiKey(body.payload.apiKey);
                        tokenRecord.save();
                        startEventActivity();
                        break;
                    case 202:
                        startSignupActivity(userId, userPw, loginPlatform, authCode);
                        break;
                    case 201:
                        tokenRecord.setApiKey(body.payload.apiKey);
                        tokenRecord.save();
                        registerDeviceInfo(body.payload.apiKey);
                        break;
                    case 400:
                    case 401:
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_login_fail),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                    default:
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
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

    void procLoginCaldav(String userId, String userPw, String loginPlatform){
        Log.i(TAG, "procLoginCaldav");
        procLogin(userId, userPw, loginPlatform, "null", "null");
    }

    void procLoginGoogle(String subject, String authCode){
        Log.i(TAG, "procLoginGoogle");
        procLogin("null", "null", LoginPlatform.GOOGLE.value, subject, authCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Util.RC_INTENT_GOOGLE_SIGNIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            Log.d(TAG, "handleSignInResult:" + result.isSuccess());
            Log.d(TAG, "handleSignInResult:" + result.getStatus().getStatus());


            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.d(TAG, acct.getDisplayName());
                Log.i(TAG, "id token : " + acct.getIdToken());
                Log.i(TAG, "serverauthcode : " + acct.getServerAuthCode());
                Log.i(TAG, "id : " + acct.getId());
                Log.d(TAG, "email : " + acct.getEmail());

                procLoginGoogle(acct.getId(), acct.getServerAuthCode());

            } else {
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_login_fail),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }


}
