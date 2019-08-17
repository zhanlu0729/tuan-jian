package com.banksteel.bone.api.log.bin.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author 杨新伦
 * @date 2018-11-14
 */
public class DateUtils {

    private DateUtils() {

    }

    private static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
    };

    public static String format(Date date) {
        return DATE_FORMAT_THREAD_LOCAL.get().format(date);
    }

    public static Date parse(String date) throws ParseException {
        String dateStr = date.trim();
        if (dateStr.length() == 19) {
            return DATE_FORMAT_THREAD_LOCAL.get().parse(dateStr + ".000");
        } else if (dateStr.length() == 10) {
            return DATE_FORMAT_THREAD_LOCAL.get().parse(dateStr + " 00:00:00.000");
        }
        return DATE_FORMAT_THREAD_LOCAL.get().parse(dateStr);
    }

}
