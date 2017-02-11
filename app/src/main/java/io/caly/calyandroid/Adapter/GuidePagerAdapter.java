package io.caly.calyandroid.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import io.caly.calyandroid.Fragment.GuideItemFragment;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class GuidePagerAdapter extends FragmentStatePagerAdapter {

    Fragment[] fragmentList;

    FragmentManager fragmentManager;

    public GuidePagerAdapter(FragmentManager fm) {
        super(fm);

        this.fragmentManager = fm;
        fragmentList = new Fragment[3];

        fragmentList[0] = new GuideItemFragment();
        fragmentList[1] = new GuideItemFragment();
        fragmentList[2] = new GuideItemFragment();
    }

    @Override
    public int getCount() {
        return fragmentList.length;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList[position];
    }
}
