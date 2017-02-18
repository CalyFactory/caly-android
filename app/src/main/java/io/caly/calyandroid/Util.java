package io.caly.calyandroid;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.UUID;

import io.caly.calyandroid.Model.HttpService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project Caly
 * @since 17. 2. 11
 */

public class Util {

    //로그에 쓰일 tag
    private static final String TAG = Util.class.getSimpleName();

    

    private static HttpService httpService;

    public static String[] dayOfDate = {"일","월","화","수","목","금","토"};

    // http service
    public static HttpService getHttpService() {
        if(httpService == null){
            Util.httpService =
                    new Retrofit.Builder()
                            .baseUrl(CalyApplication.getContext().getString(R.string.app_server))
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(HttpService.class);
        }

        return Util.httpService;
    }

    public static String getUUID(){
        Context context = CalyApplication.getContext();
        TelephonyManager teleManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String tmSerial = teleManager.getSimSerialNumber();
        String tmDeviceId = teleManager.getDeviceId();
        String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        if (tmSerial  == null) tmSerial   = "1";
        if (tmDeviceId== null) tmDeviceId = "1";
        if (androidId == null) androidId  = "1";
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDeviceId.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    public static int dayOfDate(int year, int month, int day){
        Calendar cal= Calendar.getInstance ();

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DATE, day);


        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }
}
