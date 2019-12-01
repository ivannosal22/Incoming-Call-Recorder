package com.edison4mobile.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.edison4mobile.callrecorder.MainActivity;
import com.edison4mobile.callrecorder.PrefManager;
import com.edison4mobile.callrecorder.R;
import com.edison4mobile.database.CallLog;
import com.edison4mobile.database.Database;
import com.edison4mobile.upload.UploadFileAsync;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BulkTransfer extends Service {
    private static Timer timer = new Timer();
    private Context ctx;
    public static boolean bstart = false;
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
        showStatusBarIcon();
    }
    private void showStatusBarIcon() {
        Context context = getApplicationContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context )
                .setSmallIcon(R.mipmap.ic_launcher);

        Intent intent = new Intent( context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 100 , intent, 0);
        builder.setContentIntent(pIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = builder.build();
        mNotificationManager.notify(100, notif);
    }
    private void startService()
    {
        long interval = 5000;
        PrefManager prefManager = new PrefManager(this);
        if(prefManager.getUploadPeriod().equals("0"))
        {
            timer.scheduleAtFixedRate(new mainTask(), 0, interval);
        }
        else if(prefManager.getUploadPeriod().equals("1"))
        {
            timer.scheduleAtFixedRate(new mainTask(), 0, interval * 2);
        }
        else if(prefManager.getUploadPeriod().equals("2"))
        {
            timer.scheduleAtFixedRate(new mainTask(), 0, interval * 3);
        }
        else
        {
            timer.scheduleAtFixedRate(new mainTask(), 0, interval * 4);
        }

    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
        }
    }

    public void onDestroy()
    {
        ((NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(100);
        super.onDestroy();

    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            if(RecordCallService.manageInfoFile != null)
            {

                UploadFileAsync uploadFileAsync = new UploadFileAsync(getApplicationContext());
                uploadFileAsync.sendfileurl = RecordCallService.manageInfoFile.filepath;
                bstart = true;
                uploadFileAsync.execute();
                while (bstart){};
            }



        }
    };
}