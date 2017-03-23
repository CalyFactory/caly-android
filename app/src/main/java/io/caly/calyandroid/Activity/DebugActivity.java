package io.caly.calyandroid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.EditText;

import net.jspiner.prefer.Prefer;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.Base.BaseAppCompatActivity;
import io.caly.calyandroid.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        edtServer.setText(Prefer.get("app_server", getString(R.string.app_server)));
    }


    @OnClick(R.id.btn_debug_apply)
    void onApplyClick(){
        Prefer.set("app_server", edtServer.getText().toString());
        Log.d(TAG, "server url : " + Prefer.get("app_server", getString(R.string.app_server)));
        restartApp();
    }

    void restartApp(){

        ActivityCompat.finishAffinity((Activity)this);

        Intent intent = new Intent(this, DebugActivity.class);
        startActivity(intent);
    }

}
