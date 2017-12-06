package cardam2.cardam2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.ListView;
import android.content.res.Configuration;
import android.widget.TextView;
import 	android.content.Context;

import org.json.JSONObject;

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
    public ArrayList<TextView> titles;
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        checkCurUser();

        //drawer
        initLeftDrawer();

        Button addBt =  (Button) findViewById(R.id.add_button);
        addBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, PhotoActivity.class);
                startActivityForResult(intent, 1);
            }
        });


        Button searchBt =  (Button) findViewById(R.id.search_button);
        searchBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, SearchActivity.class);
                startActivityForResult(intent, 2);
            }
        });


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
            mDrawerLayout.openDrawer(mDrawerList);

            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    public void init(){
        cardamUrl = getResources().getString(R.string.cardamUrl);//"http://192.168.0.13:80";
        db = new DBHelper(this);
        //db.deleteDatabase();
        cardamUrlSearchRegnum = cardamUrl + getResources().getString(R.string.cardamUrlSearchRegnum);//cardamUrl + "/public/api/searchvehicle";
        cardamUrlCheckLogin = cardamUrl + getResources().getString(R.string.cardamUrlCheckLogin);
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
                   if(position == 0){imageView.setImageResource(R.drawable.ic_search);}
                   if(position == 1){imageView.setImageResource(R.drawable.ic_add_pic);}
                   if(position == 2){imageView.setImageResource(0);}
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
        mDrawerToggle.syncState();

    }


    private void selectItem(int position) {
        String category;

        if(position == 0){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent, 1);
        }
        if(position == 1){
            Intent intent = new Intent(this, PhotoActivity.class);
            startActivityForResult(intent, 2);
        }
        if(position == 2){
            db.deleteCurUser(db.dbMyFitness);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 3);
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
        PostTask jt = new PostTask(cardamUrlCheckLogin, mainActivity, postParams, R.id.CoordinatorLayout, PostTask.CHECKLOGINTYPE);
        jt.execute();

    }



}
