package com.nkvoronov.tvprogram.common;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class DateUtils
{
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static String diffDate(Date dateStart, Date dateEnd) {
        //Duration duration = Duration.between(dateStart, dateEnd);
        return "";
    }

    public static String getFormatDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static Date getDateFromString(String date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.fillInStackTrace();
            Log.d(TAG, e.getMessage());
            return null;
        }
    }
}
