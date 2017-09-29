package cardam.cardam;

/**
 * Created by iopiopi on 8/3/17.
 */
import android.app.Activity;
import android.content.Intent;
import 	android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.EditText;

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

import static android.app.Activity.RESULT_OK;

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
    private String RegStatus = "";

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
                    db.addCurUser(db.dbMyFitness, postDataParams.getString("username"), postDataParams.getString("password"), postRespond.getInt("id"));
                    mActivity.finish();
                }
                if(type == PostTask.CHECKLOGINTYPE){
                    if(!postDataParams.getString("isOld").equals("yes")) {
                        db.addCurUser(db.dbMyFitness, postDataParams.getString("username"), postDataParams.getString("password"), postRespond.getInt("id"));
                        List<KeyValueList> postParams = db.getCurUser(db.dbMyFitness);
                        int i = 1;
                        mActivity.finish();
                    }
                    else{
                        Intent intent = new Intent(mActivity, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        mActivity.startActivityForResult(intent, 1);
                    }
                }
                if(type == PostTask.REGTYPE){

                    RegStatus = "SENT";
                    Intent intent = mActivity.getIntent();
                    intent.putExtra("REGISTRATION", "SENT");
                    mActivity.setResult(RESULT_OK, intent);
                    mActivity.finish();
                }
                if(type == PostTask.RESTORETYPE){
                    RegStatus = "SENT";
                    Intent intent = mActivity.getIntent();
                    intent.putExtra("RESTORING", "SENT");
                    mActivity.setResult(RESULT_OK, intent);
                    mActivity.finish();
                }

            }
            else{
                Snackbar snackbar = Snackbar.make(mActivity.findViewById(viewId), postRespond.getString("errorMsg"), Snackbar.LENGTH_LONG);

                if(type == PostTask.LOGINTYPE){
                    snackbar.show();
                }
                if(type == PostTask.CHECKLOGINTYPE){
                    if(!postDataParams.getString("isOld").equals("yes")) {
                        snackbar.show();
                    }
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    mActivity.startActivityForResult(intent, 1);
                }
                if(type == PostTask.REGTYPE){
                    snackbar.show();

                }
                if(type == PostTask.RESTORETYPE){
                    snackbar.show();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(type == PostTask.CHECKLOGINTYPE) {
            //ConstraintLayout rlMain = (ConstraintLayout) mActivity.findViewById(R.id.constraintLayout2);
            //rlMain.setVisibility(ConstraintLayout.VISIBLE);
            EditText et = (EditText) mActivity.findViewById(R.id.regnumTv);
            et.setVisibility(EditText.VISIBLE);
            Button bt = (Button) mActivity.findViewById(R.id.searchBt);
            bt.setVisibility(Button.VISIBLE);

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
            db.addCurUser(db.dbMyFitness, postDataParams.getString("username"), postDataParams.getString("password"), postDataParams.getInt("id"));
        }
    catch (JSONException e) {
        e.printStackTrace();
    }
    }

    public String getRegStatus(){
        return RegStatus;
    }

}