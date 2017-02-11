package io.caly.calyandroid.Model;

import com.orm.SugarRecord;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class SessionRecord extends SugarRecord {

    private String sessionKey;

    public SessionRecord(){

    }

    public SessionRecord(String sessionKey){
        this.sessionKey = sessionKey;
    }

    public static SessionRecord getSessionRecord(){
        SessionRecord sessionRecord = SessionRecord.last(SessionRecord.class);
        if(sessionRecord == null){
            sessionRecord = new SessionRecord(null);
        }
        return sessionRecord;
    }

    public static void destorySession(){
        SessionRecord sessionRecord = getSessionRecord();
        if(sessionRecord!=null){
            sessionRecord.delete();
        }
    }

    public static boolean checkSessionExist(){
        if(SessionRecord.getSessionRecord().getSessionKey() == null) {
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
