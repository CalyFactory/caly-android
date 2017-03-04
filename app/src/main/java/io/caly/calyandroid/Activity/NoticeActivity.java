package io.caly.calyandroid.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Adapter.NoticeListAdapter;
import io.caly.calyandroid.Adapter.SettingListAdapter;
import io.caly.calyandroid.Model.DataModel.NoticeModel;
import io.caly.calyandroid.Model.DataModel.SettingItemModel;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 22
 */

public class NoticeActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.recycler_notice)
    RecyclerView recyclerList;

    NoticeListAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

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
        ArrayList<NoticeModel> dataList = new ArrayList<>();

        dataList.add(new NoticeModel());
        dataList.add(new NoticeModel());
        dataList.add(new NoticeModel());
        dataList.add(new NoticeModel());
        dataList.add(new NoticeModel());

        recyclerAdapter = new NoticeListAdapter(dataList);
        recyclerList.setAdapter(recyclerAdapter);
    }

}
