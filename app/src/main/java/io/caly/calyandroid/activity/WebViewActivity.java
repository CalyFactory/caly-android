package io.caly.calyandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.R;
import io.caly.calyandroid.page.base.BaseAppCompatActivity;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.LogType;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
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
 * @since 17. 3. 9
 */

public class WebViewActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;

    @Bind(R.id.web_webview)
    WebView webView;

    @Bind(R.id.loading_webview)
    SpinKitView loadingView;

    String url;

    long startSecond;
    String eventHashKey;
    String recoHashKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        init();
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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startSecond = System.currentTimeMillis();



        url = getIntent().getStringExtra("url");
        Logger.i(TAG,"move url : " + url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getIntent().getStringExtra("url"));
        webView.setWebViewClient(new WebViewClientClass());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if(newProgress>=50){
                    loadingView.setVisibility(View.GONE);
                }
                else{
                    loadingView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        if(getIntent().hasExtra("recoHashKey")){

            long endSecond = System.currentTimeMillis();
            long residenseTime = endSecond - startSecond;
            requestSetRecoLog(
                    TokenRecord.getTokenRecord().getApiKey(),
                    getIntent().getStringExtra("eventHashKey"),
                    LogType.CATEGORY_CELL.value,
                    LogType.LABEL_RECO_DEEPLINK.value,
                    LogType.ACTION_CLICK.value,
                    residenseTime,
                    getIntent().getStringExtra("recoHashKey")
            );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recodetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_recodetail_share:
                requestSetRecoLog(
                        TokenRecord.getTokenRecord().getApiKey(),
                        getIntent().getStringExtra("eventHashKey"),
                        LogType.CATEGORY_CELL.value,
                        LogType.LABEL_RECO_SHARE_KAKAO_INBLOG.value,
                        LogType.ACTION_CLICK.value,
                        NULL,
                        getIntent().getStringExtra("recoHashKey")
                );
                String[] snsList = {
                    "com.kakao.talk", //kakaotalk
                };
                boolean sended = false;
                for(String snsPackage : snsList){
                    if(Util.isPackageInstalled(snsPackage)){

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,"[캘리] 여기어때요? \n" + url);
                        intent.setPackage("com.kakao.talk");

                        startActivity(intent);
                        sended = true;
                    }
                }
                if(!sended){
                    Toast.makeText(getBaseContext(), "공유 할 수 있는 SNS가 설치 되어있지 않습니다.",Toast.LENGTH_LONG).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private class WebViewClientClass extends WebViewClient {



        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    void requestSetRecoLog (String apikey, String eventHashkey, int category, int label, int action, long residenseTime, String recoHashkey){
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
                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }

                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

            }
        });
    }

}
