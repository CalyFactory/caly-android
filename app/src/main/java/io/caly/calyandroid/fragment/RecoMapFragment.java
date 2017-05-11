package io.caly.calyandroid.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.R;
import io.caly.calyandroid.adapter.RecoMapListAdapter;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.fragment.base.BaseFragment;
import io.caly.calyandroid.model.Category;
import io.caly.calyandroid.model.LogType;
import io.caly.calyandroid.model.dataModel.RecoModel;
import io.caly.calyandroid.model.event.MapPermissionGrantedEvent;
import io.caly.calyandroid.model.event.RecoListLoadStateChangeEvent;
import io.caly.calyandroid.model.event.RecoMapFilterChangeEvent;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.model.response.RecoResponse;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.Util;
import io.caly.calyandroid.util.tracker.AnalysisTracker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.sql.Types.NULL;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 4. 19
 */

public class RecoMapFragment extends BaseFragment {

    @Bind(R.id.mapview_recomap_map)
    MapView mapView;

    @Bind(R.id.pager_recomap)
    ViewPager pager;

    @Bind(R.id.linear_map_category_1) //restaurent
    LinearLayout linearCategory1;

    @Bind(R.id.linear_map_category_2) //cafe
    LinearLayout linearCategory2;

    @Bind(R.id.linear_map_category_3) //place
    LinearLayout linearCategory3;


//    @Bind(R.id.fab_map_filter)
//    FabSpeedDial fabFilter;

    RecoMapListAdapter adapter;

    GoogleMap googleMap;

    ArrayList<Marker> markerList;
    List<RecoModel> recoList;

    boolean isPermissionGranted = false;



    public RecoMapFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = null;

        v = inflater.inflate(R.layout.fragment_recomap, null);

        ButterKnife.bind(this, v);

        mapView.onCreate(savedInstanceState);
        init();

        return v;
    }

    void init() {



        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getActivity());
                mapView.onResume();

                RecoMapFragment.this.googleMap = googleMap;

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Logger.d(TAG, "onMarkerClick");
                        String recoHashKey = (String) marker.getTag();

                        for (int i = 0; i < adapter.recoList.size(); i++) {
                            RecoModel recoModel = adapter.recoList.get(i);
                            if (recoHashKey.equals(recoModel.recoHashKey)) {
                                pager.setCurrentItem(i);
                                break;
                            }
                        }

                        return false;
                    }
                });
                /*
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        if(RecoMapFragment.this.googleMap.getMyLocation() == null){
                            return false;
                        }
                        LatLng loc = new LatLng(RecoMapFragment.this.googleMap.getMyLocation().getLatitude(),RecoMapFragment.this.googleMap.getMyLocation().getLongitude());
                        RecoMapFragment.this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                        return true;
                    }
                });*/
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        Log.d(TAG, "click myLocation");
                        if (recoList.size() != 0) {
                            requestSetRecoLog(
                                    TokenRecord.getTokenRecord().getApiKey(),
                                    recoList.get(0).eventHashKey,
                                    LogType.CATEGORY_RECO_MAP_VIEW.value,
                                    LogType.LABEL_RECO_MAP_MY_LOCATION.value,
                                    LogType.ACTION_CLICK.value,
                                    NULL,
                                    null
                            );
                        }

                        return false;
                    }
                });

                addData(recoList);
                moveCameraWithoutAnimate(recoList.get(0));
                markerList.get(0).showInfoWindow();

                if(isPermissionGranted){
                    mapPermissionGrantedEventCallback(null);
                }
            }
        });

        pager.setClipToPadding(false);
        pager.setPadding(
                (int)Util.convertDpToPixel(27f, getContext()),
                0,
                (int)Util.convertDpToPixel(25.5f, getContext()),
                0
        );
        pager.setPageMargin(
                (int)Util.convertDpToPixel(9f, getContext())
        );
        adapter = new RecoMapListAdapter(getContext(), LayoutInflater.from(getContext()));
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Logger.d(TAG, "onPageSelected");
                String recoHashKey = adapter.recoList.get(position).recoHashKey;
                for (int i = 0; i < markerList.size(); i++) {
                    String markerTag = (String) markerList.get(i).getTag();
                    if (recoHashKey.equals(markerTag)) {
                        markerList.get(i).showInfoWindow();
                        moveCamera(adapter.recoList.get(position));
                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        markerList = new ArrayList<>();

        /*
        fabFilter.setMenuListener(new SimpleMenuListenerAdapter(){
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_filter_all:
                        filterList(null);
                        break;
                    case R.id.menu_filter_restaurant:
                        filterList(Category.RESTAURANT);
                        break;
                    case R.id.menu_filter_cafe:
                        filterList(Category.CAFE);
                        break;
                    case R.id.menu_filter_place:
                        filterList(Category.PLACE);
                        break;
                }
                return super.onMenuItemSelected(menuItem);
            }
        });*/
    }

    public RecoMapFragment setData(List<RecoModel> recoList){
        this.recoList = recoList;
        return this;
    }

    public List<RecoModel> getData(){
        return  this.recoList;
    }

    void filterList(Category category){
        //TODO : 이부분이 많이 느린데 별도의 쓰레드에서 처리해야하지 않을까
        Log.d(TAG, "marker list size : " + markerList.size());

        adapter.recoList.clear();
        adapter.notifyDataSetChanged();
        googleMap.clear();
        markerList.clear();

        for(int i=0;i<recoList.size();i++){
            if(category == null){
                adapter.addItem(recoList.get(i));
                addMarker(recoList.get(i));
            }
            else if(recoList.get(i).category.equals(category.value)){
                adapter.addItem(recoList.get(i));
                addMarker(recoList.get(i));
            }
        }
        if(adapter.recoList.size()>0) {
            Log.d(TAG, "filtered, move camera");
            moveCamera(adapter.recoList.get(0));
            markerList.get(0).showInfoWindow();
        }
    }

    void addMarker(RecoModel recoModel) {
        View markerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_map_marker, null);
        ImageView imvMarker = ((ImageView)markerView.findViewById(R.id.imv_map_marker));
        switch (recoModel.category){
            case "restaurant":
                imvMarker.setImageResource(R.drawable.ic_checkin_r);
                break;
            case "cafe":
                imvMarker.setImageResource(R.drawable.ic_checkin_c);
                break;
            case "place":
                imvMarker.setImageResource(R.drawable.ic_checkin_p);
                break;
        }
        LatLng position = new LatLng(recoModel.lat, recoModel.lng);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(recoModel.title);
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), markerView)));

        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(recoModel.recoHashKey);

        markerList.add(marker);
    }

    void addMarkers(List<RecoModel> recoList) {
        for (RecoModel recoModel : recoList) {
            addMarker(recoModel);
        }
    }

    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void moveCamera(RecoModel recoModel) {
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(recoModel.lat, recoModel.lng), 16);
        googleMap.animateCamera(center);
    }

    public void moveCameraWithoutAnimate(RecoModel recoModel){
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(recoModel.lat, recoModel.lng), 16);
        googleMap.moveCamera(center);

    }

    @Subscribe
    public void mapPermissionGrantedEventCallback(MapPermissionGrantedEvent event) {
        Logger.d(TAG, "permission event");
        if (
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Logger.e(TAG, "permission not granted");
            return;
        }

        if(googleMap == null){
            isPermissionGranted = true;
        }
        else {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Subscribe
    public void recoMapFilterChangeEventCallback(RecoMapFilterChangeEvent event){
        switch (event.index){
            case 0:

                if (recoList.size() != 0) {
                    requestSetRecoLog(
                            TokenRecord.getTokenRecord().getApiKey(),
                            recoList.get(0).eventHashKey,
                            LogType.CATEGORY_RECO_MAP_VIEW.value,
                            LogType.LABEL_RECO_MAP_FILTER_ALL.value,
                            LogType.ACTION_CLICK.value,
                            NULL,
                            null
                    );
                }

                linearCategory1.setVisibility(View.VISIBLE);
                linearCategory2.setVisibility(View.VISIBLE);
                linearCategory3.setVisibility(View.VISIBLE);
                filterList(null);
                break;
            case 1:
                if (recoList.size() != 0) {
                    requestSetRecoLog(
                            TokenRecord.getTokenRecord().getApiKey(),
                            recoList.get(0).eventHashKey,
                            LogType.CATEGORY_RECO_MAP_VIEW.value,
                            LogType.LABEL_RECO_MAP_FILTER_RESTAURANT.value,
                            LogType.ACTION_CLICK.value,
                            NULL,
                            null
                    );
                }

                linearCategory1.setVisibility(View.VISIBLE);
                linearCategory2.setVisibility(View.GONE);
                linearCategory3.setVisibility(View.GONE);
                filterList(Category.RESTAURANT);
                break;
            case 2:
                if (recoList.size() != 0) {
                    requestSetRecoLog(
                            TokenRecord.getTokenRecord().getApiKey(),
                            recoList.get(0).eventHashKey,
                            LogType.CATEGORY_RECO_MAP_VIEW.value,
                            LogType.LABEL_RECO_MAP_FILTER_CAFE.value,
                            LogType.ACTION_CLICK.value,
                            NULL,
                            null
                    );
                }

                linearCategory1.setVisibility(View.GONE);
                linearCategory2.setVisibility(View.VISIBLE);
                linearCategory3.setVisibility(View.GONE);
                filterList(Category.CAFE);
                break;
            case 3:
                if (recoList.size() != 0) {
                    requestSetRecoLog(
                            TokenRecord.getTokenRecord().getApiKey(),
                            recoList.get(0).eventHashKey,
                            LogType.CATEGORY_RECO_MAP_VIEW.value,
                            LogType.LABEL_RECO_MAP_FILTER_PLACE.value,
                            LogType.ACTION_CLICK.value,
                            NULL,
                            null
                    );
                }

                linearCategory1.setVisibility(View.GONE);
                linearCategory2.setVisibility(View.GONE);
                linearCategory3.setVisibility(View.VISIBLE);
                filterList(Category.PLACE);
                break;
        }
    }

    @Subscribe
    public void recoListLoadStateChangeEvent(RecoListLoadStateChangeEvent doneEvent) {
        switch (doneEvent.loadingState) {
            case STATE_LOADING:
                break;
            case STATE_DONE:
                Response<RecoResponse> response = doneEvent.response;
                RecoResponse body = response.body();
                addData(body.payload.data);

                if(doneEvent.category == Category.RESTAURANT) {
                    if (body.payload.data.size() > 0) {
                        moveCamera(body.payload.data.get(0));
                        markerList.get(0).showInfoWindow();
                    }
                }
                break;
            case STATE_EMPTY:
                break;
            case STATE_ERROR:
                break;
        }
    }

    void addData(List<RecoModel> recoList){

        if(this.recoList == null) this.recoList = new ArrayList<>();
        this.recoList.addAll(recoList);
        adapter.addItems(recoList);
        addMarkers(recoList);
    }
    void requestSetRecoLog (String apikey, String eventHashkey, int category, int label, int action, long residenseTime, String recoHashkey) {
        ApiClient.getService().setRecoLog(
                AnalysisTracker.getAppSession().getSessionKey().toString(),
                apikey,
                eventHashkey,
                category,
                label,
                action,
                residenseTime,
                recoHashkey
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Logger.d(TAG, "onResponse code : " + response.code());
                Logger.d(TAG, "param" + Util.requestBodyToString(call.request().body()));

                switch (response.code()) {
                    case 200:
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        break;
                }

            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                if (t instanceof MalformedJsonException || t instanceof JsonSyntaxException) {
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }

                Logger.e(TAG, "onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

            }
        });
    }

}
