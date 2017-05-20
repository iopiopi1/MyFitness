package com.example.iopiopi.myfitness;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Button;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.Entry;
import MyFitness.ChartData;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.ListView;
import android.content.res.Configuration;

public class MainActivity extends AppCompatActivity {

    DBHelper db;
    private String[] menuAppItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private int currentMeasurementType = 1;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> options;
    private Spinner mSpinner;
    private int chartTimeFrame = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBHelper(this);
        db.deleteDatabase();
        options = db.getAllBodyMeasureCategoriesString();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,options);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int i, long lng) {
                String category = adapter.getItemAtPosition(i).toString();
                drawLineChart(category, currentMeasurementType, chartTimeFrame);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddMeasurementActivity.class);
                Bundle b = new Bundle();
                b.putInt("currentMeasurementType", currentMeasurementType);
                intent.putExtras(b);
                startActivityForResult(intent, 1);
            }
        });

        initButtons();
        initLeftDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void drawLineChart(String category, int type, int timeFrame){
        db = new DBHelper(this);

        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<ChartData> chartData = db.getUserMeasurementLinearChart(category, type, timeFrame);
        if(chartData.size() > 0) {
            List<Entry> entries = new ArrayList<Entry>();
            for (ChartData data : chartData) {
                entries.add(new Entry(data.getDataX(), data.getDataY()));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Label");
            dataSet.setColor(1);
            dataSet.setValueTextColor(1);
            dataSet.setLineWidth(1);
            dataSet.setDrawCircleHole(false);
            //.setDrawCubic(true);
            //dataSet.line
            LineData lineData = new LineData(dataSet);
            //lineData.set
            chart.setData(lineData);
            chart.getAxisLeft().setDrawLabels(false);
            chart.getAxisRight().setDrawLabels(false);
            chart.getXAxis().setDrawLabels(false);
            chart.getXAxis().setDrawGridLines(false); // disable grid lines for the XAxis
            //chart.getAxisLeft().setDrawGridLines(false); // disable grid lines for the left YAxis
            chart.getAxisRight().setDrawGridLines(false);
            chart.invalidate();
        }
        else{
            chart.clear();
        }
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        mDrawerLayout.closeDrawer(mDrawerList);
        String category;
        currentMeasurementType = position + 1;

        if(currentMeasurementType > 1){ // hide the spinner
            mSpinner.setVisibility(View.INVISIBLE);
            category = "Unknown";
        }
        else{
            mSpinner.setVisibility(View.VISIBLE);
            category = mSpinner.getSelectedItem().toString();
        }

        drawLineChart(category, currentMeasurementType, chartTimeFrame);
    }

    @Override
    public void setTitle(CharSequence title) {
        /*mTitle = title;
        getActionBar().setTitle(mTitle);*/
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
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String category;
                if(currentMeasurementType > 1){ // hide the spinner
                    mSpinner.setVisibility(View.INVISIBLE);
                    category = "Unknown";
                }
                else{
                    mSpinner.setVisibility(View.VISIBLE);
                    category = mSpinner.getSelectedItem().toString();
                }
                drawLineChart(category, currentMeasurementType, chartTimeFrame);
            }
        }
    }

    public void initLeftDrawer(){
        menuAppItemTitles = db.getAllMeasureTypes();
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, R.id.textView, menuAppItemTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
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
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };


        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void initButtons(){

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chartTimeFrame = 1;
                String category;
                if(currentMeasurementType > 1){ // hide the spinner
                    mSpinner.setVisibility(View.INVISIBLE);
                    category = "Unknown";
                }
                else{
                    mSpinner.setVisibility(View.VISIBLE);
                    category = mSpinner.getSelectedItem().toString();
                }
                drawLineChart(category, currentMeasurementType, chartTimeFrame);
            }
        });

        Button button2 = (Button) findViewById(R.id.button4);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chartTimeFrame = 2;
                String category;
                if(currentMeasurementType > 1){ // hide the spinner
                    mSpinner.setVisibility(View.INVISIBLE);
                    category = "Unknown";
                }
                else{
                    mSpinner.setVisibility(View.VISIBLE);
                    category = mSpinner.getSelectedItem().toString();
                }
                drawLineChart(category, currentMeasurementType, chartTimeFrame);
            }
        });

        Button button3 = (Button) findViewById(R.id.button6);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chartTimeFrame = 3;
                String category;
                if(currentMeasurementType > 1){ // hide the spinner
                    mSpinner.setVisibility(View.INVISIBLE);
                    category = "Unknown";
                }
                else{
                    mSpinner.setVisibility(View.VISIBLE);
                    category = mSpinner.getSelectedItem().toString();
                }
                drawLineChart(category, currentMeasurementType, chartTimeFrame);
            }
        });

        Button button4 = (Button) findViewById(R.id.button5);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chartTimeFrame = 4;
                String category;
                if(currentMeasurementType > 1){ // hide the spinner
                    mSpinner.setVisibility(View.INVISIBLE);
                    category = "Unknown";
                }
                else{
                    mSpinner.setVisibility(View.VISIBLE);
                    category = mSpinner.getSelectedItem().toString();
                }
                drawLineChart(category, currentMeasurementType, chartTimeFrame);
            }
        });
    }

}
