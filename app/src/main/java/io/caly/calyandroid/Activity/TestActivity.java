package io.caly.calyandroid.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.Util;
import io.caly.calyandroid.View.GoogleOAuthDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 18
 */

public class TestActivity extends BaseAppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        init();
        mapView.onCreate(savedInstanceState);
    }

    void init(){
        ButterKnife.bind(this);

        Log.d(TAG, "uuid : " +Util.getUUID());

        initMap();
    }

    @Bind(R.id.map)
    MapView mapView;

    void initMap(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(TestActivity.this);
                mapView.onResume();
            }
        });
    }


    FirebaseRemoteConfig remoteConfig;


    void initFirebase(){

        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        remoteConfig.setConfigSettings(configSettings);
        remoteConfig.setDefaults(R.xml.remote_config_defaults);

        long cacheExpiration = 0;
        remoteConfig.fetch(cacheExpiration).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(TestActivity.this, "success", Toast.LENGTH_LONG).show();

                    String hello = remoteConfig.getString("hello");
                    Log.i(TAG, "complete : " + hello);
                    remoteConfig.activateFetched();
                }
                else{
                    Toast.makeText(TestActivity.this, "fail", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    void test(){

        ApiClient.getService().test(
                "test"
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Log.d(TAG,"onResponse code : " + response.code());

                BasicResponse body = response.body();
                switch (response.code()){
                    case 200:
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


}
