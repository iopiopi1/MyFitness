package cardam2.cardam2;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.content.Intent;
import android.provider.MediaStore;
import java.io.File;
import java.io.IOException;
import android.os.Environment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.GridLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.view.Menu;
import android.util.Log;

public class PhotoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private TabItem photoTI;
    private TabItem galleryTI;
    private TabHost tab;
    private Activity mActivity;
    private DBHelper db;
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

            AddPicsFragment addpics = new AddPicsFragment();
            //addpics.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, addpics).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String cardamUrl = getResources().getString(R.string.cardamUrl);
        cardamUrl = cardamUrl + getResources().getString(R.string.cardamUrlImageUpoad);
        Log.e("image was photos:",photos.toString());
        switch (item.getItemId()) {
            case R.id.action_favorite:
                //PostFilesTask fileTask = new PostFilesTask(cardamUrl, photos, mActivity);
                PostFilesTask fileTask = null;
                int count = 0;
                int maxTries = 5;
                String resultMessage = null;

                for(int i = 0; i < photos.size(); i++){
                    ArrayList<File> photo = new ArrayList<>();
                    Log.e("image was photo(0.5):",photos.get(i).toString());
                    photo.add(0, photos.get(i));
                    fileTask = new PostFilesTask(cardamUrl, photo, mActivity);
                    fileTask.execute();
                }
                Snackbar snackbar = Snackbar.make(mActivity.findViewById(R.id.constraintLayout3), R.string.photos_success_upload, Snackbar.LENGTH_LONG);
                snackbar.show();
                photos.clear();
                //reloadPhotos();
                invalidateOptionsMenu();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(photos.size() > 0) {
            menu.findItem(R.id.action_favorite).setVisible(true);
        }
        else{
            menu.findItem(R.id.action_favorite).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }






}
