package io.caly.calyandroid.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Adapter.RecoTabPagerAdapter;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.DataModel.EventModel;
import io.caly.calyandroid.Model.Event.RecoListLoadDoneEvent;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.Util;
import io.caly.calyandroid.View.FeedbackDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 14
 */

public class RecommendListActivity extends BaseAppCompatActivity {

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


        pagerAdapter = new RecoTabPagerAdapter(getSupportFragmentManager(), eventData);
        pagerRecoList.setOffscreenPageLimit(4);
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

    @Subscribe
    public void recoListLoadDoneEvent(RecoListLoadDoneEvent doneEvent){
        switch (doneEvent.category){
            case RESTAURANT:
                tabLayout.getTabAt(0).setText("식당("+doneEvent.dataCount+")");
                break;
            case CAFE:
                tabLayout.getTabAt(1).setText("카페("+doneEvent.dataCount+")");
                break;
            case PLACE:
                tabLayout.getTabAt(2).setText("액티비티("+doneEvent.dataCount+")");
                break;
        }
    }

    @OnClick(R.id.fab_recolist_feedback)
    void onFeedBackClick(){
        new FeedbackDialog(RecommendListActivity.this, new FeedbackDialog.DialogCallback() {
            @Override
            public void onPositive(final FeedbackDialog dialog, String contents) {
                ApiClient.getService().requests(
                        TokenRecord.getTokenRecord().getApiKey(),
                        contents
                ).enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        Log.d(TAG,"onResponse code : " + response.code());

                        BasicResponse body = response.body();
                        switch (response.code()){
                            case 200:

                                Toast.makeText(
                                        getBaseContext(),
                                        getString(R.string.toast_msg_success_send_feedback),
                                        Toast.LENGTH_LONG
                                ).show();
                                dialog.dismiss();

                                break;
                            default:
                                Log.e(TAG,"status code : " + response.code());
                                Toast.makeText(
                                        getBaseContext(),
                                        getString(R.string.toast_msg_server_internal_error),
                                        Toast.LENGTH_LONG
                                ).show();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
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
            public void onNegative(FeedbackDialog dialog) {
                dialog.dismiss();
            }
        }).show();



        Tracker t = ((CalyApplication)getApplication()).getDefaultTracker();
        t.setScreenName(this.getClass().getName());
        t.send(
                new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.ga_action_button_click))
                        .setAction(Util.getCurrentMethodName())
                        .build()
        );
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
