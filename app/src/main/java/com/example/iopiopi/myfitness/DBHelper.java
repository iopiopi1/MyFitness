package com.example.iopiopi.myfitness;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Context;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;

import MyFitness.KeyValueList;
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


    public static final String APP_UNIQUE_ID_TABLE_NAME = "AppUniqueId";
    public static final String APP_UNIQUE_ID_COLUMN_ID = "id";
    public static final String APP_UNIQUE_ID_COLUMN_TIMESTAMP = "timestamp";

    SQLiteDatabase dbMyFitness;
    private List<KeyValueList> postParams;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        dbMyFitness = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        dbMyFitness = db;

        db.execSQL("CREATE TABLE AppUniqueId (id INTEGER PRIMARY KEY, timestamp TEXT )"
        );

        db.execSQL("CREATE TABLE curUser (id INTEGER PRIMARY KEY, authLogin TEXT, authPass TEXT, status TEXT )"
        );

        db.execSQL("CREATE TABLE JsonAnswer (id INTEGER PRIMARY KEY, JsonAnswer TEXT )"
        );

        db.execSQL("INSERT INTO JsonAnswer (id, JsonAnswer) VALUES(1, NULL)"
        );

        db.execSQL("INSERT INTO curUser (id, authLogin, authPass, status) VALUES(1, '', '', 'NON_ACTIVE')"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS AppUniqueId");
        db.execSQL("DROP TABLE IF EXISTS JsonAnswer");
        db.execSQL("DROP TABLE IF EXISTS curUser");
        this.onCreate(db);
    }

    @Override
    public void onOpen (SQLiteDatabase db) {
        dbMyFitness = db;
    }

    public int addUniqueId(SQLiteDatabase db){
        //SQLiteDatabase db = dbMyFitness;//this.getWritableDatabase();
        db.execSQL("INSERT INTO AppUniqueId (timestamp) VALUES('0')");
        String SQLquery = "SELECT MAX(id) FROM AppUniqueId";
        Cursor cur = db.rawQuery(SQLquery, null);
        cur.moveToFirst();
        int ID = cur.getInt(0);
        cur.close();
        return ID;
    }

    public void addJsonResultDatabase(SQLiteDatabase db, String JsonRes) {
        //SQLiteDatabase db = dbMyFitness;//this.getWritableDatabase();
        db.execSQL("UPDATE JsonAnswer SET JsonAnswer = '" + JsonRes + "' WHERE id = 1");
    }


    public void addCurUser(SQLiteDatabase db, String login, String Pass) {
        //SQLiteDatabase db = dbMyFitness;//this.getWritableDatabase();
        db.execSQL("DELETE FROM curUser");
        db.execSQL("INSERT INTO curUser (authLogin , authPass , status ) VALUES('"+login+"','"+Pass+"','ACTIVE')");
    }

    public List<KeyValueList> getCurUser(SQLiteDatabase db) {
        //SQLiteDatabase db = dbMyFitness;//this.getWritableDatabase();
        String SQLquery = "SELECT authLogin , authPass , status FROM curUser";
        Cursor cur = db.rawQuery(SQLquery, null);
        cur.moveToFirst();
        String authLogin = cur.getString(0);
        String authPass = cur.getString(1);
        cur.close();
        postParams = new ArrayList<KeyValueList>();
        postParams.add(0, new KeyValueList("username", authLogin));
        postParams.add(1, new KeyValueList("password", authPass));

        return postParams;

    }

    public String getJsonResultDatabase(SQLiteDatabase db) {
        String SQLquery = "SELECT JsonAnswer FROM JsonAnswer WHERE id = 1";
        Cursor cur = db.rawQuery(SQLquery, null);
        cur.moveToFirst();
        String respond = cur.getString(0);
        cur.close();
        return respond;
    }

    public void deleteDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        this.onUpgrade(db,0, 1);
    }

}


