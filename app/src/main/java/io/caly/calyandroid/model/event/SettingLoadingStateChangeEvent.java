package io.caly.calyandroid.model.event;

/**
 * Created by jspiner on 2017. 4. 11..
 */

public class SettingLoadingStateChangeEvent {

    public boolean isEnable;

    public SettingLoadingStateChangeEvent(boolean isEnable){
        this.isEnable = isEnable;
    }

}
