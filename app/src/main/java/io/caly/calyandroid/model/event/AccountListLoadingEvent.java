package io.caly.calyandroid.model.event;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 4. 19
 */

public class AccountListLoadingEvent {

    public boolean enable;

    public AccountListLoadingEvent(boolean enable){
        this.enable = enable;
    }

}
