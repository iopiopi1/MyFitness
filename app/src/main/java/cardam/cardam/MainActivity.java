package cardam.cardam;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    private Snackbar snackbarWrongSearchRegnum;
    private Snackbar snackbarEmptySearchRes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        checkCurUser();

        regnumTv = (TextView) findViewById(R.id.regnumTv);

        Button searchBt =  (Button) findViewById(R.id.searchBt);

        searchBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                regnumSearch = regnumTv.getText().toString();
                //regnumSearch = "c1";
                cardamUrlSearchRegnumCurrent = cardamUrlSearchRegnum + "/" + regnumSearch;
                searchRegnum(cardamUrlSearchRegnumCurrent, regnumSearch);
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
            mDrawerLayout.openDrawer(mDrawerList);

            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }


    public void searchRegnum(String regnumURL, String regnum){
        String pattern = "[ABEHKMOPCTYXabehkmopctyx]\\d{3}[ABEHKMOPCTYXabehkmopctyx]{2}\\d{2,3}|[АВЕКМНОРСТУХавекмнорстух]\\d{3}[АВЕКМНОРСТУХавекмнорстух]{2}\\d{2,3}";
        boolean isMatched = Pattern.matches(pattern, regnum);

        if(!isMatched){
            snackbarWrongSearchRegnum.show();

            return;
        }
        else{
            if(snackbarWrongSearchRegnum.isShown()){
                snackbarWrongSearchRegnum.dismiss();
            }
        }

        if(images.size() > 0 ){
            for(int i = 0; i < images.size(); i++){
                rlSearch.removeView(images.get(i));
                rlSearch.removeView(titles.get(i));
            }
        }
        JsonTask jt = new JsonTask(this, db, regnumURL, images, rlSearch, mainActivity, JsonTask.MULTTYPE);
        jt.execute();
        images = jt.getImages();
        titles = jt.getTitles();
    }

    public void init(){
        snackbarWrongSearchRegnum = Snackbar.make(findViewById(R.id.CoordinatorLayout), R.string.search_wrong_regnum, Snackbar.LENGTH_INDEFINITE);
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
            String ActivityName = this.getClass().getSimpleName();
            if(!ActivityName.equals("MainActivity")){
                finish();
            }
        }
        if(position == 1){
            Intent intent = new Intent(this, PhotoActivity.class);
            startActivityForResult(intent, 1);
        }
        if(position == 2){
            db.deleteCurUser(db.dbMyFitness);
            Intent intent = new Intent(this, LoginActivity.class);
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

    public Snackbar getSnackbar(String name){
        Snackbar sn = null;
        switch(name) {
            case "snackbarWrongSearchRegnum":
                sn = snackbarWrongSearchRegnum;
                break;
            case "snackbarEmptySearchRes":
                sn = snackbarEmptySearchRes;
                break;

        }
        return sn;
    }

    public void checkCurUser(){
        postParams = db.getCurUser(db.dbMyFitness);
        //String isOld =
        KeyValueList isOld = postParams.get(2);//.toString().equals("yes");
        String isOldString = isOld.getValue();
        if(isOldString.equals("yes")) {
            PostTask jt = new PostTask(cardamUrlCheckLogin, mainActivity, postParams, R.id.CoordinatorLayout, PostTask.CHECKLOGINTYPE);
            jt.execute();
        }
        else {
            EditText et = (EditText) findViewById(R.id.regnumTv);
            et.setVisibility(EditText.VISIBLE);
            Button bt = (Button) findViewById(R.id.searchBt);
            bt.setVisibility(Button.VISIBLE);
        }
    }

}
