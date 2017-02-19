package io.caly.calyandroid.Model.Response;


import com.google.gson.annotations.SerializedName;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project Caly
 * @since 17. 2. 11
 */

public class BasicResponse {

    @SerializedName("code")
    public int code;

    @SerializedName("payload")
    public SessionResponse.Payload payload;

    public class Payload {

        @SerializedName("msg")
        public String msg;

    }

}





