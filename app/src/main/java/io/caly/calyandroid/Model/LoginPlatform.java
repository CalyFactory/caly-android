package io.caly.calyandroid.Model;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 25
 */

public enum  LoginPlatform {

    CALDAV_NAVER("naver"),
    CALDAV_ICAL("ical"),
    GOOGLE("google");

    public final String value;

    LoginPlatform(final String value){
        this.value = value;
    }


}
