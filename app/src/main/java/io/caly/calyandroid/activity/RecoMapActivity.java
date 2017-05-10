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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.R;
import io.caly.calyandroid.activity.base.BaseAppCompatActivity;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.fragment.RecoMapFragment;
import io.caly.calyandroid.model.LogType;
import io.caly.calyandroid.model.dataModel.RecoListWrapModel;
import io.caly.calyandroid.model.dataModel.RecoModel;
import io.caly.calyandroid.model.event.MapPermissionGrantedEvent;
import io.caly.calyandroid.model.event.RecoMapFilterChangeEvent;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.BusProvider;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by jspiner on 2017. 5. 6..
 */

public class RecoMapActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;

    RecoMapFragment recoMapFragment;

    long startSecond;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomap);

        init();
    }

    void init() {
        ButterKnife.bind(this);
        startSecond = System.currentTimeMillis();
        //set toolbar
        tvToolbarTitle.setText("지도로 보기");
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

        Intent intent = getIntent();
        String intentData = intent.getStringExtra("data");

        List<RecoModel> recoList = new Gson().fromJson(intentData, RecoListWrapModel.class).recoList;

        recoMapFragment = new RecoMapFragment().setData(recoList);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.linear_recomap_container, recoMapFragment);
        transaction.commit();


        delayHandler.sendEmptyMessageDelayed(0, 10);

    }

    Handler delayHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            checkGPSPermission();
            super.handleMessage(msg);
        }
    };

    void checkGPSPermission(){
        Log.d(TAG, "checkGPSPermission");
        Log.d(TAG, "ACCESS_FINE_LOCATION " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        Log.d(TAG, "ACCESS_COARSE_LOCATION " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION));
        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            BusProvider.getInstance().post(new MapPermissionGrantedEvent());

        }
        else{
            Log.d(TAG, "request permission");
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    Util.RC_PERMISSION_FINE_LOCATION
            );
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            //denied
            Log.d(TAG, "permission Denied");

        }else{
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                //allowed
                Log.d(TAG, "permission allowd");
                BusProvider.getInstance().post(new MapPermissionGrantedEvent());
            } else{
                //set to never ask again
                Log.d(TAG, "permission never ask");

            }
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);

        List<RecoModel> recoList  = recoMapFragment.getData();

        long endSecond = System.currentTimeMillis();
        long residenseTime = endSecond - startSecond;
        if (recoList.size() != 0){
            requestSetRecoLog(
                    TokenRecord.getTokenRecord().getApiKey(),
                    recoList.get(0).eventHashKey,
                    LogType.CATEGORY_VIEW.value,
                    LogType.LABEL_RECO_GOFULLMAP.value,
                    LogType.ACTION_CLICK.value,
                    residenseTime,
                    null
            );
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_filter:
                break;
            case R.id.menu_filter_all:
                BusProvider.getInstance().post(new RecoMapFilterChangeEvent(0));
                break;
            case R.id.menu_filter_restaurant:
                BusProvider.getInstance().post(new RecoMapFilterChangeEvent(1));
                break;
            case R.id.menu_filter_cafe:
                BusProvider.getInstance().post(new RecoMapFilterChangeEvent(2));
                break;
            case R.id.menu_filter_place:
                BusProvider.getInstance().post(new RecoMapFilterChangeEvent(3));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mapfilter, menu);
        return true;
    }

    void requestSetRecoLog (String apikey, String eventHashkey, int category, int label, int action, long residenseTime, String recoHashkey) {
        ApiClient.getService().setRecoLog(
                "세션키 자리야 성민아!!!!!!!",
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
