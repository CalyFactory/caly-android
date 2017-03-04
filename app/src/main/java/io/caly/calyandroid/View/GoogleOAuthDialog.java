package io.caly.calyandroid.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.TestActivity;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 3
 */

public class GoogleOAuthDialog extends Dialog {

    //로그에 쓰일 tag
    private static final String TAG = GoogleOAuthDialog.class.getSimpleName();

    @Bind(R.id.webview_googleoauth)
    WebView webView;

    private static String REDIRECT_URI = "https://ssoma.xyz:55566/googleAuthCallBack";
    private static String OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private static String OAUTH_SCOPE = "https://www.googleapis.com/auth/calendar " +
            "https://www.googleapis.com/auth/userinfo.email " +
            "https://www.googleapis.com/auth/calendar.readonly"
            ;

    public GoogleOAuthDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_googleoauth);

        init();
    }

    void init() {

        ButterKnife.bind(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(
                OAUTH_URL +
                "?redirect_uri=" + REDIRECT_URI +
                "&response_type=code&client_id=" + getContext().getString(R.string.google_client_id) +
                "&scope=" + OAUTH_SCOPE +
                "&access_type=offline" +
                "&state=" + TokenRecord.getTokenRecord().getApiKey()
        );
        webView.setWebViewClient(new WebViewClient() {

            boolean authComplete = false;
            Intent resultIntent = new Intent();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.d(TAG, "onPageFinished : " + url);

                /*
                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    Log.i(TAG, "CODE : " + authCode);
                    authComplete = true;
                    resultIntent.putExtra("code", authCode);
                    TestActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                    setResult(Activity.RESULT_CANCELED, resultIntent);

                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("Code", authCode);
                    edit.commit();
                    auth_dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Authorization Code is: " +authCode, Toast.LENGTH_SHORT).show();
                }else if(url.contains("error=access_denied")){
                    Log.i(TAG, "ACCESS_DENIED_HERE");
                    resultIntent.putExtra("code", authCode);
                    authComplete = true;
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();

                    auth_dialog.dismiss();
                }*/
            }
        });
    }

}
