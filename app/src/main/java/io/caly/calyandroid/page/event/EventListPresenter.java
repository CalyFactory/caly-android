package io.caly.calyandroid.page.event;

import io.caly.calyandroid.page.base.BasePresenter;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 16
 */

public class EventListPresenter extends BasePresenter implements EventListContract.Presenter {

    EventListFragment eventListView;

    public EventListPresenter(EventListFragment eventListView){
        this.eventListView = eventListView;

        eventListView.setPresenter(this);
    }

}
