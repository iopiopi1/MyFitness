package cardam2.cardam2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import java.security.acl.Group;
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
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.view.Gravity;
import android.widget.RelativeLayout;

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
    private NavigationView navigationView;

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
    protected void onResume(){
        super.onResume();
        makeUserProfileNavigation(null);
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
            mDrawerLayout.openDrawer(Gravity.LEFT);
            NavigationView nv = (NavigationView) findViewById(R.id.navigation_view);
            nv.bringToFront();

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

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.drawerNavSearch:
                        Intent intent = new Intent(mainActivity, SearchActivity.class);
                        startActivityForResult(intent, 1);
                        return true;
                    case R.id.drawerNavAdd:
                        intent = new Intent(mainActivity, PhotoActivity.class);
                        startActivityForResult(intent, 2);
                        return true;
                    case R.id.drawerNavExit:
                        db.deleteCurUser(db.dbMyFitness);
                        intent = new Intent(mainActivity, LoginActivity.class);
                        startActivityForResult(intent, 3);
                        return true;
                    default:
                        return true;
                }
            }
        });
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
            }
        };


        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    public void checkCurUser(){
        postParams = db.getCurUser(db.dbMyFitness);
        PostTask jt = new PostTask(cardamUrlCheckLogin, mainActivity, postParams, R.id.CoordinatorLayout, PostTask.CHECKLOGINTYPE);
        jt.execute();

    }

    public void makeUserProfileNavigation(List<KeyValueList> params){
        if(params == null){
            params = db.getCurUser(db.dbMyFitness);
        }
        NavigationView nv = (NavigationView) findViewById(R.id.navigation_view);
        RelativeLayout nvRl = (RelativeLayout)nv.getHeaderView(0);
        Button btUser = nvRl.findViewById(R.id.userProfileButton);
        TextView tv1 = nvRl.findViewById(R.id.username);
        TextView tv2 = nvRl.findViewById(R.id.drawEmail);
        String username = params.get(0).getValue();
        String userEmail = params.get(3).getValue();
        if(username != "Net takogo usera" && username.length() > 0){
            btUser.setText(username.substring(0,1));
            tv1.setText(username);
            tv2.setText(userEmail);
            nvRl.invalidate();
        }


    }

}
