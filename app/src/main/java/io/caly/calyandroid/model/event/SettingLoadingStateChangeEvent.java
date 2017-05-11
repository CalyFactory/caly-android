package io.caly.calyandroid.model.event;

/**
 * Created by jspiner on 2017. 4. 11..
 */

public class SettingLoadingStateChangeEvent {

    public String text;
    public boolean isEnable;

    public SettingLoadingStateChangeEvent(String text, boolean isEnable){
        this.isEnable = isEnable;
        this.text = text;
    }

    public SettingLoadingStateChangeEvent(boolean isEnable){
        this.isEnable = isEnable;
    }

}
