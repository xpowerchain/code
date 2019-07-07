package com.lunary.owerwallet.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/11.
 */
public class DateUtil {
    public static String DateDistance(long startTime){
        long timeLong = System.currentTimeMillis() - startTime;
        if(timeLong<0){
            timeLong=0;
        }
        if (timeLong<60*1000)
            return timeLong/1000 + "秒前";
        else if (timeLong<60*60*1000){
            timeLong = timeLong/1000 /60;
            return timeLong + "分钟前";
        } else if (timeLong<60*60*24*1000){
            timeLong = timeLong/60/60/1000;
            return timeLong+"小时前";
        } else if ((timeLong/1000/60/60/24)<7){
            timeLong = timeLong/1000/ 60 / 60 / 24;
            return timeLong + "天前";
        }else{
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
            return formatter.format(new Date(startTime));
        }
    }
    public static String dateToStrLong(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date(time));
        return dateString;
    }
    public static String dateToStrLong2(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        String dateString = formatter.format(new Date(time));
        return dateString;
    }
    public static String dateToStrLong2(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        String dateString = formatter.format(new Date(time));
        return dateString;
    }
    // yyyy-MM-dd'T'HH:mm:ss.SSSXXX
    public static String dateToSSSXXXLong(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.SSS'--'");
        String dateString = formatter.format(new Date(time));
        return dateString;
    }
}
