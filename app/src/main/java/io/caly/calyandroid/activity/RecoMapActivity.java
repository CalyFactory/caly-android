package io.caly.calyandroid.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.R;
import io.caly.calyandroid.activity.base.BaseAppCompatActivity;
import io.caly.calyandroid.fragment.RecoListFragment;
import io.caly.calyandroid.fragment.RecoMapFragment;
import io.caly.calyandroid.model.dataModel.RecoListWrapModel;
import io.caly.calyandroid.model.dataModel.RecoModel;
import io.caly.calyandroid.util.Util;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by jspiner on 2017. 5. 6..
 */

public class RecoMapActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    RecoMapFragment recoMapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomap);

        init();
    }

    void init() {
        ButterKnife.bind(this);

        //set toolbar
        toolbar.setTitle("지도로 보기");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        Util.centerToolbarTitle(toolbar);
        Util.setToolbarFontSize(toolbar, 18);
        TypefaceUtils.load(getResources().getAssets(), getString(R.string.font_nanum_extra_bold));

        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String intentData = intent.getStringExtra("data");

        List<RecoModel> recoList = new Gson().fromJson(intentData, RecoListWrapModel.class).recoList;

        recoMapFragment = new RecoMapFragment().setData(recoList);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.linear_recomap_container, recoMapFragment);
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
