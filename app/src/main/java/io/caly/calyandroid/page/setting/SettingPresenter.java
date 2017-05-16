package io.caly.calyandroid.page.setting;

import io.caly.calyandroid.page.base.BasePresenter;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 16
 */

public class SettingPresenter extends BasePresenter implements SettingContract.Presenter {

    SettingFragment settingView;

    public SettingPresenter(SettingFragment settingView){
        this.settingView = settingView;
        settingView.setPresenter(this);
    }



}