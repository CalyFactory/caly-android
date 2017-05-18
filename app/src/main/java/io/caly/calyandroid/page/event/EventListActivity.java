package io.caly.calyandroid.page.event;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.squareup.otto.Subscribe;

import net.jspiner.prefer.Prefer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.R;
import io.caly.calyandroid.activity.RecoListActivity;
import io.caly.calyandroid.page.account.list.AccountListFragment;
import io.caly.calyandroid.page.account.list.AccountListPresenter;
import io.caly.calyandroid.page.base.BaseAppCompatActivity;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.LogType;
import io.caly.calyandroid.model.dataModel.BannerModel;
import io.caly.calyandroid.model.dataModel.EventModel;
import io.caly.calyandroid.model.event.AccountListRefreshEvent;
import io.caly.calyandroid.model.event.EventListRefreshEvent;
import io.caly.calyandroid.model.event.GoogleSyncDoneEvent;
import io.caly.calyandroid.model.event.RecoReadyEvent;
import io.caly.calyandroid.model.event.TestEvent;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.model.response.EventResponse;
import io.caly.calyandroid.page.notice.NoticeActivity;
import io.caly.calyandroid.page.setting.SettingActivity;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.ConfigClient;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.StringFormmater;
import io.caly.calyandroid.util.Util;
import io.caly.calyandroid.util.eventListener.RecyclerItemClickListener;
import io.caly.calyandroid.util.tracker.AnalysisTracker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class EventListActivity extends BaseAppCompatActivity {

    private int currentTailPageNum = 1;
    private int currentHeadPageNum = -1;

    private boolean isLoading = false;
    private final int LOADING_THRESHOLD = 2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;

    @Bind(R.id.imv_toolbar_logo)
    ImageView imvLogo;

    EventListFragment eventListView;
    EventListContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventlist);

        init();

        Logger.d(TAG, "hashcode : " + EventListActivity.super.hashCode());
        Logger.d(TAG, "SESSION : " + AnalysisTracker.getAppSession().getSessionKey());
    }

    void init(){

        ButterKnife.bind(this);

        initToolbar();


        eventListView = EventListFragment.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_eventlist_container, eventListView);
        transaction.commit();

        presenter = new EventListPresenter(
                eventListView
        );

    }

    void initToolbar(){

        //set toolbar
        tvToolbarTitle.setText("");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        imvLogo.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

}
