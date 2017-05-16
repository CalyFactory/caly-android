package io.caly.calyandroid.page.notice;

import io.caly.calyandroid.model.dataModel.NoticeModel;
import io.caly.calyandroid.page.base.BasePresenterModel;
import io.caly.calyandroid.page.base.BaseView;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 16
 */

public class NoticeContract {

    public interface View extends BaseView<Presenter> {
        void changeProgressState(boolean isLoading);
        void addListItem(NoticeModel noticeModel);
    }

    public interface Presenter extends BasePresenterModel {
        void loadNotice();
    }
}
