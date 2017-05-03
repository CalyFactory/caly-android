package io.caly.calyandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.model.dataModel.EventModel;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.StringFormmater;
import io.caly.calyandroid.util.Util;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 12
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private ArrayList<EventModel> dataList;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.tv_eventrow_day)
        TextView tvEventDay;

        @Nullable
        @Bind(R.id.tv_eventrow_daystring)
        TextView tvEventDayString;

        @Nullable
        @Bind(R.id.tv_eventrow_summary)
        TextView tvEventSummary;

        @Nullable
        @Bind(R.id.tv_eventrow_date)
        TextView tvEventDate;

        @Nullable
        @Bind(R.id.tv_eventrow_location)
        TextView tvEventLocation;

        @Nullable
        @Bind(R.id.line_eventlist)
        View viewIndicator;

        @Nullable
        @Bind(R.id.linear_eventlist_row)
        LinearLayout cardRow;

        @Nullable
        @Bind(R.id.tv_eventrow_yearmonth)
        TextView tvYearMonth;

        @Nullable
        @Bind(R.id.linear_eventrow_state)
        LinearLayout linearState;

        @Nullable
        @Bind(R.id.tv_eventrow_state)
        TextView tvState;

        @Nullable
        @Bind(R.id.imv_eventrow_state)
        ImageView imvState;

        @Nullable
        @Bind(R.id.tv_eventrow_recocount)
        TextView tvRecoCount;

        public ViewHolder(View view){
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public EventListAdapter(Context context, ArrayList<EventModel> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType){
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_eventlist_row1, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_eventlist_row2, parent, false);
                break;
        }

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public void addTail(EventModel data){
        dataList.add(data);
        notifyItemInserted(dataList.size());
    }

    public void addHead(EventModel data){
        dataList.add(0, data);
        notifyItemInserted(0);
    }

    public void addItem(int position, EventModel data){
        dataList.add(position, data);
        notifyItemInserted(position);
    }

    public void removeAll(){
        dataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).isHeader?2:1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventModel eventModel = dataList.get(position);

        switch (getItemViewType(position)){
            case 1:
                boolean isNewDay = false;
                if(position!=0){
                    EventModel prevModel = dataList.get(position-1);


                    if(prevModel.startYear == eventModel.startYear &&
                            prevModel.startMonth == eventModel.startMonth &&
                            prevModel.startDay == eventModel.startDay){
                        isNewDay = false;
                    }
                    else{
                        isNewDay = true;
                    }
                }
                else{
                    isNewDay = true;
                }

                if (isNewDay){
//                    holder.viewIndicator.setVisibility(View.VISIBLE);
                    holder.tvEventDayString.setVisibility(View.VISIBLE);
                    holder.tvEventDay.setVisibility(View.VISIBLE);
                }
                else{
//                    holder.viewIndicator.setVisibility(View.INVISIBLE);
                    holder.tvEventDayString.setVisibility(View.INVISIBLE);
                    holder.tvEventDay.setVisibility(View.INVISIBLE);
                }

                //주말
                int dayOfDate = Util.dayOfDate(eventModel.startYear, eventModel.startMonth, eventModel.startDay);
                if (dayOfDate == 0 || dayOfDate == 6){
                    holder.tvEventDayString.setTextColor(Color.rgb(223,115,101));
                }
                else{
                    holder.tvEventDayString.setTextColor(Color.rgb(111,111,111));
                }
                holder.tvEventDayString.setText(Util.dayOfDate[dayOfDate]);

                holder.tvEventDay.setText(String.valueOf(eventModel.startDay));
                holder.tvEventSummary.setText(eventModel.summaryText);
                holder.tvEventDate.setText(
                        StringFormmater.simpleRangeTimeFormat(
                                eventModel.startDateTime,
                                eventModel.endDateTime
                        )
                );
                if(eventModel.location == null){
                    holder.tvEventLocation.setText("위치정보가 없습니다.");
                }
                else{
                    holder.tvEventLocation.setText(eventModel.location);
                }

                switch (eventModel.recoState){
                    case STATE_BEING_RECOMMEND:
                        holder.linearState.setBackgroundColor(ContextCompat.getColor(context, R.color.white_two));
                        holder.tvState.setText("추천중");
                        holder.tvRecoCount.setText("");
                        holder.imvState.setImageResource(R.drawable.ic_message);
                        break;
                    case STATE_NOTHING_TO_RECOMMEND:
                        holder.linearState.setBackgroundColor(ContextCompat.getColor(context, R.color.white_two));
                        holder.tvState.setText("일정확인");
                        holder.tvRecoCount.setText("");
                        holder.imvState.setImageResource(R.drawable.question_mark);
                        break;
                    case STATE_DONE_RECOMMEND:
                        holder.linearState.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_sky_blue));
                        holder.tvState.setText("추천완료");
                        holder.tvRecoCount.setText("" + eventModel.totalRecoCnt);
                        holder.imvState.setImageResource(R.drawable.oval_3);
                        break;
                }
                break;
            case 2:
                holder.tvYearMonth.setText(
                        StringFormmater.monthFormat(
                                eventModel.startMonth
                        )
                );
                break;
        }

    }

    public EventModel getItem(int position){
        return dataList.get(position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}