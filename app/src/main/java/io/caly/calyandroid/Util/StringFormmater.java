package io.caly.calyandroid.Util;

import java.util.Calendar;
import java.util.Date;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 21
 */

public class StringFormmater {

    public static String simpleRangeTimeFormat(Date startDate, Date endDate){
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        return String.format(
                "%02d:%02d~%02d:%02d",
                startCalendar.get(Calendar.HOUR),
                startCalendar.get(Calendar.MINUTE),
                endCalendar.get(Calendar.HOUR),
                endCalendar.get(Calendar.MINUTE)

        );
    }

}
