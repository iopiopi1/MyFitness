package com.example.iopiopi.myfitness;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import MyFitness.MeasureCategory;
import MyFitness.Measurement;

public class AddMeasurementActivity extends AppCompatActivity {

    public List<Measurement> measurementList;
    public EditText editDate;
    private int currentMeasurementType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurement);
        Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        currentMeasurementType = -1; // or other values
        if(b != null) {
            currentMeasurementType = b.getInt("currentMeasurementType");
        }

        initializeActivity();

    }

    protected void initializeActivity() {
        EditText editText;
        DBHelper db = new DBHelper(this);
        ArrayList<MeasureCategory> measures = db.getAllBodyMeasureCategories(currentMeasurementType);
        LinearLayout rl = (LinearLayout) findViewById(R.id.addMeasuresLinear);

        Calendar calendar=Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        measurementList = new ArrayList<Measurement>();

        int uqId = db.addUniqueId();

        TextView textview = new TextView(this);
        textview.setText("Date");
        textview.setId(uqId++);
        rl.addView(textview);

        editDate = new EditText(this);

        editDate.setId(uqId++);
        editDate.setText(month + "/" + day + "/" + year);
        rl.addView(editDate);

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddMeasurementActivity.this, DatePickerActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        //rl.addView(datepicker);

        int i = -1;
        for (MeasureCategory measureCategory: measures)
        {
            i++;
            textview = new TextView(this);
            textview.setText(measureCategory.getCategoryName());
            textview.setId(uqId++);
            rl.addView(textview);


            editText = new EditText(this);
            editText.setText("");
            editText.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setId(uqId++);
            measurementList.add(i, new Measurement(0, null, measureCategory.getCategoryId(), 1, editText.getId()));

            rl.addView(editText);
        }

        Button saveButton = new Button(this);

        saveButton.setId(uqId++);
        saveButton.setText("Save");
        rl.addView(saveButton);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            for(Measurement measurement: measurementList){
                DBHelper db = new DBHelper(AddMeasurementActivity.this);
                EditText editText = (EditText) findViewById(measurement.getUqId());
                Float value = Float.valueOf(editText.getText().toString());
                db.addMeasurment(measurement.getCategoryId(), value, editDate.getText().toString());
            }
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton5);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(AddMeasurementActivity.this, AddCategoryActivity.class);
            startActivityForResult(intent, 2);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                //String strEditText = data.getStringExtra("editTextValue");
                LinearLayout rl = (LinearLayout) findViewById(R.id.addMeasuresLinear);
                //rl mSpinner = (Spinner) findViewById(R.id.spinner);
                //EditText editDate = (EditText) findViewById(1);
                String date = data.getStringExtra("datePicked");

                editDate.setText(date);
            }
        }
        else if(requestCode == 2){
            if(resultCode == RESULT_OK) {
                finish();
                startActivity(getIntent());
            }
        }
    }



}
