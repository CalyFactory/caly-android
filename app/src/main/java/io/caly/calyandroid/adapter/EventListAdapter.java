package io.caly.calyandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.LogType;
import io.caly.calyandroid.model.dataModel.EventModel;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.StringFormmater;
import io.caly.calyandroid.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 12
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    //로그에 쓰일 tag
    public final String TAG = CalyApplication.class.getSimpleName() + "/" + this.getClass().getSimpleName();

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
        @Bind(R.id.linear_eventlist_row)
        LinearLayout cardRow;

        @Nullable
        @Bind(R.id.tv_eventrow_yearmonth)
        TextView tvYearMonth;

        @Nullable
        @Bind(R.id.linear_eventrow_state)
        FrameLayout linearState;

        @Nullable
        @Bind(R.id.linear_eventrow_ripple)
        LinearLayout linearRipple;

        @Nullable
        @Bind(R.id.tv_eventrow_state)
        TextView tvState;

        @Nullable
        @Bind(R.id.imv_eventrow_state)
        ImageView imvState;

        @Nullable
        @Bind(R.id.tv_eventrow_recocount)
        TextView tvRecoCount;

        @Nullable
        @Bind(R.id.imv_eventrow_unknown)
        ImageView imvUnknown;

        @Nullable
        @Bind(R.id.linear_eventrow_row)
        LinearLayout linearRow;

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final EventModel eventModel = dataList.get(position);

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
                    holder.tvEventDayString.setVisibility(View.VISIBLE);
                    holder.tvEventDay.setVisibility(View.VISIBLE);
                }
                else{
                    holder.tvEventDayString.setVisibility(View.INVISIBLE);
                    holder.tvEventDay.setVisibility(View.INVISIBLE);
                }

                //주말
                int dayOfDate = Util.dayOfDate(eventModel.startYear, eventModel.startMonth, eventModel.startDay);

                switch (dayOfDate){
                    case 6: //토
                        holder.tvEventDay.setTextColor(context.getResources().getColor(R.color.dark_sky_blue_four));
                        break;
                    case 0: //일
                        holder.tvEventDay.setTextColor(context.getResources().getColor(R.color.pale_red));
                        break;
                    default:
                        holder.tvEventDay.setTextColor(context.getResources().getColor(R.color.greyish_brown_two));
                        break;
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

                holder.linearRipple.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        holder.linearRow.dispatchTouchEvent(motionEvent);
                        return false;
                    }
                });
                holder.linearState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //추천완료, 추천중 클릭시만 처리
                        Logger.d(TAG, "onLinearStateClick");

                        if(getItemCount()-1 < position) return;
                        EventModel eventModel = getItem(position);

                        switch (eventModel.recoState){
                            case STATE_BEING_RECOMMEND: //추천중
                                requestSetEventLog (TokenRecord.getTokenRecord().getApiKey(),
                                        eventModel.eventHashKey,
                                        LogType.CATEGORY_CELL.value,
                                        LogType.LABEL_EVENT_CELL_ANALYZING.value,
                                        LogType.ACTION_CLICK.value);

                                Toast.makeText(context, "캘리가 일정을 분석하는 중이에요", Toast.LENGTH_LONG).show();
                                break;
                            case STATE_DONE_RECOMMEND: //추천완료
                                break;

                            case STATE_NOTHING_TO_RECOMMEND: //추천불가
                                requestSetEventLog (TokenRecord.getTokenRecord().getApiKey(),
                                        eventModel.eventHashKey,
                                        LogType.CATEGORY_CELL.value,
                                        LogType.LABEL_EVENT_CELL_QUESTIONMARK.value,
                                        LogType.ACTION_CLICK.value);
                                Toast.makeText(context, "일정 정보가 부족하여 추천 하기 어려워요 ㅜㅜ", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });

                switch (eventModel.recoState){
                    case STATE_BEING_RECOMMEND:
                        holder.linearState.setBackgroundColor(ContextCompat.getColor(context, R.color.white_two));
                        holder.tvState.setText("분석중");
                        holder.tvRecoCount.setText("");
                        holder.imvState.setImageResource(R.drawable.ic_message);
                        holder.imvState.setVisibility(View.VISIBLE);
                        holder.imvUnknown.setVisibility(View.GONE);
                        break;
                    case STATE_NOTHING_TO_RECOMMEND:
                        holder.imvUnknown.setVisibility(View.VISIBLE);
                        holder.linearState.setBackgroundColor(Color.TRANSPARENT);
                        holder.imvState.setVisibility(View.GONE);
                        holder.tvState.setText("");
                        holder.tvRecoCount.setText("");
                        break;
                    case STATE_DONE_RECOMMEND:
                        holder.linearState.setBackgroundColor(ContextCompat.getColor(context, R.color.tealish));
                        holder.tvState.setText("추천완료");
                        holder.tvRecoCount.setText("" + eventModel.totalRecoCnt);
                        holder.imvState.setImageResource(R.drawable.oval_3);
                        holder.imvState.setVisibility(View.VISIBLE);
                        holder.imvUnknown.setVisibility(View.GONE);
                        break;
                }
                break;
            case 2:
                holder.tvYearMonth.setText(
                                eventModel.startMonth + "월"
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
    void requestSetEventLog (String apikey, String eventHashkey, int category, int label, int action) {
        ApiClient.getService().setEventLog(
                "세션키 자리야 성민아!!!!!!!",
                apikey,
                eventHashkey,
                category,
                label,
                action
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());
                Logger.d(TAG, "param" + Util.requestBodyToString(call.request().body()));

                switch (response.code()){
                    case 200:
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

            }
        });
    }

}
