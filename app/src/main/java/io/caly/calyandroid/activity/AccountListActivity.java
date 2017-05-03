package io.caly.calyandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import io.caly.calyandroid.util.Logger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.activity.base.BaseAppCompatActivity;
import io.caly.calyandroid.adapter.AccountListAdapter;
import io.caly.calyandroid.model.dataModel.AccountModel;
import io.caly.calyandroid.model.event.AccountListLoadingEvent;
import io.caly.calyandroid.model.event.AccountListRefreshEvent;
import io.caly.calyandroid.model.LoginPlatform;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.AccountResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.ApiClient;
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

    @Bind(R.id.linear_loading_parent)
    LinearLayout linearLoading;

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

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set recyclerview
        recyclerList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerList.setLayoutManager(layoutManager);

        ArrayList<AccountModel> accountModels = new ArrayList<>();
        recyclerAdapter = new AccountListAdapter(AccountListActivity.this, accountModels);
        recyclerList.setAdapter(recyclerAdapter);


        loadAccountList();
    }

    void loadAccountList(){
        Logger.i(TAG, "loadAccountList");
        ApiClient.getService().accountList(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());

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
                    Logger.e(TAG,"status code : " + response.code());
                    Toast.makeText(
                            getBaseContext(),
                            getString(R.string.toast_msg_server_internal_error),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {

                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Subscribe
    public void onAccountListLoadingCallback(AccountListLoadingEvent event){
        if(event.enable){
            linearLoading.setVisibility(View.VISIBLE);
        }
        else{
            linearLoading.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onAccountLIstRefreshEventCallback(AccountListRefreshEvent event){
        Logger.i(TAG, "onAccountLIstRefreshEventCallback");
        loadAccountList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAccountList();
    }

    int responseResult = 2;

    @Override
    public void onBackPressed() {
        setResult(responseResult);
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accountlist, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_accountlist_add:
                Intent intent = new Intent(getBaseContext(), AccountAddActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
