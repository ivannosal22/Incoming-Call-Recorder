package com.edison4mobile.callrecorder;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.edison4mobile.receivers.MyAlarmReceiver;
import com.edison4mobile.services.BulkTransfer;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    public static Context context;
    private Handler handler = new Handler();
    public PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();

        AppPreferences preferences = AppPreferences.getInstance(this);
        preferences.setRecordingOutgoingEnabled(true);
        preferences.setRecordingIncomingEnabled(true);
        AppPreferences.getInstance(MainActivity.this).setRecordingEnabled(true);
        Intent bulkIntent = new Intent(this, BulkTransfer.class);
        startService(bulkIntent);
        PrefManager pref = new PrefManager(this);
        if(!pref.getFirstOutgoingDoneCalling()){
            showMessageAlert();
        }
        prefManager = new PrefManager(this);
        context = this;
        MyAlarmReceiver.setAlarm(this);


    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        initControl();
    }

    private void selectSpinnerValue(Spinner spinner, String myString)
    {
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equals(myString)){
                spinner.setSelection(i);
                break;
            }
        }
    }
    private void initControl() {
        Spinner spinner_storage = (Spinner)findViewById(R.id.spinner_storage_place);
        Spinner spinner_clear = (Spinner)findViewById(R.id.spinner_clear_period);
        Spinner spinner_upload = (Spinner)findViewById(R.id.spinner_upload_period);
        ((Switch)findViewById(R.id.switch_dropping_call)).setChecked(prefManager.isDroppingEnabled());
        ((EditText)findViewById(R.id.url_location)).setText(prefManager.getUrl());




        findViewById(R.id.save_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);

        List<String> list_storage = new ArrayList<>();
        list_storage.add("Internal_Storage");
        list_storage.add("External_Storage");
        ArrayAdapter<String> storage_spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_storage);
        spinner_storage.setAdapter(storage_spinner_adapter);


        List<String> list_clear = new ArrayList<>();
        list_clear.add("0");
        list_clear.add("1");
        list_clear.add("2");
        list_clear.add("3");
        ArrayAdapter<String> clear_spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_clear);
        spinner_clear.setAdapter(clear_spinner_adapter);


        List<String> list_upload = new ArrayList<>();
        list_upload.add("0");
        list_upload.add("1");
        list_upload.add("2");
        list_upload.add("3");
        ArrayAdapter<String> upload_spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_upload);
        spinner_upload.setAdapter(upload_spinner_adapter);

        selectSpinnerValue(spinner_storage, prefManager.getStoragePlace());
        selectSpinnerValue(spinner_clear, prefManager.getClearPeriod());
        selectSpinnerValue(spinner_upload, prefManager.getUploadPeriod());
    }

    public void showMessageAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);


        alertDialog.setTitle("Welcome to my app!");
        alertDialog.setInverseBackgroundForced(true);

        alertDialog.setMessage("Fist you have to make outgoing call!\n And restart App!");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
                finish();
            }
        });

        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                showMessageAlert();
            }
        });

        alertDialog.show();
    }
    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return false;
    }

    Menu optionsMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    boolean doubleBackToExitPressedOnce;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        // Does the user really want to exit?
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.press_back_again), Toast.LENGTH_LONG).show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (null != intent) {
            // User clicked the {@link RecordCallService#displayNotification} to listen to the recording
            long id = intent.getIntExtra("RecordingId", -1);
            if (-1 != id) {

                intent.putExtra("RecordingId", -1); // run only once...
            }
        }
    }


    boolean permissionReadContacts;
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void checkPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS,  Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE,
        Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                Manifest.permission.MODIFY_AUDIO_SETTINGS, "READ_PRECISE_PHONE_STATE", "READ_PROFILE",
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0x01: {  // validateRequestPermissionsRequestCode in FragmentActivity requires requestCode to be of 8 bits, meaning the range is from 0 to 255.
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) { // we only asked for one permission
                        permissionReadContacts = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.save_button:
                if(((EditText)findViewById(R.id.url_location)).getText().toString().equals(""))
                {
                    Toast.makeText(this, "please enter the url", Toast.LENGTH_LONG).show();
                    break;
                }
                Spinner spinner_storage = (Spinner)findViewById(R.id.spinner_storage_place);
                prefManager.setURL(((EditText)findViewById(R.id.url_location)).getText().toString());
                prefManager.setStoragePlace(String.valueOf(spinner_storage.getSelectedItem()));
                Spinner spinner_clear = (Spinner)findViewById(R.id.spinner_clear_period);

                prefManager.setClearPeriod(String.valueOf(spinner_clear.getSelectedItem()));
                Spinner spinner_upload = (Spinner)findViewById(R.id.spinner_upload_period);

                prefManager.setUploadPeriod(String.valueOf(spinner_upload.getSelectedItem()));
                prefManager.setDroppingEnable(((Switch)findViewById(R.id.switch_dropping_call)).isChecked());
                Intent intent = new Intent(this, BulkTransfer.class);
                stopService(intent);
                startService(intent);
                MyAlarmReceiver.cancleAlarm(this);
                MyAlarmReceiver.setAlarm(this);
                finish();
                break;
            case R.id.cancel_button:
                finish();
                break;
        }
    }
}
