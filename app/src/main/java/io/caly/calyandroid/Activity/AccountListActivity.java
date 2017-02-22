package io.caly.calyandroid.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Adapter.AccountListAdapter;
import io.caly.calyandroid.Adapter.EventListAdapter;
import io.caly.calyandroid.Model.AccountModel;
import io.caly.calyandroid.Model.EventModel;
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

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.recycler_accountlist)
    RecyclerView recyclerList;

    AccountListAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountlist);

        init();
    }

    void init(){
        ButterKnife.bind(this);


        //set toolbar
        toolbar.setTitle("계정 목록");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set recyclerview
        recyclerList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerList.setLayoutManager(layoutManager);

        ArrayList<AccountModel> accountModels = new ArrayList<>();

        accountModels.add(new AccountModel(true));
        accountModels.add(new AccountModel(false));
        accountModels.add(new AccountModel(false));
        accountModels.add(new AccountModel(true));
        accountModels.add(new AccountModel(false));
        accountModels.add(new AccountModel(false));
        accountModels.add(new AccountModel(false));
        accountModels.add(new AccountModel(false));
        accountModels.add(new AccountModel(true));
        accountModels.add(new AccountModel(false));
        accountModels.add(new AccountModel(false));

        recyclerAdapter = new AccountListAdapter(accountModels);
        recyclerList.setAdapter(recyclerAdapter);


//        loadAccountList();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
