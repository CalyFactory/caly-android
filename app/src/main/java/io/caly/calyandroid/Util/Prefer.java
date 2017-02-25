package io.caly.calyandroid.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.caly.calyandroid.CalyApplication;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 25
 */

public class Prefer {

    //로그에 쓰일 tag
    private static final String TAG = Prefer.class.getSimpleName();

    private static SharedPreferences preferences;

    public static synchronized SharedPreferences getSharedPreferences(){
        if(preferences == null){
            preferences = CalyApplication.getContext().getSharedPreferences("caly", Context.MODE_PRIVATE);
        }
        return preferences;
    }

    public static <T> void put(String key, T value){
        if(key==null || value==null){
            throw new NullPointerException();
        }

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        if(value instanceof Integer){
            Log.d(TAG, "integer");
            editor.putInt(key, (int)value);
        }
        else if(value instanceof String){
            Log.d(TAG, "string");
            editor.putString(key, (String)value);
        }
        else if(value instanceof Boolean){
            Log.d(TAG, "boolean");
            editor.putBoolean(key, (Boolean)value);
        }
        else if(value instanceof Float){
            Log.d(TAG, "float");
            editor.putFloat(key, (Float)value);
        }
        else if(value instanceof Long){
            Log.d(TAG, "long");
            editor.putLong(key, (Long)value);
        }
        else{
            Log.d(TAG, "unknown");
            editor.putString(key, new Gson().toJson(value));
        }

        editor.commit();
    }

    public static <T> Object get(String key, T defaultValue){
        if (key == null){
            throw new NullPointerException();
        }

        SharedPreferences preference = getSharedPreferences();

        if(defaultValue instanceof Integer){
            Log.d(TAG, "integer");
            return preference.getInt(key, (int)defaultValue);
        }
        else if(defaultValue instanceof String){
            Log.d(TAG, "string");
            return preference.getString(key, (String)defaultValue);
        }
        else if(defaultValue instanceof Boolean){
            Log.d(TAG, "boolean");
            return preference.getBoolean(key, (Boolean)defaultValue);
        }
        else if(defaultValue instanceof Float){
            Log.d(TAG, "float");
            return preference.getFloat(key, (Float)defaultValue);
        }
        else if(defaultValue instanceof Long){
            Log.d(TAG, "long");
            return preference.getLong(key, (Long)defaultValue);
        }
        else{
            Log.d(TAG, "unknown");
            return preference.getString(key, (String)defaultValue);
        }


    }

}
