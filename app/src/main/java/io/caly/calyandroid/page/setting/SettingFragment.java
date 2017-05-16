package io.caly.calyandroid.page.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.R;
import io.caly.calyandroid.activity.PolicyActivity;
import io.caly.calyandroid.model.dataModel.SettingItemModel;
import io.caly.calyandroid.model.event.SettingLoadingStateChangeEvent;
import io.caly.calyandroid.page.base.BaseFragment;
import io.caly.calyandroid.page.signup.SignupContract;
import io.caly.calyandroid.page.signup.SignupFragment;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.Util;
import io.caly.calyandroid.util.eventListener.TextViewLinkHandler;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 16
 */

public class SettingFragment extends BaseFragment implements SettingContract.View{


    @Bind(R.id.recycler_settinglist)
    RecyclerView recyclerList;

    @Bind(R.id.linear_loading_parent)
    LinearLayout linearLoading;

    @Bind(R.id.tv_progress_title)
    TextView tvProgressTitle;

    SettingListAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;

    SettingContract.Presenter presenter;


    public static GoogleApiClient mGoogleApiClient;

    public static SettingFragment getInstance(){
        return new SettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        ButterKnife.bind(this, view);

        init();
        return view;
    }

    void init(){

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

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .enableAutoManage(getActivity() , onGoogleConnectionFailedListener)
                .build();;
    }

    GoogleApiClient.OnConnectionFailedListener onGoogleConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Logger.d(TAG, "onConnectionFailed : " + connectionResult.getErrorMessage());
        }
    };


    @Override
    public void setPresenter(SettingContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Subscribe
    public void onSettingLoadingStateChangeEventListener(SettingLoadingStateChangeEvent event){
        if(event.isEnable){
            tvProgressTitle.setText(event.text);
            linearLoading.setVisibility(View.VISIBLE);
        }
        else {
            linearLoading.setVisibility(View.GONE);
        }
    }
}
