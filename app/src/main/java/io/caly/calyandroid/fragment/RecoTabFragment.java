package io.caly.calyandroid.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;

import io.caly.calyandroid.adapter.RecommendListAdapter;
import io.caly.calyandroid.fragment.base.BaseFragment;
import io.caly.calyandroid.model.Category;
import io.caly.calyandroid.model.dataModel.EventModel;
import io.caly.calyandroid.model.dataModel.RecoModel;
import io.caly.calyandroid.model.event.RecoListLoadStateChangeEvent;
import io.caly.calyandroid.model.event.RecoListScrollEvent;
import io.caly.calyandroid.model.event.TestEvent;
import io.caly.calyandroid.model.response.RecoResponse;
import io.caly.calyandroid.util.BusProvider;
import io.caly.calyandroid.util.Logger;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.eventListener.RecyclerItemClickListener;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public class RecoTabFragment extends BaseFragment {


    @Bind(R.id.recycler_recommandlist)
    RecyclerView recyclerList;

    @Bind(R.id.tv_reco_nodata)
    TextView tvNodata;

    @Bind(R.id.spinkit_recolist)
    View spinKitView;

    Category category;
    EventModel eventData;

    RecommendListAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;

    public RecoTabFragment() { super(); }

    public RecoTabFragment setEvent(EventModel eventData){
        this.eventData = eventData;
        return this;
    }

    public RecoTabFragment setCategory(Category category){
        this.category = category;
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
            v = inflater.inflate(R.layout.fragment_recolist, null);

            ButterKnife.bind(this, v);

            init();

        }

        return v;
    }

    void init(){

        //set recyclerview
        recyclerList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerList.setLayoutManager(layoutManager);


        ArrayList<RecoModel> dataList = new ArrayList<>();
        recyclerAdapter = new RecommendListAdapter(getContext(), dataList);
        recyclerList.setAdapter(recyclerAdapter);


        recyclerList.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getContext(),
                        recyclerList,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position) {


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }
                )
        );

        recyclerList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy<0){
                    Log.d(TAG, "scroll up");
                    BusProvider.getInstance().post(new RecoListScrollEvent(true));
                }
                else { //scroll down
                    Log.d(TAG, "scroll down");
                    BusProvider.getInstance().post(new RecoListScrollEvent(false));
                }

            }
        });

    }

    void showListLoadAnimation(){
        spinKitView.setVisibility(View.VISIBLE);

    }

    void hideListLoadAnimation(){
        spinKitView.setVisibility(View.GONE);
    }

    @Subscribe
    public void testEventCallback(TestEvent event) {
        Log.d(TAG," event received " );
    }

    @Subscribe
    public void recoListLoadStateChangeEvent(RecoListLoadStateChangeEvent doneEvent) {
        if (doneEvent.category == category) {

            switch (doneEvent.loadingState) {
                case STATE_LOADING:
                      showListLoadAnimation();
                    break;
                case STATE_DONE:
                    hideShimmerAdapter();

                    Response<RecoResponse> response = doneEvent.response;
                    RecoResponse body = response.body();

                    recyclerAdapter.addItems(body.payload.data);
                    if(body.payload.data.size()==0){
                        tvNodata.setVisibility(View.VISIBLE);
                    }

                    break;
                case STATE_EMPTY:
                    tvNodata.setVisibility(View.VISIBLE);
                    break;
                case STATE_ERROR:
                    hideShimmerAdapter();
                    break;
            }
        }
    }

    void hideShimmerAdapter(){
        hideShimmerHandler.sendEmptyMessageDelayed(0,0);
    }

    Handler hideShimmerHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            hideListLoadAnimation();
        }
    };

}
