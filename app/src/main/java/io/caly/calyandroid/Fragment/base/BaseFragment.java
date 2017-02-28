package io.caly.calyandroid.Fragment.base;


import android.support.v4.app.Fragment;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Fragment.GuideItemFragment;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public class BaseFragment extends Fragment {

    //로그에 쓰일 tag
    public final String TAG = CalyApplication.class.getSimpleName() + "/" + this.getClass().getSimpleName();

}
