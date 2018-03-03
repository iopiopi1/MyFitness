package cardam2.cardam2;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.text.Html;

import java.util.List;

import MyFitness.KeyValueList;
import java.util.ArrayList;

public class UserPoliciesActivity extends AppCompatActivity {

    private Boolean statementAccepted = false;
    private Boolean secPolicyAccepted = false;
    private String curType = null;
    private DBHelper db;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_policies);

        mActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        db = new DBHelper(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                statementAccepted = extras.getBoolean("statementAccepted");
                secPolicyAccepted = extras.getBoolean("secPolicyAccepted");
            }
        }

        TextView tv = (TextView) findViewById(R.id.textView3);
        if(statementAccepted == false){
            tv.setText(Html.fromHtml(getResources().getString(R.string.agreement)));
            curType = "statementAccepted";
        }
        else if(secPolicyAccepted == false){
            tv.setText(Html.fromHtml(getResources().getString(R.string.confidentiality)));
            curType = "secPolicyAccepted";
        }

        Button decline =  (Button) findViewById(R.id.decline);
        decline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db.deleteCurUser(db.dbMyFitness);
                Intent intent = new Intent(mActivity, LoginActivity.class);
                startActivityForResult(intent, 3);
            }
        });

        Button accept =  (Button) findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {

            if(curType == "secPolicyAccepted"){
                db.updateCPAcceptence(db.dbMyFitness);
                List<KeyValueList> postParams = db.getCurUser(db.dbMyFitness);
                String cardamUrl = getResources().getString(R.string.cardamUrl);
                String cardamUrlUpdPolicies = cardamUrl + getResources().getString(R.string.cardamUrlUpdPolicies);
                PostTask jt = new PostTask(cardamUrlUpdPolicies, mActivity, postParams, R.id.const111, -1);
                jt.execute();
                Intent intent = new Intent(mActivity, MainActivity.class);
                startActivity(intent);
            }
            if(curType == "statementAccepted"){
                db.updateUSAcceptence(db.dbMyFitness);
                ConstraintLayout cstr = (ConstraintLayout) findViewById(R.id.const111);
                TextView tv = cstr.findViewById(R.id.textView3);
                tv.setText(Html.fromHtml(getResources().getString(R.string.confidentiality)));
                ScrollView scrl = cstr.findViewById(R.id.scrollview1);
                scrl.fullScroll(ScrollView.FOCUS_UP);
                cstr.invalidate();
                curType = "secPolicyAccepted";
            }
            }
        });


    }
}
