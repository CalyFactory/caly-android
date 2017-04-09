package io.caly.calyandroid.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.LoginActivity;
import io.caly.calyandroid.Fragment.Base.BaseFragment;
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

    @Bind(R.id.imv_guide_text)
    ImageView imvGuideText;

    @Bind(R.id.btn_guide_start)
    Button btnStart;

    @Bind(R.id.imv_guide_indicator1)
    ImageView imvIndicator1;

    @Bind(R.id.imv_guide_indicator2)
    ImageView imvIndicator2;

    @Bind(R.id.imv_guide_indicator3)
    ImageView imvIndicator3;

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
        int textId = 0;
        switch (resourceId){
            case 0:
                imageId = R.drawable.guide_1;
                textId = R.drawable.guide_text_1;

                imvIndicator1.setImageResource(R.drawable.indicator_active);
                break;
            case 1:
                imageId = R.drawable.guide_2;
                textId = R.drawable.guide_text_2;

                imvIndicator2.setImageResource(R.drawable.indicator_active);
                break;
            case 2:
                imageId = R.drawable.guide_3;
                textId = R.drawable.guide_text_3;

                imvIndicator3.setImageResource(R.drawable.indicator_active);

//                tvGuideText.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
                break;
        }

        Picasso.with(getContext())
                .load(imageId)
                .resize(1080,1920)
                .centerInside()
                .into(imvGuide);

        Picasso.with(getContext())
                .load(textId)
                .into(imvGuideText);
    }


    @OnClick(R.id.btn_guide_start)
    void onGuideStartClick(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
}
