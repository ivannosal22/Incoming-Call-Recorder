package com.edison4mobile.utils;

import android.content.Context;

import com.edison4mobile.callrecorder.AppPreferences;
import com.edison4mobile.callrecorder.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

/**
 * Created by don on 3/17/2017.
 */

public class ManageInfoFile {
    private Context appContext;

    public String infofilename;
    public String filepath;
    public ManageInfoFile(Context context, String filename)
    {
        appContext = context;
        infofilename = filename;
    }
    public void wrtieFileOnInternalStorage(String body)
    {



        File file = new File(AppPreferences.getInstance(MainActivity.context).getFilesDirectory(),infofilename);
        filepath = file.getAbsolutePath() + "/" + infofilename;
        if(!file.exists()){
            file.mkdir();
        }

        try{
            File gpxfile = new File(file, infofilename);
            OutputStreamWriter outStreamWriter = null;
            FileOutputStream outStream = null;
            outStream = new FileOutputStream(gpxfile, true) ;
            outStreamWriter = new OutputStreamWriter(outStream);
            outStreamWriter.append(body);
            outStreamWriter.flush();

        }catch (Exception ignored){

        }
    }

}
