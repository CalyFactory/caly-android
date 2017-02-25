package io.caly.calyandroid.Model.ORM;

import com.orm.SugarRecord;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class TokenRecord extends SugarRecord {

    private String sessionKey;

    public TokenRecord(){

    }

    public TokenRecord(String sessionKey){
        this.sessionKey = sessionKey;
    }

    public static TokenRecord getSessionRecord(){
        TokenRecord tokenRecord = TokenRecord.last(TokenRecord.class);
        if(tokenRecord == null){
            tokenRecord = new TokenRecord(null);
        }
        return tokenRecord;
    }

    public static void destorySession(){
        TokenRecord tokenRecord = getSessionRecord();
        if(tokenRecord !=null){
            tokenRecord.delete();
        }
    }

    public static boolean checkSessionExist(){
        if(TokenRecord.getSessionRecord().getSessionKey() == null) {
            return false;
        }
        else{
            return true;
        }
    }


    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
}
