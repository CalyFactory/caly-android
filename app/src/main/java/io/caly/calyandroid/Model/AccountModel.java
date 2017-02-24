package io.caly.calyandroid.Model;

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

    @SerializedName("create_datetime")
    public Date createDateTime;

    @SerializedName("user_id")
    public String userId;

    @Expose
    public boolean isHeader;

    @Expose
    public String title;

    public AccountModel(boolean isHeader){
        this.isHeader = isHeader;
    }

}
