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
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Adapter.AccountAddAdapter;
import io.caly.calyandroid.Adapter.AccountListAdapter;
import io.caly.calyandroid.Model.AccountModel;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 22
 */

public class AccountAddActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    private static final String TAG = AccountAddActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.recycler_accountlist)
    RecyclerView recyclerList;

    AccountAddAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountadd);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        //set toolbar
        toolbar.setTitle("계정 추가");
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

        ArrayList<String> dataList = new ArrayList<>();
        dataList.add("Google 계정");
        dataList.add("Naver 계정");
        dataList.add("Apple 계정");
        recyclerAdapter = new AccountAddAdapter(dataList);
        recyclerList.setAdapter(recyclerAdapter);
    }
}
