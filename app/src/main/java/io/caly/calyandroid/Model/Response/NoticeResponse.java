package io.caly.calyandroid.Model.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.caly.calyandroid.Model.DataModel.NoticeModel;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 4
 */

public class NoticeResponse extends BasicResponse {

    @SerializedName("payload")
    public Payload payload;

    public class Payload {

        @SerializedName("data")
        public List<NoticeModel> data;

    }


}
