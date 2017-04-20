package io.caly.calyandroid.Activity;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import io.caly.calyandroid.Util.Logger;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
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
import io.caly.calyandroid.Model.DataModel.RecoModel;
import io.caly.calyandroid.Model.Event.RecoListLoadDoneEvent;
import io.caly.calyandroid.Model.Event.RecoMoreClickEvent;
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

    @Bind(R.id.layout_drawer)
    LinearLayout layoutDrawer;

    @Bind(R.id.layout_drawer_inside)
    LinearLayout layoutDrawerInside;

    RecoTabPagerAdapter pagerAdapter;

    EventModel eventData;

    TranslateAnimation drawerInAnimation;
    TranslateAnimation drawerOutAnimation;

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
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
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

        drawerInAnimation = new TranslateAnimation(0, 0, 800, 0);
        drawerInAnimation.setDuration(150);
        drawerInAnimation.setFillAfter(false);

        drawerOutAnimation = new TranslateAnimation(0, 0, 0, 800);
        drawerOutAnimation.setDuration(150);
        drawerOutAnimation.setFillAfter(false);

        drawerOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layoutDrawer.setVisibility(View.GONE);
                Util.setStatusBarColor(
                        RecommendListActivity.this,
                        getResources().getColor(R.color.colorPrimaryDark)
                );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

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
                        Logger.d(TAG,"onResponse code : " + response.code());

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
                                Logger.e(TAG,"status code : " + response.code());
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

        if(layoutDrawer.getVisibility() == View.VISIBLE){
            drawOutDrawer();
        }
        else{
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }


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

    void drawInDrawer(){

        layoutDrawer.setVisibility(View.VISIBLE);

        Util.setStatusBarColor(this, Color.BLACK);

        layoutDrawerInside.startAnimation(drawerInAnimation);
    }

    void drawOutDrawer(){
        layoutDrawerInside.startAnimation(drawerOutAnimation);
    }

    RecoModel recoModel;

    @Subscribe
    public void onRecoMoreClickCallback(RecoMoreClickEvent event){
        this.recoModel = event.recoModel;
        drawInDrawer();
    }

    @OnClick(R.id.layout_drawer)
    void onDrawerClick(){
        drawOutDrawer();
    }

    @OnClick(R.id.layout_draweritem_1)
    void onDrawerItemClick1(){
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        clipboardManager.setPrimaryClip(ClipData.newPlainText("text", recoModel.deepUrl));
        Toast.makeText(getBaseContext(), "복사되었습니다.",Toast.LENGTH_LONG).show();

        drawOutDrawer();
    }

    @OnClick(R.id.layout_draweritem_2)
    void onDrawerItemClick2(){

        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", recoModel.sourceUrl);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        drawOutDrawer();
    }

    @OnClick(R.id.layout_draweritem_3)
    void onDrawerItemClick3(){
        String[] snsList = {
                "com.kakao.talk", //kakaotalk
        };
        boolean sended = false;
        for(String snsPackage : snsList){
            if(Util.isPackageInstalled(snsPackage)){

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"[캘리] 여기어때요? \n" + recoModel.deepUrl);
                intent.setPackage("com.kakao.talk");

                startActivity(intent);
                sended = true;
            }
        }
        if(!sended){
            Toast.makeText(getBaseContext(), "공유 할 수 있는 SNS가 설치 되어있지 않습니다.",Toast.LENGTH_LONG).show();
        }

        drawOutDrawer();
    }

}
