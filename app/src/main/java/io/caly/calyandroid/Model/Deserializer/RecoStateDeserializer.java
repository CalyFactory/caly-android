package io.caly.calyandroid.Model.Deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.caly.calyandroid.Model.RecoState;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 3. 5
 */

public class RecoStateDeserializer implements JsonDeserializer<RecoState> {
    @Override
    public RecoState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int typeInt = json.getAsInt();
        return RecoState.findByAbbr(typeInt);
    }
}
