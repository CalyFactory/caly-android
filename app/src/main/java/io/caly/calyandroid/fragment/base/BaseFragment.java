package io.caly.calyandroid.fragment.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import io.caly.calyandroid.util.BusProvider;
import io.caly.calyandroid.util.Logger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.caly.calyandroid.CalyApplication;

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

    @Override
    public void onAttach(Context context) {
        Logger.i(TAG, "onAttach");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Logger.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.i(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Logger.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Logger.i(TAG, "onStart");
        super.onStart();

        BusProvider.getInstance().register(this);
    }

    @Override
    public void onResume() {
        Logger.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Logger.i(TAG, "onStop");
        super.onStop();

        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onPause() {
        Logger.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
