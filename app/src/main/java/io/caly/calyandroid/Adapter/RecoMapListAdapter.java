package io.caly.calyandroid.Adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 4. 25
 */

public class RecoMapListAdapter extends PagerAdapter{

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + RecoMapListAdapter.class.getSimpleName();

    LayoutInflater inflater;

    public RecoMapListAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;

        view = inflater.inflate(R.layout.item_reco_mapitem, null);

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View)object);
    }
}
