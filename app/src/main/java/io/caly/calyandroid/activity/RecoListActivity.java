package io.caly.calyandroid.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.fragment.RecoListFragment;
import io.caly.calyandroid.fragment.RecoMapFragment;
import io.caly.calyandroid.model.Category;
import io.caly.calyandroid.model.dataModel.RecoListWrapModel;
import io.caly.calyandroid.model.dataModel.RecoModel;
import io.caly.calyandroid.model.event.MapPermissionGrantedEvent;
import io.caly.calyandroid.model.event.TestEvent;
import io.caly.calyandroid.model.response.RecoResponse;
import io.caly.calyandroid.util.BusProvider;
import io.caly.calyandroid.util.Logger;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.activity.base.BaseAppCompatActivity;
import io.caly.calyandroid.model.dataModel.EventModel;
import io.caly.calyandroid.model.event.RecoListLoadStateChangeEvent;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 14
 */

public class RecoListActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;

    EventModel eventData;

    PAGE_TYPE pageType;

    RecoListFragment recoListFragment;
//    RecoMapFragment recoMapFragment;

    List<RecoModel> recoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        //set toolbar
        tvToolbarTitle.setText("추천 목록");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventData = ApiClient.getGson()
                .fromJson(
                        getIntent().getStringExtra("event"),
                        EventModel.class
                );
        tvToolbarTitle.setText(eventData.summaryText);

        recoListFragment = new RecoListFragment().setData(eventData);
//        recoMapFragment = new RecoMapFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.linear_reco_container, recoListFragment, "list");
//        transaction.add(R.id.linear_reco_container, recoMapFragment, "map");
//        transaction.hide(recoMapFragment);
        transaction.commit();

        pageType = PAGE_TYPE.LIST;

        Message msg1 = new Message();
        msg1.obj = Category.RESTAURANT;

        Message msg2 = new Message();
        msg2.obj = Category.CAFE;

        Message msg3 = new Message();
        msg3.obj = Category.PLACE;
        recoLoadHandler.sendMessage(msg1);
        recoLoadHandler.sendMessage(msg2);
        recoLoadHandler.sendMessage(msg3);
    }

    Handler recoLoadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            loadList((Category)msg.obj);
        }
    };

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        /*
        if(layoutDrawer.getVisibility() == View.VISIBLE){
            drawOutDrawer();
        }
        else{
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reco, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_reco_changeview:
                startMapActivity();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void changeView(){
        MenuItem item = toolbar.getMenu().findItem(R.id.menu_reco_changeview);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        switch (pageType){
            case LIST:
                checkGPSPermission();
                pageType = PAGE_TYPE.MAP;
                item.setIcon(getResources().getDrawable(R.drawable.group_4));
                transaction.hide(recoListFragment);
//                transaction.show(recoMapFragment);
                break;
            case MAP:
                pageType = PAGE_TYPE.LIST;
                item.setIcon(getResources().getDrawable(R.drawable.ic_checkin_map));
                transaction.show(recoListFragment);
//                transaction.hide(recoMapFragment);

                break;
        }
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    void checkGPSPermission(){
        Logger.d(TAG, "checkGPSPermission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            BusProvider.getInstance().post(new MapPermissionGrantedEvent());

        }
        else{
            Logger.d(TAG, "request permission");
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    Util.RC_PERMISSION_FINE_LOCATION
            );
        }
    }

    enum PAGE_TYPE{
        LIST,
        MAP
    }


    void loadList(final Category category){


        BusProvider.getInstance().post(
                new RecoListLoadStateChangeEvent(
                        category,
                        0,
                        null,
                        RecoListLoadStateChangeEvent.LOADING_STATE.STATE_LOADING
                )
        );

        ApiClient.getService().getRecoList(
                TokenRecord.getTokenRecord().getApiKey(),
                eventData.eventHashKey,
                category.value
        ).enqueue(new Callback<RecoResponse>() {
            @Override
            public void onResponse(Call<RecoResponse> call, Response<RecoResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());

                RecoResponse body = response.body();

                switch (response.code()){
                    case 200:
                        if(recoList == null) recoList = new ArrayList<RecoModel>();
                        recoList.addAll(body.payload.data);
                        BusProvider.getInstance().post(
                                new RecoListLoadStateChangeEvent(
                                        category,
                                        body.payload.data.size(),
                                        response,
                                        RecoListLoadStateChangeEvent.LOADING_STATE.STATE_DONE
                                )
                        );
                        break;
                    case 201: // no data

                        BusProvider.getInstance().post(
                                new RecoListLoadStateChangeEvent(
                                        category,
                                        0,
                                        response,
                                        RecoListLoadStateChangeEvent.LOADING_STATE.STATE_EMPTY
                                )
                        );
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        Logger.e(TAG,"status code : " + response.code());
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();

                        BusProvider.getInstance().post(
                                new RecoListLoadStateChangeEvent(
                                        category,
                                        0,
                                        response,
                                        RecoListLoadStateChangeEvent.LOADING_STATE.STATE_ERROR
                                )
                        );
                        break;
                }
            }

            @Override
            public void onFailure(Call<RecoResponse> call, Throwable t) {
                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                Toast.makeText(
                        getBaseContext(),
                        getResources().getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();

                BusProvider.getInstance().post(
                        new RecoListLoadStateChangeEvent(
                                category,
                                0,
                                null,
                                RecoListLoadStateChangeEvent.LOADING_STATE.STATE_ERROR
                        )
                );
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){
            //denied

        }else{
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                //allowed
                BusProvider.getInstance().post(new MapPermissionGrantedEvent());
            } else{
                //set to never ask again

            }
        }

    }

    void startMapActivity(){
        Intent intent = new Intent(RecoListActivity.this, RecoMapActivity.class);
        intent.putExtra("data", new Gson().toJson(new RecoListWrapModel(recoList)));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }



    /*

    void drawInDrawer(){

        layoutDrawer.setVisibility(View.VISIBLE);

        Util.setStatusBarColor(this, Color.BLACK);

        layoutDrawerInside.startAnimation(drawerInAnimation);
    }

    void drawOutDrawer(){
        layoutDrawerInside.startAnimation(drawerOutAnimation);
    }

    RecoModel recoModel;

    @Subscribe
    public void onRecoMoreClickCallback(RecoMoreClickEvent event){
        this.recoModel = event.recoModel;
        drawInDrawer();
    }

    @OnClick(R.id.layout_drawer)
    void onDrawerClick(){
        drawOutDrawer();
    }

    @OnClick(R.id.layout_draweritem_1)
    void onDrawerItemClick1(){
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        clipboardManager.setPrimaryClip(ClipData.newPlainText("text", recoModel.deepUrl));
        Toast.makeText(getBaseContext(), "복사되었습니다.",Toast.LENGTH_LONG).show();

        drawOutDrawer();
    }

    @OnClick(R.id.layout_draweritem_2)
    void onDrawerItemClick2(){

        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", recoModel.sourceUrl);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

        drawOutDrawer();
    }

    @OnClick(R.id.layout_draweritem_3)
    void onDrawerItemClick3(){
        String[] snsList = {
                "com.kakao.talk", //kakaotalk
        };
        boolean sended = false;
        for(String snsPackage : snsList){
            if(Util.isPackageInstalled(snsPackage)){

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"[캘리] 여기어때요? \n" + recoModel.deepUrl);
                intent.setPackage("com.kakao.talk");

                startActivity(intent);
                sended = true;
            }
        }
        if(!sended){
            Toast.makeText(getBaseContext(), "공유 할 수 있는 SNS가 설치 되어있지 않습니다.",Toast.LENGTH_LONG).show();
        }

        drawOutDrawer();
    }*/

}
