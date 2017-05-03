package io.caly.calyandroid.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import io.caly.calyandroid.util.Logger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.jspiner.prefer.Prefer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.activity.SplashActivity;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.model.dataModel.AccountModel;
import io.caly.calyandroid.model.event.AccountListLoadingEvent;
import io.caly.calyandroid.model.event.AccountListRefreshEvent;
import io.caly.calyandroid.model.event.EventListRefreshEvent;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.model.response.SyncResponse;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.BusProvider;
import io.caly.calyandroid.util.StringFormmater;
import io.caly.calyandroid.view.WithDrawalDialog;
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
            holder.tvInfo.setText(StringFormmater.accountStateFormat(accountModel.latestSyncTime));
            if(accountModel.loginPlatform.equals("google")){
                holder.imvSync.setVisibility(View.INVISIBLE);
            }
            else{
                holder.imvSync.setVisibility(View.VISIBLE);
            }
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
                            accountModel.userId,
                            accountModel.loginPlatform
                    ).enqueue(new Callback<SyncResponse>() {
                        @Override
                        public void onResponse(Call<SyncResponse> call, Response<SyncResponse> response) {

                            holder.imvSync.clearAnimation();
                            Logger.d(TAG,"onResponse code : " + response.code());
                            SyncResponse body = response.body();
                            switch (response.code()){
                                case 200:
                                    BusProvider.getInstance().post(new EventListRefreshEvent());
                                    holder.tvInfo.setText(StringFormmater.accountStateFormat(body.payload.data.latestSyncTime));
                                    Toast.makeText(
                                            context,
                                            context.getString(R.string.toast_msg_sync_done),
                                            Toast.LENGTH_LONG
                                    ).show();
                                    break;
                                default:
                                    Logger.e(TAG,"status code : " + response.code());
                                    Toast.makeText(
                                            context,
                                            context.getString(R.string.toast_msg_server_internal_error),
                                            Toast.LENGTH_LONG
                                    ).show();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<SyncResponse> call, Throwable t) {
                            Logger.e(TAG,"onfail : " + t.getMessage());
                            Logger.e(TAG, "fail " + t.getClass().getName());

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

                    int count = 0;
                    for(int i=0;i<dataList.size();i++){
                        if(!dataList.get(i).isHeader) count++;
                    }
                    if(count==1){
                        showWithDrawalDialog();
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("캘린더 계정을 제거하시겠습니까?");
                    builder.setTitle("캘린더 계정 제거");
                    builder.setPositiveButton("제거", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            BusProvider.getInstance().post(new AccountListLoadingEvent(true));
                            ApiClient.getService().removeAccount(
                                    TokenRecord.getTokenRecord().getApiKey(),
                                    accountModel.loginPlatform,
                                    accountModel.userId
                            ).enqueue(new Callback<BasicResponse>() {
                                @Override
                                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                    Logger.d(TAG,"onResponse code : " + response.code());

                                    BusProvider.getInstance().post(new AccountListLoadingEvent(false));

                                    BasicResponse body = response.body();
                                    switch (response.code()){
                                        case 200:
                                            BusProvider.getInstance().post(new EventListRefreshEvent());

                                            Toast.makeText(
                                                    context,
                                                    context.getString(R.string.toast_msg_account_remove_success),
                                                    Toast.LENGTH_LONG
                                            ).show();

                                            BusProvider.getInstance().post(new AccountListRefreshEvent());

                                            break;
                                        default:
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
                                    Logger.d(TAG,"onfail : " + t.getMessage());
                                    Logger.d(TAG, "fail " + t.getClass().getName());

                                    BusProvider.getInstance().post(new AccountListLoadingEvent(false));

                                    Toast.makeText(
                                            context,
                                            context.getString(R.string.toast_msg_network_error),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            });

                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();

                }
            });
        }
    }

    void showWithDrawalDialog(){



        WithDrawalDialog withDrawalDialog = new WithDrawalDialog(
                context, true,
                new WithDrawalDialog.DialogCallback() {
                    @Override
                    public void onPositive(WithDrawalDialog dialog, final String content) {
                        dialog.dismiss();
                        BusProvider.getInstance().post(new AccountListLoadingEvent(true));
                        ApiClient.getService().withdrawal(
                                TokenRecord.getTokenRecord().getApiKey(),
                                content
                        ).enqueue(new Callback<BasicResponse>() {
                            @Override
                            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                Logger.d(TAG,"onResponse code : " + response.code());
                                BusProvider.getInstance().post(new AccountListLoadingEvent(false));

                                BasicResponse body = response.body();
                                switch (response.code()){
                                    case 200:

                                        TokenRecord.destoryToken();
                                        Prefer.getSharedPreferences().edit().clear().commit();
                                        Prefer.set("isDidRun", true);

                                        ActivityCompat.finishAffinity((Activity)context);

                                        Intent intent = new Intent(context, SplashActivity.class);
                                        context.startActivity(intent);

                                        Toast.makeText(
                                                context,
                                                context.getString(R.string.toast_msg_withdrawal_success),
                                                Toast.LENGTH_LONG
                                        ).show();


                                        break;
                                    default:
                                        Logger.e(TAG,"status code : " + response.code());
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
                                Logger.e(TAG,"onfail : " + t.getMessage());
                                Logger.e(TAG, "fail " + t.getClass().getName());
                                BusProvider.getInstance().post(new AccountListLoadingEvent(false));


                                Toast.makeText(
                                        context,
                                        context.getString(R.string.toast_msg_network_error),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });

                    }

                    @Override
                    public void onNegative(WithDrawalDialog dialog) {
                        dialog.dismiss();
                    }
                }
        );
        withDrawalDialog.show();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }




}
