package io.caly.calyandroid.Model;


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
    public String payload;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}





