package io.caly.calyandroid.model;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public enum Category {
    RESTAURANT("restaurant"),
    CAFE("cafe"),
    PLACE("place");

    public final String value;

    Category(final String value){
        this.value = value;
    }
}
