package io.caly.calyandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;

import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.util.Logger;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import io.caly.calyandroid.activity.base.BaseAppCompatActivity;
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

    /*
    @Bind(R.id.imv_signup_man)
    ImageView imvGenderMan;

    @Bind(R.id.imv_signup_woman)
    ImageView imvGenderWoman;*/

    @Bind(R.id.edt_signup_birth)
    EditText edtBirth;

    @Bind(R.id.cb_signup_policy)
    CheckBox cbPolicy;

    @Bind(R.id.btn_signup_proc)
    Button btnSignup;

    @Bind(R.id.linear_loading_parent)
    LinearLayout linearLoading;

    @Bind(R.id.radio_signup_gender_man)
    RadioButton radioGenderMan;

    @Bind(R.id.radio_signup_gender_woman)
    RadioButton radioGenderWoman;

    @Bind(R.id.tv_progress_title)
    TextView tvProgressTitle;

    @Bind(R.id.linear_signup_edtarea)
    LinearLayout linearEdtArea;

    int selectedGender = -1;


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



//        imvGenderMan.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
//        imvGenderWoman.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);


        cbPolicy.setText(Html.fromHtml("캘리의 <a href='signup'>이용정책</a>에 동의합니다."));
        cbPolicy.setMovementMethod(new TextViewLinkHandler() {
            @Override
            public void onLinkClick(String url) {
                Intent intent = new Intent(SignupActivity.this, PolicyActivity.class);
                startActivityForResult(intent, Util.RC_INTENT_POLICY_RESPONSE);
            }
        });

        tvProgressTitle.setText("회원가입 중입니다");


        updateButton();
    }

    /*
    @OnClick(R.id.imv_signup_man)
    void onGenderManClick(){

        selectedGender = Gender.MAN.value;
//        imvGenderMan.clearColorFilter();
//        imvGenderWoman.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);

        updateButton();

    }

    @OnClick(R.id.imv_signup_woman)
    void onGenderWomanClick(){

        selectedGender = Gender.WOMAN.value;
//        imvGenderMan.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
//        imvGenderWoman.clearColorFilter();

        updateButton();
    }*/


    @OnClick({R.id.radio_signup_gender_man, R.id.radio_signup_gender_woman})
    void onGenderCheckChanged(){
        Logger.d(TAG,"changed");
        if(radioGenderMan.isChecked()){
            selectedGender = Gender.MAN.value;
        }
        else if(radioGenderWoman.isChecked()){
            selectedGender = Gender.WOMAN.value;
        }
        updateButton();
    }


    @OnCheckedChanged(R.id.cb_signup_policy)
    void onPolicyCheckChanged(){
        updateButton();
    }

    @OnTextChanged(R.id.edt_signup_birth)
    void onTextChanged(){
        updateButton();
    }

    @OnClick(R.id.btn_signup_proc)
    void onSignupClick(){
        Logger.d(TAG,"onSignupClick");

        switch (getInputState()){
            case GENDER_NOT_SELECTED:
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_signup_gender_not_selected),
                        Toast.LENGTH_LONG
                ).show();
                break;
            case BIRTH_NOT_SELECTED:
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_signup_birth_not_selected),
                        Toast.LENGTH_LONG
                ).show();
                break;
            case POLICY_NOT_SELECTED:
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_signup_policy_not_selected),
                        Toast.LENGTH_LONG
                ).show();
                break;
            case ALL_SELECTED:
                requestSignup();
                break;
        }

        Tracker t = ((CalyApplication)getApplication()).getDefaultTracker();
        t.setScreenName(this.getClass().getName());
        t.send(
                new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.ga_action_button_click))
                        .setAction(Util.getCurrentMethodName())
                        .build()
        );
    }

    @OnClick(R.id.linear_signup_edtarea)
    void onEdtAreaClick(){
        edtBirth.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtBirth, InputMethodManager.SHOW_IMPLICIT);
    }

    void requestSignup(){
        Logger.i(TAG, "requestSignup");

        linearLoading.setVisibility(View.VISIBLE);
        final Bundle bundleData = getIntent().getExtras();

        ApiClient.getService().signUp(
                bundleData.getString("userId"),
                bundleData.getString("userPw"),
                bundleData.getString("authCode"),
                selectedGender,
                Integer.parseInt(edtBirth.getText().toString()),
                bundleData.getString("loginPlatform"),
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
                Logger.d(TAG, "param" + Util.requestBodyToString(call.request().body()));

                linearLoading.setVisibility(View.GONE);
                SessionResponse body = response.body();

                switch (response.code()){
                    case 200:
                        Logger.d(TAG, "session : " + body.payload.apiKey);
                        TokenRecord session = TokenRecord.getTokenRecord();
                        session.setApiKey(body.payload.apiKey);
                        session.setLoginPlatform(bundleData.getString("loginPlatform"));
                        session.setUserId(bundleData.getString("userId"));
                        session.save();

                        Intent intent = new Intent(SignupActivity.this, EventListActivity.class);
                        intent.putExtra("first", true);
                        intent.putExtra("loginPlatform", bundleData.getString("loginPlatform"));
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
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
                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }

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

    void updateButton(){
        if(checkEnable()){
//            btnSignup.setEnabled(true);
//            btnSignup.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btnSignup.setTextColor(getResources().getColor(R.color.white));
            btnSignup.getBackground().setColorFilter(null);
        }
        else{
//            btnSignup.setEnabled(false);
//            btnSignup.setBackgroundColor(Color.GRAY);
            btnSignup.setTextColor(Color.GRAY);
            btnSignup.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
    }

    boolean checkEnable(){
        if(edtBirth.getText().toString().length()!=4) return false;
        if(selectedGender==-1) return false;
        if(cbPolicy.isChecked()==false) return false;

        return true;
    }

    private INPUT_STATE getInputState(){
        if(selectedGender==-1) return INPUT_STATE.GENDER_NOT_SELECTED;
        if(edtBirth.getText().toString().length()!=4) return INPUT_STATE.BIRTH_NOT_SELECTED;
        if(cbPolicy.isChecked()==false) return INPUT_STATE.POLICY_NOT_SELECTED;

        return INPUT_STATE.ALL_SELECTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case Util.RC_INTENT_POLICY_RESPONSE:

                    Logger.d(TAG,"agree : " + data.getBooleanExtra("agree", false));
                    cbPolicy.setChecked(
                            data.getBooleanExtra("agree", false)
                    );

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private enum INPUT_STATE{
        BIRTH_NOT_SELECTED,
        GENDER_NOT_SELECTED,
        POLICY_NOT_SELECTED,
        ALL_SELECTED
    }
}
