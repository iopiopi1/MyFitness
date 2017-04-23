package com.example.iopiopi.myfitness;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.ScrollView;
import java.util.Calendar;

import java.util.ArrayList;

import java.util.ArrayList;

public class AddMeasurementActivity extends AppCompatActivity {
    protected int year;
    protected int month;
    protected int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurement);
        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeActivity();

    }

    protected void initializeActivity() {
        DBHelper db = new DBHelper(this);
        ArrayList<String> measures = db.getAllBodyMeasureCategories();
        LinearLayout rl = (LinearLayout) findViewById(R.id.addMeasuresLinear);


        Calendar calendar=Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePicker datepicker = new DatePicker(this);
        datepicker.setEnabled(true);
        datepicker.updateDate(year, month, day);

        TextView textview=new TextView(this);
        textview.setText("Date");
        rl.addView(textview);

        rl.addView(datepicker);


        for (String measure: measures)
        {
            textview=new TextView(this);
            textview.setText(measure);
            rl.addView(textview);

            EditText editText = new EditText(this);
            editText.setText("");
            //setContentView(editText);
            rl.addView(editText);
        }


    }



}
