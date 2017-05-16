package io.caly.calyandroid.page.signup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;
import io.caly.calyandroid.activity.EventListActivity;
import io.caly.calyandroid.activity.PolicyActivity;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.DeviceType;
import io.caly.calyandroid.model.Gender;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.SessionResponse;
import io.caly.calyandroid.page.base.BaseFragment;
import io.caly.calyandroid.page.notice.NoticeFragment;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.Util;
import io.caly.calyandroid.util.eventListener.TextViewLinkHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.caly.calyandroid.page.signup.INPUT_STATE.ALL_SELECTED;
import static io.caly.calyandroid.page.signup.INPUT_STATE.BIRTH_NOT_SELECTED;
import static io.caly.calyandroid.page.signup.INPUT_STATE.GENDER_NOT_SELECTED;
import static io.caly.calyandroid.page.signup.INPUT_STATE.POLICY_NOT_SELECTED;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 16
 */

public class SignupFragment extends BaseFragment implements SignupContract.View {

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

    SignupContract.Presenter presenter;

    public static SignupFragment getInstance(){
        return new SignupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        ButterKnife.bind(this, view);

        init();
        return view;
    }

    void init(){

        cbPolicy.setText(Html.fromHtml("캘리의 <a href='signup'>이용정책</a>에 동의합니다."));
        cbPolicy.setMovementMethod(new TextViewLinkHandler() {
            @Override
            public void onLinkClick(String url) {
                Intent intent = new Intent(getActivity(), PolicyActivity.class);
                startActivityForResult(intent, Util.RC_INTENT_POLICY_RESPONSE);
            }
        });

        tvProgressTitle.setText("회원가입 중입니다");


        updateButton();
    }

    @OnClick({R.id.radio_signup_gender_man, R.id.radio_signup_gender_woman})
    void onGenderCheckChanged(){
        Logger.d(TAG,"changed");
        presenter.onGenderCheckChanged();
    }


    @OnCheckedChanged(R.id.cb_signup_policy)
    void onPolicyCheckChanged(){
        presenter.onPolicyCheckChanged();
    }

    @OnTextChanged(R.id.edt_signup_birth)
    void onBirthTextChanged(){
        presenter.onBirthTextChanged();
    }

    @OnClick(R.id.btn_signup_proc)
    void onSignupClick(){
        Logger.d(TAG,"onSignupClick");
        presenter.onSignupClick();
    }

    @OnClick(R.id.linear_signup_edtarea)
    void onEdtAreaClick(){
        presenter.onEdtAreaClick();
    }

    @Override
    public void updateButton(){
        if(presenter.checkEnable()){
            btnSignup.setTextColor(getResources().getColor(R.color.white));
            btnSignup.getBackground().setColorFilter(null);
        }
        else{
            btnSignup.setTextColor(Color.GRAY);
            btnSignup.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public Bundle getIntentBundle() {
        return getActivity().getIntent().getExtras();
    }

    @Override
    public void setPresenter(SignupContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void changeProgressState(boolean isLoading) {
        if(isLoading){
            linearLoading.setVisibility(View.VISIBLE);
        }
        else{
            linearLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPolicyCheckState(boolean isChecked) {
        cbPolicy.setChecked(isChecked);
    }

    @Override
    public void startEventListActivity() {
        Bundle bundleData = getActivity().getIntent().getExtras();

        Intent intent = new Intent(getActivity(), EventListActivity.class);
        intent.putExtra("first", true);
        intent.putExtra("loginPlatform", bundleData.getString("loginPlatform"));
        startActivity(intent);

        finishActivity();
    }
}
