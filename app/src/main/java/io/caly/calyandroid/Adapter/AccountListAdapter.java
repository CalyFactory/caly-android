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
import io.caly.calyandroid.Model.DataModel.AccountModel;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.StringFormmater;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 22
 */

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder>  {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + AccountListAdapter.class.getSimpleName();

    private ArrayList<AccountModel> dataList;

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

    public void setData(ArrayList<AccountModel> dataList){
        this.dataList = dataList;
    }

    public AccountListAdapter(ArrayList<AccountModel> dataList){
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
        AccountModel accountModel = dataList.get(position);

        if(accountModel.isHeader){
            holder.tvTitle.setText(accountModel.title);
        }
        else{
            holder.tvTitle.setText(accountModel.userId);
            holder.tvInfo.setText(StringFormmater.accountStateFormat(accountModel));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }




}
