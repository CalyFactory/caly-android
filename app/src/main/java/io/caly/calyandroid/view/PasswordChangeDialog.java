package io.caly.calyandroid.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class PasswordChangeDialog extends Dialog {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + PasswordChangeDialog.class.getSimpleName();

    @Bind(R.id.tv_login_title)
    TextView tvLoginTitle;

    @Bind(R.id.edt_login_id)
    EditText edtLoginId;

    @Bind(R.id.edt_login_pw)
    EditText edtLoginPw;

    @Bind(R.id.btn_login_cancel)
    Button btnLoginCancel;

    @Bind(R.id.btn_login_ok)
    Button btnLoginOk;

    String loginTitle;
    String userId;

    LoginDialogCallback dialogCallback;

    public PasswordChangeDialog(Context context, String loginTitle, String userId, LoginDialogCallback dialogCallback) {
        super(context);

        this.userId = userId;
        this.loginTitle = loginTitle;
        this.dialogCallback = dialogCallback;
    }

    public PasswordChangeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PasswordChangeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_change_password);

        init();
    }

    void init(){

        ButterKnife.bind(this);

        //remove focus
        edtLoginId.clearFocus();
        edtLoginId.setText(userId);

        //init layout
        tvLoginTitle.setText(loginTitle);

    }

    @OnClick(R.id.btn_login_ok)
    void onLoginOkClick(){
        dialogCallback.onPositive(
                this,
                edtLoginId.getText().toString(),
                edtLoginPw.getText().toString()
        );
    }

    @OnClick(R.id.btn_login_cancel)
    void onLoginCancelClick(){
        dialogCallback.onNegative(this);
    }

    public interface LoginDialogCallback {
        public void onPositive(PasswordChangeDialog dialog, String userId, String userPw);
        public void onNegative(PasswordChangeDialog dialog);
    }

}
