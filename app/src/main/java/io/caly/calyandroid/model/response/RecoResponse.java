package io.caly.calyandroid.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.caly.calyandroid.model.dataModel.RecoModel;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public class RecoResponse extends BasicResponse {

    @SerializedName("payload")
    public Payload payload;

    public class Payload {

        @SerializedName("data")
        public List<RecoModel> data;

    }

}
