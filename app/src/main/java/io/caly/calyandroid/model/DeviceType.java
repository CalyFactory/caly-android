package io.caly.calyandroid.model;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 18
 */

public enum DeviceType {

    ANDROID(1),
    IPHONE(2),
    WEB(3);

    public final int value;

    DeviceType(final int value){
        this.value = value;
    }

}
