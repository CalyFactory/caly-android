package io.caly.calyandroid.model;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 18
 */

public enum  Gender {

    MAN(1),
    WOMAN(2);

    public final int value;

    Gender(final int value){
        this.value = value;
    }
}
