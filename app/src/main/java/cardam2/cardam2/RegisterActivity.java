package cardam2.cardam2;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import MyFitness.KeyValueList;
import android.view.inputmethod.InputMethodManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText regLoginEt;
    private EditText regEmailEt;
    private EditText regPassEt1;
    private EditText regPassEt2;
    public Snackbar snackbar;
    private int viewId;
    private List<KeyValueList> postParams;
    private String cardamUrl;
    private String cardamUrlRegister;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        activity = this;

        init();

        Button submitBt =  (Button) findViewById(R.id.regUserBt);
        submitBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View view = activity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                String regLoginStr = regLoginEt.getText().toString();
                String regEmailStr = regEmailEt.getText().toString();
                String regPassStr1 = regPassEt1.getText().toString();
                String regPassStr2 = regPassEt2.getText().toString();


                ;

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(regEmailStr).matches()){
                    Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.coordRegister), R.string.snackbarUserRegWrngEmail, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                if(!regPassStr1.equals(regPassStr2)){
                    snackbar = Snackbar.make(findViewById(viewId), R.string.snackbarPassEqualInp, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    return;
                }
                if(regLoginStr.length() < 2 || regEmailStr.length() < 2 ||
                regPassStr1.length() < 2 || regPassStr2.length() < 2    ){
                    snackbar = Snackbar.make(findViewById(viewId), R.string.snackbarLoginWrongInp, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    return;
                }
                else{
                    postParams = new ArrayList<KeyValueList>();
                    postParams.add(0, new KeyValueList("username", regLoginStr));
                    postParams.add(1, new KeyValueList("email", regEmailStr));
                    postParams.add(2, new KeyValueList("password", regPassStr1));
                    postParams.add(3, new KeyValueList("password_repeat", regPassStr2));
                    PostTask jt = new PostTask(cardamUrlRegister, activity, postParams, viewId, PostTask.REGTYPE);
                    jt.execute();

                    /*
                    if(jt.getStatus() == AsyncTask.Status.FINISHED){
                        String res = jt.getRegStatus();
                    }
                    if(jt.getRegStatus().equals("SENT")){
                        Snackbar snackbar = Snackbar.make(activity.findViewById(viewId), R.string.snackbarUserRegSuccess, Snackbar.LENGTH_LONG);
                    }*/
                }
            }
        });

    }

    public void init(){
        regLoginEt = (EditText) findViewById(R.id.regLoginEt);
        regEmailEt = (EditText) findViewById(R.id.regEmailEt);
        regPassEt1 = (EditText) findViewById(R.id.regPassEt1);
        regPassEt2 = (EditText) findViewById(R.id.regPassEt2);
        viewId = R.id.user_Reg_Linear;
        cardamUrl = getResources().getString(R.string.cardamUrl);
        cardamUrlRegister = cardamUrl + getResources().getString(R.string.cardamUrlRegister);
    }

}
