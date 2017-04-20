package io.caly.calyandroid.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import io.caly.calyandroid.Util.Logger;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.TestActivity;
import io.caly.calyandroid.CalyApplication;
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
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + GoogleOAuthDialog.class.getSimpleName();

    private LoginCallback loginCallback;

    @Bind(R.id.webview_googleoauth)
    WebView webView;

    private static String REDIRECT_URI = "https://ssoma.xyz:55566/googleAuthCallBack";
    private static String OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private static String OAUTH_SCOPE = "https://www.googleapis.com/auth/calendar " +
            "https://www.googleapis.com/auth/userinfo.email " +
            "https://www.googleapis.com/auth/calendar.readonly";

    public GoogleOAuthDialog(Context context, LoginCallback loginCallback) {
        super(context);

        this.loginCallback = loginCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_googleoauth);

        init();
    }

    void init() {

        ButterKnife.bind(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(
                OAUTH_URL +
                "?redirect_uri=" + REDIRECT_URI +
                "&response_type=code" +
                "&client_id=" + getContext().getString(R.string.google_client_id) +
                "&scope=" + OAUTH_SCOPE +
                "&access_type=offline" +
                "&approval_prompt=force"
        );
        webView.setWebViewClient(new WebViewClient() {

            boolean authComplete = false;
            Intent resultIntent = new Intent();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                Logger.d(TAG, "onPageStarted : " + url);

                if(url.contains("code=") && authComplete != true){
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("code");
                    loginCallback.onLoginSuccess(GoogleOAuthDialog.this, authCode);
                }
                else if(url.contains("error=")){
                    Uri uri = Uri.parse(url);
                    String errorCode = uri.getQueryParameter("error");
                    loginCallback.onLoginFailed(GoogleOAuthDialog.this, errorCode);
                }
                else{
                    super.onPageStarted(view, url, favicon);
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Logger.d(TAG, "onPageFinished : " + url);
            }
        });
    }

    public interface LoginCallback{
        public void onLoginSuccess(Dialog dialog, String code);
        public void onLoginFailed(Dialog dialog, String error);
    }

}
