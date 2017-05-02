package io.caly.calyandroid.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import net.jspiner.prefer.Prefer;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.R;
import io.caly.calyandroid.activity.EventListActivity;
import io.caly.calyandroid.activity.GuideActivity;
import io.caly.calyandroid.activity.LoginActivity;
import io.caly.calyandroid.activity.SplashActivity;
import io.caly.calyandroid.contract.SplashContract;
import io.caly.calyandroid.fragment.base.BaseFragment;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.model.response.SessionResponse;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.ConfigClient;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.Util;
import io.caly.calyandroid.view.PasswordChangeDialog;
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

public class SplashFragment extends BaseFragment implements SplashContract.View {

    SplashContract.Presenter presenter;


    long startTimeMillisec;

    @Bind(R.id.linear_splash_logo)
    LinearLayout linearLogo;

    @Bind(R.id.linear_splash_login)
    LinearLayout linearLogin;

    public static SplashFragment getInstance(){
        return new SplashFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        ButterKnife.bind(this, view);

        init();
        return view;
    }

    void init(){

        startTimeMillisec = System.currentTimeMillis();

        //firebase init
        Logger.i(TAG, "pushToken : " + FirebaseInstanceId.getInstance().getToken());
        FirebaseMessaging.getInstance().subscribeToTopic("noti");

        if(presenter.isPermissionGranted(getActivity())){
            presenter.requestVersionCheck();
        }
        else{
            presenter.requestPermission(getActivity());
        }
    }

    @Override
    public void setPresenter(SplashContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showToast(CharSequence text, int duration) {
        Toast.makeText(getActivity(), text, duration).show();
    }

    @Override
    public void showToast(int resId, int duration) {
        Toast.makeText(getActivity(), resId, duration).show();
    }

    @Override
    public void finishActivity() {
        getActivity().finish();
    }

    @Override
    public void startSplash(){
        //update remote config
        ConfigClient.getConfig()
                .fetch(
                        BuildConfig.DEBUG?
                                0:
                                getResources()
                                        .getInteger(
                                                R.integer.firebase_remoteconfig_cache_expiretime
                                        )
                ).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                    timerHandler.sendEmptyMessageDelayed(1,diffTimeMillisec>1500?1500:1500-diffTimeMillisec);
                                                }
                                                Prefer.set("isDidRun", true);
                                            }
                                        }
        );
    }

    void startLoginAnimation(){
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -0.8f
        );
        animation.setDuration(800);
        animation.setFillAfter(true);
        linearLogo.startAnimation(animation);


        linearLogin.setVisibility(View.VISIBLE);
        AlphaAnimation fadeOut = new AlphaAnimation(0f, 1f);
        fadeOut.setStartOffset(200);
        fadeOut.setDuration(600);

        linearLogin.startAnimation(fadeOut);

    }

    @Override
    public void startLoginActivity(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    public void startGuideActivity(){
        Intent intent = new Intent(getActivity(), GuideActivity.class);
        startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void startEventActivity(){
        Intent intent = new Intent(getActivity(), EventListActivity.class);
        startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void startUpdateMarketPage(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
        startActivity(intent);

        getActivity().finish();
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
                    Logger.d(TAG, "no login");
                    startLoginAnimation();
                }
                else{
                    Logger.d(TAG,"session : " + tokenRecord.getApiKey());

                    presenter.requestLoginCheck(tokenRecord.getApiKey());

                }

            }


            super.handleMessage(msg);
        }

    };

    @Override
    public void showChangePasswordDialog(){
        PasswordChangeDialog dialog = new PasswordChangeDialog(getActivity(),
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
                                Logger.d(TAG,"onResponse code : " + response.code());

                                BasicResponse body = response.body();
                                switch (response.code()) {
                                    case 200:
                                        presenter.requestLoginCheck(TokenRecord.getTokenRecord().getApiKey());
                                        break;
                                    case 401:
                                        showToast(R.string.toast_msg_login_fail, Toast.LENGTH_LONG);
                                        showChangePasswordDialog();
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<BasicResponse> call, Throwable t) {

                                Logger.e(TAG,"onfail : " + t.getMessage());
                                Logger.e(TAG, "fail " + t.getClass().getName());

                                showToast(
                                        R.string.toast_msg_network_error,
                                        Toast.LENGTH_LONG
                                );
                            }
                        });

                    }

                    @Override
                    public void onNegative(PasswordChangeDialog dialog) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
        dialog.show();
    }

}
