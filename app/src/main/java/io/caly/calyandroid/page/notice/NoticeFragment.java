package io.caly.calyandroid.page.notice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.R;
import io.caly.calyandroid.model.dataModel.NoticeModel;
import io.caly.calyandroid.page.base.BaseFragment;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 16
 */

public class NoticeFragment extends BaseFragment implements NoticeContract.View{

    NoticeContract.Presenter presenter;

    @Bind(R.id.recycler_notice)
    RecyclerView recyclerList;

    @Bind(R.id.linear_loading_parent)
    LinearLayout linearLoading;

    NoticeListAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;

    public static NoticeFragment getInstance(){
        return new NoticeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notice, container, false);

        ButterKnife.bind(this, view);

        init();
        return view;
    }

    void init(){

        //set recycler view
        recyclerList.setHasFixedSize(true);

        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerList.setLayoutManager(layoutManager);

        // test data
        ArrayList<NoticeModel> dataList = new ArrayList<>();

        recyclerAdapter = new NoticeListAdapter(dataList);
        recyclerList.setAdapter(recyclerAdapter);

        presenter.loadNotice();
    }

    @Override
    public void setPresenter(NoticeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void changeProgressState(boolean isLoading) {
        if(isLoading){
            linearLoading.setVisibility(View.VISIBLE);
        }
        else{
            linearLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void addListItem(NoticeModel noticeModel) {
        recyclerAdapter.addItem(noticeModel);
    }
}
