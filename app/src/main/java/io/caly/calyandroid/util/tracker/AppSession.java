package io.caly.calyandroid.util.tracker;

import java.util.UUID;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 10
 */

public class AppSession {

    private UUID sessionKey;
    private long expireTimeSeconds;
    private long expireTime;

    public AppSession(long expireTime){
        this.expireTime = expireTime;
        this.expireTimeSeconds = System.currentTimeMillis() / 1000 + expireTime;
        generateKey();
    }

    private void generateKey(){
        this.sessionKey = UUID.randomUUID();
    }

    public void refreshKey(){
        this.expireTimeSeconds = System.currentTimeMillis() / 1000 + expireTime;
        generateKey();
    }

    public boolean isExpired(){
        return System.currentTimeMillis() / 1000 > expireTimeSeconds;
    }

    public UUID getSessionKey(){
        if(isExpired()){
            refreshKey();
        }
        return sessionKey;
    }

}