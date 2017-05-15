package io.caly.calyandroid.page.account.add;

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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;
import io.caly.calyandroid.adapter.AccountAddAdapter;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.LoginPlatform;
import io.caly.calyandroid.model.event.EventListRefreshEvent;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.page.base.BaseFragment;
import io.caly.calyandroid.page.splash.SplashFragment;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.BusProvider;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.StringFormmater;
import io.caly.calyandroid.util.Util;
import io.caly.calyandroid.util.eventListener.RecyclerItemClickListener;
import io.caly.calyandroid.view.LoginDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jspiner on 2017. 5. 15..
 */

public class AccountAddFragment extends BaseFragment implements AccountAddContract.View {

    @Bind(R.id.recycler_accountlist)
    RecyclerView recyclerList;

    @Bind(R.id.linear_loading_parent)
    LinearLayout linearLoading;

    @Bind(R.id.tv_progress_title)
    TextView tvProgressTitle;

    AccountAddAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;

    GoogleApiClient mGoogleApiClient;

    AccountAddContract.Presenter presenter;

    public static AccountAddFragment getInstance(){
        return new AccountAddFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accountadd, container, false);

        ButterKnife.bind(this, view);

        init();
        return view;
    }

    void init(){


        //set recyclerview
        recyclerList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerList.setLayoutManager(layoutManager);

        ArrayList<String> dataList = new ArrayList<>();
        dataList.add("Google 계정");
        dataList.add("Naver 계정");
        dataList.add("Apple 계정");
        recyclerAdapter = new AccountAddAdapter(dataList);
        recyclerList.setAdapter(recyclerAdapter);

        recyclerList.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
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
                                        dialog = new LoginDialog(getActivity(), "Naver로 로그인", new LoginDialog.LoginDialogCallback(){

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
                                        dialog = new LoginDialog(getActivity(), "Apple로 로그인", new LoginDialog.LoginDialogCallback(){

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

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() , onGoogleConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        tvProgressTitle.setText("캘린더를 가져오는 중입니다");
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

        Tracker t = ((CalyApplication)getActivity().getApplication()).getDefaultTracker();
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

                        showToast(
                                getString(R.string.toast_msg_add_account_success),
                                Toast.LENGTH_LONG
                        );
                        finishActivity();
                        break;
                    case 201: //TODO : 에러라면 code가 300~500번대어야 말이 될듯?
                        showToast(
                                getString(R.string.toast_msg_add_account_error),
                                Toast.LENGTH_LONG
                        );
                        break;
                    case 401:
                        showToast(
                                getString(R.string.toast_msg_login_fail),
                                Toast.LENGTH_LONG
                        );
                        break;
                    case 403:
                        showToast(
                                getString(R.string.toast_msg_add_account_duplicate),
                                Toast.LENGTH_LONG
                        );
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        Logger.e(TAG,"status code : " + response.code());
                        showToast(
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        );
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                linearLoading.setVisibility(View.GONE);
                showToast(
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                );
            }
        });
    }

    @Override
    public void setPresenter(AccountAddContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void finishActivity() {

    }
}
