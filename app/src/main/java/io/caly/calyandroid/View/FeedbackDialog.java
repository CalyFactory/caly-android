package io.caly.calyandroid.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 5
 */

public class FeedbackDialog extends Dialog {

    @Bind(R.id.edt_feedback_text)
    EditText edtFeedback;

    DialogCallback callback;

    public FeedbackDialog(Context context, DialogCallback callback){
        super(context);
        this.callback = callback;

    }

    public FeedbackDialog(Context context) {
        super(context);
    }

    public FeedbackDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected FeedbackDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_feedback);

        init();
    }

    void init(){

        ButterKnife.bind(this);


    }

    @OnClick(R.id.btn_login_ok)
    void onLoginOkClick(){
        callback.onPositive(
                this,
                edtFeedback.getText().toString()
        );
    }

    @OnClick(R.id.btn_login_cancel)
    void onLoginCancelClick(){
        callback.onNegative(this);
    }

    public interface DialogCallback {
        public void onPositive(FeedbackDialog dialog, String contents);
        public void onNegative(FeedbackDialog dialog);
    }

}
