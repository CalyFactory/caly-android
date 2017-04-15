package io.caly.calyandroid.Adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.DataModel.AccountModel;
import io.caly.calyandroid.Model.DataModel.NoticeModel;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.Response.NoticeResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.StringFormmater;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @Nullable
        @Bind(R.id.tv_account_title)
        TextView tvTitle;

        @Nullable
        @Bind(R.id.tv_account_info)
        TextView tvInfo;

        @Nullable
        @Bind(R.id.imv_account_sync)
        ImageView imvSync;

        @Nullable
        @Bind(R.id.imv_account_delete)
        ImageView imvDelete;

        public ViewHolder(View view){
            super(view);

            ButterKnife.bind(this, view);

        }

    }

    public void setData(ArrayList<AccountModel> dataList){
        this.dataList = dataList;
    }

    public AccountListAdapter(Context context, ArrayList<AccountModel> dataList){
        this.context = context;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AccountModel accountModel = dataList.get(position);

        if(accountModel.isHeader){
            holder.tvTitle.setText(accountModel.title);
        }
        else{
            holder.tvTitle.setText(accountModel.userId);
            holder.tvInfo.setText(StringFormmater.accountStateFormat(accountModel));
            holder.imvSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation a = new RotateAnimation(0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                    a.setRepeatCount(-1);
                    a.setDuration(1000);

                    holder.imvSync.startAnimation(a);

                    ApiClient.getService().caldavManualSync(
                            TokenRecord.getTokenRecord().getApiKey(),
                            accountModel.userId
                    ).enqueue(new Callback<BasicResponse>() {
                        @Override
                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {

                            holder.imvSync.clearAnimation();
                            Log.d(TAG,"onResponse code : " + response.code());
                            BasicResponse body = response.body();
                            switch (response.code()){
                                case 200:
                                    Toast.makeText(
                                            context,
                                            context.getString(R.string.toast_msg_sync_done),
                                            Toast.LENGTH_LONG
                                    ).show();
                                    break;
                                default:
                                    Log.e(TAG,"status code : " + response.code());
                                    Toast.makeText(
                                            context,
                                            context.getString(R.string.toast_msg_server_internal_error),
                                            Toast.LENGTH_LONG
                                    ).show();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<BasicResponse> call, Throwable t) {
                            Log.e(TAG,"onfail : " + t.getMessage());
                            Log.e(TAG, "fail " + t.getClass().getName());

                            holder.imvSync.clearAnimation();
                            Toast.makeText(
                                    context,
                                    context.getString(R.string.toast_msg_network_error),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
                }
            });
            holder.imvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }




}
