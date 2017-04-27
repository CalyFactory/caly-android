package io.caly.calyandroid.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;

import io.caly.calyandroid.Model.Event.TestEvent;
import io.caly.calyandroid.Util.Logger;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Adapter.RecommendListAdapter;
import io.caly.calyandroid.Fragment.Base.BaseFragment;
import io.caly.calyandroid.Model.Category;
import io.caly.calyandroid.Model.DataModel.EventModel;
import io.caly.calyandroid.Model.DataModel.RecoModel;
import io.caly.calyandroid.Model.Event.RecoListLoadStateChangeEvent;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.Response.RecoResponse;
import io.caly.calyandroid.Model.TrackingType;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.BusProvider;
import io.caly.calyandroid.Util.EventListener.RecyclerItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
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
    ShimmerRecyclerView recyclerList;

    @Bind(R.id.tv_reco_nodata)
    TextView tvNodata;

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

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Logger.i(TAG, "traking click");

                                        if(recyclerAdapter.getItemCount() - 1 < position){
                                            return;
                                        }

                                        try {
                                            ApiClient.getService().tracking(
                                                    TokenRecord.getTokenRecord().getApiKey(),
                                                    eventData.eventHashKey,
                                                    recyclerAdapter.getItem(position).recoHashKey,
                                                    TrackingType.CLICK.value,
                                                    0
                                            ).execute();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }
                )
        );

    }

    /*
    void loadList(){
        Logger.i(TAG, "loadList");

        if(category == Category.RESTAURANT) {
            recyclerList.showShimmerAdapter();
        }

        ApiClient.getService().getRecoList(
                TokenRecord.getTokenRecord().getApiKey(),
                eventData.eventHashKey,
                category.value
        ).enqueue(new Callback<RecoResponse>() {
            @Override
            public void onResponse(Call<RecoResponse> call, Response<RecoResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());

                RecoResponse body = response.body();
                int dataSize = 0;
                hideShimmerAdapter();

                switch (response.code()){
                    case 200:
                        recyclerAdapter.addItems(body.payload.data);
                        dataSize = body.payload.data.size();
                        if(dataSize==0){
                            tvNodata.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 201: // no data
                        tvNodata.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Logger.e(TAG,"status code : " + response.code());
                        Toast.makeText(
                                getActivity(),
                                getString(R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        ).show();
                        break;
                }

                BusProvider.getInstance().post(
                        new RecoListLoadStateChangeEvent(
                                category,
                                dataSize,
                                true
                        )
                );
            }

            @Override
            public void onFailure(Call<RecoResponse> call, Throwable t) {
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                Toast.makeText(
                        getActivity(),
                        getResources().getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();


                BusProvider.getInstance().post(
                        new RecoListLoadStateChangeEvent(
                                category,
                                0,
                                true
                        )
                );
            }
        });
    }*/

    @Subscribe
    public void testEventCallback(TestEvent event) {
        Log.d(TAG," event received " );
    }

    @Subscribe
    public void recoListLoadStateChangeEvent(RecoListLoadStateChangeEvent doneEvent) {
        if (doneEvent.category == category) {

            switch (doneEvent.loadingState) {
                case STATE_LOADING:
                    if(category == Category.RESTAURANT) {
                        recyclerList.showShimmerAdapter();
                    }
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
            recyclerList.hideShimmerAdapter();
        }
    };

}
