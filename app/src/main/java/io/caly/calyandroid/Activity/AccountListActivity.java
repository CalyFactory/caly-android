package io.caly.calyandroid.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;
import io.caly.calyandroid.Model.AccountModel;
import io.caly.calyandroid.Model.ORM.SessionRecord;
import io.caly.calyandroid.Model.Response.AccountResponse;
import io.caly.calyandroid.Model.Response.BasicResponse;
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
 * @since 17. 2. 22
 */

public class AccountListActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    private static final String TAG = AccountListActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountlist);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        loadAccountList();
    }

    void loadAccountList(){
        Util.getHttpService().accountList(
                SessionRecord.getSessionRecord().getSessionKey()
        ).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                if(response.code() == 200){
                    AccountResponse body = response.body();

                    for(AccountModel accountModel : body.payload.data){
                        
                    }

                }
                else{
                    Log.e(TAG,"status code : " + response.code());
                    Toast.makeText(
                            getBaseContext(),
                            getString(R.string.toast_msg_server_internal_error),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {

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
