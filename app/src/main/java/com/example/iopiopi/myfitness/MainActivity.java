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

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

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
import 	android.content.Context;
import com.bumptech.glide.Glide;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.view.ViewGroup.LayoutParams;
import android.util.Log;
import 	android.view.ViewGroup;
import MyFitness.KeyValueList;
import 	android.view.LayoutInflater;

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
    private List<KeyValueList> postParams;
    private String cardamUrlCheckLogin;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private String[] menuAppItemTitles;
    private String[] menuAppItemIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        init();
        checkCurUser();

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
        //drawer
        initLeftDrawer();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }


    public void searchRegnum(String regnumURL){
        JsonTask jt = new JsonTask(this, db, regnumURL, images, rlSearch, mainActivity, JsonTask.MULTTYPE);
        jt.execute();
    }

    public void init(){
        //Intent intent = new Intent(this, PhotoActivity.class);
        //startActivityForResult(intent, 1);
        cardamUrl = getResources().getString(R.string.cardamUrl);//"http://192.168.0.13:80";
        cardamUrlSearchRegnum = cardamUrl + getResources().getString(R.string.cardamUrlSearchRegnum);//cardamUrl + "/public/api/searchvehicle";
        cardamUrlCheckLogin = cardamUrl + getResources().getString(R.string.cardamUrlCheckLogin);
        rlSearch = (LinearLayout) findViewById(R.id.linearLayout);
        rlSearch.setOrientation(LinearLayout.VERTICAL);
        images = new ArrayList<ImageView>();
        db = new DBHelper(this);
        //db.deleteDatabase();


    }

    public void initLeftDrawer(){
        menuAppItemTitles = getResources().getStringArray(R.array.menuAppItemTitles);
        menuAppItemIcons = getResources().getStringArray(R.array.menuAppItemIcons);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(
            new ArrayAdapter<String>(this,
                R.layout.drawer_list_item,
                R.id.textView,
                menuAppItemTitles)
           {
               @Override
               public View getView(int position, View convertView, ViewGroup parent) {
                   LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   View rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);
                   TextView textView = (TextView) rowView.findViewById(R.id.textView);
                   ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
                   textView.setText(menuAppItemTitles[position]);
                   if(position == 0){imageView.setImageResource(R.drawable.pressure);}
                   if(position == 1){imageView.setImageResource(R.drawable.scale);}
                   return rowView;
               }
           }
        );

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                //R.drawable.pressure,
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        )
        {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                mDrawerList.bringToFront();
                mDrawerLayout.requestLayout();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }


        };


        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);


    }


    private void selectItem(int position) {
        String category;

        if(position == 0){
            String ActivityName = this.getClass().getSimpleName();
            if(!ActivityName.equals("MainActivity")){
                finish();
            }
        }
        if(position == 1){
            Intent intent = new Intent(this, PhotoActivity.class);
            startActivityForResult(intent, 1);
        }
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


    public void checkCurUser(){
        postParams = db.getCurUser(db.dbMyFitness);
        PostTask jt = new PostTask(cardamUrlCheckLogin, mainActivity, postParams, R.id.constraintLayoutMain, PostTask.CHECKLOGINTYPE);
        jt.execute();
    }

}
