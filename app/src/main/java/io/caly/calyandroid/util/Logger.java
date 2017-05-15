package io.caly.calyandroid.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.caly.calyandroid.BuildConfig;
import io.caly.calyandroid.CalyApplication;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 4. 20
 */

public class Logger {

    //로그에 쓰일 tag
    public static final String TAG = CalyApplication.class.getSimpleName() + "/" + Logger.class.getSimpleName();

    public static void v(String tag, String msg){
        if(BuildConfig.DEBUG){
            Log.v(tag, msg);

            saveData(LogType.VERBOSE, tag, msg);
        }
    }

    public static void d(String tag, String msg){
        if(BuildConfig.DEBUG){
            Log.d(tag, msg);

            saveData(LogType.DEBUG, tag, msg);
        }
    }

    public static void i(String tag, String msg){
        if(BuildConfig.DEBUG){
            Log.i(tag, msg);

            saveData(LogType.INFO, tag, msg);
        }
    }

    public static void w(String tag, String msg){
        if(BuildConfig.DEBUG){
            Log.w(tag, msg);

            saveData(LogType.WARNING, tag, msg);
        }
    }

    public static void e(String tag, String msg){
        if(BuildConfig.DEBUG){
            Log.e(tag, msg);

            saveData(LogType.ERROR, tag, msg);
        }
    }

    public static void saveData(LogType logType, String tag, String msg){

        try {
            File cacheFile = new File(CalyApplication.getContext().getCacheDir() + File.separator
                    + getDataFileName());
            cacheFile.createNewFile();
            Log.i(TAG, "filename : " + cacheFile.getAbsolutePath());

            FileOutputStream fos = new FileOutputStream(cacheFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
            PrintWriter pw = new PrintWriter(osw);

            pw.println(String.format(
                    "[%s/%s] %s : %s \n",
                    new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()),
                    logType,
                    tag,
                    msg
                    )
            );

            pw.flush();
            pw.close();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "error");
        }
        /*
        FileOutputStream outputStream;
        String fileName = getDataFileName();

        try {
            outputStream = CalyApplication.getContext()
                    .openFileOutput(fileName, Context.MODE_APPEND);
            outputStream.write(
                    String.format(
                            "[%s/%s] %s : %s \n",
                            new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()),
                            logType,
                            tag,
                            msg
                    ).getBytes()
            );
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "log save error : " + e.getMessage());
        }
*/
    }

    public static String readData(){
        Log.i(TAG, "readData");
        FileInputStream inputStream;
        StringBuilder builder = new StringBuilder();
        String fileName = getDataFileName();

        try {
            String line;
            inputStream = CalyApplication.getContext().openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            line = reader.readLine();

            while(line != null) {
                builder.append(line + "\n");
                line = reader.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "log read error : " + e.getMessage());
        }

        return builder.toString();
    }

    public static String getDataFileName(){
        String fileName =
                        "calylog_" +
                        new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime()) +
                        ".log";
        return  fileName;
    }

    public enum LogType{
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR
    }


}
