package io.caly.calyandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import io.caly.calyandroid.util.Logger;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.activity.base.BaseAppCompatActivity;
import io.caly.calyandroid.adapter.SettingListAdapter;
import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.model.dataModel.SettingItemModel;
import io.caly.calyandroid.model.event.SettingLoadingStateChangeEvent;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.Util;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 12
 */

public class LegacySettingActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.recycler_settinglist)
    RecyclerView recyclerList;

    @Bind(R.id.linear_loading_parent)
    LinearLayout linearLoading;

    SettingListAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;


    public static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legacy_activity_setting);

        init();
    }

    void init(){

        ButterKnife.bind(this);


        //set toolbar
        toolbar.setTitle("환경 설정");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        Util.centerToolbarTitle(toolbar);

        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set recycler view
        recyclerList.setHasFixedSize(true);

        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerList.setLayoutManager(layoutManager);

        // test data
        ArrayList<SettingItemModel> dataList = new ArrayList<>();

        dataList.add(new SettingItemModel("CalyFactory"));
        dataList.add(new SettingItemModel("공지사항", "Caly의 공지사항을 알려드립니다."));
        dataList.add(new SettingItemModel("문의하기", "문제가 있다면, 알려주세요."));
        dataList.add(new SettingItemModel("기본설정"));
        dataList.add(new SettingItemModel("어플리케이션 버전", BuildConfig.VERSION_NAME));
//        dataList.add(new SettingItemModel("최신버전 확인","마켓으로 이동해 최신버전으로 업데이트"));
        dataList.add(new SettingItemModel("푸시설정","푸시 알람을 끄고 켤 수 있습니다."));
        dataList.add(new SettingItemModel("계정설정"));
        //dataList.add(new SettingItemModel("계정추가","Google 혹은 caldav계정을 추가할 수 있습니다."));
        dataList.add(new SettingItemModel("캘린더 계정 관리","로그인된 계정의 정보를 추가/수정할 수 있습니다."));
        dataList.add(new SettingItemModel("로그아웃","통합계정을 로그아웃하고 다른계정으로 로그인 할 수 있습니다."));
        dataList.add(new SettingItemModel("회원탈퇴","Caly에 연결된 계정을 탈퇴합니다."));


        recyclerAdapter = new SettingListAdapter(dataList);
        recyclerList.setAdapter(recyclerAdapter);



        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestServerAuthCode(getString(R.string.google_client_id), true)
                        .requestIdToken(getString(R.string.google_client_id))
                        .requestScopes(
                                new Scope("https://www.googleapis.com/auth/calendar"),
                                new Scope("https://www.googleapis.com/auth/userinfo.email"),
                                new Scope("https://www.googleapis.com/auth/calendar.readonly")
                        ).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .enableAutoManage(this , onGoogleConnectionFailedListener)
                .build();;
    }

    GoogleApiClient.OnConnectionFailedListener onGoogleConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Logger.d(TAG, "onConnectionFailed : " + connectionResult.getErrorMessage());
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    int responseResult = 0;

    @Override
    public void onBackPressed() {
        setResult(responseResult);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode : " + requestCode);
        Log.d(TAG, "resultCode : " + resultCode);

        switch (requestCode){
            case 1: //accountlistactivity
                if(resultCode == 2){
                    responseResult = 2;
                }
                break;
        }
    }

    @Subscribe
    public void onSettingLoadingStateChangeEventListener(SettingLoadingStateChangeEvent event){
        if(event.isEnable){
            linearLoading.setVisibility(View.VISIBLE);
        }
        else {
            linearLoading.setVisibility(View.GONE);
        }
    }
}
