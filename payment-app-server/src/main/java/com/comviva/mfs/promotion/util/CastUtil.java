package com.comviva.mfs.promotion.util;

import com.comviva.mfs.promotion.exception.DataConversionException;
import com.comviva.mfs.promotion.exception.DateParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by sumit.das on 12/26/2016.
 */
public class CastUtil {

    public static Date getDate(Map map, String key) {
        Object value = map.get(key);
        try {
            return getDate(value);
        } catch (DateParseException e) {
            throw new DataConversionException(map, key, Date.class, e);
        }
    }

    private static Date getDate(Object object) {
        if (object instanceof Date) {
            return (Date) object;
        }
        return getDate(getString(object));
    }

    public static Date getDate(String dateTimeString) {
        if(isBlank(dateTimeString)) {
            return null;
        }
        return DateUtil.parseDate(dateTimeString);
    }


    public static String getString(Object value) {
        if (value == null) {
            return null;
        }
        String stringValue = value.toString();
        return StringUtils.trim(stringValue);
    }

    public static String dateToString(Date date) {
        return date == null ? null : DateFormatUtils.format(date, DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern());
    }
}
