package io.caly.calyandroid.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import io.caly.calyandroid.Activity.RecoListActivity;
import io.caly.calyandroid.Adapter.RecoTabPagerAdapter;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Fragment.Base.BaseFragment;
import io.caly.calyandroid.Model.DataModel.EventModel;
import io.caly.calyandroid.Model.Event.RecoListLoadDoneEvent;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.Logger;
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
 * @since 17. 4. 25
 */

public class RecoListFragment extends BaseFragment {

    @Bind(R.id.tab_recolist)
    TabLayout tabLayout;

    @Bind(R.id.pager_recolist)
    ViewPager pagerRecoList;
/*
    @Bind(R.id.layout_drawer)
    LinearLayout layoutDrawer;

    @Bind(R.id.layout_drawer_inside)
    LinearLayout layoutDrawerInside;*/

    RecoTabPagerAdapter pagerAdapter;

    /*
    TranslateAnimation drawerInAnimation;
    TranslateAnimation drawerOutAnimation;*/

    EventModel eventData;

    public RecoListFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = null;

        if (v == null) {
            v = inflater.inflate(R.layout.fragment_reco, null);

            ButterKnife.bind(this, v);

            init();

        }

        return v;
    }

    public RecoListFragment setData(EventModel eventData){
        this.eventData = eventData;
        return this;
    }

    void init(){

        //init tab layout
        tabLayout.addTab(tabLayout.newTab().setText("식당"));
        tabLayout.addTab(tabLayout.newTab().setText("카페"));
        tabLayout.addTab(tabLayout.newTab().setText("액티비티"));


        pagerAdapter = new RecoTabPagerAdapter(getFragmentManager(), eventData);
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
/*
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
                        getActivity(),
                        getResources().getColor(R.color.colorPrimaryDark)
                );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });*/
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
        new FeedbackDialog(getActivity(), new FeedbackDialog.DialogCallback() {
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
                                        getContext(),
                                        getString(R.string.toast_msg_success_send_feedback),
                                        Toast.LENGTH_LONG
                                ).show();
                                dialog.dismiss();

                                break;
                            default:
                                Logger.e(TAG,"status code : " + response.code());
                                Toast.makeText(
                                        getContext(),
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
                                getContext(),
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



        Tracker t = ((CalyApplication)getActivity().getApplication()).getDefaultTracker();
        t.setScreenName(this.getClass().getName());
        t.send(
                new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.ga_action_button_click))
                        .setAction(Util.getCurrentMethodName())
                        .build()
        );
    }

}
