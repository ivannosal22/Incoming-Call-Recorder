package com.edison4mobile.listeners;

import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.edison4mobile.callrecorder.MainActivity;
import com.edison4mobile.callrecorder.PrefManager;
import com.edison4mobile.database.CallLog;
import com.edison4mobile.database.Database;
import com.edison4mobile.services.RecordCallService;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Jeff on 01-May-16.
 * <p/>
 * The logic is a little odd here...
 * <p/>
 * When a incoming call comes in, we get a CALL_STATE_RINGING that provides the incoming number and all is easy and good...
 * on the other hand, a Outgoing call generates a ACTION_NEW_OUTGOING_CALL with the phone number, then an a CALL_STATE_IDLE and then a
 * CALL_STATE_OFFHOOK when the call connects - we never get the outgoing number in the PhoneState Change
 * <p/>
 */
public class PhoneListener extends PhoneStateListener {

    private static PhoneListener instance = null;
    private static boolean ring;
    private static boolean callReceived;
    /**
     * Must be called once on app startup
     *
     * @param context - application context
     * @return
     */
    public static PhoneListener getInstance(Context context) {
        if (instance == null) {
            instance = new PhoneListener(context);
        }
        return instance;
    }

    public static boolean hasInstance() {
        return null != instance;
    }

    private final Context context;
    private CallLog phoneCall;

    private PhoneListener(Context context) {
        this.context = context;
    }

    AtomicBoolean isRecording = new AtomicBoolean();
    AtomicBoolean isWhitelisted = new AtomicBoolean();


    public void setOutgoing(String phoneNumber) {
        if (null == phoneCall)
            phoneCall = new CallLog();
        phoneCall.setPhoneNumber(phoneNumber);
        phoneCall.setOutgoing();

        isWhitelisted.set(Database.isWhitelisted(context, phoneCall.getPhoneNumber()));
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE: // Idle... no call
                if(ring && !callReceived)
                {

                    ring = false;
                    if (isRecording.get()) {

                        RecordCallService.stopRecording(context);
                        phoneCall = null;
                        isRecording.set(false);

                    }


                }
                callReceived = false;
                if (isRecording.get()) {

                    RecordCallService.stopRecording(context);
                    phoneCall = null;
                    isRecording.set(false);

                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                PrefManager pref = new PrefManager(context);
                pref.putFirstOutgoingDoneCalling(true);
                callReceived = true;
                if (isWhitelisted.get()) {
                    isWhitelisted.set(false);
                    return;
                }
                if (!isRecording.get()) {

                    isRecording.set(true);

                    if (null == phoneCall)
                        phoneCall = new CallLog();
                    if (!incomingNumber.isEmpty()) {
                        phoneCall.setPhoneNumber(incomingNumber);
                    }

                }
                break;
            case TelephonyManager.CALL_STATE_RINGING:

                ring = true;
                if (null == phoneCall)
                    phoneCall = new CallLog();
                if (!isRecording.get()) {
                    SystemClock.sleep(1000);
                    isRecording.set(true);


                    if (!incomingNumber.isEmpty()) {
                        phoneCall.setPhoneNumber(incomingNumber);
                        isWhitelisted.set(Database.isWhitelisted(context, phoneCall.getPhoneNumber()));
                    }


                    RecordCallService.sartRecording(context, phoneCall);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            disconnectCall();
                        }
                    }, 10000);
                }

                break;
        }

    }
    public void disconnectCall(){
        PrefManager prefManager = new PrefManager(MainActivity.context);
        if(!prefManager.isDroppingEnabled())
        {
            return;
        }
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
