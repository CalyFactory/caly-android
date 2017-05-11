package io.caly.calyandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.activity.SplashActivity;
import io.caly.calyandroid.fragment.base.BaseFragment;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class GuideItemFragment extends BaseFragment {

    @Bind(R.id.imv_guide_item)
    ImageView imvGuide;

    @Bind(R.id.linear_guide_start)
    LinearLayout btnStart;

    @Bind(R.id.imv_guide_indicator1)
    ImageView imvIndicator1;

    @Bind(R.id.imv_guide_indicator2)
    ImageView imvIndicator2;

    @Bind(R.id.imv_guide_indicator3)
    ImageView imvIndicator3;

    @Bind(R.id.tv_guide_title)
    TextView tvGuideTitle;

    @Bind(R.id.tv_guide_subtitle)
    TextView tvGuideSubTitle;

    int resourceId;

    public GuideItemFragment() {
        super();
    }

    public Fragment setResourceId(int resourceId){
        this.resourceId = resourceId;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;

        if (v == null) {
            v = inflater.inflate(R.layout.fragment_guide, null);

            ButterKnife.bind(this, v);

            init();

        }

        return v;
    }

    void init(){
        int imageId = 0;
        int titleId = 0;
        int subTitleId = 0;
        switch (resourceId){
            case 0:
                imageId = R.drawable.guide_01;
                titleId = R.string.text_guide_title_1;
                subTitleId = R.string.text_guide_subtitle_1;

                imvIndicator1.setImageResource(R.drawable.indicator_active);
                break;
            case 1:
                imageId = R.drawable.guide_02;
                titleId = R.string.text_guide_title_2;
                subTitleId = R.string.text_guide_subtitle_2;

                imvIndicator2.setImageResource(R.drawable.indicator_active);
                break;
            case 2:
                imageId = R.drawable.guide_03;
                titleId = R.string.text_guide_title_3;
                subTitleId = R.string.text_guide_subtitle_3;

                imvIndicator3.setImageResource(R.drawable.indicator_active);

//                tvGuideText.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
                break;
        }

        tvGuideTitle.setText(getString(titleId));
        tvGuideSubTitle.setText(getString(subTitleId));
        Picasso.with(getContext())
                .load(imageId)
                .resize(1080,1920)
                .centerInside()
                .into(imvGuide);
    }


    @OnClick(R.id.linear_guide_start)
    void onGuideStartClick(){
        Intent intent = new Intent(getActivity(), SplashActivity.class);
        startActivity(intent);

        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}
