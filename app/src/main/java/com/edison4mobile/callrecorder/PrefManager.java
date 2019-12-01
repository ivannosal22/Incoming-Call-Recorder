package com.edison4mobile.callrecorder;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lincoln on 05/05/16.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "PREF_PHONE";

    private static final String IS_FILE_UPLOADING = "is_file_uploading";
    private static final String IS_ENABLE_DRIPPING_INCOMING_CALL = "is_enabled_dropping_call";
    private static final String URL_LOCATION = "url_location";
    private static final String STORAGE_PLACE = "storage_place";
    public static final String CLEAR_PERIOD = "clear_period";
    public static final String UPLOAD_PERIOD = "upload_period";
    private static final String CURRENT_FILENAME = "current_filename";
    private static final String CURRENT_FILE_UPLOADED = "current_file_uploaded";
    public PrefManager(Context context) {
        this._context = context;

        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void putFirstOutgoingDoneCalling(boolean b) {
        editor.putBoolean(IS_FILE_UPLOADING, b);
        editor.commit();
    }

    public boolean getFirstOutgoingDoneCalling() {
        return pref.getBoolean(IS_FILE_UPLOADING, false);
    }

    public void setDroppingEnable(boolean checked) {
        editor.putBoolean(IS_ENABLE_DRIPPING_INCOMING_CALL, checked);
        editor.commit();
    }
    public boolean isDroppingEnabled()
    {
        return pref.getBoolean(IS_ENABLE_DRIPPING_INCOMING_CALL, false);
    }

    public void setURL(String url) {
        editor.putString(URL_LOCATION, url);
        editor.commit();
    }
    public String getUrl()
    {
        return pref.getString(URL_LOCATION, "http://edison4mobile.kjbsoft.com/call_recorder/uploaded.php");
    }

    public void setStoragePlace(String storage_place) {
        editor.putString(STORAGE_PLACE, storage_place);
        editor.commit();
    }
    public String getStoragePlace()
    {
        return pref.getString(STORAGE_PLACE, "Internal_Storage");
    }

    public void setClearPeriod(String clearperiod) {
        editor.putString(CLEAR_PERIOD, clearperiod);
        editor.commit();
    }
    public String getClearPeriod()
    {
        return pref.getString(CLEAR_PERIOD, "0");
    }

    public void setUploadPeriod(String uploadPeriod) {
        editor.putString(UPLOAD_PERIOD, uploadPeriod);
        editor.commit();
    }
    public String getUploadPeriod()
    {
        return pref.getString(UPLOAD_PERIOD, "0");
    }
    public void setCurrentFileName(String fileName)
    {
        editor.putString(CURRENT_FILENAME , fileName);
        editor.commit();
    }
    public String getCurrentFileName()
    {
        return pref.getString(CURRENT_FILENAME, "initial");
    }
    public boolean isCurrentFileUploaded()
    {
        return pref.getBoolean(CURRENT_FILE_UPLOADED, false);
    }
    public void setCurrentFileUpload(boolean flag)
    {
        editor.putBoolean(CURRENT_FILE_UPLOADED, flag);
        editor.commit();
    }
}
