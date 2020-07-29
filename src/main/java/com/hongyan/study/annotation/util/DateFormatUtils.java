package com.hongyan.study.annotation.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class DateFormatUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateFormatUtils.class);
    public static final String DEFAULT_DATETIME_FORMAT_SEC = "yyyy-MM-dd HH:mm:ss";

    public DateFormatUtils() {
    }

    public static Date parsDate(String date) throws ParseException {
        String pattern = null;
        String pattern1 = "yyyy-MM-dd HH:mm:ss";
        String pattern2 = "MM dd yyyy  h:mm";
        String pattern3 = "MM dd yyyy h:mm";
        String pattern4 = "yyyy-MM-dd";
        String s1 = "((19|20)\\d{2})-(0?[1-9]|1[0-2])-(0?[1-9]|[123]\\d) (0?\\d|1\\d|2[0-3]):(0?\\d|[1-5]\\d):(0?\\d|[1-5]\\d)";
        String s2 = "(0?[1-9]|1[0-2]) (0?[1-9]|[123]\\d) ((19|20)\\d{2})  (0?\\d|[12]\\d):(0?\\d|[1-5]\\d)[AaPp][Mm]";
        String s3 = "(0?[1-9]|1[0-2]) (0?[1-9]|[123]\\d) ((19|20)\\d{2}) (0?\\d|[12]\\d):(0?\\d|[1-5]\\d)[AaPp][Mm]";
        String s4 = "((19|20)\\d{2})-(0?[1-9]|1[0-2])-(0?[1-9]|[123]\\d)";
        if (StringUtils.isEmpty(date)) {
            throw new RuntimeException("时间为空");
        } else {
            if (date.matches("((19|20)\\d{2})-(0?[1-9]|1[0-2])-(0?[1-9]|[123]\\d) (0?\\d|1\\d|2[0-3]):(0?\\d|[1-5]\\d):(0?\\d|[1-5]\\d)")) {
                pattern = "yyyy-MM-dd HH:mm:ss";
            } else if (date.matches("(0?[1-9]|1[0-2]) (0?[1-9]|[123]\\d) ((19|20)\\d{2})  (0?\\d|[12]\\d):(0?\\d|[1-5]\\d)[AaPp][Mm]")) {
                pattern = "MM dd yyyy  h:mm";
            } else if (date.matches("(0?[1-9]|1[0-2]) (0?[1-9]|[123]\\d) ((19|20)\\d{2}) (0?\\d|[12]\\d):(0?\\d|[1-5]\\d)[AaPp][Mm]")) {
                pattern = "MM dd yyyy h:mm";
            } else {
                if (!date.matches("((19|20)\\d{2})-(0?[1-9]|1[0-2])-(0?[1-9]|[123]\\d)")) {
                    logger.error("parsDate format error date : {}", date);
                    throw new RuntimeException("时间格式不正确");
                }

                pattern = "yyyy-MM-dd";
            }

            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(date);
        }
    }

    public static String getNowDateTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}

