package io.caly.calyandroid.page.event;

import io.caly.calyandroid.page.base.BasePresenterModel;
import io.caly.calyandroid.page.base.BaseView;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 16
 */

public class EventListContract {

    public interface View extends BaseView<Presenter> {

    }

    public interface Presenter extends BasePresenterModel {

    }
}
