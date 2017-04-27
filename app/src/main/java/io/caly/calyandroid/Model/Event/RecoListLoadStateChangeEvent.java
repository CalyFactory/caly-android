package io.caly.calyandroid.Model.Event;

import io.caly.calyandroid.Model.Category;
import io.caly.calyandroid.Model.Response.RecoResponse;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 21
 */

public class RecoListLoadStateChangeEvent {

    public Category category;
    public int dataCount;
    public LOADING_STATE loadingState;
    public Response<RecoResponse> response;

    public RecoListLoadStateChangeEvent(Category category, int dataCount, Response<RecoResponse> response, LOADING_STATE loadingState){
        this.category = category;
        this.dataCount = dataCount;
        this.response = response;
        this.loadingState = loadingState;
    }

    public enum LOADING_STATE{
        STATE_LOADING,
        STATE_DONE,
        STATE_EMPTY,
        STATE_ERROR
    }

}
