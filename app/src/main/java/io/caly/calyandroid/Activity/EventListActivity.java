package io.caly.calyandroid.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Adapter.EventListAdapter;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.SessionRecord;
import io.caly.calyandroid.Model.TestModel;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.Util;
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

public class EventListActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    private static final String TAG = EventListActivity.class.getSimpleName();


    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.recycler_eventlist)
    RecyclerView recyclerList;

    @Bind(R.id.tv_eventlist_year)
    TextView tvEventYear;

    @Bind(R.id.tv_eventlist_month)
    TextView tvEventMonth;

    @Bind(R.id.avi_eventlist)
    AVLoadingIndicatorView aviLoader;

    @Bind(R.id.linear_eventlist_loader)
    LinearLayout linearLoader;

    EventListAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventlist);

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


        //set recyclerview
        recyclerList.setHasFixedSize(true);

        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerList.setLayoutManager(layoutManager);

        // test data
        ArrayList<TestModel> dataList = new ArrayList<>();
        for(int j=0;j<3;j++) {
            for (int i = 0; i < 10; i++) {
                dataList.add(new TestModel(2017+j, 1 + i, 1, "소마 센터 멘토링", "11:00 ~ 12:00", "강남역 아남타워빌딩"));
                dataList.add(new TestModel(2017+j, 1 + i, 1, "멘토링", "11:00 ~ 12:00", "강남역 아남타워빌딩"));
                dataList.add(new TestModel(2017+j, 1 + i, 1, "데이트", "11:00 ~ 12:00", "강남역 아남타워빌딩"));
                dataList.add(new TestModel(2017+j, 1 + i, 5, "소마 센터 멘토링", "11:00 ~ 12:00", "강남역 아남타워빌딩"));
                dataList.add(new TestModel(2017+j, 1 + i, 5, "삼성면접", "11:00 ~ 12:00", "강남역 아남타워빌딩"));
                dataList.add(new TestModel(2017+j, 1 + i, 14, "친구 생일", "11:00 ~ 12:00", "강남역 아남타워빌딩"));
                dataList.add(new TestModel(2017+j, 1 + i, 23, "집보러가는날", "11:00 ~ 12:00", "강남역 아남타워빌딩"));
                dataList.add(new TestModel(2017+j, 1 + i, 23, "데이트", "11:00 ~ 12:00", "강남역 아남타워빌딩"));
                dataList.add(new TestModel(2017+j, 1 + i, 23, "영화보는날", "11:00 ~ 12:00", "강남역 아남타워빌딩"));
            }
        }
        recyclerAdapter = new EventListAdapter(dataList);
        recyclerList.setAdapter(recyclerAdapter);

        recyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                int position = ((StaggeredGridLayoutManager)layoutManager).findFirstVisibleItemPositions(null)[0];
                TestModel testModel = recyclerAdapter.getItem(position);

                tvEventYear.setText(testModel.year+"");
                tvEventMonth.setText(testModel.month+"월");
            }
        });


        Intent intent = getIntent();
        if(intent.getBooleanExtra("first", false)){
            syncCalendar();
        }
        else{
            linearLoader.setVisibility(View.GONE);
        }

    }

    void syncCalendar(){
        Util.getHttpService().sync(
                SessionRecord.getSessionRecord().getSessionKey()
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                linearLoader.setVisibility(View.GONE);
                if(response.code() == 200){
                    BasicResponse body = response.body();
                    Toast.makeText(getBaseContext(),"동기화성공",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(
                            getBaseContext(),
                            getString(R.string.toast_msg_server_internal_error),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Log.d(TAG,"onfail : " + t.getMessage());
                Log.d(TAG, "fail " + t.getClass().getName());

                linearLoader.setVisibility(View.GONE);
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            linearLoader.setVisibility(View.GONE);
//            aviLoader.smoothToHide();
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eventlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_eventlist_setting){
            Intent intent = new Intent(EventListActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
