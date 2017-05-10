package io.caly.calyandroid.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.CalyApplication;
import okhttp3.RequestBody;
import okio.Buffer;

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
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + Util.class.getSimpleName();

    public static String[] dayOfDate = {"일요일","월요일","화요일","수요일","목요일","금요일","토요일"};
    public static String[] dayOfDateEng = {"SUN","MON","TUE","WEN","TUR","FRI","SAT"};

    // intent result code
    public static final int RC_INTENT_GOOGLE_SIGNIN = 10011;
    public static final int RC_INTENT_POLICY_RESPONSE = 10021;
    public static final int RC_PERMISSION_PHONE_STATE = 10031;
    public static final int RC_PERMISSION_FINE_LOCATION = 10032;

    public static String requestBodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    @Deprecated
    public static void centerToolbarTitle(final Toolbar toolbar) {
        final CharSequence title = toolbar.getTitle();
        final ArrayList<View> outViews = new ArrayList<>(1);
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT);
        if (!outViews.isEmpty()) {
            final TextView titleView = (TextView) outViews.get(0);
            titleView.setGravity(Gravity.CENTER_HORIZONTAL);
            final Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) titleView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.setMargins(0,0,0,0);
            toolbar.requestLayout();
        }
    }

    @Deprecated
    public static void setToolbarFontSize(Toolbar toolbar, int fontSize){
        final CharSequence title = toolbar.getTitle();
        final ArrayList<View> outViews = new ArrayList<>(1);
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT);
        if (!outViews.isEmpty()) {
            final TextView titleView = (TextView) outViews.get(0);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
            toolbar.requestLayout();
        }

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

    public static String getAppVersion(){
        String versionName = BuildConfig.VERSION_NAME;
        return (versionName);
    }

    public static String getSdkLevel(){
        String apiLevel = Build.VERSION.SDK;
        String os = System.getProperty("os.version");
        return TextUtils.join(";", new String[]{apiLevel, os});

    }

    public static String getDeviceInfo(){
        String device = Build.DEVICE;
        String model = Build.MODEL;
        String product = Build.PRODUCT;

        return TextUtils.join(";", new String[]{device, model, product});

    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static String getCurrentMethodName()
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

        return ste[ste.length - 1].getMethodName();
    }

    //텍스트 파일 불러오기
    public static String readTextFile(Context context, String file) {
        String text;
        try {
            InputStream is = context.getAssets().open(file);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            text = new String(buffer);
        }
        catch (Exception e){
            text = "error" + e.getMessage();
            e.printStackTrace();
        }
        return text;
    }

    public static boolean isPackageInstalled(String uri) {
        PackageManager pm = CalyApplication.getContext().getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }

}
