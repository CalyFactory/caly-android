package io.caly.calyandroid.Util;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public class BusProvider {

    private static MainThreadBus bus;

    public synchronized static MainThreadBus getInstance(){
        if(bus == null){
            bus = new MainThreadBus();
        }
        return bus;
    }

    public static class MainThreadBus extends Bus{
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            if(Looper.myLooper() == Looper.getMainLooper()){
                super.post(event);
            }
            else{
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainThreadBus.super.post(event);
                    }
                });
            }
        }
    }
}
