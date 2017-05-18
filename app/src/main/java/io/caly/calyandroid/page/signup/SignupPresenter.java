package io.caly.calyandroid.page.signup;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.DeviceType;
import io.caly.calyandroid.model.Gender;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.SessionResponse;
import io.caly.calyandroid.page.base.BasePresenter;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 16
 */

public class SignupPresenter extends BasePresenter implements SignupContract.Presenter {

    SignupFragment signupView;

    int selectedGender = -1;

    public SignupPresenter(SignupFragment signupView){
        this.signupView = signupView;

        signupView.setPresenter(this);
    }

    @Override
    public void requestSignup(){
        Logger.i(TAG, "requestSignup");

        signupView.changeProgressState(true);
        final Bundle bundleData = signupView.getIntentBundle();

        ApiClient.getService().signUp(
                bundleData.getString("userId"),
                bundleData.getString("userPw"),
                bundleData.getString("authCode"),
                selectedGender,
                Integer.parseInt(signupView.edtBirth.getText().toString()),
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

                signupView.changeProgressState(false);
                SessionResponse body = response.body();

                switch (response.code()){
                    case 200:
                        Logger.d(TAG, "session : " + body.payload.apiKey);
                        TokenRecord session = TokenRecord.getTokenRecord();
                        session.setApiKey(body.payload.apiKey);
                        session.setLoginPlatform(bundleData.getString("loginPlatform"));
                        session.setUserId(bundleData.getString("userId"));
                        session.save();

                        signupView.startEventListActivity();

                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        signupView.showToast(
                                (R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        );
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

                signupView.changeProgressState(false);

                signupView.showToast(
                        (R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                );
            }
        });
    }

    @Override
    public boolean checkEnable(){
        if(signupView.edtBirth.getText().toString().length()!=4) return false;
        if(selectedGender==-1) return false;
        if(signupView.cbPolicy.isChecked()==false) return false;

        return true;
    }

    @Override
    public INPUT_STATE getInputState(){
        if(selectedGender==-1) return INPUT_STATE.GENDER_NOT_SELECTED;
        if(signupView.edtBirth.getText().toString().length()!=4) return INPUT_STATE.BIRTH_NOT_SELECTED;
        if(signupView.cbPolicy.isChecked()==false) return INPUT_STATE.POLICY_NOT_SELECTED;

        return INPUT_STATE.ALL_SELECTED;
    }

    @Override
    public void setGender(Gender gender) {
        selectedGender = gender.value;
    }

    @Override
    public void onGenderCheckChanged() {
        if(signupView.radioGenderMan.isChecked()){
            setGender(Gender.MAN);
        }
        else if(signupView.radioGenderWoman.isChecked()){
            setGender(Gender.WOMAN);
        }
        signupView.updateButton();
    }

    @Override
    public void onPolicyCheckChanged() {

        signupView.updateButton();
    }

    @Override
    public void onBirthTextChanged() {
        signupView.updateButton();
    }

    @Override
    public void onSignupClick() {
        switch (getInputState()){
            case GENDER_NOT_SELECTED:
                signupView.showToast(
                        (R.string.toast_msg_signup_gender_not_selected),
                        Toast.LENGTH_LONG
                );
                break;
            case BIRTH_NOT_SELECTED:
                signupView.showToast(
                        (R.string.toast_msg_signup_birth_not_selected),
                        Toast.LENGTH_LONG
                );
                break;
            case POLICY_NOT_SELECTED:
                signupView.showToast(
                        (R.string.toast_msg_signup_policy_not_selected),
                        Toast.LENGTH_LONG
                );
                break;
            case ALL_SELECTED:
                requestSignup();
                break;
        }

        Tracker t = ((CalyApplication)signupView.getActivity().getApplication()).getDefaultTracker();
        t.setScreenName(this.getClass().getName());
        t.send(
                new HitBuilders.EventBuilder()
                        .setCategory(signupView.getString(R.string.ga_action_button_click))
                        .setAction(Util.getCurrentMethodName())
                        .build()
        );
    }

    @Override
    public void onEdtAreaClick() {
        signupView.edtBirth.requestFocus();
        InputMethodManager imm = (InputMethodManager) signupView.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(signupView.edtBirth, InputMethodManager.SHOW_IMPLICIT);
    }
}
