package io.caly.calyandroid.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.LoginActivity;
import io.caly.calyandroid.Adapter.RecommandListAdapter;
import io.caly.calyandroid.Fragment.base.BaseFragment;
import io.caly.calyandroid.Model.Category;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.TrackingType;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.EventListener.RecyclerItemClickListener;

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

    Category category;

    RecommandListAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;


    public RecoTabFragment() { super(); }

    public Fragment setCategory(Category category){
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


        ArrayList<Object> dataList = new ArrayList<>();
        for(int i=0;i<50;i++){
            dataList.add(new Object());
        }
        recyclerAdapter = new RecommandListAdapter(dataList);
        recyclerList.setAdapter(recyclerAdapter);


        recyclerList.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getContext(),
                        recyclerList,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i(TAG, "traking click");
                                        try {
                                            ApiClient.getService().tracking(
                                                    TokenRecord.getTokenRecord().getApiKey(),
                                                    "eventhashkey",
                                                    "recohashkey",
                                                    TrackingType.CLICK
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

}
