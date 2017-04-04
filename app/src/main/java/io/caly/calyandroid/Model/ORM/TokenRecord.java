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
    private String loginPlatform;

    public TokenRecord(){

    }

    public TokenRecord(String sessionKey, String loginPlatform){
        this.apiKey = sessionKey;
        this.loginPlatform = loginPlatform;
    }

    public static TokenRecord getTokenRecord(){
        TokenRecord tokenRecord = TokenRecord.last(TokenRecord.class);
        if(tokenRecord == null){
            tokenRecord = new TokenRecord(null, null);
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

    public String getLoginPlatform() {
        return loginPlatform;
    }

    public void setLoginPlatform(String loginPlatform) {
        this.loginPlatform = loginPlatform;
    }

}
