package io.caly.calyandroid.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.TestActivity;
import io.caly.calyandroid.Fragment.Base.BaseFragment;
import io.caly.calyandroid.R;

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

    public RecoMapFragment(){

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

    void init(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getActivity());
                mapView.onResume();
                addMarker(googleMap);

                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(37.4900407, 127.0099624), 14);
                googleMap.animateCamera(center);


            }
        });
    }

    void addMarker(GoogleMap googleMap){
        View markerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_map_marker, null);
        LatLng position = new LatLng(37.4900407, 127.0099624);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("title1");
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), markerView)));

        googleMap.addMarker(markerOptions);


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


}
