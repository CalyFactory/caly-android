package io.caly.calyandroid.Activity;

import android.graphics.Color;
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
import io.caly.calyandroid.Adapter.EventListAdapter;
import io.caly.calyandroid.Adapter.SettingListAdapter;
import io.caly.calyandroid.Model.SettingItemModel;
import io.caly.calyandroid.Model.TestModel;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 12
 */

public class SettingActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    private static final String TAG = SettingActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.recycler_settinglist)
    RecyclerView recyclerList;

    SettingListAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

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

        //set recycler view
        recyclerList.setHasFixedSize(true);

        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerList.setLayoutManager(layoutManager);

        // test data
        ArrayList<SettingItemModel> dataList = new ArrayList<>();

        dataList.add(new SettingItemModel("제목1"));
        dataList.add(new SettingItemModel("설정1","sadfasdf"));
        dataList.add(new SettingItemModel("설정2","fasfdasdf"));
        dataList.add(new SettingItemModel("제목2"));
        dataList.add(new SettingItemModel("설정1","asdfasdf"));


        recyclerAdapter = new SettingListAdapter(dataList);
        recyclerList.setAdapter(recyclerAdapter);
    }
}
