package io.caly.calyandroid.View;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    private static final String TAG = LoginDialog.class.getSimpleName();

    @Bind(R.id.tvLoginTitle)
    TextView tvLoginTitle;

    @Bind(R.id.edtLoginId)
    EditText edtLoginId;

    @Bind(R.id.edtLoginPw)
    EditText edtLoginPw;

    @Bind(R.id.btnLoginCancel)
    Button btnLoginCancel;

    @Bind(R.id.btnLoginOk)
    Button btnLoginOk;

    String loginTitle;

    LoginDialogCallback dialogCallback;

    public LoginDialog(Context context, String loginTitle, LoginDialogCallback dialogCallback) {
        super(context);

        this.loginTitle = loginTitle;
        this.dialogCallback = dialogCallback;
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

        //init layout
        tvLoginTitle.setText(loginTitle);

    }

    @OnClick(R.id.btnLoginOk)
    void onLoginOkClick(){
        dialogCallback.onPositive(
                this,
                edtLoginId.getText().toString(),
                edtLoginPw.getText().toString()
        );
    }

    @OnClick(R.id.btnLoginCancel)
    void onLoginCancelClick(){
        dialogCallback.onNegative(this);
    }

    public interface LoginDialogCallback {
        public void onPositive(LoginDialog dialog, String userId, String userPw);
        public void onNegative(LoginDialog dialog);
    }

}
