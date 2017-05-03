package io.caly.calyandroid.model.dataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 22
 */

public class AccountModel {

    @SerializedName("login_platform")
    public String loginPlatform;

    @SerializedName("latestSyncTime")
    public Date latestSyncTime;

    @SerializedName("user_id")
    public String userId;

    @Expose
    public boolean isHeader;

    @Expose
    public String title;

    public AccountModel(String title){
        this.title = title;
        isHeader = true;
    }

}
