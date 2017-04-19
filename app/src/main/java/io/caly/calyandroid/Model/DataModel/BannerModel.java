package io.caly.calyandroid.Model.DataModel;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 4. 18
 */

public class BannerModel {

    @SerializedName("banner_id")
    public int banner_id;

    @SerializedName("title")
    public String title;

    @SerializedName("activationPeriod")
    public Period activationPeriod;

    @SerializedName("action")
    public Action action;

    public class Period{

        @SerializedName("startDate")
        public Date startDate;

        @SerializedName("endDate")
        public Date endDate;
    }

    public class Action{

        @SerializedName("type")
        public String type;

        @SerializedName("to")
        public String to;
    }

}
