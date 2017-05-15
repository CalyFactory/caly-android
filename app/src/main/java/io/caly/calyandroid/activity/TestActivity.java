package io.caly.calyandroid.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.event.TestEvent;
import io.caly.calyandroid.util.Logger;

import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import io.caly.calyandroid.page.base.BaseAppCompatActivity;
import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.ApiClient;
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

//    @Bind(R.id.button)
//    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);
        init();
//        mapView.onCreate(savedInstanceState);
    }
/*
    @OnClick(R.id.button)
    void onButtonClick(){
        test();
    }
*/
    void init(){
        ButterKnife.bind(this);


        Logger.d(TAG, "page hashcode : " +super.hashCode());
//        initMap();
    }

//    @Bind(R.id.map)
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

    @Produce
    public TestEvent getTestEvent(){
        return new TestEvent();
    }

    @Subscribe
    public void onTestEvent(TestEvent event){
        Logger.d(TAG, "event received : " + super.hashCode());

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
                    Logger.i(TAG, "complete : " + hello);
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
                Logger.d(TAG,"onResponse code : " + response.code());

                BasicResponse body = response.body();
                switch (response.code()){
                    case 200:
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
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());


                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }

                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }


}
