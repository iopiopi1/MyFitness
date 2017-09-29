package cardam.cardam;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.content.Intent;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public class RegnumActivity extends AppCompatActivity {

    public String regnumSearch;
    public TextView regnumTv;
    public String cardamUrl;
    public String cardamUrlViewRegnum;
    public String cardamUrlSearchRegnumCurrent;
    public JSONObject jsonRespSearchVehicle;
    public LinearLayout rlRegnum;
    public ArrayList<ImageView> images;
    public DBHelper db;
    public Activity activity;
    public int curRenumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_view_regnum);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        JsonTask jt = new JsonTask(this, db, cardamUrlViewRegnum, images, rlRegnum, activity, JsonTask.SINGLETYPE);
        jt.execute();

    }

    protected void init() {
        curRenumId = getIntent().getIntExtra("curRegnumId", 1);
        cardamUrl = getResources().getString(R.string.cardamUrl);
        cardamUrlViewRegnum = cardamUrl + getResources().getString(R.string.cardamUrlViewRegnum) + '/' + curRenumId;
        rlRegnum = (LinearLayout) findViewById(R.id.viewRegnumLinear);
        rlRegnum.setOrientation(LinearLayout.VERTICAL);
        images = new ArrayList<ImageView>();
        db = new DBHelper(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                LinearLayout rl = (LinearLayout) findViewById(R.id.addMeasuresLinear);
                String date = data.getStringExtra("datePicked");
            }
        }
        else if(requestCode == 2){
            if(resultCode == RESULT_OK) {
                finish();
                startActivity(getIntent());
            }
        }*/
    }



}
