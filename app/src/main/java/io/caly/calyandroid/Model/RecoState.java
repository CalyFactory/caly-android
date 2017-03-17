package io.caly.calyandroid.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 5
 */

public enum  RecoState {

    @SerializedName("1")
    STATE_BEING_RECOMMEND(1),
    @SerializedName("2")
    STATE_NOTHING_TO_RECOMMEND(2),
    @SerializedName("3")
    STATE_DONE_RECOMMEND(3);

    public final int value;

    RecoState(int value){
        this.value = value;
    }

    public static RecoState findByAbbr(int value){
        for (RecoState state : values()) {
            if (state.value == value){
                return state;
            }
        }
        return null;
    }
}
