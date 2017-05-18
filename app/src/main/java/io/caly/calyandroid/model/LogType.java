package io.caly.calyandroid.model;

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
    CATEGORY_RECO_MAP_VIEW(2),
    CATEGORY_RECO_MAP_CELL(3),

    ACTION_CLICK(0),
    ACTION_NONE(-1),

    //Category 가 view일경우
    LABEL_EVENT_CELL(0),
    LABEL_EVENT_CELL_ANALYZING(1),
    LABEL_EVENT_CELL_QUESTIONMARK(2),


    //Category가 Cell 일경우
    LABEL_EVENT_BANNER(0),
    LABEL_EVENT_SYNC(1),
    LABEL_EVENT_BANNER_CLOSE(2),
    LABEL_EVENT_BANNER_SHOWING(3),

    //Cell일경우
    LABEL_RECO_DEEPLINK(0),
    LABEL_RECO_ITEM_MAP(1),
    LABEL_RECO_SHARE_KAKAO_INCELL(2),
    LABEL_RECO_SHARE_KAKAO_INBLOG(3),

    //View일겨웅
    LABEL_RECO_GOFULLMAP(0),
    LABEL_RECO_TAP_RESTAURANT(1),
    LABEL_RECO_TAP_CAFE(2),
    LABEL_RECO_TAP_PLACE(3),

    //LabelRecoMap
    LABEL_RECO_MAP_MY_LOCATION(0),
    LABEL_RECO_MAP_FILTER_ALL(1),
    LABEL_RECO_MAP_FILTER_RESTAURANT(2),
    LABEL_RECO_MAP_FILTER_CAFE(3),
    LABEL_RECO_MAP_FILTER_PLACE(4),

    LABEL_RECO_MAP_DEEPLINK(0),
    LABEL_RECO_MAP_SHARE_KAKAO_INCELL(1),

    SCREEN_ON_START(1),
    SCREEN_ON_STOP (2);





    public final int value;

    LogType(final int value){
        this.value = value;
    }
}
