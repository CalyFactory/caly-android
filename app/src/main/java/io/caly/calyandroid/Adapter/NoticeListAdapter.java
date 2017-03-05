package io.caly.calyandroid.Adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.DataModel.NoticeModel;
import io.caly.calyandroid.Model.DataModel.TestModel;
import io.caly.calyandroid.R;

/**
 * Created by jspiner on 2017. 3. 4..
 */

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.ViewHolder>   {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + NoticeListAdapter.class.getSimpleName();

    private ArrayList<NoticeModel> dataList;

    public NoticeListAdapter(ArrayList<NoticeModel> dataList){
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View view){
            super(view);

            ButterKnife.bind(this, view);

        }

        @Nullable
        @OnClick(R.id.linear_notice_header)
        void onHeaderClick(){
            int position = getAdapterPosition();
        }

    }

    @Override
    public NoticeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        NoticeListAdapter.ViewHolder viewHolder = null;

        if (viewType==1){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notice_header, parent, false);

            viewHolder = new NoticeListAdapter.ViewHolder(view);
        }
        else if (viewType ==2){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notice_body, parent, false);

            viewHolder = new NoticeListAdapter.ViewHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notice_body, parent, false);

            viewHolder = new NoticeListAdapter.ViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NoticeListAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
