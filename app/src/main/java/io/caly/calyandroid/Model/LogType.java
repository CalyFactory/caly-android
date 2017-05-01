package io.caly.calyandroid.Model;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 18
 */

public enum LogType {

    //CATEGORY 분류
    CATEGORY_VIEW(0),
    CATEGORY_CELL(1),

    ACTION_CLICK(0),

    //Category 가 view일경우
    LABEL_EVENT_CELL(0),

    //Category가 Cell 일경우
    LABEL_EVENT_BANNER(0),
    LABEL_EVENT_SYNC(1),
    LABEL_EVENT_BANNER_CLOSE(2),

    //Cell일경우
    RECO_LABEL_GOBLOG(0),
    RECO_LABEL_GOPLACEMAP(1),
    RECO_LABEL_SHARE_KAKAO(2),

    RECO_LABEL_GOMAP(0),
    RECO_LABEL_TAP_RESTAURANT(1),
    RECO_LABEL_TAP_CAFE(2),
    RECO_LABEL_TAP_PLACE(3);



    public final int value;

    LogType(final int value){
        this.value = value;
    }
}
