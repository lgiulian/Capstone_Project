package com.crilu.gothandroid.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateUtils {

    public static String convertDateFormat2NewDateFormat(String origDate, DateFormat origDateFormat, DateFormat newDateFormat) {
        String newDateStr = null;
        Date date = null;
        try {
            date = origDateFormat.parse(origDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newDateStr = newDateFormat.format(date);

        return newDateStr;
    }
}
