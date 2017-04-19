package io.caly.calyandroid.View;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
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

public class WithDrawalDialog extends Dialog {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + WithDrawalDialog.class.getSimpleName();

    DialogCallback dialogCallback;

    @Bind(R.id.edt_withdrawal_content)
    EditText edtContent;

    @Bind(R.id.tv_withdrawal_enable)
    TextView tvEnable;

    boolean enable;

    public WithDrawalDialog(Context context, DialogCallback dialogCallback) {
        super(context);

        this.dialogCallback = dialogCallback;
    }


    public WithDrawalDialog(Context context, boolean enable, DialogCallback dialogCallback) {
        super(context);

        this.enable = enable;
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

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_withdrawal);

        init();
    }

    void init(){

        ButterKnife.bind(this);

        if(enable){
            tvEnable.setVisibility(View.VISIBLE);
        }
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
        public void onPositive(WithDrawalDialog dialog, String content);
        public void onNegative(WithDrawalDialog dialog);
    }
}
