package cardam2.cardam2;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import MyFitness.KeyValueList;

public class SearchActivity extends AppCompatActivity {

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
    public Activity mActivity;
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
        setContentView(R.layout.activity_search);

        mActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        regnumTv = (TextView) findViewById(R.id.regnumTv);

        Button searchBt =  (Button) findViewById(R.id.searchBt);

        searchBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                regnumSearch = regnumTv.getText().toString();
                String regnumLatyn =  changeABC(regnumSearch);
                //regnumSearch = "c1";
                cardamUrlSearchRegnumCurrent = cardamUrlSearchRegnum + "/" + regnumLatyn;
                searchRegnum(cardamUrlSearchRegnumCurrent, regnumSearch);
            }
        });

    }

    public void init(){
        snackbarWrongSearchRegnum = Snackbar.make(findViewById(R.id.csLayout2), R.string.search_wrong_regnum, Snackbar.LENGTH_INDEFINITE);
        cardamUrl = getResources().getString(R.string.cardamUrl);//"http://192.168.0.13:80";
        cardamUrlSearchRegnum = cardamUrl + getResources().getString(R.string.cardamUrlSearchRegnum);//cardamUrl + "/public/api/searchvehicle";
        cardamUrlCheckLogin = cardamUrl + getResources().getString(R.string.cardamUrlCheckLogin);
        rlSearch = (LinearLayout) findViewById(R.id.linearLayout);
        rlSearch.setOrientation(LinearLayout.VERTICAL);
        images = new ArrayList<ImageView>();
        db = new DBHelper(this);
        //db.deleteDatabase();
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

        JsonTask jt = new JsonTask(this, db, regnumURL, images, rlSearch, mActivity, JsonTask.MULTTYPE);
        jt.execute();
        images = jt.getImages();
        titles = jt.getTitles();
    }

    public String changeABC(String originalStr){
        String changedABC = "";

        for(int i = 0; i < originalStr.length(); i++){
            changedABC = changedABC + getLatyn(originalStr.charAt(i));
        }


        return changedABC;
    }

    public char getLatyn(char originChar){

        char latChar;
        originChar = Character.toUpperCase(originChar);
        Character cyrChar = new Character(originChar);
        if(cyrChar.toString().equals("А")){
            String str = "A";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("В")){
            String str = "B";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("Е")){
            String str = "E";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("Н")){
            String str = "H";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("К")){
            String str = "K";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("М")){
            String str = "M";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("О")){
            String str = "O";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("Р")){
            String str = "P";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("С")){
            String str = "C";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("Т")) {
            String str = "T";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("У")){
            String str = "Y";
            latChar = str.charAt(0);
            return latChar;
        }
        if(cyrChar.toString().equals("Х")) {
            String str = "X";
            latChar = str.charAt(0);
            return latChar;
        }

        latChar = originChar;
        return latChar;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

}
