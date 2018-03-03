package cardam2.cardam2;

/**
 * Created by iopiopi on 8/3/17.
 */
import android.app.Activity;
import android.content.Intent;
import 	android.os.AsyncTask;
import android.support.design.widget.Snackbar;

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
import java.util.Date;
import java.text.SimpleDateFormat;

import MyFitness.KeyValueList;

import static android.app.Activity.RESULT_OK;
import java.text.ParseException;
import java.util.TimeZone;

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
    public static final int UPDATEPOLICIES = 4;
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
                    db.addCurUser(db.dbMyFitness, postDataParams.getString("username"), postDataParams.getString("password"), postRespond.getInt("id"),  postRespond.getString("email"));
                    mActivity.finish();
                    Boolean statementAccepted = postRespond.getBoolean("userStAccd");
                    Boolean secPolicyAccepted = postRespond.getBoolean("secPolAccd");
                    if(statementAccepted == false || secPolicyAccepted == false){
                        Intent intent = new Intent(mActivity, UserPoliciesActivity.class);
                        intent.putExtra("statementAccepted", statementAccepted);
                        intent.putExtra("secPolicyAccepted", secPolicyAccepted);
                        mActivity.startActivityForResult(intent, 1);
                    }

                }
                if(type == PostTask.CHECKLOGINTYPE){
                    String status = checkLoginStatus(postRespond.getString("passTime"), postDataParams.getString("loggedTime"));
                    if(!status.equals("yes")) {
                        List<KeyValueList> postParams = db.getCurUser(db.dbMyFitness);
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
                    Snackbar snackbar = Snackbar.make(mActivity.findViewById(viewId), R.string.snackbarUserRegSuccess, Snackbar.LENGTH_LONG);
                    snackbar.show();
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
                    String status = "";
                    if(postRespond.has("passTime") && postDataParams.has("loggedTime")) {
                        status = checkLoginStatus(postRespond.getString("passTime"), postDataParams.getString("loggedTime"));
                    }
                    if(!status.equals("yes")) {
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
            c.setConnectTimeout(3000);

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
            return "{\"state\":\"failed\",\"errorMsg\" : \"Произошла ошибка " + ex.toString() +"\"}";
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    //disconnect error
                    return "{\"state\":\"failed\",\"errorMsg\" : \"Произошла ошибка " + ex.toString() +"\"}";
                }
            }

        }
        return "{\"state\":\"failed\",\"errorMsg\" : \"Произошла ошибка\"}";
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
            db.addCurUser(db.dbMyFitness, postDataParams.getString("username"), postDataParams.getString("password"), postDataParams.getInt("id"), postDataParams.getString("email"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getRegStatus(){
        return RegStatus;
    }

    public String checkLoginStatus(String passChangedTimeServ, String passLoggedLoc){
        String isOld = null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dtpassChangedTimeServ = null;
        Date dtpassLoggedLoc = null;
        try {
            dtpassChangedTimeServ = simpleDateFormat.parse(passChangedTimeServ);
            dtpassLoggedLoc = simpleDateFormat.parse(passLoggedLoc);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(dtpassChangedTimeServ.after(dtpassLoggedLoc)){
            isOld = "yes";
        }
        else{
            isOld = "no";
        }


        return isOld;
    }

}