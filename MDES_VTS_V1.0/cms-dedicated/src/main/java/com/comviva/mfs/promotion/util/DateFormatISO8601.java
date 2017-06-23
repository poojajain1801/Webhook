package com.comviva.mfs.promotion.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Deals with date in ISO-8601 format.
 * Created by tarkeshwar.v on 2/20/2017.
 */
public class DateFormatISO8601 {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";
    /**
     * Prepares date in ISO8601 format.
     * @param calendar  Calender instance.
     * @return Formatted date
     */
    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat(DATE_FORMAT).format(date);
        return /*formatted.substring(0, 22) + ":" + formatted.substring(22)*/formatted;
    }

    /** Get current date and time formatted as ISO 8601 string. */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /** Transform ISO 8601 string to Calendar. */
    public static Calendar toCalendar(final String iso8601string) throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        Date date = new SimpleDateFormat(DATE_FORMAT).parse(s);
        calendar.setTime(date);
        return calendar;
    }
}
