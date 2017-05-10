package io.caly.calyandroid.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.util.Logger;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.activity.base.BaseAppCompatActivity;
import io.caly.calyandroid.adapter.AccountAddAdapter;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.model.event.EventListRefreshEvent;
import io.caly.calyandroid.model.LoginPlatform;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.BusProvider;
import io.caly.calyandroid.util.eventListener.RecyclerItemClickListener;
import io.caly.calyandroid.util.StringFormmater;
import io.caly.calyandroid.util.Util;
import io.caly.calyandroid.view.LoginDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 22
 */

public class AccountAddActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;

    @Bind(R.id.recycler_accountlist)
    RecyclerView recyclerList;

    @Bind(R.id.linear_loading_parent)
    LinearLayout linearLoading;

    AccountAddAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountadd);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        //set toolbar
        tvToolbarTitle.setText("계정 추가");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set recyclerview
        recyclerList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerList.setLayoutManager(layoutManager);

        ArrayList<String> dataList = new ArrayList<>();
        dataList.add("Google 계정");
        dataList.add("Naver 계정");
        dataList.add("Apple 계정");
        recyclerAdapter = new AccountAddAdapter(dataList);
        recyclerList.setAdapter(recyclerAdapter);

        recyclerList.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getBaseContext(),
                        recyclerList,
                        new RecyclerItemClickListener.OnItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {

                                Dialog dialog = null;
                                switch (position){
                                    case 0: //google
                                        getGoogleAuthCode();
                                        /*
                                        dialog = new GoogleOAuthDialog(AccountAddActivity.this, new GoogleOAuthDiaLogger.LoginCallback() {
                                            @Override
                                            public void onLoginSuccess(Dialog dialog, String code) {
                                                requestAddGoogle(code);
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onLoginFailed(Dialog dialog, String error) {
                                                dialog.dismiss();

                                            }
                                        });*/
                                        break;
                                    case 1: //naver
                                        dialog = new LoginDialog(AccountAddActivity.this, "Naver로 로그인", new LoginDialog.LoginDialogCallback(){

                                            @Override
                                            public void onPositive(LoginDialog dialog, String userId, String userPw) {
                                                requestAddCaldav(LoginPlatform.CALDAV_NAVER.value, StringFormmater.hostnameAuthGenerator(userId, "naver.com"), userPw);
                                                dialog.dismiss();

                                            }

                                            @Override
                                            public void onNegative(LoginDialog dialog) {
                                                dialog.dismiss();

                                            }
                                        });
                                        dialog.show();
                                        break;
                                    case 2: //ical
                                        dialog = new LoginDialog(AccountAddActivity.this, "Apple로 로그인", new LoginDialog.LoginDialogCallback(){

                                            @Override
                                            public void onPositive(LoginDialog dialog, String userId, String userPw) {
                                                requestAddCaldav(LoginPlatform.CALDAV_ICAL.value, userId, userPw);
                                                dialog.dismiss();

                                            }

                                            @Override
                                            public void onNegative(LoginDialog dialog) {
                                                dialog.dismiss();

                                            }
                                        });
                                        dialog.show();
                                        break;
                                }


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }
                )
        );

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestServerAuthCode(getString(R.string.google_client_id), true)
                        .requestIdToken(getString(R.string.google_client_id))
                        .requestScopes(
                                new Scope("https://www.googleapis.com/auth/calendar"),
                                new Scope("https://www.googleapis.com/auth/userinfo.email"),
                                new Scope("https://www.googleapis.com/auth/calendar.readonly")
                        ).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , onGoogleConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    GoogleApiClient.OnConnectionFailedListener onGoogleConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Logger.d(TAG, "onConnectionFailed : " + connectionResult.getErrorMessage());
        }
    };

    void getGoogleAuthCode(){

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Util.RC_INTENT_GOOGLE_SIGNIN);

        Tracker t = ((CalyApplication)getApplication()).getDefaultTracker();
        t.setScreenName(this.getClass().getName());
        t.send(
                new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.ga_action_button_click))
                        .setAction(Util.getCurrentMethodName())
                        .build()
        );
    }

    void requestAddCaldav(String loginPlatform, String userId, String userPw){
        requestAddAccount(loginPlatform, userId, userPw, "null");
    }

    void requestAddGoogle(String authCode){
        requestAddAccount(LoginPlatform.GOOGLE.value, "null", "null", authCode);
    }

    void requestAddAccount(String loginPlatform, String userId, String userPw, String authCode){
        Logger.i(TAG, "requestAddAccount");
        linearLoading.setVisibility(View.VISIBLE);
        ApiClient.getService().addAccount(
                TokenRecord.getTokenRecord().getApiKey(),
                loginPlatform,
                userId,
                userPw,
                authCode
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());

                linearLoading.setVisibility(View.GONE);
                BasicResponse body = response.body();
                switch (response.code()){
                    case 200:
                        BusProvider.getInstance().post(new EventListRefreshEvent());

                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_add_account_success),
                                Toast.LENGTH_LONG
                        ).show();
                        finish();
                        break;
                    case 201: //TODO : 에러라면 code가 300~500번대어야 말이 될듯?
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_add_account_error),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                    case 401:
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_login_fail),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                    case 403:
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_add_account_duplicate),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        Logger.e(TAG,"status code : " + response.code());
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                linearLoading.setVisibility(View.GONE);
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Util.RC_INTENT_GOOGLE_SIGNIN) {
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

                requestAddGoogle(acct.getServerAuthCode());

            } else {
                switch (result.getStatus().getStatusCode()){

                    case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                        /*
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_login_canceled),
                                Toast.LENGTH_LONG
                        ).show();*/
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
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
