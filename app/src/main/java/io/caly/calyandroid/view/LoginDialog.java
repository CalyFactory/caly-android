package io.caly.calyandroid.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.TransformationMethod;
import android.view.View;
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

public class LoginDialog extends Dialog {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + LoginDialog.class.getSimpleName();

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

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_login);

        init();
    }

    void init(){

        ButterKnife.bind(this);

        //remove focus
        edtLoginId.clearFocus();

        //init layout
        tvLoginTitle.setText(loginTitle);

        edtLoginPw.setTransformationMethod(new TransformationMethod() {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {
                return new PasswordCharSequence(source);
            }

            class PasswordCharSequence implements CharSequence {
                private CharSequence mSource;
                public PasswordCharSequence(CharSequence source) {
                    mSource = source; // Store char sequence
                }
                public char charAt(int index) {
                    return '*'; // This is the important part
                }
                public int length() {
                    return mSource.length(); // Return default
                }
                public CharSequence subSequence(int start, int end) {
                    return mSource.subSequence(start, end); // Return default
                }
            }
            @Override
            public void onFocusChanged(View view, CharSequence charSequence, boolean b, int i, Rect rect) {

            }
        });

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
        public void onPositive(LoginDialog dialog, String userId, String userPw);
        public void onNegative(LoginDialog dialog);
    }

}
