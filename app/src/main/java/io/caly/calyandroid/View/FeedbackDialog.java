package io.caly.calyandroid.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import butterknife.ButterKnife;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 5
 */

public class FeedbackDialog extends Dialog {

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

}
