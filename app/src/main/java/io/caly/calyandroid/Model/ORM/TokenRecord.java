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

    private String apiKey;

    public TokenRecord(){

    }

    public TokenRecord(String sessionKey){
        this.apiKey = sessionKey;
    }

    public static TokenRecord getTokenRecord(){
        TokenRecord tokenRecord = TokenRecord.last(TokenRecord.class);
        if(tokenRecord == null){
            tokenRecord = new TokenRecord(null);
        }
        return tokenRecord;
    }

    public static void destoryToken(){
        TokenRecord tokenRecord = getTokenRecord();
        if(tokenRecord !=null){
            tokenRecord.delete();
        }
    }

    public static boolean checkSessionExist(){
        if(TokenRecord.getTokenRecord().getApiKey() == null) {
            return false;
        }
        else{
            return true;
        }
    }


    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
