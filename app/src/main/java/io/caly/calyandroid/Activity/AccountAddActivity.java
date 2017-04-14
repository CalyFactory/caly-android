package io.caly.calyandroid.Activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Adapter.AccountAddAdapter;
import io.caly.calyandroid.Model.LoginPlatform;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.EventListener.RecyclerItemClickListener;
import io.caly.calyandroid.View.GoogleOAuthDialog;
import io.caly.calyandroid.View.LoginDialog;
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

    @Bind(R.id.recycler_accountlist)
    RecyclerView recyclerList;

    AccountAddAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountadd);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        //set toolbar
        toolbar.setTitle("계정 추가");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        setSupportActionBar(toolbar);

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

                                        dialog = new GoogleOAuthDialog(AccountAddActivity.this, new GoogleOAuthDialog.LoginCallback() {
                                            @Override
                                            public void onLoginSuccess(Dialog dialog, String code) {
                                                requestAddGoogle(code);
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onLoginFailed(Dialog dialog, String error) {
                                                dialog.dismiss();

                                            }
                                        });
                                        break;
                                    case 1: //naver
                                        dialog = new LoginDialog(AccountAddActivity.this, "Naver로 로그인", new LoginDialog.LoginDialogCallback(){

                                            @Override
                                            public void onPositive(LoginDialog dialog, String userId, String userPw) {
                                                requestAddCaldav(LoginPlatform.CALDAV_NAVER.value, userId, userPw);
                                                dialog.dismiss();

                                            }

                                            @Override
                                            public void onNegative(LoginDialog dialog) {
                                                dialog.dismiss();

                                            }
                                        });
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
                                        break;
                                }

                                dialog.show();

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }
                )
        );
    }

    void requestAddCaldav(String loginPlatform, String userId, String userPw){
        requestAddAccount(loginPlatform, userId, userPw, "null");
    }

    void requestAddGoogle(String authCode){
        requestAddAccount(LoginPlatform.GOOGLE.value, "null", "null", authCode);
    }

    void requestAddAccount(String loginPlatform, String userId, String userPw, String authCode){
        Log.i(TAG, "requestAddAccount");

        ApiClient.getService().addAccount(
                TokenRecord.getTokenRecord().getApiKey(),
                loginPlatform,
                userId,
                userPw,
                authCode
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                BasicResponse body = response.body();
                switch (response.code()){
                    case 200:
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
                    case 403:
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_add_account_duplicate),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                    default:
                        Log.e(TAG,"status code : " + response.code());
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
