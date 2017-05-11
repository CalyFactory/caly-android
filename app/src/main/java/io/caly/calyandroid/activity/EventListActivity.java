package io.caly.calyandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import io.caly.calyandroid.activity.base.BaseAppCompatActivity;
import io.caly.calyandroid.adapter.EventListAdapter;
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

public class

EventListActivity extends BaseAppCompatActivity {

    private int currentTailPageNum = 1;
    private int currentHeadPageNum = -1;

    private boolean isLoading = false;
    private final int LOADING_THRESHOLD = 2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;

    @Bind(R.id.recycler_eventlist)
    RecyclerView recyclerList;

    @Bind(R.id.tv_eventlist_month)
    TextView tvEventMonth;

    @Bind(R.id.tv_eventlist_yearmonth)
    TextView tvEventYearMonth;

    @Bind(R.id.linear_eventlist_loader) // 동기화
    LinearLayout linearSyncProgress;

    @Bind(R.id.linear_eventlist_still) //추천
    LinearLayout linearRecoProgress;

    @Bind(R.id.tv_eventlist_nodata)
    TextView tvNodata;

    @Bind(R.id.linear_banner)
    LinearLayout linearBanner;

    @Bind(R.id.tv_banner_title)
    TextView tvBannerTitle;

    @Bind(R.id.tv_banner_close)
    TextView tvBannerClose;

//    @Bind(R.id.drawer_layout)
//    DrawerLayout drawerLayout;

    @Bind(R.id.imv_toolbar_logo)
    ImageView imvLogo;

    @Bind(R.id.spinkit_eventlist)
    View spinKitView;

    EventListAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;

    String loginPlatform;

    BannerModel bannerModel;

    @Bind(R.id.fab_eventlist_today)
    FloatingActionButton fabToday;

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

        /*
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerOpened(View drawerView) {
                Logger.d(TAG, "drawer opened");
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Logger.d(TAG, "drawer closed");
                super.onDrawerClosed(drawerView);

            }

        };

        drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });

        mDrawerToggle.syncState();*/

        //set recyclerview
        recyclerList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerList.setLayoutManager(layoutManager);


        recyclerAdapter = new EventListAdapter(EventListActivity.this, new ArrayList<EventModel>());
        recyclerList.setAdapter(recyclerAdapter);

        recyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(isLoading) return;

                if(recyclerAdapter.getItemCount()!=0) {
                    int position = layoutManager.findFirstVisibleItemPosition();
                    if(position<0) return;
                    EventModel eventModel = recyclerAdapter.getItem(position);

                    Logger.d(TAG, eventModel.startMonth + "월");
//                    toolbar.setTitle(StringFormmater.yearMonthFormat(eventModel.startYear, eventModel.startMonth));
                    tvEventMonth.setText(StringFormmater.fullMonths[eventModel.startMonth - 1]);
                    tvEventYearMonth.setText(StringFormmater.yearMonthFormat(eventModel.startYear, eventModel.startMonth));
                    if (getStartDateListIndex() == layoutManager.findFirstVisibleItemPosition()) {

                        fabToday.hide();
                    } else {
                        fabToday.show();
                    }
                }

                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                Logger.d(TAG, "total : " + totalItemCount + " first : " + firstVisibleItem + " last : " + lastVisibleItem);
                if(totalItemCount<=1) return;

                if(totalItemCount - 1 <= lastVisibleItem + LOADING_THRESHOLD){
                    Logger.d(TAG, "last item, loading more");
                    loadMoreEventList(currentTailPageNum);
                }
                else if(firstVisibleItem < LOADING_THRESHOLD){
                    Logger.d(TAG, "first item, loading prev");
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
                        if(recyclerAdapter.getItemCount()-1 < position) return;
                        EventModel eventModel = recyclerAdapter.getItem(position);
                        if(eventModel.isHeader) return;
                        // 여기서는 아이템 클릭 이벤트이기 때문에
                        // 추천완료만 처리하고 분석중과 추천불가
                        // EventListAdapter 안에서처리
                        switch (eventModel.recoState){
                            case STATE_BEING_RECOMMEND: //추천중
                               break;
                            case STATE_DONE_RECOMMEND: //추천완료
                                startRecommandActivity(eventModel);
                                break;
                            case STATE_NOTHING_TO_RECOMMEND: //추천불가
                                break;
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

        checkBanner();

        fabToday.hide();
    }

    void startRecommandActivity(EventModel eventModel){

        Logger.d(TAG, "eventHashkey = >");
        requestSetEventLog (TokenRecord.getTokenRecord().getApiKey(),
                eventModel.eventHashKey,
                LogType.CATEGORY_CELL.value,
                LogType.LABEL_EVENT_CELL.value,
                LogType.ACTION_CLICK.value);

        Intent intent = new Intent(this, RecoListActivity.class);
        intent.putExtra("event", ApiClient.getGson().toJson(eventModel));
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

    }


    void checkBanner(){
        String activeBanner = ConfigClient.getConfig().getString("active_banner");
        Logger.d(TAG, "active banner : " + activeBanner);
        if(activeBanner.length() < 1) return;

        bannerModel = ApiClient.getGson().fromJson(activeBanner, BannerModel.class);
        Date todayDate = new Date();
        if(
                todayDate.after(bannerModel.activationPeriod.startDate) &&
                todayDate.before(bannerModel.activationPeriod.endDate)) {
            if(!Prefer.get("banner_dismiss_"+bannerModel.banner_id, false)){

                Logger.i(TAG, "active banner");

                Message message = new Message();
                message.obj = bannerModel;
                bannerHandler.sendMessageDelayed(message, 4000);
            }
        }
    }

    Handler bannerHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            linearBanner.setVisibility(View.VISIBLE);

            TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0f
            );
            animation.setDuration(300);
            linearBanner.startAnimation(animation);
            tvBannerTitle.setText(((BannerModel)msg.obj).title);
        }
    };

    private List<EventModel> addHeaderToEventList(int lastYear, int lastMonth, List<EventModel> eventModelList){

        if(lastYear == eventModelList.get(0).startYear && lastMonth == eventModelList.get(0).startMonth){

        }
        else{
            eventModelList.add(
                    0,
                    new EventModel(
                            eventModelList.get(0).startYear,
                            eventModelList.get(0).startMonth
                    )
            );
        }
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
        Logger.i(TAG, "loadMoreEventList(" + pageNum + ")");

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
                            EventModel lastItem = recyclerAdapter.getItem(recyclerAdapter.getItemCount() - 1);
                            if(pageNum<0){
                                body.payload.data = addHeaderToEventList(0, 0, body.payload.data);
                                Collections.reverse(body.payload.data);
                            }
                            else{
                                body.payload.data = addHeaderToEventList(lastItem.startYear, lastItem.startMonth, body.payload.data);
                            }
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
                            Logger.e(TAG,"status code : " + response.code());
                            dataNotifyHandler.sendEmptyMessageDelayed(3,2000);
                            break;
                    }

                } catch (IOException e) {
                    Logger.e(TAG, "error : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void showListLoadAnimtaion(){
        spinKitView.setVisibility(View.VISIBLE);
    }

    void hideListLoadAnimation(){
        spinKitView.setVisibility(View.GONE);
    }

    void loadEventList(){
        Logger.i(TAG, "loadEventList");

        showListLoadAnimtaion();
        ApiClient.getService().getEventList(
                TokenRecord.getTokenRecord().getApiKey(),
                0
        ).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());

                switch (response.code()){
                    case 200:
                        EventResponse body = response.body();
                        Logger.d(TAG, "json : " + new Gson().toJson(body));
                        int i=0;
                        body.payload.data = addHeaderToEventList(0, 0, body.payload.data);
                        for(EventModel eventModel : body.payload.data){
                            Logger.d(TAG, "json : " + new Gson().toJson(eventModel));
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
                            hideListLoadAnimation();
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
                        hideListLoadAnimation();
                        tvNodata.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        Logger.e(TAG,"status code : " + response.code());
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


                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }

                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();


                linearRecoProgress.setVisibility(View.GONE);
            }
        });

    }

    void requestSyncCalendar(){
        Logger.i(TAG, "requestSyncCalendar");
        ApiClient.getService().sync(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());

                BasicResponse body = response.body();

                switch (response.code()) {
                    case 200: //추천 종료됨
                        checkCalendarSync();
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        retrySync();
                        break;
                }
            }
            

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Logger.d(TAG,"onfail : " + t.getMessage());
                Logger.d(TAG, "fail " + t.getClass().getName());

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
        Logger.i(TAG, "checkCalendarSync" + EventListActivity.super.hashCode());
        ApiClient.getService().checkSync(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code() +" " + EventListActivity.super.hashCode());

                BasicResponse body = response.body();

                switch (response.code()){
                    case 200: //추천 종료됨
                        linearRecoProgress.setVisibility(View.GONE);
                        linearSyncProgress.setVisibility(View.GONE);
                        loadEventList();
                        break;
                    case 201: //추천중
                        Logger.d(TAG, "201 hashcode : " + EventListActivity.super.hashCode());
                        linearRecoProgress.setVisibility(View.VISIBLE);
                        linearSyncProgress.setVisibility(View.GONE);
                        break;
                    case 202: //동기화중
                        linearRecoProgress.setVisibility(View.GONE);
                        linearSyncProgress.setVisibility(View.VISIBLE);
                        requestSyncCalendar();
                        break;
                    case 203: //동기화가 진행중
                        linearRecoProgress.setVisibility(View.GONE);
                        linearSyncProgress.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
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

                Logger.d(TAG,"onfail : " + t.getMessage());
                Logger.d(TAG, "fail " + t.getClass().getName());

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

    void requestSetEventLog (String apikey, String eventHashkey, int category, int label, int action) {
        ApiClient.getService().setEventLog(
                AnalysisTracker.getAppSession().getSessionKey().toString(),
                apikey,
                eventHashkey,
                category,
                label,
                action
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());
                Logger.d(TAG, "param" + Util.requestBodyToString(call.request().body()));

                switch (response.code()){
                    case 200:
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

            }
        });
    }

    void syncCalendar(){
        Logger.i(TAG, "syncCalendar");

        Logger.d(TAG,"loginplatform : " + loginPlatform);
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
        if(!this.isFinishing()){
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
    }

    void refreshEvent(){

        Message message = dataNotifyHandler.obtainMessage();
        message.what = 2;
        dataNotifyHandler.sendMessage(message);

        currentTailPageNum = 1;
        currentHeadPageNum = -1;

        showListLoadAnimtaion();
        loadEventList();
    }


    @Subscribe
    public void onTestEvent(TestEvent event){
        Logger.d(TAG, "event received : " + EventListActivity.super.hashCode());
    }

    @Subscribe
    public void eventListRefreshCallback(EventListRefreshEvent event){
        refreshEvent();
    }

    @Subscribe
    public void googleSyncDoneEventCallback(GoogleSyncDoneEvent event){
        Logger.i(TAG, "googleSyncDoneEventCallback");
//        checkRecoState();
        syncCalendar();
    }

    @Subscribe
    public void recoReadyEventCallback(RecoReadyEvent event){
        Logger.i(TAG, "recoReadyEventCallback");
        refreshEvent();
//        checkRecoState();
    }

    @Subscribe
    public void accountListRefreshEventCallback(AccountListRefreshEvent event){
        Logger.i(TAG, "accountListRefreshEventCallback : " + EventListActivity.super.hashCode());
        syncCalendar();

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
        Logger.d(TAG, "position : " + position + " item size : " + recyclerAdapter.getItemCount());
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

    void startBannerActivity(){
        Intent intent;
        switch (bannerModel.action.to){
            case "NoticeActivity":
                intent = new Intent(this, NoticeActivity.class);
                break;
            default:
                intent = new Intent(this, NoticeActivity.class);
                break;
        }
        startActivity(intent);
    }

    void startBannerUrl(){
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(bannerModel.action.to));
        startActivity(intent);
    }

    @OnClick(R.id.btn_eventlist_skipreco)
    public void onSkipRecoClick(){
        linearSyncProgress.setVisibility(View.GONE);
        loadEventList();

    }

    @OnClick(R.id.fab_eventlist_today)
    public void onTodayButtonClick(){
        recyclerList.smoothScrollToPosition(getStartDateListIndex());
        Logger.d(TAG, "fab hide");
        fabToday.hide();
    }

    int getStartDateListIndex(){
        Date todayDate = new Date();
        for(int i=0;i<recyclerAdapter.getItemCount();i++){
            EventModel eventModel = recyclerAdapter.getItem(i);
            if(eventModel.isHeader) continue;

            if(eventModel.startDateTime.after(todayDate)){
                if(i==0) return 0;
                return i-1;
            }
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_eventlist, menu);
        return true;
    }

    @OnClick(R.id.tv_banner_close)
    void onBannerCloseClick(){

        requestSetEventLog (TokenRecord.getTokenRecord().getApiKey(),
                null,
                LogType.CATEGORY_VIEW.value,
                LogType.LABEL_EVENT_BANNER_CLOSE.value,
                LogType.ACTION_CLICK.value);


        Logger.i(TAG, "onBannerCloseClick()");
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1.0f
        );
        animation.setDuration(300);
        linearBanner.startAnimation(animation);

        linearBanner.setVisibility(View.GONE);

        Prefer.set("banner_dismiss_" + bannerModel.banner_id, true);

    }

    @OnClick(R.id.linear_banner)
    void onBannerClick(){


        requestSetEventLog (TokenRecord.getTokenRecord().getApiKey(),
                            null,
                            LogType.CATEGORY_VIEW.value,
                            LogType.LABEL_EVENT_BANNER.value,
                            LogType.ACTION_CLICK.value);

        switch (bannerModel.action.type){
            case "intent":
                startBannerActivity();
                break;
            case "url":
                startBannerUrl();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "requestCode : " + requestCode);
        Logger.d(TAG, "resultCode : " + resultCode);

        switch (requestCode){
            case 1: //setting activity
                if(resultCode == 2){
                    refreshEvent();
                }
                break;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_eventlist_refresh:
                requestSetEventLog (TokenRecord.getTokenRecord().getApiKey(),
                                    null,
                                    LogType.CATEGORY_VIEW.value,
                                    LogType.LABEL_EVENT_SYNC.value,
                                    LogType.ACTION_CLICK.value);
                refreshEvent();
                break;

            case R.id.menu_eventlist_setting:
                Intent intent = new Intent(EventListActivity.this, SettingActivity.class);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void hideShimmerAdapter(){
        hideShimmerHandler.sendEmptyMessageDelayed(0,0);
    }

    Handler hideShimmerHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            hideListLoadAnimation();
        }
    };
}
