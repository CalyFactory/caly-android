package io.caly.calyandroid.Util;

import java.util.Calendar;
import java.util.Date;

import io.caly.calyandroid.Model.DataModel.AccountModel;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 21
 */

public class StringFormmater {
    
    public static String hostnameAuthGenerator(String userId, String hostname){
        if(userId.indexOf("@")==-1){
            return userId+"@"+hostname;
        }
        return userId;
    }

    public static String simpleRangeTimeFormat(Date startDate, Date endDate){
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate==null?startDate:endDate);

        return String.format(
                "%02d:%02d~%02d:%02d",
                startCalendar.get(Calendar.HOUR_OF_DAY),
                startCalendar.get(Calendar.MINUTE),
                endCalendar.get(Calendar.HOUR_OF_DAY),
                endCalendar.get(Calendar.MINUTE)

        );
    }

    public static String yearMonthFormat(int year, int month){
        return String.format(
                "%04d년 %02d월",
                year,
                month

        );
    }

    public static String accountStateFormat(Date latestSyncTime){
        if(latestSyncTime == null){
            return "동기화중입니다.";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(latestSyncTime);

        return String.format(
                "마지막 동기화 : %04d.%02d.%02d %02d:%02d:%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
        );
    }

    public static String simpleDateFormat(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return String.format(
                "%02d.%02d.%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.DAY_OF_MONTH)

        );

    }

    public static String hashTagFormat(String hashtags){
        if(hashtags==null){
            return "";
        }
        String[] tags = hashtags.split(",");
        for(int i=0;i<tags.length;i++){
            tags[i] = "#" + tags[i].trim();
        }
        return join(tags, " ");
    }

    public static String join(String[] list, String separator){
        StringBuilder builder = new StringBuilder();
        builder.append(list[0]);
        for(int i=1;i<list.length;i++){
            builder.append(separator).append(list[i]);
        }

        return builder.toString();
    }

}
