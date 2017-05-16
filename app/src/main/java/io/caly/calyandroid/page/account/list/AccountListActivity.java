package io.caly.calyandroid.page.account.list;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.page.account.add.AccountAddActivity;
import io.caly.calyandroid.page.account.add.AccountAddFragment;
import io.caly.calyandroid.page.account.add.AccountAddPresenter;
import io.caly.calyandroid.util.Logger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.page.base.BaseAppCompatActivity;
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

    @Bind(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;

    int responseResult = 2;

    AccountListPresenter presenter;
    AccountListFragment accountListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountlist);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        initToolbar();


        accountListView = AccountListFragment.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_accountadd_container, accountListView);
        transaction.commit();

        presenter = new AccountListPresenter(
                accountListView
        );
    }

    void initToolbar(){

        //set toolbar
        tvToolbarTitle.setText("계정 목록");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


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
    protected void onResume() {
        super.onResume();
        loadAccountList();
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
