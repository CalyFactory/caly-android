package io.caly.calyandroid.contract.base;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 2
 */

public interface BaseView<T> {

        void setPresenter(T presenter);
        void showToast(CharSequence text, int duration);
        void showToast(int resId, int duration);
        void finishActivity();

}
