package io.caly.calyandroid.Model.DataModel;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 13
 */

public class SettingItemModel {

    public boolean isTitle;
    public String title;
    public String description;

    public SettingItemModel(String title){
        isTitle = true;
        this.title = title;
        this.description = "";
    }

    public SettingItemModel(String title, String description){
        isTitle = false;
        this.title = title;
        this.description = description;

    }

    
}
