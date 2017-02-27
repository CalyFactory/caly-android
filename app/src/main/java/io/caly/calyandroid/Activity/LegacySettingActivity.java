package io.caly.calyandroid.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Adapter.SettingListAdapter;
import io.caly.calyandroid.Model.SettingItemModel;
import io.caly.calyandroid.R;

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

    SettingListAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legacy_activity_setting);

        init();
    }

    void init(){

        ButterKnife.bind(this);


        //set toolbar
        toolbar.setTitle("일정 목록");
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
        dataList.add(new SettingItemModel("어플리케이션 버전", "v0.0.1+beta"));
        dataList.add(new SettingItemModel("최신버전 확인","마켓으로 이동해 최신버전으로 업데이트"));
        dataList.add(new SettingItemModel("푸시설정","푸시 알람을 끄고 켤 수 있습니다."));
        dataList.add(new SettingItemModel("계정설정"));
        dataList.add(new SettingItemModel("계정수정","로그인된 계정의 정보를 수정할 수 있습니다."));
        dataList.add(new SettingItemModel("계정추가","Google 혹은 caldav계정을 추가할 수 있습니다."));
        dataList.add(new SettingItemModel("로그아웃","통합계정을 로그아웃하고 다른계정으로 로그인 할 수 있습니다."));


        recyclerAdapter = new SettingListAdapter(dataList);
        recyclerList.setAdapter(recyclerAdapter);
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
