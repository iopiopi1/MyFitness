package com.example.iopiopi.myfitness;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import java.util.Calendar;

public class DatePickerActivity extends AppCompatActivity {

    protected int cYear;
    protected int Cmonth;
    protected int Cday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Calendar calendar=Calendar.getInstance();

        cYear = calendar.get(Calendar.YEAR);
        Cmonth = calendar.get(Calendar.MONTH);
        Cday = calendar.get(Calendar.DAY_OF_MONTH);

        DatePicker datepicker = new DatePicker(this);
        datepicker.setEnabled(true);
        datepicker.updateDate(cYear, Cmonth, Cday);

        LinearLayout rl = (LinearLayout) findViewById(R.id.datePickerLinear);

        rl.addView(datepicker);
        //datepicker.on
        datepicker.init(cYear, Cmonth, Cday, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Intent intent = new Intent();
                String date = month + 1 + "/" + dayOfMonth + "/" + year;
                intent.putExtra("datePicked", date.toString());
                //startActivity(intent);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }

}
