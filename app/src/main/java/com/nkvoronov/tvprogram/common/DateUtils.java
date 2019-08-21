package com.nkvoronov.tvprogram.common;

import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class DateUtils {

    public static Date addDays(Date date, int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, count);
        return calendar.getTime();
    }

    public static String getDuration(Date startDate, Date endDate) {
        long diffDate = endDate.getTime() - startDate.getTime();
        int hours = (int) (diffDate / 3600000);
        int minutes = (int) ((diffDate % 3600000) / 60000);
        return addZero(hours) + ":" + addZero(minutes);
    }

    public static String getDateFormat(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String addZero(int value) {
        if (value < 10) {
            return "0" + new Integer(value).toString();
        } else {
            return new Integer(value).toString();
        }
    }

    public static String intToTime(int value, String sep, Boolean isZN, Boolean isSec) {
        String zn = "";
        int del = 60;
        if (isSec) {
            del = del * 60;
        }
        int crh = value / del;
        int crm;
        if (isSec) {
            crm = (value - crh * del) / 60;
        } else crm = value - crh * del;
        if (isZN) {
            if (value >= 0) {
                zn = "+";
            } else zn = "-";
        }
        return zn + addZero(crh) + sep + addZero(crm);
    }

}
