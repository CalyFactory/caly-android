package io.caly.calyandroid.model.orm;

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
    private String userId;

    public TokenRecord(){

    }

    public TokenRecord(String sessionKey, String loginPlatform, String userId){
        this.apiKey = sessionKey;
        this.loginPlatform = loginPlatform;
        this.userId = userId;
    }

    public static TokenRecord getTokenRecord(){
        TokenRecord tokenRecord = TokenRecord.last(TokenRecord.class);
        if(tokenRecord == null){
            tokenRecord = new TokenRecord(null, null, null);
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
