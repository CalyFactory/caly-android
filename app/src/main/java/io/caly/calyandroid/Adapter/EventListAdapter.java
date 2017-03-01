package io.caly.calyandroid.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Model.DataModel.EventModel;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.StringFormmater;
import io.caly.calyandroid.Util.Util;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 12
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private ArrayList<EventModel> dataList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_eventrow_day)
        TextView tvEventDay;

        @Bind(R.id.tv_eventrow_daystring)
        TextView tvEventDayString;

        @Bind(R.id.tv_eventrow_summary)
        TextView tvEventSummary;

        @Bind(R.id.tv_eventrow_date)
        TextView tvEventDate;

        @Bind(R.id.tv_eventrow_location)
        TextView tvEventLocation;

        @Bind(R.id.line_eventlist)
        View viewIndicator;

        public ViewHolder(View view){
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public EventListAdapter(ArrayList<EventModel> dataList){
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_eventlist_row1, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public void addTail(EventModel data){
        dataList.add(data);
    }

    public void addHead(EventModel data){
        dataList.add(0, data);
    }

    public void addItem(int position, EventModel data){
        dataList.add(position, data);
        notifyItemInserted(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventModel eventModel = dataList.get(position);

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
            holder.viewIndicator.setVisibility(View.VISIBLE);
            holder.tvEventDayString.setVisibility(View.VISIBLE);
            holder.tvEventDay.setVisibility(View.VISIBLE);
        }
        else{
            holder.viewIndicator.setVisibility(View.INVISIBLE);
            holder.tvEventDayString.setVisibility(View.INVISIBLE);
            holder.tvEventDay.setVisibility(View.INVISIBLE);
        }

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
        holder.tvEventLocation.setText("위치정보가엄어영");

    }

    public EventModel getItem(int position){
        return dataList.get(position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
