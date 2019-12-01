package com.edison4mobile.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.edison4mobile.callrecorder.MainActivity;
import com.edison4mobile.callrecorder.PrefManager;
import com.edison4mobile.services.CleanupService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MyAlarmReceiver extends BroadcastReceiver {
    public MyAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PrefManager prefManager = new PrefManager(MainActivity.context);
        if(prefManager.isCurrentFileUploaded())
        {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());

            prefManager.setCurrentFileName(date);
            prefManager.setCurrentFileUpload(false);
            CleanupService.sartCleaning(context);
        }



    }



    public static void setAlarm(Context context) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.cancel(alarmIntent);

        PrefManager prefManager = new PrefManager(MainActivity.context);
        long timer = AlarmManager.INTERVAL_HOUR;
        timer = 5000;
        if(prefManager.getClearPeriod().equals("0"))
        {

        }
        else if(prefManager.getClearPeriod().equals("1"))
        {
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    timer,
                    timer, alarmIntent);
        }
        else if(prefManager.getClearPeriod().equals("2"))
        {
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    timer * 2,
                    timer * 2, alarmIntent);
        }
        else if(prefManager.getClearPeriod().equals("3"))
        {
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    timer * 3,
                    timer * 3, alarmIntent);
        }

    }


    public static void cancleAlarm(Context context) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.cancel(alarmIntent);
    }

}
