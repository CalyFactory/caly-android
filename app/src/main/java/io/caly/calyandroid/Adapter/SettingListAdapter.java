package io.caly.calyandroid.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.Activity.SplashActivity;
import io.caly.calyandroid.Model.ORM.SessionRecord;
import io.caly.calyandroid.Model.SettingItemModel;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 13
 */

public class SettingListAdapter extends RecyclerView.Adapter<SettingListAdapter.ViewHolder> {

    //로그에 쓰일 tag
    private static final String TAG = SettingListAdapter.class.getSimpleName();

    ArrayList<SettingItemModel> dataList = new ArrayList<>();

    public SettingListAdapter(ArrayList<SettingItemModel> dataList){
        this.dataList = dataList;
        Log.d(TAG," size : " +dataList.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.tv_setting_title)
        TextView tvSettingTitle;

        @Nullable
        @Bind(R.id.tv_setting_desc)
        TextView tvSettingDesc;

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
                    break;
                case 2:         // 문의하기
                    Uri uri = Uri.parse("mailto:calyfactory@gmail.com");
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);

                    context.startActivity(it);
                    break;
                case 10:
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("정말 로그아웃하시겠습니까?");
                    builder.setTitle("로그아웃");
                    builder.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            SessionRecord.destorySession();
                            ActivityCompat.finishAffinity((Activity)context);

                            Intent intent = new Intent(context, SplashActivity.class);
                            context.startActivity(intent);
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
                default:
                    Log.d(TAG,"clicked");
                    break;
            }
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
        else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_setting_row1, parent, false);
        }

        viewHolder = new ViewHolder(view, parent.getContext());
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
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
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
