package io.caly.calyandroid.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import io.caly.calyandroid.Fragment.RecoTabFragment;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public class RecoTabPagerAdapter extends FragmentStatePagerAdapter {

    //로그에 쓰일 tag
    private static final String TAG = RecoTabPagerAdapter.class.getSimpleName();

    Fragment[] fragmentList;

    public RecoTabPagerAdapter(FragmentManager fm) {
        super(fm);

        fragmentList= new Fragment[]{
                new RecoTabFragment(),
                new RecoTabFragment(),
                new RecoTabFragment()
        };
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList[position];
    }

    @Override
    public int getCount() {
        return fragmentList.length;
    }
}
