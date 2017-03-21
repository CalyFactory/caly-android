package io.caly.calyandroid.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

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
 * @since 17. 3. 14
 */

public class RetrySyncDialog extends Dialog {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + RetrySyncDialog.class.getSimpleName();

    DialogCallback dialogCallback;

    @Bind(R.id.edt_withdrawal_content)
    EditText edtContent;

    public RetrySyncDialog(Context context, DialogCallback dialogCallback) {
        super(context);

        this.dialogCallback = dialogCallback;
    }

    public RetrySyncDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RetrySyncDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_withdrawal);

        init();
    }

    void init(){

        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_login_ok)
    void onLoginOkClick(){
        if(edtContent.getText().toString().length()>10){
            dialogCallback.onPositive(
                    this,
                    edtContent.getText().toString()
            );
        }
        else{
            Toast.makeText(
                    getContext(),
                    getContext().getString(R.string.toast_msg_withdrawal_lengh_not_enough),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @OnClick(R.id.btn_login_cancel)
    void onLoginCancelClick(){
        dialogCallback.onNegative(this);
    }



    public interface DialogCallback {
        public void onPositive(RetrySyncDialog dialog, String content);
        public void onNegative(RetrySyncDialog dialog);
    }
}
