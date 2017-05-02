package io.caly.calyandroid.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 4. 16
 */

public class SyncResponse extends BasicResponse {

    @SerializedName("payload")
    public Payload payload;

    public class Payload{

        @SerializedName("data")
        public Data data;

        public class Data{

            @SerializedName("latestSyncTime")
            public Date latestSyncTime;

        }
    }

}
