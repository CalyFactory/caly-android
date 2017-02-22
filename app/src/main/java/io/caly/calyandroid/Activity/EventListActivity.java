package io.caly.calyandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Adapter.EventListAdapter;
import io.caly.calyandroid.Model.EventModel;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.Response.EventResponse;
import io.caly.calyandroid.Model.ORM.SessionRecord;
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

    private int currentTailPageNum = 1;
    private int currentHeadPageNum = -1;

    private boolean isLoading = false;
    private final int LOADING_THRESHOLD = 2;

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
    LinearLayoutManager layoutManager;


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

        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerList.setLayoutManager(layoutManager);


        recyclerAdapter = new EventListAdapter(new ArrayList<EventModel>());
        recyclerList.setAdapter(recyclerAdapter);

        recyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                int position = layoutManager.findFirstVisibleItemPosition();
                EventModel eventModel = recyclerAdapter.getItem(position);

                tvEventYear.setText(eventModel.startYear+"");
                tvEventMonth.setText(eventModel.startMonth+"월");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(isLoading) return;

                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if(totalItemCount<=1) return;

                if(totalItemCount - 1 == lastVisibleItem + LOADING_THRESHOLD){
                    Log.d(TAG, "last item, loading more");
                    loadMoreEventList(currentTailPageNum);
                }
                else if(firstVisibleItem < LOADING_THRESHOLD){
                    Log.d(TAG, "first item, loading prev");
                    loadMoreEventList(currentHeadPageNum);
                }

            }
        });

        Intent intent = getIntent();
        if(intent.getBooleanExtra("first", false)){
            syncCalendar();
        }
        else{
            linearLoader.setVisibility(View.GONE);
            loadEventList();
        }

    }

    /*
    Message
    what
        0 : 추가
        1 : 삭제(예정)
    arg1
        추가삭제변경 할 위치index
     obj
        추가삭제변경 할 객체
     */
    Handler dataNotifyHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){

                case 0:
                    recyclerAdapter.addItem(msg.arg1, (EventModel)msg.obj);
                    break;
                case 1:
                    break;
                default:
                    recyclerAdapter.notifyDataSetChanged();
            }
        }
    };

    void loadMoreEventList(final int pageNum){

        isLoading = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Response<EventResponse> response = Util.getHttpService().getList(
                            SessionRecord.getSessionRecord().getSessionKey(),
                            pageNum
                    ).execute();


                    if(response.code() == 200){
                        EventResponse body = response.body();
                        Log.d(TAG, "json : " + new Gson().toJson(body));
                        Collections.reverse(body.payload.data);
                        for(EventModel eventModel : body.payload.data){

                            Message message = dataNotifyHandler.obtainMessage();
                            message.what = 0;
                            message.obj = eventModel;

                            if(pageNum<0){
                                message.arg1 = 0;
                                dataNotifyHandler.sendMessage(message);

                            }
                            else{
                                message.arg1 = recyclerAdapter.getItemCount();
                                dataNotifyHandler.sendMessage(message);
                            }
                        }

                        if(pageNum<0) {
                            currentHeadPageNum--;
                        }
                        else{
                            currentTailPageNum++;
                        }
                        isLoading=false;
                    }
                    else{
                        Log.e(TAG,"status code : " + response.code());
                    }

                } catch (IOException e) {
                    Log.e(TAG, "error : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void loadEventList(){
        Util.getHttpService().getList(
                SessionRecord.getSessionRecord().getSessionKey(),
                0
        ).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                if(response.code() == 200){
                    EventResponse body = response.body();
                    Log.d(TAG, "json : " + new Gson().toJson(body));
                    for(EventModel eventModel : body.payload.data){

                        Message message = dataNotifyHandler.obtainMessage();
                        message.what = 0;
                        message.arg1 = recyclerAdapter.getItemCount();
                        message.obj = eventModel;
                        dataNotifyHandler.sendMessage(message);
                    }

                    loadMoreEventList(currentHeadPageNum);
                }
                else{
                    Log.e(TAG,"status code : " + response.code());
                    Toast.makeText(
                            getBaseContext(),
                            getString(R.string.toast_msg_server_internal_error),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

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

                    loadEventList();
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
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        }
        return super.onOptionsItemSelected(item);
    }
}
