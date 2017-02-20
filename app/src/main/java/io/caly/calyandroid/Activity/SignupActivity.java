package io.caly.calyandroid.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.caly.calyandroid.Model.DeviceType;
import io.caly.calyandroid.Model.Gender;
import io.caly.calyandroid.Model.Response.SessionResponse;
import io.caly.calyandroid.Model.SessionRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.Util;
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

public class SignupActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    private static final String TAG = SignupActivity.class.getSimpleName();

    @Bind(R.id.imv_signup_man)
    ImageView imvGenderMan;

    @Bind(R.id.imv_signup_woman)
    ImageView imvGenderWoman;

    @Bind(R.id.edt_signup_birth)
    EditText edtBirth;

    @Bind(R.id.cb_signup_policy)
    CheckBox cbPolicy;

    @Bind(R.id.btn_signup_proc)
    Button btnSignup;

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


        btnSignup.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        imvGenderMan.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
        imvGenderWoman.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    @OnClick(R.id.imv_signup_man)
    void onGenderManClick(){

        selectedGender = Gender.MAN;
        imvGenderMan.clearColorFilter();
        imvGenderWoman.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);

        updateButton();
    }

    @OnClick(R.id.imv_signup_woman)
    void onGenderWomanClick(){

        selectedGender = Gender.WOMAN;
        imvGenderMan.setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
        imvGenderWoman.clearColorFilter();

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
        Bundle bundleData = getIntent().getExtras();

        Util.getHttpService().signUp(
                bundleData.getString("userId"),
                bundleData.getString("userPw"),
                bundleData.getString("authCode"),
                selectedGender,
                Integer.parseInt(edtBirth.getText().toString()),
                bundleData.getString("loginPlatform"),
                FirebaseInstanceId.getInstance().getToken(),
                DeviceType.ANDROID,
                Util.getAppVersion(),
                Util.getDeviceInfo(),
                Util.getUUID(),
                Util.getSdkLevel()
        ).enqueue(new Callback<SessionResponse>() {
            @Override
            public void onResponse(Call<SessionResponse> call, Response<SessionResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());
                Log.d(TAG, "param" + Util.requestBodyToString(call.request().body()));
                if(response.code() == 200){
                    SessionResponse body = response.body();

                    switch (body.code){
                        case 200:

                            SessionRecord session = SessionRecord.getSessionRecord();
                            session.setSessionKey(body.payload.sessionKey);
                            session.save();

                            Intent intent = new Intent(SignupActivity.this, EventListActivity.class);
                            intent.putExtra("first", true);
                            startActivity(intent);
                            finish();
                            break;
                        default:
                            Toast.makeText(
                                    getBaseContext(),
                                    getString(R.string.toast_msg_server_internal_error),
                                    Toast.LENGTH_LONG
                            ).show();
                            break;
                    }
                }
                else{
                    Toast.makeText(
                            getBaseContext(),
                            getString(R.string.toast_msg_server_internal_error),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<SessionResponse> call, Throwable t) {

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

    void updateButton(){
        if(checkEnable()){
            btnSignup.setEnabled(true);
            btnSignup.getBackground().setColorFilter(null);
        }
        else{
            btnSignup.setEnabled(false);
            btnSignup.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
    }

    boolean checkEnable(){
        if(edtBirth.getText().toString().length()!=4) return false;
        if(selectedGender==-1) return false;
        if(cbPolicy.isChecked()==false) return false;

        return true;
    }
}
