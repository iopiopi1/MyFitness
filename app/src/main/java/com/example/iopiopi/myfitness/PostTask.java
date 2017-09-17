package com.example.iopiopi.myfitness;

/**
 * Created by iopiopi on 8/3/17.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import 	android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import 	java.io.DataOutputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import MyFitness.KeyValueList;

public class PostTask extends AsyncTask<String, String, String> {
    private String URL;
    private int viewId;
    private Activity mActivity;
    private int type;
    private JSONObject postDataParams;
    private DBHelper db;
    public static final int LOGINTYPE = 0;
    public static final int RESTORETYPE = 1;
    public static final int REGTYPE = 2;
    public static final int CHECKLOGINTYPE = 3;


    public PostTask(String sendUrl, Activity act, List<KeyValueList> postParams, int vViewId, int tType) {
        URL = sendUrl;
        mActivity = act;
        viewId = vViewId;
        postDataParams = new JSONObject();
        type = tType;
        try {
            for (int i = 0; i < postParams.size(); i++) {
                postDataParams.put(postParams.get(i).getKey(), postParams.get(i).getValue());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        db = new DBHelper(mActivity);
    }

    @Override
    protected String doInBackground(String... data) {
        String result = getJSON(URL);
        return result;
    }

    @Override
    protected void onPostExecute(String strings) {
        super.onPostExecute(strings);
        JSONObject postRespond;
        try {
            postRespond = new JSONObject(strings);
            String state = postRespond.getString("state");
            if(state.equals("success")){
                if(type == PostTask.LOGINTYPE){
                    db.addCurUser(db.dbMyFitness, postDataParams.getString("username"), postDataParams.getString("password"));
                    mActivity.finish();
                }
                if(type == PostTask.CHECKLOGINTYPE){

                }
                if(type == PostTask.REGTYPE){

                }
                if(type == PostTask.RESTORETYPE){

                }

            }
            else{
                Snackbar snackbar = Snackbar.make(mActivity.findViewById(viewId), postRespond.getString("errorMsg"), Snackbar.LENGTH_LONG);
                snackbar.show();
                if(type == PostTask.LOGINTYPE){

                }
                if(type == PostTask.CHECKLOGINTYPE){
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivityForResult(intent, 1);
                }
                if(type == PostTask.REGTYPE){

                }
                if(type == PostTask.RESTORETYPE){

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getJSON(String url) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("POST");
            c.setDoInput(true);
            c.setDoOutput(true);
            c.setReadTimeout(10000);
            c.setConnectTimeout(15000);

            DataOutputStream dStream = new DataOutputStream(c.getOutputStream());
            dStream.writeBytes(getPostDataString(postDataParams));
                dStream.flush();
                dStream.close();
            c.connect();
            String cc = c.getContent().toString();
            int status = c.getResponseCode();

            BufferedReader br;
            StringBuilder sb;
            String line;
            switch (status) {
                case 200:
                    br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
                case 201:
                    br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (Exception ex) {
            return ex.toString();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    //disconnect error
                }
            }
        }
        return null;
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public void setCurUserDB(JSONObject postDataParams){
        try {
            db.addCurUser(db.dbMyFitness, postDataParams.getString("username"), postDataParams.getString("password"));
        }
    catch (JSONException e) {
        e.printStackTrace();
    }
    }

}