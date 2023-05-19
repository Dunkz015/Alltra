package com.app.alltra.home;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MonthRangeUtil {

    public static String getCurrentMonthRange() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
        String startDay = dateFormat.format(startDate);
        String endDay = dateFormat.format(endDate);

        return startDay + " - " + endDay;
    }
}

