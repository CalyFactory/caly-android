package io.caly.calyandroid.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import io.caly.calyandroid.Util.Logger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import net.jspiner.prefer.Prefer;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.AccountAddActivity;
import io.caly.calyandroid.Activity.AccountListActivity;
import io.caly.calyandroid.Activity.LegacySettingActivity;
import io.caly.calyandroid.Activity.NoticeActivity;
import io.caly.calyandroid.Activity.SettingActivity;
import io.caly.calyandroid.Activity.SplashActivity;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.Event.SettingLoadingStateChangeEvent;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.DataModel.SettingItemModel;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.ApiClient;
import io.caly.calyandroid.Util.BusProvider;
import io.caly.calyandroid.View.LoginDialog;
import io.caly.calyandroid.View.WithDrawalDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 13
 */

public class SettingListAdapter extends RecyclerView.Adapter<SettingListAdapter.ViewHolder> {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + SettingListAdapter.class.getSimpleName();

    ArrayList<SettingItemModel> dataList = new ArrayList<>();

    public SettingListAdapter(ArrayList<SettingItemModel> dataList){
        this.dataList = dataList;
        Logger.d(TAG," size : " +dataList.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.tv_setting_title)
        TextView tvSettingTitle;

        @Nullable
        @Bind(R.id.tv_setting_desc)
        TextView tvSettingDesc;

        @Nullable
        @Bind(R.id.switch_setting_row)
        SwitchCompat switchRow;

        public int position;

        Context context;

        public ViewHolder(View view, Context context) {
            super(view);

            ButterKnife.bind(this, view);
            this.context = context;
        }

        @Nullable
        @OnClick(R.id.linear_setting_row)
        void onSettingRowClick(){
            switch (position){
                case 0:
                    break;
                case 1:         // 공지사항
                    Intent intent = new Intent(context, NoticeActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    break;
                case 2:         // 문의하기
                    Uri uri = Uri.parse("mailto:contact@landing.caly.io");
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);

                    context.startActivity(it);
                    break;
                /*
                case 5:
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                    marketIntent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
                    context.startActivity(marketIntent);
                    break;*/
                case 5:         // push 설정
                    switchRow.setChecked(!switchRow.isChecked());
                    break;

/*                case 7:
//                    Toast.makeText(context, context.getString(R.string.toast_msg_not_support), Toast.LENGTH_LONG).show();
                    startAccountAddActivity();
                    break;*/
                case 7:
//                    Toast.makeText(context, context.getString(R.string.toast_msg_not_support), Toast.LENGTH_LONG).show();
                    startAccountListActivity();
                    break;
                case 8:
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("정말 로그아웃하시겠습니까?");
                    builder.setTitle("로그아웃");
                    builder.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            BusProvider.getInstance().post(new SettingLoadingStateChangeEvent(true));
                            ApiClient.getService().logout(
                                    TokenRecord.getTokenRecord().getApiKey()
                            ).enqueue(new Callback<BasicResponse>() {
                                @Override
                                public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                    Logger.d(TAG,"onResponse code : " + response.code());
                                    BusProvider.getInstance().post(new SettingLoadingStateChangeEvent(false));

                                    if(response.code() == 200){
                                        BasicResponse body = response.body();

                                        TokenRecord.destoryToken();
                                        Prefer.getSharedPreferences().edit().clear().commit();
                                        Prefer.set("isDidRun", true);

                                        ActivityCompat.finishAffinity((Activity)context);

                                        Intent intent = new Intent(context, SplashActivity.class);
                                        context.startActivity(intent);

                                        signOutGoogle();
                                    }
                                    else{
                                        Logger.e(TAG,"status code : " + response.code());
                                        Toast.makeText(
                                                context,
                                                context.getString(R.string.toast_msg_server_internal_error),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<BasicResponse> call, Throwable t) {

                                    Logger.e(TAG,"onfail : " + t.getMessage());
                                    Logger.e(TAG, "fail " + t.getClass().getName());
                                    BusProvider.getInstance().post(new SettingLoadingStateChangeEvent(false));

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
                    break;
                case 9:
                    WithDrawalDialog withDrawalDialog = new WithDrawalDialog(
                            context,
                            new WithDrawalDialog.DialogCallback() {
                                @Override
                                public void onPositive(WithDrawalDialog dialog, final String content) {
                                    dialog.dismiss();
                                    BusProvider.getInstance().post(new SettingLoadingStateChangeEvent(true));
                                    ApiClient.getService().withdrawal(
                                            TokenRecord.getTokenRecord().getApiKey(),
                                            content
                                    ).enqueue(new Callback<BasicResponse>() {
                                        @Override
                                        public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                                            Logger.d(TAG,"onResponse code : " + response.code());

                                            BusProvider.getInstance().post(new SettingLoadingStateChangeEvent(false));
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
                                            BusProvider.getInstance().post(new SettingLoadingStateChangeEvent(false));

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


                    break;
                default:
                    Logger.d(TAG,"clicked");
                    break;
            }

        }

        @Nullable
        @OnCheckedChanged(R.id.switch_setting_row)
        void onSwitchCheckChanged(){
            Logger.d(TAG,"checked : " + switchRow.isChecked());
            Prefer.set("isPushReceive", switchRow.isChecked());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        ApiClient.getService().setReceivePush(
                                TokenRecord.getTokenRecord().getApiKey(),
                                switchRow.isChecked()?1:0
                        ).execute();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        void startAccountListActivity(){
            Intent intent = new Intent(context, AccountListActivity.class);
            ((Activity)context).startActivityForResult(intent, 1);
            ((Activity)context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }

        void startAccountAddActivity(){
            Intent intent = new Intent(context, AccountAddActivity.class);
            context.startActivity(intent);
            ((Activity)context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }

        void signOutGoogle(){

            Auth.GoogleSignInApi.signOut(LegacySettingActivity.mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Logger.d(TAG,"Signout message : " +status.getStatusMessage());
                        }
                    });
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        ViewHolder viewHolder = null;

        if(viewType==1){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_setting_title, parent, false);
        }
        else if(viewType==2){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_setting_row1, parent, false);
        }
        else if(viewType==3){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_setting_row2, parent, false);
        }
        else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_setting_row1, parent, false);
        }

        viewHolder = new ViewHolder(view, parent.getContext());
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==5) return 3;
        return dataList.get(position).isTitle?1:2;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.position = position;

        if(dataList.get(position).isTitle){
            holder.tvSettingTitle.setText(dataList.get(position).title);
        }
        else{
            holder.tvSettingTitle.setText(dataList.get(position).title);
            holder.tvSettingDesc.setText(dataList.get(position).description);
        }

        if(position==5){
            holder.switchRow.setChecked(Prefer.get("isPushReceive", true));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
