package io.caly.calyandroid.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Adapter.RecoTabPagerAdapter;
import io.caly.calyandroid.Adapter.RecommandListAdapter;
import io.caly.calyandroid.Model.DataModel.EventModel;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.TrackingType;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.EventListener.RecyclerItemClickListener;
import io.caly.calyandroid.Util.Util;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 14
 */

public class RecommandListActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tab_recolist)
    TabLayout tabLayout;

    @Bind(R.id.pager_recolist)
    ViewPager pagerRecoList;

    RecoTabPagerAdapter pagerAdapter;

    EventModel eventData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommandlist);

        init();
    }

    void init(){
        ButterKnife.bind(this);


        //set toolbar
        toolbar.setTitle("추천 목록");
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
        
        eventData = ApiClient.getGson()
                .fromJson(
                        getIntent().getStringExtra("event"),
                        EventModel.class
                );

        //init tab layout
        tabLayout.addTab(tabLayout.newTab().setText("식당"));
        tabLayout.addTab(tabLayout.newTab().setText("카페"));
        tabLayout.addTab(tabLayout.newTab().setText("액티비티"));

        pagerAdapter = new RecoTabPagerAdapter(getSupportFragmentManager());
        pagerRecoList.setAdapter(pagerAdapter);
        pagerRecoList.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout)
        );
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pagerRecoList.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
}
