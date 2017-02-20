package io.caly.calyandroid.Model;

import com.google.gson.annotations.SerializedName;

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
    public String startDateTime;

    @SerializedName("end_dt")
    public Date endDateTime;

    @SerializedName("summary")
    public String summaryText;

    // TODO : recurrance 객체 만들기
    @SerializedName("recurrance")
    public String recurrance;

}
