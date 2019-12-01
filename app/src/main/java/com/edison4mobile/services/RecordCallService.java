package com.edison4mobile.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.edison4mobile.callrecorder.LocalBroadcastActions;
import com.edison4mobile.callrecorder.MainActivity;
import com.edison4mobile.callrecorder.PrefManager;
import com.edison4mobile.callrecorder.R;
import com.edison4mobile.database.CallLog;
import com.edison4mobile.database.Database;
import com.edison4mobile.utils.GetNameHelper;
import com.edison4mobile.utils.ManageInfoFile;

import java.util.ArrayList;
import java.util.Calendar;


public class RecordCallService extends Service {

    public final static String ACTION_START_RECORDING = "com.jlcsoftware.ACTION_CLEAN_UP";
    public final static String ACTION_STOP_RECORDING = "com.jlcsoftware.ACTION_STOP_RECORDING";
    public final static String EXTRA_PHONE_CALL = "com.jlcsoftware.EXTRA_PHONE_CALL";
    public static ManageInfoFile manageInfoFile;
    public RecordCallService(){
    }

    @Override
    public void onCreate() {
        manageInfoFile = new ManageInfoFile(this, "Incoming_recorder_info");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(LocalBroadcastActions.NEW_RECORDING_BROADCAST));
        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ContentValues parcelableExtra = intent.getParcelableExtra(EXTRA_PHONE_CALL);
        startRecording(new CallLog(parcelableExtra));
        return START_NOT_STICKY ;
    }

    @Override
    public void onDestroy() {
        stopRecording();

        super.onDestroy();
    }

    private CallLog phoneCall;
    boolean isRecording = false;

    private void stopRecording() {

        if (isRecording) {
            try {
                if(!phoneCall.isOutgoing())
                {

                    Database database = Database.getInstance(getApplicationContext());
                    ArrayList<CallLog> array_list = database.getAllCalls();
                    boolean temp = false;
                    for(int i = 0 ; i < array_list.size() ; i ++)
                    {
                        if(array_list.get(i).getPhoneNumber().equals(phoneCall.getPhoneNumber()))
                        {
                            temp = true;
                        }
                    }
                    if(temp)
                        return;
                    PrefManager prefManager = new PrefManager(this);
                    manageInfoFile.infofilename = prefManager.getCurrentFileName();
                    manageInfoFile.wrtieFileOnInternalStorage(GetNameHelper.generateFileName(phoneCall) + "\n");
                }
                Toast.makeText(this, "StopRecording", Toast.LENGTH_SHORT).show();
                phoneCall.setEndTime(Calendar.getInstance());
                isRecording = false;

                phoneCall.save(getBaseContext());
                displayNotification(phoneCall);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(LocalBroadcastActions.NEW_RECORDING_BROADCAST));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        phoneCall = null;
    }




    private void startRecording(CallLog phoneCall) {
        if (!isRecording) {

            this.phoneCall = phoneCall;

            try {
                this.phoneCall.setSartTime(Calendar.getInstance());
                Toast.makeText(getApplicationContext(), "StartRecording", Toast.LENGTH_SHORT).show();
                isRecording = true;



            } catch (Exception e) {
                e.printStackTrace();
                isRecording = false;

                this.phoneCall = null;
                isRecording = false;
            }
        }
    }

    public void displayNotification(CallLog phoneCall) {
        PrefManager prefManager = new PrefManager(this);
        if(!prefManager.isDroppingEnabled())
            return;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_recording_conversation_white_24);
        builder.setContentTitle(getApplicationContext().getString(R.string.notification_title));
        builder.setContentText(getApplicationContext().getString(R.string.notification_text));
        builder.setContentInfo(getApplicationContext().getString(R.string.notification_more_text));
        builder.setAutoCancel(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis())); // fake action to force PendingIntent.FLAG_UPDATE_CURRENT
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.putExtra("RecordingId", phoneCall.getId());

        builder.setContentIntent(PendingIntent.getActivity(this, 0xFeed, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        notificationManager.notify(0xfeed, builder.build());
    }


    public static void sartRecording(Context context, CallLog phoneCall) {
        Intent intent = new Intent(context, RecordCallService.class);
        intent.setAction(ACTION_START_RECORDING);
        intent.putExtra(EXTRA_PHONE_CALL, phoneCall.getContent());
        context.startService(intent);
    }


    public static void stopRecording(Context context) {
        Intent intent = new Intent(context, RecordCallService.class);
        intent.setAction(ACTION_STOP_RECORDING);
        context.stopService(intent);

    }

}
