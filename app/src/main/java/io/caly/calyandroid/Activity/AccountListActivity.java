package io.caly.calyandroid.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Adapter.AccountListAdapter;
import io.caly.calyandroid.Model.DataModel.AccountModel;
import io.caly.calyandroid.Model.LoginPlatform;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.Response.AccountResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
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

public class AccountListActivity extends BaseAppCompatActivity {

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
        recyclerAdapter = new AccountListAdapter(accountModels);
        recyclerList.setAdapter(recyclerAdapter);


        loadAccountList();
    }

    void loadAccountList(){
        ApiClient.getService().accountList(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                if(response.code() == 200){
                    AccountResponse body = response.body();

                    ArrayList<AccountModel> googleAccountList = new ArrayList<AccountModel>();
                    ArrayList<AccountModel> naverAccountList = new ArrayList<AccountModel>();
                    ArrayList<AccountModel> appleAccountList = new ArrayList<AccountModel>();

                    for(AccountModel accountModel : body.payload.data){
                        switch (LoginPlatform.getInstance(accountModel.loginPlatform)){
                            case CALDAV_NAVER:
                                naverAccountList.add(accountModel);
                                break;
                            case CALDAV_ICAL:
                                appleAccountList.add(accountModel);
                                break;
                            case GOOGLE:
                                googleAccountList.add(accountModel);
                                break;
                        }
                    }

                    ArrayList<AccountModel> accountList = new ArrayList<AccountModel>();

                    if(googleAccountList.size()!=0){
                        accountList.add(new AccountModel("Google Calendar 계정"));
                        accountList.addAll(googleAccountList);
                    }
                    if(naverAccountList.size()!=0){
                        accountList.add(new AccountModel("Naver Calendar 계정"));
                        accountList.addAll(naverAccountList);
                    }
                    if(appleAccountList.size()!=0){
                        accountList.add(new AccountModel("Apple Calendar 계정"));
                        accountList.addAll(appleAccountList);
                    }

                    recyclerAdapter.setData(accountList);
                    recyclerAdapter.notifyDataSetChanged();

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
