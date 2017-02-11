package io.caly.calyandroid.View;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.SplashActivity;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class LoginDialog extends Dialog {


    //로그에 쓰일 tag
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Bind(R.id.edtLoginId)
    EditText edtLoginId;

    @Bind(R.id.edtLoginPw)
    EditText edtLoginPw;

    public LoginDialog(Context context) {
        super(context);
    }

    public LoginDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoginDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_login);

        init();
    }

    void init(){

        ButterKnife.bind(this);

        //remove focus
        edtLoginId.clearFocus();
    }
}
