package io.caly.calyandroid.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import io.caly.calyandroid.Fragment.RecoListFragment;
import io.caly.calyandroid.Util.Logger;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Adapter.RecoTabPagerAdapter;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.DataModel.EventModel;
import io.caly.calyandroid.Model.DataModel.RecoModel;
import io.caly.calyandroid.Model.Event.RecoListLoadDoneEvent;
import io.caly.calyandroid.Model.Event.RecoMoreClickEvent;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.Util;
import io.caly.calyandroid.View.FeedbackDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    EventModel eventData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        //set toolbar
        toolbar.setTitle("추천 목록");
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventData = ApiClient.getGson()
                .fromJson(
                        getIntent().getStringExtra("event"),
                        EventModel.class
                );


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.linear_reco_container, new RecoListFragment().setData(eventData));
        transaction.commit();

    }

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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
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
