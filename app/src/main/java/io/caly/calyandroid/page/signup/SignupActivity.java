package io.caly.calyandroid.page.signup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;

import io.caly.calyandroid.activity.EventListActivity;
import io.caly.calyandroid.activity.PolicyActivity;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.page.notice.NoticeFragment;
import io.caly.calyandroid.page.notice.NoticePresenter;
import io.caly.calyandroid.util.Logger;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.caly.calyandroid.page.base.BaseAppCompatActivity;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.model.DeviceType;
import io.caly.calyandroid.model.Gender;
import io.caly.calyandroid.model.response.SessionResponse;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.eventListener.TextViewLinkHandler;
import io.caly.calyandroid.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 15
 */

public class SignupActivity extends BaseAppCompatActivity {

    SignupFragment signupView;
    SignupContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
    }

    void init() {

        ButterKnife.bind(this);

        initToolbar();

        signupView = SignupFragment.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_signup_container, signupView);
        transaction.commit();

        presenter = new SignupPresenter(
                signupView
        );

    }

    void initToolbar(){
        //set toolbar
        Util.setStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDark));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Util.RC_INTENT_POLICY_RESPONSE:

                    Logger.d(TAG, "agree : " + data.getBooleanExtra("agree", false));

                    signupView.setPolicyCheckState(
                            data.getBooleanExtra("agree", false)
                    );

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}