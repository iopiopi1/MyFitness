package com.example.iopiopi.myfitness;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.net.HttpURLConnection;
import java.io.DataOutputStream;
import java.util.Map;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import MyFitness.KeyValueList;

/**
 * Created by iopiopi on 8/11/17.
 */

public class PostFilesTask extends AsyncTask<String, String, String> {

    private String URL;
    private List<File> imgs;
    private Map<String, String> postParams;
    private JSONObject resultJSON;
    private String result;

    public PostFilesTask(String sendUrl, List<File> photos) {
        URL = sendUrl;
        imgs = photos;

        /*postParams = new ArrayList<KeyValueList>();
        postParams.add(0, new KeyValueList("vehicle_uid", null));
        postParams.add(1, new KeyValueList("vehicle_id", null));*/

        postParams = new HashMap<String, String>();
        postParams.put("vehicle_id", "");
        postParams.put("vehicle_uid", "");
    }


    @Override
    protected String doInBackground(String... data) {

        String result = multipartRequest(URL, postParams, imgs, "files[f]", "image/jpeg");

        return result;
    }

    public String multipartRequest(String urlTo, Map<String, String> parmas, List<File> photos, String filefield, String fileMimeType) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;


        try {


            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            for (int i=0; i < photos.size(); i++) {
                if(i == 9){
                    break;
                }
                //File file = new File(filepath);
                File file = photos.get(i);
                FileInputStream fileInputStream = new FileInputStream(file);

                String[] q = file.getName().split("/");
                int idx = q.length - 1;

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                fileInputStream.close();
            }

            // Upload POST Data
            Iterator<String> keys = parmas.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = parmas.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            if (200 != connection.getResponseCode()) {
                //throw new Exception("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);


            inputStream.close();
            outputStream.flush();
            outputStream.close();

        }  catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }


    @Override
    protected void onPostExecute(String strings) {
        super.onPostExecute(strings);
        try {
            resultJSON = new JSONObject(strings);
            result = resultJSON.getString("status");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    private String getStatuss() {
        return result;
    }

}
