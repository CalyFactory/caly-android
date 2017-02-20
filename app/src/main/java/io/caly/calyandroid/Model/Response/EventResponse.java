package io.caly.calyandroid.Model.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.caly.calyandroid.Model.EventModel;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 20
 */

public class EventResponse extends BasicResponse {

    @SerializedName("payload")
    public List<EventModel> payload;

}
