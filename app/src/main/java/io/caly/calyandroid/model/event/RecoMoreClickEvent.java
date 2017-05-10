package io.caly.calyandroid.model.event;

import io.caly.calyandroid.model.dataModel.RecoModel;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 4. 4
 */

public class RecoMoreClickEvent {

    public RecoModel recoModel;

    public RecoMoreClickEvent(RecoModel recoModel){
        this.recoModel = recoModel;
    }

}
