package io.caly.calyandroid.Adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

        @Nullable
        @Bind(R.id.linear_notice_header)
        public LinearLayout linearHeader;

        public ViewHolder(View view){
            super(view);

            ButterKnife.bind(this, view);

        }


    }

    public void addItem(NoticeModel noticeModel){
        dataList.add(noticeModel);
        notifyItemInserted(dataList.size()-1);
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


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NoticeListAdapter.ViewHolder holder, final int position) {
        final NoticeModel noticeModel = dataList.get(position);

        if(noticeModel.isHeader){
            holder.linearHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(noticeModel.isExpandabled){

                        dataList.remove(position + 1);
                        noticeModel.isExpandabled = false;
                        notifyItemRemoved(position + 1);

                    }
                    else{
                        dataList.add(position + 1, new NoticeModel(noticeModel.noticeDescription));
                        noticeModel.isExpandabled = true;
                        notifyItemInserted(position + 1);
                    }
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).isHeader?1:2;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
