package cardam.cardam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import MyFitness.KeyValueList;

public class LoginActivity extends AppCompatActivity {

    private String cardamUrl;
    private String cardamUrlCheckLogin;
    private EditText loginNameET;
    private EditText loginPassET;
    private Activity activity;
    private Context context;
    public Snackbar snackbar;
    private List<KeyValueList> postParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        activity = this;
        context = getApplicationContext();
        init();
        loginNameET = (EditText) findViewById(R.id.loginNameET);
        loginPassET = (EditText) findViewById(R.id.loginPassET);

        Button submitBt =  (Button) findViewById(R.id.button3);
        submitBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            if(loginNameET.getText().length() < 2 || loginPassET.getText().length() < 2){
                snackbar = Snackbar.make(findViewById(R.id.cont_login_LinearL), R.string.snackbarLoginWrongInp, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            else{
                String loginNameStr = loginNameET.getText().toString();
                String loginPassStr = loginPassET.getText().toString();
                postParams = new ArrayList<KeyValueList>();
                postParams.add(0, new KeyValueList("username", loginNameStr));
                postParams.add(1, new KeyValueList("password", loginPassStr));
                postParams.add(2, new KeyValueList("isOld", "no"));
                PostTask jt = new PostTask(cardamUrlCheckLogin, activity, postParams, R.id.cont_login_LinearL, PostTask.LOGINTYPE);
                jt.execute();

            }
            }
        });

        TextView tv = (TextView) findViewById(R.id.textView2);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, RestorePassActivity.class);
                startActivityForResult(intent, 1);

            }

        });

        TextView registerTv = (TextView) findViewById(R.id.registerTv);
        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, RegisterActivity.class);
                startActivityForResult(intent, 2);

            }

        });

    }

    @Override
    public void setTitle(CharSequence title) {

    }

    public void init(){
        cardamUrl = getResources().getString(R.string.cardamUrl);
        cardamUrlCheckLogin = cardamUrl + getResources().getString(R.string.cardamUrlCheckLogin);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("RESTORING");
                if(result.equals("SENT")){
                    Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.cont_login_LinearL), R.string.snackbarUserRestoreSuccess, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }

        if (requestCode == 2) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("REGISTRATION");
                if(result.equals("SENT")){
                    Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.cont_login_LinearL), R.string.snackbarUserRegSuccess, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }
    }

}
