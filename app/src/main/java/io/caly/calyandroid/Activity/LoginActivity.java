package io.caly.calyandroid.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util;
import io.caly.calyandroid.View.LoginDialog;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project Caly
 * @since 17. 2. 11
 */

public class LoginActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int codeSignIn = 10011;

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
                        .requestScopes(
                                new Scope("https://www.googleapis.com/auth/calendar"),
                                new Scope("https://www.googleapis.com/auth/userinfo.profile"),
                                new Scope("https://www.googleapis.com/auth/calendar.readonly")
                        )
                        .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , onGoogleConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    OnConnectionFailedListener onGoogleConnectionFailedListener = new OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    };

    @OnClick(R.id.btn_login_google)
    void onGoogleLoginClick(){
        Log.d(TAG,"onclick");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, codeSignIn);
    }

    @OnClick(R.id.btn_login_naver)
    void onNaverLoginClick(){
        LoginDialog dialog = new LoginDialog(this, "Naver로 로그인", new LoginDialog.LoginDialogCallback() {
            @Override
            public void onPositive(LoginDialog dialog, String userId, String userPw) {
                dialog.dismiss();

                Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
                startActivity(intent);
                finish();
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

                Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onNegative(LoginDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == codeSignIn) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            Log.d(TAG, "handleSignInResult:" + result.isSuccess());
            Log.d(TAG, "handleSignInResult:" + result.getStatus().getStatus());


            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                Log.d(TAG, acct.getDisplayName());
                Log.d(TAG, "id token : " + acct.getIdToken());
                Log.d(TAG, "serverauthcode : " + acct.getServerAuthCode());
                Log.d(TAG, "id : " + acct.getId());
                Log.d(TAG, "email : " + acct.getEmail());

                Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(getBaseContext(),"로그인실패",Toast.LENGTH_LONG).show();
                // Signed out, show unauthenticated UI.
            }
        }
    }


}
