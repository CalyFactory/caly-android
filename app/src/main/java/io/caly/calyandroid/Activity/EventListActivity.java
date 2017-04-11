package io.caly.calyandroid.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Adapter.EventListAdapter;
import io.caly.calyandroid.Model.DataModel.EventModel;
import io.caly.calyandroid.Model.Event.GoogleSyncDoneEvent;
import io.caly.calyandroid.Model.Event.RecoReadyEvent;
import io.caly.calyandroid.Model.RecoState;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.Response.EventResponse;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.EventListener.RecyclerItemClickListener;
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

    @Bind(R.id.recycler_eventlist)
    ShimmerRecyclerView recyclerList;

    /*
    @Bind(R.id.tv_eventlist_year)
    TextView tvEventYear;

    @Bind(R.id.tv_eventlist_month)
    TextView tvEventMonth;

    @Bind(R.id.btn_eventlist_prev)
    ImageButton imvEventPrev;

    @Bind(R.id.btn_eventlist_next)
    ImageButton imvEventNext;

    */
    @Bind(R.id.linear_eventlist_loader) // 동기화
    LinearLayout linearSyncProgress;

    @Bind(R.id.linear_eventlist_still) //추천
    LinearLayout linearRecoProgress;

    @Bind(R.id.tv_eventlist_nodata)
    TextView tvNodata;

    EventListAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;

    String loginPlatform;

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
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
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
/*
                if(recyclerAdapter.getItemCount()==0) return;
                int position = layoutManager.findFirstVisibleItemPosition();
                EventModel eventModel = recyclerAdapter.getItem(position);

                Log.d(TAG, eventModel.startMonth+"월");
                tvEventYear.setText(eventModel.startYear+"");
                tvEventMonth.setText(eventModel.startMonth+"월");*/
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(isLoading) return;

                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                Log.d(TAG, "total : " + totalItemCount + " first : " + firstVisibleItem + " last : " + lastVisibleItem);
                if(totalItemCount<=1) return;

                if(totalItemCount - 1 <= lastVisibleItem + LOADING_THRESHOLD){
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

                        EventModel eventModel = recyclerAdapter.getItem(position);

                        if(eventModel.recoState == RecoState.STATE_DONE_RECOMMEND){
                            startRecommandActivity(eventModel);
                        }


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }
            )
        );

        Intent intent = getIntent();
        loginPlatform = intent.getStringExtra("loginPlatform");
        //TODO: 로직이 변경되어 의미 없는 if문이 존재하는데 동작 확인 테스트 후 삭제요망
        if(intent.getBooleanExtra("first", false)){
            syncCalendar();
        }
        else{
            syncCalendar();
        }

    }

    void startRecommandActivity(EventModel eventModel){
        Intent intent = new Intent(EventListActivity.this, RecommendListActivity.class);
        intent.putExtra("event", ApiClient.getGson().toJson(eventModel));
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

    }

    List<EventModel> addHeaderToEventList(List<EventModel> eventModelList){

        eventModelList.add(
                0,
                new EventModel(
                        eventModelList.get(0).startYear,
                        eventModelList.get(0).startMonth
                )
        );
        for(int i=1;i<eventModelList.size();i++){
            if(eventModelList.get(i-1).isHeader || eventModelList.get(i).isHeader) continue;
            if(eventModelList.get(i-1).startYear != eventModelList.get(i).startYear ||
                    eventModelList.get(i-1).startMonth != eventModelList.get(i).startMonth){
                eventModelList.add(
                        i,
                        new EventModel(
                                eventModelList.get(i).startYear,
                                eventModelList.get(i).startMonth
                        )
                );
            }
        }

        return eventModelList;
    }

    /*
    Message
    what
        0 : 추가(제일아레에)
        1 : 추가(제일위에)
        2 : 전체삭제
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
                    recyclerAdapter.addTail((EventModel)msg.obj);
                    break;
                case 1:
                    recyclerAdapter.addHead((EventModel)msg.obj);
                    break;
                case 2:
                    recyclerAdapter.removeAll();
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
//                            Collections.reverse(body.payload.data);
                            body.payload.data = addHeaderToEventList(body.payload.data);
                            for(EventModel eventModel : body.payload.data){

                                Message message = dataNotifyHandler.obtainMessage();
                                message.obj = eventModel;

                                if(pageNum<0){
                                    message.what = 1;

                                }
                                else{
                                    message.what = 0;
                                }
                                dataNotifyHandler.sendMessage(message);
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
                            isLoading=false;
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

        recyclerList.showShimmerAdapter();
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
                        body.payload.data = addHeaderToEventList(body.payload.data);
                        for(EventModel eventModel : body.payload.data){
                            Log.d(TAG, "json : " + new Gson().toJson(eventModel));
                            Message message = dataNotifyHandler.obtainMessage();
                            message.what = 0;
                            message.arg1 = i;
                            message.obj = eventModel;
                            dataNotifyHandler.sendMessage(message);
                            hideShimmerAdapter();
                            i++;
                        }
                        if(body.payload.data.size()==0){
                            tvNodata.setVisibility(View.VISIBLE);
                        }
                        else{
                            tvNodata.setVisibility(View.GONE);
                        }
                        break;
                    case 201:
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_no_more_data),
                                Toast.LENGTH_LONG
                        ).show();
                        tvNodata.setVisibility(View.VISIBLE);
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

                linearRecoProgress.setVisibility(View.GONE);
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


                linearRecoProgress.setVisibility(View.GONE);
            }
        });

    }

    @Deprecated
    void checkRecoState(){
        ApiClient.getService().checkRepoState(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {

                Log.d(TAG,"onResponse code : " + response.code());

                linearRecoProgress.setVisibility(View.GONE);

                BasicResponse body = response.body();
                switch (response.code()){
                    case 200:
                        linearSyncProgress.setVisibility(View.GONE);
                        loadEventList();
                        break;
                    case 201:
                        linearSyncProgress.setVisibility(View.VISIBLE);
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

    void requestSyncCalendar(){
        Log.i(TAG, "requestSyncCalendar");
        ApiClient.getService().sync(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                BasicResponse body = response.body();

                switch (response.code()) {
                    case 200: //추천 종료됨
                        checkCalendarSync();
                        break;
                    default:
                        retrySync();
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Log.d(TAG,"onfail : " + t.getMessage());
                Log.d(TAG, "fail " + t.getClass().getName());

                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
                retrySync();
            }
        });
    }

    void checkCalendarSync(){
        Log.i(TAG, "checkCalendarSync");
        ApiClient.getService().checkSync(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                BasicResponse body = response.body();

                switch (response.code()){
                    case 200: //추천 종료됨
                        linearRecoProgress.setVisibility(View.GONE);
                        linearSyncProgress.setVisibility(View.GONE);
                        loadEventList();
                        break;
                    case 201: //추천중
                        linearRecoProgress.setVisibility(View.VISIBLE);
                        linearSyncProgress.setVisibility(View.GONE);
                        break;
                    case 202: //동기화중
                        linearRecoProgress.setVisibility(View.GONE);
                        linearSyncProgress.setVisibility(View.VISIBLE);
                        requestSyncCalendar();
                        break;
                    default:
                        linearRecoProgress.setVisibility(View.GONE);
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();
                        retrySync();
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {

                Log.d(TAG,"onfail : " + t.getMessage());
                Log.d(TAG, "fail " + t.getClass().getName());

                linearRecoProgress.setVisibility(View.GONE);
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
                retrySync();
            }
        });

    }

    void syncGoogle(){
        Log.i(TAG, "syncGoogle");

        ApiClient.getService().checkSync(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {

                switch (response.code()){
                    case 200: //추천 종료됨
                        linearRecoProgress.setVisibility(View.GONE);
                        linearSyncProgress.setVisibility(View.GONE);
                        loadEventList();
                        break;
                    case 201: //추천중
                        linearRecoProgress.setVisibility(View.VISIBLE);
                        linearSyncProgress.setVisibility(View.GONE);
                        break;
                    case 202: //동기화중
                        linearRecoProgress.setVisibility(View.GONE);
                        linearSyncProgress.setVisibility(View.VISIBLE);
                        break;
                    default:
                        linearRecoProgress.setVisibility(View.GONE);
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();
                        retrySync();
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Log.d(TAG,"onfail : " + t.getMessage());
                Log.d(TAG, "fail " + t.getClass().getName());

                linearRecoProgress.setVisibility(View.GONE);
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
                retrySync();
            }
        });

    }

    @Deprecated
    void syncCaldav2(){
        Log.i(TAG, "checkCalendarSync");
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
                        linearRecoProgress.setVisibility(View.GONE);
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();
                        retrySync();
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Log.d(TAG,"onfail : " + t.getMessage());
                Log.d(TAG, "fail " + t.getClass().getName());

                linearRecoProgress.setVisibility(View.GONE);
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
                retrySync();
            }
        });
    }

    @Deprecated
    void syncGoogle2(){
        Log.i(TAG, "syncGoogle");

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
                        linearRecoProgress.setVisibility(View.GONE);
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();
                        retrySync();
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Log.d(TAG,"onfail : " + t.getMessage());
                Log.d(TAG, "fail " + t.getClass().getName());
                retrySync();

                linearRecoProgress.setVisibility(View.GONE);
                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
        /*
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
        }).start();*/
    }

    void syncCalendar(){
        Log.i(TAG, "syncCalendar");

        Log.d(TAG,"loginplatform : " + loginPlatform);
        checkCalendarSync();
        //TODO : syncCaldav와 syncGoogle의 역할이 모호해져서 합칠 필요가 있음
        /*
        switch (LoginPlatform.getInstance(loginPlatform)){
            case GOOGLE:
                syncGoogle();
                break;
            case CALDAV_ICAL:
            case CALDAV_NAVER:
                checkCalendarSync();
                break;
            default:
                checkCalendarSync();
        }*/
    }

    void retrySync(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("동기화를 실패했습니다. 재시도 하시겠습니까?");
        builder.setTitle("재시도");
        builder.setPositiveButton("재시도", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                syncCalendar();

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.show();
    }

    void refreshEvent(){

        Message message = dataNotifyHandler.obtainMessage();
        message.what = 2;
        dataNotifyHandler.sendMessage(message);

        currentTailPageNum = 1;
        currentHeadPageNum = -1;

        recyclerList.showShimmerAdapter();
        loadEventList();
    }

    @Subscribe
    public void googleSyncDoneEventCallback(GoogleSyncDoneEvent event){
        Log.i(TAG, "googleSyncDoneEventCallback");
//        linearRecoProgress.setVisibility(View.GONE);
//        checkRecoState();
        syncCalendar();
    }

    @Subscribe
    public void recoReadyEventCallback(RecoReadyEvent event){
        Log.i(TAG, "recoReadyEventCallback");
        syncCalendar();
//        checkRecoState();
    }

    /*
    @OnClick(R.id.btn_eventlist_prev)
    void onEventPrevClick(){
        if(recyclerAdapter.getItemCount()==0) return;
        int position = layoutManager.findFirstVisibleItemPosition();
        if(position==0) return;
        recyclerList.smoothScrollToPosition(position-1);


        Tracker t = ((CalyApplication)getApplication()).getDefaultTracker();
        t.setScreenName(this.getClass().getName());
        t.send(
                new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.ga_category_button))
                        .setAction(getString(R.string.ga_action_click))
                        .setLabel("onEventPrevClick")
                        .build()
        );
    }

    @OnClick(R.id.btn_eventlist_next)
    void onEventNextClick(){
        if(recyclerAdapter.getItemCount()==0) return;
        int position = layoutManager.findLastVisibleItemPosition();
        if(position==recyclerAdapter.getItemCount() - 1) return;
        Log.d(TAG, "position : " + position + " item size : " + recyclerAdapter.getItemCount());
        recyclerList.smoothScrollToPosition(position + 1);


        Tracker t = ((CalyApplication)getApplication()).getDefaultTracker();
        t.setScreenName(this.getClass().getName());
        t.send(
                new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.ga_category_button))
                        .setAction(getString(R.string.ga_action_click))
                        .setLabel("onEventNextClick")
                        .build()
        );
    }*/

    @OnClick(R.id.btn_eventlist_skipreco)
    public void onSkipRecoClick(){
        linearSyncProgress.setVisibility(View.GONE);
        loadEventList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eventlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_eventlist_refresh:
                refreshEvent();
                break;
            case R.id.menu_eventlist_setting:
                Intent intent = new Intent(EventListActivity.this, LegacySettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void hideShimmerAdapter(){
        hideShimmerHandler.sendEmptyMessageDelayed(0,2000);
    }

    Handler hideShimmerHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            recyclerList.hideShimmerAdapter();
        }
    };
}
