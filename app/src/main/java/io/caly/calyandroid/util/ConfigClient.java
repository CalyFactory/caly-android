package io.caly.calyandroid.util;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.R;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 4. 18
 */

public class ConfigClient {

    public static FirebaseRemoteConfig remoteConfig;

    public static FirebaseRemoteConfig getConfig(){
        if(remoteConfig == null) {
            remoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();

            remoteConfig.setConfigSettings(configSettings);
            remoteConfig.setDefaults(R.xml.remote_config_defaults);
        }
        return remoteConfig;
    }

}
