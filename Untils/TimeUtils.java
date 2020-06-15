package com.gengy.control.Untils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @date on 2020/1/13
 * 描述       10:49
 * com.gengy.control.Untils
 */
public class TimeUtils {

    public static String getCurrentTime(){
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
    }


}
