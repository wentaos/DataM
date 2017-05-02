package com.winchannel.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qiaoboxiang on 2017/4/27.
 */
public class DateUtil {

    private static String DATE_TIME_FORMATE = "yyyy-MM-dd HH:mm:ss";

    public static String getStandDateTime(){
        return  getStandDateTime(new Date());
    }
    
    public static String getStandDateTime(Date date){
        SimpleDateFormat formate = new SimpleDateFormat(DATE_TIME_FORMATE);
        return  formate.format(date);
    }

}
