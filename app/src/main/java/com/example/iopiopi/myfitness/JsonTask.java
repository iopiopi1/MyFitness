package com.example.iopiopi.myfitness;

import android.app.Activity;
import android.content.Intent;
import 	android.os.AsyncTask;
import android.content.Context;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import android.view.View;
import com.bumptech.glide.Glide;
import android.content.Intent;

/**
 * Created by root on 7/20/17.
 */

public class JsonTask extends AsyncTask<Void, Void, String> {
    private Context mContext;
    private String mUrl;
    private JSONObject respond;
    private String resultString;
    private JSONObject jsonRespSearchVehicle;
    private DBHelper db;
    private LinearLayout rlSearch;
    private ArrayList<ImageView> images;
    private Activity parActivity;
    private int curType;

    private String regnum;

    public static final int MULTTYPE = 0;
    public static final int SINGLETYPE = 1;

    public JSONObject getRespond() {


        return respond;
    }

    public void setRespond(JSONObject respond) {
        this.respond = respond;
    }



    public JsonTask(Context context, DBHelper dbs, String url,
                    ArrayList<ImageView> imgs, LinearLayout rls, Activity act, int type) {
        mContext = context;
        mUrl = url;
        db = dbs;
        rlSearch = rls;
        images = imgs;
        parActivity = act;
        curType = type;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        this.resultString = getJSON(mUrl);
        return resultString;
    }

    @Override
    protected void onPostExecute(String strings) {
        super.onPostExecute(strings);
        int uqId;
        //db.addJsonResultDatabase(db.dbMyFitness, strings);
        if(curType == JsonTask.MULTTYPE) {
            try {
                jsonRespSearchVehicle = new JSONObject(strings);
                JSONArray vehicles = jsonRespSearchVehicle.getJSONArray("vehicles");
                for (int i = 0; i < vehicles.length(); i++) {
                    JSONObject jsonobject = vehicles.getJSONObject(i);
                    String url = jsonobject.getString("imagePath");
                    final int curRegnumId = jsonobject.getInt("vehicleId");
                    ImageView targetImageView = new ImageView(mContext);
                    uqId = db.addUniqueId(db.dbMyFitness);
                    targetImageView.setId(uqId);
                    targetImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, RegnumActivity.class);
                            intent.putExtra("curRegnumId", curRegnumId);
                            parActivity.startActivityForResult(intent, 1);
                        }
                    });
                    images.add(targetImageView);
                    String cardamUrl = mContext.getResources().getString(R.string.cardamUrl);
                    Glide
                            .with(mContext)
                            .load(cardamUrl + "/" + url)
                            .into(targetImageView);
                    rlSearch.addView(targetImageView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(curType == JsonTask.SINGLETYPE) {
            try {
                jsonRespSearchVehicle = new JSONObject(strings);
                JSONArray imgs = jsonRespSearchVehicle.getJSONArray("images");
                JSONObject vehicles = jsonRespSearchVehicle.getJSONObject("vehicles"); //getJSONArray("vehicles");
                regnum = vehicles.getString("regnum");
                //regnum = vehicles.getString(1); //jsonRespSearchVehicle.getString("regnum");
                TextView txRegnum = (TextView) parActivity.findViewById(R.id.regnumTV);
                txRegnum.setText(regnum);
                for (int i = 0; i < imgs.length(); i++) {
                    ImageView targetImageView = new ImageView(mContext);
                    uqId = db.addUniqueId(db.dbMyFitness);
                    targetImageView.setId(uqId);
                    images.add(targetImageView);
                    JSONObject jsonobject = imgs.getJSONObject(i);
                    String url = jsonobject.getString("imagePath");
                    String cardamUrl = mContext.getResources().getString(R.string.cardamUrl);
                    Glide
                            .with(mContext)
                            .load(cardamUrl + "/" + url)
                            .into(targetImageView);
                    rlSearch.addView(targetImageView);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void startActivityForResult(Intent intent, int i) {
    }

    public String getJSON(String url) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.connect();
            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
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

    private JSONObject getJsonRespond(){
        Log.e("My App", resultString);
        try {
            respond = new JSONObject(resultString);

        } catch (JSONException e) {
            Log.i("My App", resultString);
        }

        return respond;
    }

}