package io.caly.calyandroid.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 15
 */

public class SignupActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    private static final String TAG = SignupActivity.class.getSimpleName();

    @Bind(R.id.imv_signup_man)
    ImageView imvGenderMan;

    @Bind(R.id.imv_signup_woman)
    ImageView imvGenderWoman;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
    }

    void init(){

        ButterKnife.bind(this);

        //set toolbar
        Util.setStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDark));

        imvGenderMan.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
        imvGenderWoman.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    @OnClick(R.id.imv_signup_man)
    void onGenderManClick(){

        imvGenderMan.clearColorFilter();
        imvGenderWoman.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    @OnClick(R.id.imv_signup_woman)
    void onGenderWomanClick(){

        imvGenderMan.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
        imvGenderWoman.clearColorFilter();
    }
}
