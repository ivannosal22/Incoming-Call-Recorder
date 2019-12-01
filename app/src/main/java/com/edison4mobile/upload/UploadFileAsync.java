package com.edison4mobile.upload;

import android.content.Context;
import android.os.AsyncTask;

import com.edison4mobile.callrecorder.MainActivity;
import com.edison4mobile.callrecorder.PrefManager;
import com.edison4mobile.services.BulkTransfer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadFileAsync extends AsyncTask<String, Void, String> {
    private int serverResponseCode = 0;
    public String sendfileurl;
    private Context serContext;
    @Override
    protected String doInBackground(String... params) {

        try
        {
            try {
                String sourceFileUri;
                sourceFileUri = sendfileurl;
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile;

                sourceFile = new File(sourceFileUri);
                if(sourceFile == null)
                {
                    BulkTransfer.bstart = false;
                    return "Executed";
                }
              if (sourceFile.isFile()) {

                    try {
                        PrefManager prefManager = new PrefManager(MainActivity.context);
                        String upLoadServerUri = prefManager.getUrl();

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(upLoadServerUri);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("bill", sourceFileUri);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
                                + sourceFileUri + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        // Responses from the server (code and message)
                        serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();

                        if (serverResponseCode == 200) {

                            prefManager.setCurrentFileUpload(true);



                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();
                        BulkTransfer.bstart = false;
                    } catch (Exception e) {
                        BulkTransfer.bstart = false;
                        // dialog.dismiss();
                        e.printStackTrace();

                    }
                    // dialog.dismiss();
                    BulkTransfer.bstart = false;
                } // End else block


            } catch (Exception ex) {
                // dialog.dismiss();
                BulkTransfer.bstart = false;
                ex.printStackTrace();
            }
        }catch (NullPointerException ignored)
        {

        }
        BulkTransfer.bstart = false;
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    public UploadFileAsync(Context context) {
        serContext = context;
    }
}
