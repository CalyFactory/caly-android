package io.caly.calyandroid.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import butterknife.ButterKnife;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 14
 */

public class WithDrawalDialog extends Dialog {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + WithDrawalDialog.class.getSimpleName();

    DialogCallback dialogCallback;

    public WithDrawalDialog(Context context, DialogCallback dialogCallback) {
        super(context);

        this.dialogCallback = dialogCallback;
    }

    public WithDrawalDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected WithDrawalDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
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

    }




    public interface DialogCallback {
        public void onPositive(LoginDialog dialog, String content);
        public void onNegative(LoginDialog dialog);
    }
}
