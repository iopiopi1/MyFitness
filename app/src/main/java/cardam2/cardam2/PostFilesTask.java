package cardam2.cardam2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

import android.support.design.widget.Snackbar;
import android.util.Log;

/**
 * Created by iopiopi on 8/11/17.
 */

public class PostFilesTask extends AsyncTask<String, Integer, String> {

    private String URL;
    private List<File> imgs;
    private Map<String, String> postParams;
    private JSONObject resultJSON;
    private String result;
    private String resultToAct;
    private DBHelper db;
    private Context parActivity;
    private String status;
    private int percentage;
    private boolean isLast;

    public PostFilesTask(String sendUrl, List<File> photos, Context mActivity, int perc, boolean last) {
        URL = sendUrl;
        imgs = photos;
        Log.e("image was photos1:",photos.toString());
        Log.e("image was photos2:",imgs.toString());
        parActivity = mActivity;
        db = new DBHelper(parActivity);
        int userId = db.getCurUserId(db.dbMyFitness);
        status = "Not executed";

        postParams = new HashMap<String, String>();
        postParams.put("vehicle_id", "");
        postParams.put("vehicle_uid", "");
        postParams.put("userId", Integer.toString(userId));
        resultToAct = "notFinished";
        percentage = perc;
        isLast = last;
    }


    @Override
    protected String doInBackground(String... data) {

        Log.e("image was photos2:",imgs.toString());
        publishProgress();
        String result = multipartRequest(URL, postParams, imgs, "files[f]", "image/jpeg");
        /*publishProgress();
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //String result = "";
        status = "Executed";
        return result;
    }

    public String multipartRequest(String urlTo, Map<String, String> parmas, List<File> photos, String filefield, String fileMimeType) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        Log.e("image was photos3:",photos.toString());

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        Log.e("image count", Integer.toString(photos.size()));

        if(photos.size() > 0) {
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
                for (int i = 0; i < photos.size(); i++) {
                    if (i == 6) {
                        break;
                    }
                    //File file = new File(filepath);

                    File file = photos.get(i);
                    Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    bmp = ImageHelper.checkOrientation(bmp, file.getAbsolutePath());
                    file = photos.set(i, ImageHelper.bitmapToJpeg(file.getAbsolutePath(), bmp));

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

                    //if (outputStream != null) {
                        outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                        outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(value);
                        outputStream.writeBytes(lineEnd);
                    //}

                }

                //if (outputStream != null) {
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                //}

                if (200 != connection.getResponseCode()) {
                    //throw new Exception("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
                }

                inputStream = connection.getInputStream();

                result = this.convertStreamToString(inputStream);
                Log.e("image result:", result);
                Log.e("image post params:", parmas.toString());
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                connection.disconnect();

            } catch (IOException e) {Log.e("image error:", "Smth happened");
                e.printStackTrace();
            }

        }
        else{
            Log.e("WARNING", "фотки не подгрузились");
        }
        return result;

    }

    protected void onProgressUpdate(Integer... progress) {
        PhotoActivity photoActivity = (PhotoActivity)parActivity;
        photoActivity.addpicsFragment.turnOnProgressBar(percentage);
        Log.e("progress", String.valueOf(percentage));
    }

    @Override
    protected void onPostExecute(String strings) {
        super.onPostExecute(strings);
        try {
            resultJSON = new JSONObject(strings);
            Log.e("image:", strings);
            result = resultJSON.getString("status");
            resultToAct = resultJSON.getString("status");
            if(!resultToAct.equals("success")){
                PostFilesTask pe = new PostFilesTask(URL, imgs, parActivity, percentage, isLast);
                pe.execute();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        db.uploadPhoto(db.dbMyFitness);
        PhotoActivity photoActivity = (PhotoActivity)parActivity;
        AddPicsFragment addpicsFragment = photoActivity.addpicsFragment;
        if(isLast) {
            photoActivity.addpicsFragment.turnOffProgressBar();
            Snackbar snackbar = Snackbar.make(photoActivity.findViewById(R.id.constraintLayout3), R.string.photos_success_upload, Snackbar.LENGTH_LONG);
            addpicsFragment.photos.clear();
            addpicsFragment.reloadPhotos();
            snackbar.show();
            photoActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            photoActivity.invalidateOptionsMenu();
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
    public String getStatuss() {
        return resultToAct;
    }


}
