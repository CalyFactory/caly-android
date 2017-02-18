package io.caly.calyandroid.Model.Response;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 18
 */

public class SessionResponse {

    @SerializedName("code")
    public int code;

    @SerializedName("payload")
    public Payload payload;

    public class Payload {

        @SerializedName("sessionkey")
        public String sessionKey;

    }
}
