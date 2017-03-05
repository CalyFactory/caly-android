package io.caly.calyandroid.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Adapter.EventListAdapter;
import io.caly.calyandroid.Model.DataModel.EventModel;
import io.caly.calyandroid.Model.DataModel.TestModel;
import io.caly.calyandroid.Model.Event.GoogleSyncDoneEvent;
import io.caly.calyandroid.Model.LoginPlatform;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.Response.EventResponse;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.EventListener.RecyclerItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.caly.calyandroid.Model.LoginPlatform.GOOGLE;

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

    @Bind(R.id.recycler_eventlist)
    RecyclerView recyclerList;

    @Bind(R.id.tv_eventlist_year)
    TextView tvEventYear;

    @Bind(R.id.tv_eventlist_month)
    TextView tvEventMonth;

    @Bind(R.id.btn_eventlist_prev)
    ImageButton imvEventPrev;

    @Bind(R.id.btn_eventlist_next)
    ImageButton imvEventNext;

    @Bind(R.id.linear_eventlist_loader)
    LinearLayout linearLoader;

    @Bind(R.id.linear_eventlist_still)
    LinearLayout linearStill;

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

                if(recyclerAdapter.getItemCount()==0) return;
                int position = layoutManager.findFirstVisibleItemPosition();
                EventModel eventModel = recyclerAdapter.getItem(position);

                Log.d(TAG, eventModel.startMonth+"월");
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

        recyclerList.addOnItemTouchListener(new RecyclerItemClickListener(
                getBaseContext(),
                recyclerList,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(EventListActivity.this, RecommandListActivity.class);
                        intent.putExtra("event", ApiClient.getGson().toJson(recyclerAdapter.getItem(position)));
                        startActivity(intent);

                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }
            )
        );

        Intent intent = getIntent();
        if(intent.getBooleanExtra("first", false)){
            syncCalendar(intent.getStringExtra("loginPlatform"));
        }
        else{
            checkRecoState();
        }

    }

    /*
    Message
    what
        0 : 추가
        1 : 삭제(예정)
        2 :
        3 : isLoading을 초기화
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
                case 3:
                    isLoading = false;
                    break;
                default:
                    recyclerAdapter.notifyDataSetChanged();
            }
        }
    };

    void loadMoreEventList(final int pageNum){
        Log.i(TAG, "loadMoreEventList(" + pageNum + ")");

        isLoading = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Response<EventResponse> response = ApiClient.getService().getEventList(
                            TokenRecord.getTokenRecord().getApiKey(),
                            pageNum
                    ).execute();


                    switch (response.code()){
                        case 200:
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
                            break;
                        case 201:
                            break;
                        case 401:
                            isLoading=false;
                            break;
                        default:
                            Log.e(TAG,"status code : " + response.code());
                            dataNotifyHandler.sendEmptyMessageDelayed(3,2000);
                            break;
                    }

                } catch (IOException e) {
                    Log.e(TAG, "error : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void loadEventList(){
        Log.i(TAG, "loadEventList");
        ApiClient.getService().getEventList(
                TokenRecord.getTokenRecord().getApiKey(),
                0
        ).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                switch (response.code()){
                    case 200:
                        EventResponse body = response.body();
                        Log.d(TAG, "json : " + new Gson().toJson(body));
                        int i=0;
                        for(EventModel eventModel : body.payload.data){
                            Log.d(TAG, "json : " + new Gson().toJson(eventModel));
                            Message message = dataNotifyHandler.obtainMessage();
                            message.what = 0;
                            message.arg1 = i;
                            message.obj = eventModel;
                            dataNotifyHandler.sendMessage(message);
                            i++;
                        }
                        break;
                    case 201:
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_no_more_data),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                    default:
                        Log.e(TAG,"status code : " + response.code());
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                }

                linearLoader.setVisibility(View.GONE);
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


                linearLoader.setVisibility(View.GONE);
            }
        });

    }

    void checkRecoState(){
        ApiClient.getService().checkRepoState(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {

                Log.d(TAG,"onResponse code : " + response.code());

                linearLoader.setVisibility(View.GONE);

                BasicResponse body = response.body();
                switch (response.code()){
                    case 200:
                        loadEventList();
                        break;
                    case 201:
                        linearStill.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Log.e(TAG,"status code : " + response.code());
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

    void syncCaldav(){
        Log.i(TAG, "syncCaldav");
        ApiClient.getService().sync(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());


                switch (response.code()){
                    case 200:
                        BasicResponse body = response.body();

                        checkRecoState();
                        break;
                    default:
                        linearLoader.setVisibility(View.GONE);
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

    void syncGoogle(){
        Log.i(TAG, "syncGoogle");

        Toast.makeText(
                getBaseContext(),
                getString(R.string.toast_msg_google_sync_alert),
                Toast.LENGTH_LONG
        ).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<BasicResponse> response = ApiClient.getService().sync(
                            TokenRecord.getTokenRecord().getApiKey()
                    ).execute();
                    Log.d(TAG, "requested : " + response.body());
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }

    void syncCalendar(String loginPlatform){
        Log.i(TAG, "syncCalendar");

        Log.d(TAG,"loginplatform : " + loginPlatform);
        switch (LoginPlatform.getInstance(loginPlatform)){
            case GOOGLE:
                syncGoogle();
                break;
            case CALDAV_ICAL:
            case CALDAV_NAVER:
                syncCaldav();
                break;
            default:
                syncCaldav();
        }
    }

    @Subscribe
    public void googleSyncCallback(GoogleSyncDoneEvent event){
        Log.i(TAG, "googleSyncCallback");

        checkRecoState();
    }

    @OnClick(R.id.btn_eventlist_prev)
    void onEventPrevClick(){
        if(recyclerAdapter.getItemCount()==0) return;
        int position = layoutManager.findFirstVisibleItemPosition();
        if(position==0) return;
        recyclerList.smoothScrollToPosition(position-1);
    }

    @OnClick(R.id.btn_eventlist_next)
    void onEventNextClick(){
        if(recyclerAdapter.getItemCount()==0) return;
        int position = layoutManager.findLastVisibleItemPosition();
        if(position==recyclerAdapter.getItemCount() - 1) return;
        Log.d(TAG, "position : " + position + " item size : " + recyclerAdapter.getItemCount());
        recyclerList.smoothScrollToPosition(position + 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eventlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_eventlist_setting){
            Intent intent = new Intent(EventListActivity.this, LegacySettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        }
        return super.onOptionsItemSelected(item);
    }
}
