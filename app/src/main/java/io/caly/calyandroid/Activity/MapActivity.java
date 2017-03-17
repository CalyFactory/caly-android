package io.caly.calyandroid.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public class MapActivity extends NMapActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.mv_mapdetail)
    NMapView mapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);

        init();
    }

    void init(){
//        ButterKnife.bind(this);
/*
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
*/
        mapView = new NMapView(this);
        setContentView(mapView);

        mapView.setClientId(getString(R.string.naver_client_id));

        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setFocusable(true);
        mapView.setFocusableInTouchMode(true);
        mapView.requestFocus();


        /*
        //TODO : 리펙토링이 시급함;;;;;;
        NMapResourceProvider provider = new NMapResourceProvider(this) {
        };

        NMapOverlayManager nMapOverlayManager = new NMapOverlayManager(
                this,
                mapView,
                provider
        );

        NMapPOIdata poIdata = new NMapPOIdata(2, provider);
        poIdata.beginPOIdata(2);
        poIdata.addPOIitem(127.0630205, 37.5091300, "test1", 16, 0);
        poIdata.addPOIitem(127.061, 37.51, "test3", 16, 0);

        poIdata.endPOIdata();

        NMapPOIdataOverlay poIdataOverlay = nMapOverlayManager.createPOIdataOverlay(poIdata, null);

        poIdataOverlay.showAllPOIdata(0);*/

    }
}
