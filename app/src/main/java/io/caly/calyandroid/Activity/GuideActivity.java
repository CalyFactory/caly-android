package io.caly.calyandroid.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Adapter.GuidePagerAdapter;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class GuideActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    private static final String TAG = GuideActivity.class.getSimpleName();

    @Bind(R.id.pager_guide)
    ViewPager pagerGuide;

    GuidePagerAdapter guidePagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        guidePagerAdapter = new GuidePagerAdapter(getSupportFragmentManager());
        pagerGuide.setAdapter(guidePagerAdapter);

    }
}
