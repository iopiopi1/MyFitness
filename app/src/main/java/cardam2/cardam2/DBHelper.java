package cardam2.cardam2;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import MyFitness.KeyValueList;

import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

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

        db.execSQL("CREATE TABLE curUser (id INTEGER PRIMARY KEY, authLogin TEXT, authPass TEXT, status TEXT, loggedDate TEXT, email TEXT, statementAccepted INTEGER, secPolicyAccepted INTEGER)"
        );

        db.execSQL("CREATE TABLE uploadedPhoto (id INTEGER PRIMARY KEY, timestamp TEXT)"
        );

        db.execSQL("CREATE TABLE JsonAnswer (id INTEGER PRIMARY KEY, JsonAnswer TEXT )"
        );

        db.execSQL("INSERT INTO JsonAnswer (id, JsonAnswer) VALUES(1, NULL)"
        );

        //db.execSQL("INSERT INTO curUser (id, authLogin, authPass, status) VALUES(1, '', '', 'NON_ACTIVE')"
        //);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS AppUniqueId");
        db.execSQL("DROP TABLE IF EXISTS JsonAnswer");
        db.execSQL("DROP TABLE IF EXISTS curUser");
        db.execSQL("DROP TABLE IF EXISTS uploadedPhoto");
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


    public void addCurUser(SQLiteDatabase db, String login, String Pass, int id, String email) {
        //SQLiteDatabase db = dbMyFitness;//this.getWritableDatabase();
        db.execSQL("DELETE FROM curUser");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String format = simpleDateFormat.format(new Date());
        db.execSQL("INSERT INTO curUser (id, authLogin , authPass , status, loggedDate ,email, statementAccepted, secPolicyAccepted ) VALUES("+id+",'"+login+"','"+Pass+"','ACTIVE', '"+format+"', '"+email+"', 0, 0)");
    }

    public void uploadPhoto(SQLiteDatabase db){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String timestamp = simpleDateFormat.format(new Date());
        db.execSQL("INSERT INTO uploadedPhoto (timestamp) VALUES('" + timestamp + "')");

    }

    public String uploadTimeNext(SQLiteDatabase db){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String earliest;
        Date earliestDT = null;
        Date now = new Date();
        int hours = 24;
        String timestamp24h = addDateString(-24);
        String SQLquery = "SELECT timestamp FROM uploadedPhoto WHERE timestamp > '" + timestamp24h + "' ORDER BY id ASC LIMIT 1";
        Cursor cur = db.rawQuery(SQLquery, null);
        cur.moveToFirst();
        if(cur.getCount() > 0) {
            earliest = cur.getString(0);
            try {
                earliestDT = simpleDateFormat.parse(earliest);
                long diff = now.getTime() - earliestDT.getTime();
                hours = 24 - (int)(diff / (1000 * 60 * 60));
            }
            catch(ParseException e){

            }
            finally{
            }
        }
        cur.close();

        return String.valueOf(hours);
    }

    public void updateUSAcceptence(SQLiteDatabase db){
        db.execSQL("UPDATE curUser SET statementAccepted = 1");
    }

    public void updateCPAcceptence(SQLiteDatabase db){
        db.execSQL("UPDATE curUser SET secPolicyAccepted = 1");
    }

    public boolean isUSAcceptedUS(SQLiteDatabase db){
        boolean accepted = false;
        String SQLquery ="SELECT statementAccepted FROM curUser WHERE statementAccepted = 1";
        Cursor cur = db.rawQuery(SQLquery, null);
        int rows = cur.getCount();
        cur.close();

        if(rows == 1){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isCPAcceptedUS(SQLiteDatabase db){
        boolean accepted = false;
        String SQLquery ="SELECT secPolicyAccepted FROM curUser WHERE secPolicyAccepted = 1";
        Cursor cur = db.rawQuery(SQLquery, null);
        int rows = cur.getCount();
        cur.close();

        if(rows == 1){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean canUploadNew(SQLiteDatabase db){
        boolean canUpload = false;
        String timestamp24h = addDateString(-24);
        String SQLquery ="SELECT timestamp FROM uploadedPhoto WHERE timestamp > '" + timestamp24h + "'";
        Cursor cur = db.rawQuery(SQLquery, null);
        cur.moveToFirst();
        if(cur.getCount() < 6) {
            canUpload = true;
        }
        cur.close();

        return canUpload;
    }

    public String addDateString(int interval){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String timestamp = simpleDateFormat.format(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, interval);
        Date h24 = cal.getTime();
        String timestamp24h = simpleDateFormat.format(h24);

        return timestamp24h;
    }


    public void deleteCurUser(SQLiteDatabase db) {
        //SQLiteDatabase db = dbMyFitness;//this.getWritableDatabase();
        db.execSQL("DELETE FROM curUser");
    }

    public List<KeyValueList> getCurUser(SQLiteDatabase db) {
        String authLogin;
        String authPass;
        String loggedDate;
        String email;
        //SQLiteDatabase db = dbMyFitness;//this.getWritableDatabase();
        String SQLquery = "SELECT authLogin , authPass , status, loggedDate, email FROM curUser";
        Cursor cur = db.rawQuery(SQLquery, null);
        cur.moveToFirst();
        if(cur.getCount() != 0) {
            authLogin = cur.getString(0);
            authPass = cur.getString(1);
            loggedDate = cur.getString(3);
            email = cur.getString(4);
        }
        else{
            authLogin = "Net takogo usera";
            authPass = "Net takogo usera";
            loggedDate = "01-01-1900-00-00-00";
            email = "";
        }
        cur.close();

        if(loggedDate == null){
            loggedDate = "01-01-2010-00-00-00";
        }

        postParams = new ArrayList<KeyValueList>();
        postParams.add(0, new KeyValueList("username", authLogin));
        postParams.add(1, new KeyValueList("password", authPass));
        postParams.add(2, new KeyValueList("loggedTime", loggedDate));
        postParams.add(3, new KeyValueList("email", email));

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

    public int getCurUserId(SQLiteDatabase db) {
        String SQLquery = "SELECT id FROM curUser";
        Cursor cur = db.rawQuery(SQLquery, null);
        cur.moveToFirst();
        Log.e("image user:",Integer.toString(cur.getInt(0)));
        int respond = 0;
        if(cur.getCount() != 0) {
            respond = cur.getInt(0);
        }
        else{
            respond = -1;
        }

        cur.close();
        return respond;
    }

}


