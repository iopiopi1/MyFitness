package cardam2.cardam2;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import java.io.File;
import java.util.ArrayList;
import android.widget.GridLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.view.Menu;

public class PhotoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private TabItem photoTI;
    private TabItem galleryTI;
    private TabHost tab;
    private Activity mActivity;
    public DBHelper db;
    private int photosSpanId;
    private TextView photosSpanTextView;
    private Menu menu;
    private MenuItem itemForward;
    private static final String PHOTOS_KEY = "easy_image_photos_list";

    //@Bind(R.id.recycler_view)
    //protected RecyclerView recyclerView;

    private ImagesAdapter imagesAdapter;

    private ArrayList<File> photos = new ArrayList<>();
    private GridLayout grLayout;
    public AddPicsFragment addpicsFragment;
    public ProgressBarFragment progressBarFragment;
    public FragmentTransaction transaction;
    public FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            addpicsFragment = new AddPicsFragment();
            progressBarFragment = new ProgressBarFragment();
            //addpics.setArguments(getIntent().getExtras());
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, addpicsFragment).commit();
        }

    }

    public FragmentTransaction getTransaction(){
        return transaction;
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
        if(addpicsFragment.photos != null) {
            if (addpicsFragment.photos.size() > 0) {
                menu.findItem(R.id.action_favorite).setVisible(true);
            } else {
                menu.findItem(R.id.action_favorite).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }






}
