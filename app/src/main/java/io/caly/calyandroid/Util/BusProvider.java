package io.caly.calyandroid.Util;

import com.squareup.otto.Bus;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public class BusProvider {

    private static Bus bus;

    public synchronized static Bus getInstance(){
        if(bus == null){
            bus = new Bus();
        }
        return bus;
    }
}
