package io.caly.calyandroid.Adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 22
 */

public class AccountAddAdapter extends RecyclerView.Adapter<AccountAddAdapter.ViewHolder>  {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + AccountAddAdapter.class.getSimpleName();

    private ArrayList<String> dataList;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @Nullable
        @Bind(R.id.tv_account_title)
        TextView tvTitle;

        @Nullable
        @Bind(R.id.tv_account_info)
        TextView tvInfo;

        public ViewHolder(View view){
            super(view);

            ButterKnife.bind(this, view);

        }


    }

    public void setData(ArrayList<String> dataList){
        this.dataList = dataList;
    }

    public AccountAddAdapter(ArrayList<String> dataList){
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(TAG, "oncreateviewholder");
        View view = null;
        ViewHolder viewHolder = null;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accountlist_header, parent, false);

        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvTitle.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }




}
