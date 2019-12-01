package com.edison4mobile.utils;

import com.edison4mobile.database.CallLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by don3 on 21/01/2017.
 */

public class GetNameHelper {
    public static  String mycall_Countrycode = "";
    public static  boolean bOutgoing = false;
    public static String generateFileName(CallLog phoneCall){
        String tmp;
        {
            tmp = changeFormatPhoneNumber(phoneCall.getPhoneNumber());

            long start = phoneCall.getStartTime().getTimeInMillis();
            long end = phoneCall.getEndTime().getTimeInMillis();
            tmp += "-";
            if(end > start)
            {
                tmp += changeFormatCalendar(phoneCall.getStartTime());
                tmp += "-";
                tmp += changeFormatCalendar(phoneCall.getEndTime());
            }else
            {
                tmp += changeFormatCalendar(phoneCall.getEndTime());
                tmp += "-";
                tmp += changeFormatCalendar(phoneCall.getStartTime());
            }

        }

        return tmp;
    }

    /*HHMMSS-DDMMYY*/
    public static String changeFormatCalendar(Calendar calendar){
        if(calendar != null) {
            SimpleDateFormat hhformat = new SimpleDateFormat("HH");
            SimpleDateFormat mmformat = new SimpleDateFormat("mm");
            SimpleDateFormat ssformat = new SimpleDateFormat("SS");
            int sec = calendar.get(Calendar.SECOND);

            String sec_str = Integer.toString(sec);
            if(sec < 10)
                sec_str = "0" + sec_str;

            SimpleDateFormat ddformat = new SimpleDateFormat("DD");

            int day = Integer.parseInt(ddformat.format(calendar.getTime()));


            SimpleDateFormat MMformat = new SimpleDateFormat("MM");
            SimpleDateFormat yyformat = new SimpleDateFormat("EEE, MMM d, ''yy");
            int month = Integer.parseInt(MMformat.format(calendar.getTime())) - 1;
            if(month == 2){
                int year = Integer.parseInt(yyformat.format(calendar.getTime()).substring(yyformat.format(calendar.getTime()).lastIndexOf("'") + 1));
                if(year % 4 == 0)
                {
                    day = day - 29 * (day / 29);
                }
                else {
                    day = day - 28 * (day / 28);
                }
            }else if(month < 8)
            {
                if(month % 2 == 0)
                {
                    day = day - 30 * (day / 30);
                }
                else
                {
                    day = day - 31 * (day / 31);
                }
            } else if(month == 8)
            {
                day = day - 31 * (day / 31);
            }else
            {
                if(month % 2 == 1)
                {
                    day = day - 30 * (day / 30);
                }
                else
                {
                    day = day - 31 * (day / 31);
                }
            }
            String day_str = Integer.toString(day);
            if(day < 10)
            {
                day_str = "0" + day_str;
            }

            return hhformat.format(calendar.getTime()) + mmformat.format(calendar.getTime()) +
                    sec_str + "-" +
                    day_str + MMformat.format(calendar.getTime()) +
                    yyformat.format(calendar.getTime()).substring(yyformat.format(calendar.getTime()).lastIndexOf("'") + 1);
        }
        else{
            return "000000-000000";
        }
    }


    public static String changeFormatPhoneNumber(String phonenumber){
        String returnPhoneNumber = "";
        if(!phonenumber.equals("")){

            StringBuilder str = new StringBuilder(phonenumber);

            if(str.length() == 10)
                returnPhoneNumber = mycall_Countrycode + "-" + str.substring(str.length() - 10, str.length());
            else{
                if(str.substring(0, 1).equals("+"))
                    returnPhoneNumber = str.substring(1, str.length() - 10) + "-" + str.substring(str.length() - 10, str.length());
                else
                    returnPhoneNumber = str.substring(0, str.length() - 10) + "-" + str.substring(str.length() - 10, str.length());
            }


            if(bOutgoing)
                mycall_Countrycode = str.substring(0, str.length() - 10);

        }
        else{
            returnPhoneNumber = "00-0000000000";
        }


        return returnPhoneNumber;
    }
}
