package com.example.iopiopi.myfitness;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Context;

import java.text.ParseException;
import java.util.ArrayList;
import MyFitness.MeasureCategory;
import MyFitness.ChartData;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import static java.lang.Math.floor;
import static java.lang.Math.round;

/**
 * Created by iopiopi on 4/23/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyFitness.db";
    public static final String MEASURE_CATEGORY_TABLE_NAME = "MeasureCategory";
    public static final String MEASURE_CATEGORY_COLUMN_ID = "id";
    public static final String MEASURE_CATEGORY_COLUMN_NAME = "name";
    public static final String MEASURE_CATEGORY_COLUMN_TYPE = "typeId";

    public static final String MEASURE_TYPE_TABLE_NAME = "MeasureType";
    public static final String MEASURE_TYPE_COLUMN_ID = "id";
    public static final String MEASURE_TYPE_COLUMN_NAME = "name";
    public static final String MEASURE_TYPE_COLUMN_POSITION = "position";

    public static final String USER_MEASUREMENT_TABLE_NAME = "UserMeasurement";
    public static final String USER_MEASUREMENT_COLUMN_ID = "id";
    public static final String USER_MEASUREMENT_COLUMN_VALUE = "value";
    public static final String USER_MEASUREMENT_COLUMN_TIMESTAMP = "timestamp";
    public static final String USER_MEASUREMENT_COLUMN_CATEGORYID = "category";
    public static final String USER_MEASUREMENT_COLUMN_MEASURE = "measure";

    public static final int USER_MEASUREMENT_MEASURE_CENTIMETER = 1;
    public static final int USER_MEASUREMENT_CATEGORY_BODY = 1;

    public static final String APP_UNIQUE_ID_TABLE_NAME = "AppUniqueId";
    public static final String APP_UNIQUE_ID_COLUMN_ID = "id";
    public static final String APP_UNIQUE_ID_COLUMN_TIMESTAMP = "timestamp";

    private int uqId;
    private int measBodyTypeId;
    private int measBPressureTypeId;
    private int measWeightTypeId;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE "+ MEASURE_CATEGORY_TABLE_NAME +" " +
                        "(id INTEGER PRIMARY KEY, name TEXT, typeId INTEGER)"
        );

        db.execSQL("CREATE TABLE "+ USER_MEASUREMENT_TABLE_NAME +" " +
                "(id INTEGER PRIMARY KEY, value REAL, timestamp INTEGER, category INTEGER, measure INTEGER)"
        );

        db.execSQL("CREATE TABLE "+ APP_UNIQUE_ID_TABLE_NAME +" " +
                "(id INTEGER PRIMARY KEY, timestamp TEXT )"
        );

        db.execSQL("CREATE TABLE "+ MEASURE_TYPE_TABLE_NAME +" " +
                "(id INTEGER PRIMARY KEY, name TEXT, position INTEGER )"
        );

        measBodyTypeId = this.addUniqueId();
        measBPressureTypeId = this.addUniqueId();
        measWeightTypeId = this.addUniqueId();
        db.execSQL("INSERT INTO " + MEASURE_TYPE_TABLE_NAME
                + " (" + MEASURE_TYPE_COLUMN_ID + ", " + MEASURE_TYPE_COLUMN_NAME +", "+ MEASURE_TYPE_COLUMN_POSITION +")"+
                " VALUES(" + measBodyTypeId + ",'Body measurements', 1)" +
                ",(" + measWeightTypeId + ",'Weight', 2),(" + measBPressureTypeId + ",'Blood Pressure', 3)");


        uqId = this.addUniqueId();
        this.addUniqueId();this.addUniqueId();this.addUniqueId();this.addUniqueId();
        db.execSQL("INSERT INTO " + MEASURE_CATEGORY_TABLE_NAME + "("+MEASURE_CATEGORY_COLUMN_NAME+ ", " + MEASURE_CATEGORY_COLUMN_ID + ", " + MEASURE_CATEGORY_COLUMN_TYPE +
                ") VALUES('Biceps', "+ (uqId + 1) +", "+ measBodyTypeId +"), " +
                "('Thigh', "+ (uqId + 2) +", "+ measBodyTypeId +"), ('Abdominal', "+ (uqId + 3) +", "+ measBodyTypeId +"), " +
                "('Calf', "+ (uqId + 4) +", "+ measBodyTypeId +"), ('Chest', "+ (uqId + 5) +", "+ measBodyTypeId +")");

        int uqId2 = this.addUniqueId();
        this.addUniqueId();this.addUniqueId();this.addUniqueId();this.addUniqueId();this.addUniqueId();
        this.addUniqueId();this.addUniqueId();this.addUniqueId();this.addUniqueId();this.addUniqueId();
        this.addUniqueId();
        db.execSQL("INSERT INTO UserMeasurement(id,value,timestamp,category,measure) " +
                "VALUES("+(uqId2+1)+",41,'17001',"+ (uqId + 1) +",1),("+(uqId2+2)+",41,'17120',"+ (uqId + 1) +",1),("+(uqId2+3)+",42,'17130',"+ (uqId + 1) +",1), " +
                "("+(uqId2+4)+",44,'17111',"+ (uqId + 1) +",1),("+(uqId2+5)+",31,'17320',"+ (uqId + 1) +",1),("+(uqId2+6)+",21,'17420',"+ (uqId + 1) +",1)" +
                ",("+(uqId2+7)+",21,'17156',"+ (uqId + 1) +",1)"
        );

        int uqIdCategoryWeight = this.addUniqueId();
        int uqIdCategoryPressure = this.addUniqueId();
        db.execSQL("INSERT INTO " + MEASURE_CATEGORY_TABLE_NAME + "("+MEASURE_CATEGORY_COLUMN_NAME+ ", " + MEASURE_CATEGORY_COLUMN_ID + ", " + MEASURE_CATEGORY_COLUMN_TYPE +
                ") VALUES('Weight', "+ uqIdCategoryWeight +", "+ measWeightTypeId +"), " +
                "('Blood Pressure', "+ uqIdCategoryPressure +", "+ measBPressureTypeId +")");

        uqId = this.addUniqueId();this.addUniqueId();
        db.execSQL("INSERT INTO UserMeasurement(id,value,timestamp,category,measure) " +
                "VALUES("+uqId+",121,'17301',"+ uqIdCategoryWeight +",1),("+(uqId + 1) +",129,'17305',"+ uqIdCategoryWeight +",1)"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + MEASURE_CATEGORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_MEASUREMENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + APP_UNIQUE_ID_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MEASURE_TYPE_TABLE_NAME);
        this.onCreate(db);
    }

    public ArrayList<MeasureCategory> getAllBodyMeasureCategories(int currentMeasurementType){

        ArrayList<MeasureCategory> measureCategories = new ArrayList<MeasureCategory>();


        ArrayList<String> arrayCategories = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select mc.* from "+MEASURE_CATEGORY_TABLE_NAME+" AS mc" +
        " JOIN " + MEASURE_TYPE_TABLE_NAME + " AS mt ON mc." + MEASURE_CATEGORY_COLUMN_TYPE + "=" +
        " mt." + MEASURE_TYPE_COLUMN_ID + " WHERE mt." + MEASURE_TYPE_COLUMN_ID + "=" + currentMeasurementType, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            measureCategories.add(
                new MeasureCategory(res.getInt(res.getColumnIndex(MEASURE_CATEGORY_COLUMN_ID)),
                                    res.getString(res.getColumnIndex(MEASURE_CATEGORY_COLUMN_NAME)))
                );
            res.moveToNext();
        }
        return measureCategories;
    }

    public ArrayList<String> getAllBodyMeasureCategoriesString(){

        ArrayList<String> measureCategories = new ArrayList<String>();


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

    public void addCategory(String category){
        SQLiteDatabase db = this.getWritableDatabase();

        uqId = this.addUniqueId();
        db.execSQL("INSERT INTO " + MEASURE_CATEGORY_TABLE_NAME + "("+ MEASURE_CATEGORY_COLUMN_NAME + ", " + MEASURE_CATEGORY_COLUMN_ID + ", " + MEASURE_CATEGORY_COLUMN_TYPE +
                ") VALUES('" + category + "', "+ uqId + 1 +", "+ measBodyTypeId +")");
    }

    public void addMeasurment(int categoryId, float value, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        int Id = this.addUniqueId();
        String huy = "";
        DateFormat df = new SimpleDateFormat("M/d/yyyy");
        Date dateFormatted = null;

        try {
            dateFormatted = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        db.execSQL("INSERT INTO " + USER_MEASUREMENT_TABLE_NAME +
        "(" + USER_MEASUREMENT_COLUMN_ID + ", " + USER_MEASUREMENT_COLUMN_CATEGORYID + ", " + USER_MEASUREMENT_COLUMN_TIMESTAMP
            + ", " + USER_MEASUREMENT_COLUMN_MEASURE + ", " + USER_MEASUREMENT_COLUMN_VALUE + ") " +
                "VALUES(" + (Id+1) + ", " + categoryId + ", " + floor(dateFormatted.getTime()/ (24 * 60 * 60 * 1000)) + ", 1, " + value + " )");

    }

    public int addUniqueId(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + APP_UNIQUE_ID_TABLE_NAME + "("+APP_UNIQUE_ID_COLUMN_TIMESTAMP+
                ") VALUES('0')");
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String SQLquery = "SELECT MAX(id) FROM "+ APP_UNIQUE_ID_TABLE_NAME;
        Cursor cur = dbRead.rawQuery(SQLquery, null);
        cur.moveToFirst();
        int ID = cur.getInt(0);
        cur.close();
        return ID;
    }

    public void deleteDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        this.onUpgrade(db,0, 1);
    }

    public List<ChartData> getUserMeasurementLinearChart(String category, int measType, int timeFrame){
        if(category == ""){
            SQLiteDatabase db1 = this.getReadableDatabase();
            Cursor res2 =  db1.rawQuery( "select "+MEASURE_CATEGORY_COLUMN_NAME+" from "+MEASURE_CATEGORY_TABLE_NAME+
                    " WHERE " + MEASURE_CATEGORY_COLUMN_ID + " = (SELECT MIN("+MEASURE_CATEGORY_COLUMN_ID+") FROM "+MEASURE_CATEGORY_TABLE_NAME+")"+
                    " AND " + MEASURE_CATEGORY_COLUMN_TYPE + " = " + measType, null);

            res2.moveToFirst();
            while(res2.isAfterLast() == false){
                category = res2.getString(res2.getColumnIndex(MEASURE_CATEGORY_COLUMN_NAME));
                res2.moveToNext();
            }
            res2.close();
        }
        List<ChartData> chartData = new ArrayList<ChartData>();
        SQLiteDatabase db = this.getReadableDatabase();



        Date todayDate = new Date();
        long todayDay = todayDate.getTime() / (24 * 60 * 60 * 1000);

        long startDate = 0;

        switch (timeFrame) {
            case 1:  startDate = todayDay - 30;
                break;
            case 2:  startDate = todayDay - 90;
                break;
            case 3:  startDate = todayDay - 180;
                break;
            case 4:  startDate = 0;
                break;
        }

        Cursor res;
        if(category != "Unknown"){
            res = db.rawQuery( "select * from "+USER_MEASUREMENT_TABLE_NAME+ " AS um " +
                    " INNER JOIN " + MEASURE_CATEGORY_TABLE_NAME + " AS ms ON um." + USER_MEASUREMENT_COLUMN_CATEGORYID +"=ms." +MEASURE_CATEGORY_COLUMN_ID+
                    " INNER JOIN " + MEASURE_TYPE_TABLE_NAME + " AS mt ON ms." + MEASURE_CATEGORY_COLUMN_TYPE + " = mt." + MEASURE_TYPE_COLUMN_ID +
                    " WHERE (ms." + MEASURE_CATEGORY_COLUMN_NAME + " = '" + category + "' AND ms.name <> 'Unknown')" +
                    " AND um.timestamp >= " + startDate + " AND um.timestamp <= " + todayDay +
                    " ORDER BY um.timestamp ASC "
                    , null );
        }
        else{
            res = db.rawQuery( "select * from "+USER_MEASUREMENT_TABLE_NAME+ " AS um " +
                    " INNER JOIN " + MEASURE_CATEGORY_TABLE_NAME + " AS ms ON um." + USER_MEASUREMENT_COLUMN_CATEGORYID +"=ms." +MEASURE_CATEGORY_COLUMN_ID+
                    " INNER JOIN " + MEASURE_TYPE_TABLE_NAME + " AS mt ON ms." + MEASURE_CATEGORY_COLUMN_TYPE + " = mt." + MEASURE_TYPE_COLUMN_ID +
                    " WHERE (mt." + MEASURE_TYPE_COLUMN_ID + " = " + measType + ") " +
                    " AND um.timestamp >= " + startDate + " AND um.timestamp <= " + todayDay +
                    " ORDER BY um.timestamp ASC ", null );
        }


        res.moveToFirst();

        int i = 0;
        float counter = 1;
        float lastInsertedX = 0;
        float lastInsertedY = 0;
        float dY = 0;
        while(res.isAfterLast() == false){
            ChartData measurement = new ChartData(0,0);
            measurement.setDataX((long)res.getInt(res.getColumnIndex(USER_MEASUREMENT_COLUMN_TIMESTAMP)));
            measurement.setDataY(res.getFloat(res.getColumnIndex(USER_MEASUREMENT_COLUMN_VALUE)));
            if(chartData.size() == 0){
                lastInsertedX = measurement.getDataX();
                lastInsertedY = measurement.getDataY();
            }

            counter = lastInsertedX;
            int curArSize = chartData.size();
            dY = (measurement.getDataY() - lastInsertedY)/(measurement.getDataX() - lastInsertedX) / 10;
            float curDY = lastInsertedY;
            while(counter < measurement.getDataX()){
                chartData.add(i, new ChartData(counter, curDY));
                i++;
                curDY = curDY + dY;
                counter = counter + (float)0.1;
            }

            chartData.add(i, measurement);
            if(curArSize < chartData.size()) {
                lastInsertedX = counter + (float)0.1;
                lastInsertedY = measurement.getDataY();
            }

            res.moveToNext();
            i++;
        }

        res.close();

        return chartData;
    }

    public String[] getAllMeasureTypes(){
        String[] itemsArr = new String[1];
        List<String> itemsLi = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+MEASURE_TYPE_TABLE_NAME+"", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            itemsLi.add(res.getString(res.getColumnIndex(MEASURE_TYPE_COLUMN_NAME)));
            res.moveToNext();
        }

        res.close();

        itemsArr = itemsLi.toArray(new String[0]);

        return itemsArr;
    }

}


