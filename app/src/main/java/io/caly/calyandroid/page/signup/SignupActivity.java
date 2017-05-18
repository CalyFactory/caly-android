package io.caly.calyandroid.page.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import io.caly.calyandroid.util.Logger;

import butterknife.ButterKnife;
import io.caly.calyandroid.page.base.BaseAppCompatActivity;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.Util;

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