package io.caly.calyandroid.Model;

import com.orm.SugarRecord;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 11
 */

public class SettingRecord extends SugarRecord {

    private boolean isDidRun;
    private boolean isPushReceive;

    public SettingRecord(){

    }

    public static SettingRecord getSettingRecord(){
        SettingRecord settingRecord = SettingRecord.last(SettingRecord.class);
        if(settingRecord == null){
            settingRecord = new SettingRecord();
        }
        return settingRecord;
    }

    public boolean isDidRun() {
        return isDidRun;
    }

    public void setDidRun(boolean didRun) {
        isDidRun = didRun;
    }

    public boolean isPushReceive() {
        return isPushReceive;
    }

    public void setPushReceive(boolean pushReceive) {
        isPushReceive = pushReceive;
    }


}
