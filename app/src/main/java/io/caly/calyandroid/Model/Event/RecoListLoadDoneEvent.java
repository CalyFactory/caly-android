package io.caly.calyandroid.Model.Event;

import io.caly.calyandroid.Model.Category;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 21
 */

public class RecoListLoadDoneEvent {

    public Category category;
    public int dataCount;

    public RecoListLoadDoneEvent(Category category, int dataCount){
        this.category = category;
        this.dataCount = dataCount;
    }

}
