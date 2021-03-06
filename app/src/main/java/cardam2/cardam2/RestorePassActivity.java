package cardam2.cardam2;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;

import java.util.List;

import MyFitness.KeyValueList;

public class RestorePassActivity extends AppCompatActivity {

    public Snackbar snackbar;
    private EditText emailET;
    private List<KeyValueList> postParams;
    private Activity activity;
    private String cardamUrlRestoreEmail;
    private String cardamUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_pass);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        activity = this;


        Button submitBt =  (Button) findViewById(R.id.restoreEmailBT);
        submitBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            emailET = (EditText) findViewById(R.id.restoreEmailET);
            if(emailET.getText().length() < 2){
                snackbar = Snackbar.make(findViewById(R.id.restorePassLayout), R.string.snackbarUserMailWrong, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            else{
                String emailStr = emailET.getText().toString();
                postParams = new ArrayList<KeyValueList>();
                postParams.add(0, new KeyValueList("email", emailStr));
                PostTask jt = new PostTask(cardamUrlRestoreEmail, activity, postParams, R.id.restore_email_Linear, PostTask.RESTORETYPE);
                jt.execute();
            }
            }
        });

    }

    public void init(){
        cardamUrl = getResources().getString(R.string.cardamUrl);
        cardamUrlRestoreEmail = cardamUrl + getResources().getString(R.string.cardamUrlRestoreEmail);
    }

}
