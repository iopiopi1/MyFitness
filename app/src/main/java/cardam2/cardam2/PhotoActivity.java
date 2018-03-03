package cardam2.cardam2;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.view.Menu;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

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
    private ImageView targetImageView;
    @Bind(R.id.recycler_view)
    protected RecyclerView recyclerView;
    private ImagesAdapter imagesAdapter;
    public ArrayList<File> photos = new ArrayList<>();
    private GridLayout grLayout;
    private Menu optionsMenu;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mActivity = this;
        db = new DBHelper(mActivity);
        //db.delUploadedPhoto(db.dbMyFitness);////////////////////////////////////////////////////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(mActivity);
        Nammu.init(mActivity);

        if (savedInstanceState != null) {
            photos = (ArrayList<File>) savedInstanceState.getSerializable(PHOTOS_KEY);
        }

        imagesAdapter = new ImagesAdapter(mActivity, photos);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imagesAdapter);

        int permissionCheck = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Nammu.askForPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    //Nothing, this sample saves to Public gallery so it needs permission
                }

                @Override
                public void permissionRefused() {
                    mActivity.finish();
                }
            });
        }

        int permissionCheckRead = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheckRead != PackageManager.PERMISSION_GRANTED) {
            Nammu.askForPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    //Nothing, this sample saves to Public gallery so it needs permission
                }

                @Override
                public void permissionRefused() {
                    mActivity.finish();
                }
            });
        }

        EasyImage.configuration(mActivity)
                .setImagesFolderName("Cardam")
                .setAllowMultiplePickInGallery(true)
                .setCopyTakenPhotosToPublicGalleryAppFolder(true);

        init();

        ImageButton bt1 = findViewById(R.id.camera_button);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean canUpload = db.canUploadNew(db.dbMyFitness);
                if(canUpload) {
                    EasyImage.openCamera(mActivity, 0);
                }
                else{
                    String msg = getResources().getString(R.string.photos_enough_upload);
                    String hours = db.uploadTimeNext(db.dbMyFitness);
                    msg = msg  + " " +  hours  + " " +  "часа(ов)";
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.constraintLayout3), msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

        });

        ImageButton bt2 = (ImageButton) findViewById(R.id.chooser_button2);
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean canUpload = db.canUploadNew(db.dbMyFitness);
                if(canUpload) {
                    EasyImage.openGallery(mActivity, 0);
                }
                else{
                    String msg = getResources().getString(R.string.photos_enough_upload);
                    String hours = db.uploadTimeNext(db.dbMyFitness);
                    msg = msg + " " + hours + " " + "часа(ов)";
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.constraintLayout3), msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });


        /*if (findViewById(R.id.fragment_container) != null) {

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
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        optionsMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (photos.size() > 0) {
            menu.findItem(R.id.action_favorite).setVisible(true);
        } else {
            menu.findItem(R.id.action_favorite).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void init(){
        photosSpanId = 0;
        reloadPhotos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String cardamUrl = getResources().getString(R.string.cardamUrl);
        cardamUrl = cardamUrl + getResources().getString(R.string.cardamUrlImageUpoad);
        Log.e("image was photos:",photos.toString());
        switch (item.getItemId()) {
            case R.id.action_favorite:
                //turnOnProgressBar();
                PostFilesTask fileTask = null;
                ArrayList<PostFilesTask> tasks = new ArrayList<PostFilesTask>();

                for(int i = 0; i < photos.size(); i++){
                    ArrayList<File> photo = new ArrayList<>();
                    Log.e("image was photo(0.5):",photos.get(i).toString());
                    photo.add(0, photos.get(i));
                    float d = (((float)i / photos.size()) * 100);
                    int percentage = Math.round(d);
                    boolean isLast = false;
                    if(i + 1 == photos.size() ){
                        isLast = true;
                    }
                    fileTask = new PostFilesTask(cardamUrl, photo, mActivity, percentage, isLast);
                    fileTask.execute();
                    tasks.add(fileTask);
                }

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        if(!isLoading){
            super.onBackPressed();
            return;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PHOTOS_KEY, photos);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, mActivity, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imagesFiles);
            }
        });
    }

    private void onPhotosReturned(List<File> returnedPhotos) {
        photos.addAll(returnedPhotos);
        imagesAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(photos.size() - 1);
        mActivity.invalidateOptionsMenu();
        reloadPhotos();
    }

    public void reloadPhotos() {
        int uqId;
        targetImageView = null;
        grLayout = findViewById(R.id.gridLayout1);

        if(photos.size() > 0 ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            invalidateOptionsMenu();
            for (int i = 0; i < photos.size(); i++) {
                if (i > 5) {
                    break;
                }
                File imgFile = new File(photos.get(i).toString());
                if (imgFile.exists()) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    String imagePath = imgFile.getAbsolutePath();
                    Bitmap myBitmap = BitmapFactory.decodeFile(imagePath, options);
                    myBitmap = ImageHelper.checkOrientation(myBitmap, imagePath);
                    targetImageView = new ImageView(mActivity);

                    switch (i) {
                        case 0:
                            targetImageView = findViewById(R.id.imageView11);
                            break;
                        case 1:
                            targetImageView = findViewById(R.id.imageView12);
                            break;
                        case 2:
                            targetImageView = findViewById(R.id.imageView13);
                            break;
                        case 3:
                            targetImageView = findViewById(R.id.imageView14);
                            break;
                        case 4:
                            targetImageView = findViewById(R.id.imageView15);
                            break;
                        case 5:
                            targetImageView = findViewById(R.id.imageView16);
                            break;
                    }
                    targetImageView.setImageBitmap(myBitmap);
                    targetImageView.invalidate();
                }
            }
        }
        else{
            targetImageView = (ImageView) findViewById(R.id.imageView11);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = findViewById(R.id.imageView12);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = findViewById(R.id.imageView13);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = findViewById(R.id.imageView14);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = findViewById(R.id.imageView15);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = findViewById(R.id.imageView16);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
        }
    }

    public void turnOnProgressBar(int progress){
        isLoading = true;
        optionsMenu.findItem(R.id.action_favorite).setVisible(false);
        ProgressBar progressBar = findViewById(R.id.progressBar2);
        ConstraintLayout cs = findViewById(R.id.photoMainCL);
        GridLayout gr1 = findViewById(R.id.gridLayout1);
        gr1.setAlpha((float) 0.3);
        gr1.invalidate();
        ConstraintLayout cs5 = findViewById(R.id.constraintLayout5);
        cs5.setAlpha((float) 0.3);
        cs5.invalidate();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress);
        progressBar.bringToFront();
        progressBar.invalidate();
        cs.invalidate();
    }

    public void turnOffProgressBar(){
        isLoading = false;
        ProgressBar progressBar = findViewById(R.id.progressBar2);
        ConstraintLayout cs = findViewById(R.id.photoMainCL);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.invalidate();
        GridLayout gr1 = findViewById(R.id.gridLayout1);
        gr1.setAlpha((float) 1);
        gr1.invalidate();
        ConstraintLayout cs5 = findViewById(R.id.constraintLayout5);
        cs5.setAlpha((float) 1);
        cs5.invalidate();
        cs.invalidate();
    }


}
