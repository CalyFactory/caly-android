package io.caly.calyandroid.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 20
 */

public class EventModel {

    @SerializedName("event_hashkey")
    public String eventHashKey;

    @SerializedName("calendar_name")
    public String calendarName;

    @SerializedName("created_dt")
    public Date createdDateTime;

    @SerializedName("start_dt")
    public Date startDateTime;

    @SerializedName("end_dt")
    public Date endDateTime;

    @SerializedName("summary")
    public String summaryText;

    // TODO : recurrance 객체 만들기
    @SerializedName("recurrance")
    public String recurrance;


    //start datetime
    @Expose
    public int startYear;

    @Expose
    public int startMonth;

    @Expose
    public int startDay;

    @Expose
    public int startHour;

    @Expose
    public int startMinute;

    @Expose
    public int startSecond;

    //end datetime
    @Expose
    public int endYear;

    @Expose
    public int endMonth;

    @Expose
    public int endDay;

    @Expose
    public int endHour;

    @Expose
    public int endMinute;

    @Expose
    public int endSecond;


    public EventModel(){

    }

    public void setData(){

        try {
            //end datetime
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDateTime);
            this.startYear = startCalendar.get(Calendar.YEAR);
            this.startMonth = startCalendar.get(Calendar.MONTH) + 1;
            this.startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
            this.startHour = startCalendar.get(Calendar.HOUR);
            this.startMinute = startCalendar.get(Calendar.MINUTE);
            this.startSecond = startCalendar.get(Calendar.SECOND);

            //end datetime
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDateTime);
            this.endYear = endCalendar.get(Calendar.YEAR);
            this.endMonth = endCalendar.get(Calendar.MONTH) + 1;
            this.endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
            this.endHour = endCalendar.get(Calendar.HOUR);
            this.endMinute = endCalendar.get(Calendar.MINUTE);
            this.endSecond = endCalendar.get(Calendar.SECOND);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
