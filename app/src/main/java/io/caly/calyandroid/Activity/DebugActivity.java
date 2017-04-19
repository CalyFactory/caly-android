package io.caly.calyandroid.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import net.jspiner.prefer.Prefer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 23
 */

public class DebugActivity extends BaseAppCompatActivity {

    @Bind(R.id.edt_debug_serverurl)
    EditText edtServer;

    @Bind(R.id.lv_debug_activitylist)
    ListView lvActivityList;

    public static GoogleApiClient mGoogleApiClient;

    private static final Class[] classList = new Class[]{
            SplashActivity.class,
            LoginActivity.class,
            SignupActivity.class,
            EventListActivity.class,
            RecommendListActivity.class,
            AccountAddActivity.class,
            AccountListActivity.class,
            GuideActivity.class,
            LegacySettingActivity.class,
            MapActivity.class,
            NoticeActivity.class,
            PolicyActivity.class,
            LegacySettingActivity.class,
            TestActivity.class,
            WebViewActivity.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        edtServer.setText(Prefer.get("app_server", getString(R.string.app_server)));

        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, classList);

        lvActivityList.setAdapter(adapter);
        lvActivityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Class clicked = classList[i];
                startActivity(new Intent(DebugActivity.this, clicked));

            }
        });


        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestServerAuthCode(getString(R.string.google_client_id), true)
                        .requestIdToken(getString(R.string.google_client_id))
                        .requestScopes(
                                new Scope("https://www.googleapis.com/auth/calendar"),
                                new Scope("https://www.googleapis.com/auth/userinfo.email"),
                                new Scope("https://www.googleapis.com/auth/calendar.readonly")
                        ).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
    }


    @OnClick(R.id.btn_debug_apply)
    void onApplyClick(){
        Prefer.set("app_server", edtServer.getText().toString());
        Log.d(TAG, "server url : " + Prefer.get("app_server", getString(R.string.app_server)));
        ApiClient.resetService();
        restartApp();
    }

    @OnClick(R.id.btn_debug_server_1)
    void onApplyClick1(){
        Prefer.set("app_server", "https://caly.io/");
        Log.d(TAG, "server url : " + Prefer.get("app_server", getString(R.string.app_server)));
        ApiClient.resetService();
        restartApp();
    }

    @OnClick(R.id.btn_debug_server_2)
    void onApplyClick2(){
        Prefer.set("app_server", "https://devapi.caly.io:55565/");
        Log.d(TAG, "server url : " + Prefer.get("app_server", getString(R.string.app_server)));
        ApiClient.resetService();
        restartApp();
    }

    @OnClick(R.id.btn_debug_server_3)
    void onApplyClick3(){
        Prefer.set("app_server", "https://devapi.caly.io:55567/");
        Log.d(TAG, "server url : " + Prefer.get("app_server", getString(R.string.app_server)));
        ApiClient.resetService();
        restartApp();
    }

    @OnClick(R.id.btn_debug_logout)
    void onLogout(){

        TokenRecord.destoryToken();

        signOutGoogle();

        restartApp();
    }

    void signOutGoogle(){

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG,"Signout message : " +status.getStatusMessage());
                    }
                });


    }

    void restartApp(){

        ActivityCompat.finishAffinity((Activity)this);

        Intent intent = new Intent(this, ActivityLauncher.class);
        startActivity(intent);
    }

    public static class MyArrayAdapter extends ArrayAdapter<Class> {

        private Context mContext;
        private Class[] mClasses;

        public MyArrayAdapter(Context context, int resource, Class[] objects) {
            super(context, resource, objects);

            mContext = context;
            mClasses = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }

            ((TextView) view.findViewById(android.R.id.text1)).setText(mClasses[position].getSimpleName());
            ((TextView) view.findViewById(android.R.id.text2)).setText("activity 이동");

            return view;
        }

    }
}
