package io.caly.calyandroid.Model.Event;

import io.caly.calyandroid.Model.Category;
import io.caly.calyandroid.Model.Response.RecoResponse;

/**
 * Created by jspiner on 2017. 4. 27..
 */

public class RecoDataLoadDoneEvent {

    public Category category;
    public RecoResponse response;

    public RecoDataLoadDoneEvent(Category category, RecoResponse response){
        this.category = category;
        this.response = response;
    }

}
