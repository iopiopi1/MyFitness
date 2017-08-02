package com.example.iopiopi.myfitness;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.ListView;
import android.content.res.Configuration;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.view.ViewGroup.LayoutParams;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public String regnumSearch;
    public TextView regnumTv;
    public String cardamUrl;
    public String cardamUrlSearchRegnum;
    public String cardamUrlSearchRegnumCurrent;
    public JSONObject jsonRespSearchVehicle;
    public LinearLayout rlSearch;
    public ArrayList<ImageView> images;
    public DBHelper db;
    public Activity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.logo);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

        regnumTv = (TextView) findViewById(R.id.regnumTv);

        Button searchBt =  (Button) findViewById(R.id.searchBt);

        searchBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                regnumSearch = regnumTv.getText().toString();
                regnumSearch = "c1";
                cardamUrlSearchRegnumCurrent = cardamUrlSearchRegnum + "/" + regnumSearch;
                searchRegnum(cardamUrlSearchRegnumCurrent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void setTitle(CharSequence title) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event


        return super.onOptionsItemSelected(item);
    }


    public void searchRegnum(String regnumURL){
        JsonTask jt = new JsonTask(this, db, regnumURL, images, rlSearch, mainActivity, JsonTask.MULTTYPE);
        jt.execute();
    }

    public void init(){
        cardamUrl = getResources().getString(R.string.cardamUrl);//"http://192.168.0.13:80";

        cardamUrlSearchRegnum = cardamUrl + getResources().getString(R.string.cardamUrlSearchRegnum);//cardamUrl + "/public/api/searchvehicle";
        rlSearch = (LinearLayout) findViewById(R.id.linearLayout);
        rlSearch.setOrientation(LinearLayout.VERTICAL);
        images = new ArrayList<ImageView>();
        db = new DBHelper(this);
    }

}
