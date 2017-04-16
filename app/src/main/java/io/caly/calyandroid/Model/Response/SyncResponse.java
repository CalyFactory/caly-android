package io.caly.calyandroid.Model.Response;

import com.google.gson.annotations.SerializedName;

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

    class Payload{

    }

}
