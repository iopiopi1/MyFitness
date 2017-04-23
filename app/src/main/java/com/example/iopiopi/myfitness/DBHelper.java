package com.example.iopiopi.myfitness;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Context;
import java.util.ArrayList;

/**
 * Created by iopiopi on 4/23/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyFitness.db";
    public static final String MEASURE_CATEGORY_TABLE_NAME = "MeasureCategory";
    public static final String MEASURE_CATEGORY_COLUMN_ID = "id";
    public static final String MEASURE_CATEGORY_COLUMN_NAME = "name";

    public static final String USER_MEASUREMENT_TABLE_NAME = "UserMeasurement";
    public static final String USER_MEASUREMENT_COLUMN_ID = "id";
    public static final String USER_MEASUREMENT_COLUMN_VALUE = "value";
    public static final String USER_MEASUREMENT_COLUMN_TIMESTAMP = "timestamp";
    public static final String USER_MEASUREMENT_COLUMN_CATEGORY = "category";
    public static final String USER_MEASUREMENT_COLUMN_MEASURE = "measure";

    public static final int USER_MEASUREMENT_MEASURE_CENTIMETER = 1;
    public static final int USER_MEASUREMENT_CATEGORY_BODY = 1;
    //private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "CREATE TABLE "+ MEASURE_CATEGORY_TABLE_NAME +" " +
                        "(id INTEGER PRIMARY KEY, name TEXT)"
        );

        db.execSQL("INSERT INTO " + MEASURE_CATEGORY_TABLE_NAME + "("+MEASURE_CATEGORY_COLUMN_NAME+
                    ") VALUES('Biceps'), ('Thigh'), ('Abdominal'), ('Calf'), ('Chest')");

        db.execSQL("CREATE TABLE "+ USER_MEASUREMENT_TABLE_NAME +" " +
                        "(id INTEGER PRIMARY KEY, value REAL, timestamp TEXT, category INTEGER, measure INTEGER)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public ArrayList<String> getAllBodyMeasureCategories(){
        ArrayList<String> arrayCategories = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+MEASURE_CATEGORY_TABLE_NAME+"", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            arrayCategories.add(res.getString(res.getColumnIndex(MEASURE_CATEGORY_COLUMN_NAME)));
            res.moveToNext();
        }
        return arrayCategories;
    }



}
