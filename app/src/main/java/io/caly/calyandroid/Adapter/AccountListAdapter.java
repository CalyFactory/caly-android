package io.caly.calyandroid.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.AccountListActivity;
import io.caly.calyandroid.Model.AccountModel;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 22
 */

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder>  {

    //로그에 쓰일 tag
    private static final String TAG = AccountListAdapter.class.getSimpleName();

    private ArrayList<AccountModel> dataList;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View view){
            super(view);

            ButterKnife.bind(this, view);

        }

    }

    public AccountListAdapter(ArrayList<AccountModel> dataList){
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(TAG, "oncreateviewholder");
        View view = null;
        ViewHolder viewHolder = null;

        if (viewType==1){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_accountlist_header, parent, false);

            viewHolder = new ViewHolder(view);
        }
        else if (viewType ==2){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_accountlist_row, parent, false);

            viewHolder = new ViewHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_accountlist_row, parent, false);

            viewHolder = new ViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).isHeader?1:2;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }




}
