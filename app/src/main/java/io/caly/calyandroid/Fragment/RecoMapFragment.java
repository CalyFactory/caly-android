package io.caly.calyandroid.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.TestActivity;
import io.caly.calyandroid.Adapter.RecoMapListAdapter;
import io.caly.calyandroid.Fragment.Base.BaseFragment;
import io.caly.calyandroid.Model.Category;
import io.caly.calyandroid.Model.DataModel.RecoModel;
import io.caly.calyandroid.Model.Event.MapPermissionGrantedEvent;
import io.caly.calyandroid.Model.Event.RecoListLoadStateChangeEvent;
import io.caly.calyandroid.Model.Response.RecoResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.Logger;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import retrofit2.Response;

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

    @Bind(R.id.fab_map_filter)
    FabSpeedDial fabFilter;

    RecoMapListAdapter adapter;

    GoogleMap googleMap;

    ArrayList<Marker> markerList;
    List<RecoModel> recoList;

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
                        Log.d(TAG, "onMarkerClick");
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
                });
            }
        });

        pager.setClipToPadding(false);
        pager.setPadding(80, 0, 120, 0);
        pager.setPageMargin(40);
        adapter = new RecoMapListAdapter(getContext(), LayoutInflater.from(getContext()));
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected");
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
        });
    }

    void filterList(Category category){

        adapter.recoList.clear();
        adapter.notifyDataSetChanged();
        googleMap.clear();

        for(int i=0;i<recoList.size();i++){
            Log.d(TAG, "category : " + recoList.get(i).category);
            if(category == null){
                adapter.addItem(recoList.get(i));
                addMarker(recoList.get(i));
            }
            else if(recoList.get(i).category.equals(category.value)){
                Log.d(TAG, "same category!");
                adapter.addItem(recoList.get(i));
                addMarker(recoList.get(i));
            }
        }
        if(adapter.recoList.size()>0) {
            moveCamera(adapter.recoList.get(0));
        }
    }

    void addMarker(RecoModel recoModel) {
        View markerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_map_marker, null);
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
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(recoModel.lat, recoModel.lng), 14);
        googleMap.animateCamera(center);
    }

    @Subscribe
    public void mapPermissionGrantedEventCallback(MapPermissionGrantedEvent event) {
        if (
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.e(TAG, "permission not granted");
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    @Subscribe
    public void recoListLoadStateChangeEvent(RecoListLoadStateChangeEvent doneEvent) {
        switch (doneEvent.loadingState) {
            case STATE_LOADING:
                break;
            case STATE_DONE:
                Response<RecoResponse> response = doneEvent.response;
                RecoResponse body = response.body();
                if(recoList == null) recoList = new ArrayList<>();
                recoList.addAll(body.payload.data);
                adapter.addItems(body.payload.data);
                addMarkers(body.payload.data);

                if(doneEvent.category == Category.RESTAURANT) {
                    if (body.payload.data.size() > 0) {
                        moveCamera(body.payload.data.get(0));
                    }
                }

                break;
            case STATE_EMPTY:
                break;
            case STATE_ERROR:
                break;
        }
    }

}
