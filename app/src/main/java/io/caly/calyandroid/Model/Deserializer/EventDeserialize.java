package io.caly.calyandroid.Model.Deserializer;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.DataModel.EventModel;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 21
 */

public class EventDeserialize implements JsonDeserializer<EventModel> {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + EventDeserialize.class.getSimpleName();

    @Override
    public EventModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        EventModel eventModel = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create()
                .fromJson(json, typeOfT);

        eventModel.setData();

        return eventModel;
    }


}
