package com.aig.science.damagedetection.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;


public class Constants {

    private static final String LOGTAG = "Constants";
    public static final String User_Setting_file = "setting_file";
    public static final String IS_REGISTERED = "IS_REGISTERED";
    public static final String USER_ID = "USER_ID";

    public static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        //get current date time with Date()
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        long timestamp = date.getTime();
        String unixTimestamp = Long.toString(timestamp);
        return unixTimestamp;
    }

    //Constant file
    public static String convertDateTimeToUnix(String date, String time) {
        Log.i(LOGTAG,
                "convertDateTimeToUnix: convert date to unix/epoch timestamp format");

        String dateTime = date + " " + time;

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        long timestamp = 0;
        try {
            Date newFormatedate = format.parse(dateTime);
            timestamp = newFormatedate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String unixTimestamp = Long.toString(timestamp);
        return unixTimestamp;
    }

}
