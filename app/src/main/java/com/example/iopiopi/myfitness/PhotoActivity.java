package com.example.iopiopi.myfitness;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.provider.MediaStore;
import java.io.File;
import java.io.IOException;
import android.os.Environment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TabHost;

import MyFitness.KeyValueList;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;

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

    @Bind(R.id.recycler_view)
    protected RecyclerView recyclerView;

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

        ButterKnife.bind(this);
        Nammu.init(this);

        if (savedInstanceState != null) {
            photos = (ArrayList<File>) savedInstanceState.getSerializable(PHOTOS_KEY);
        }

        imagesAdapter = new ImagesAdapter(this, photos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imagesAdapter);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    //Nothing, this sample saves to Public gallery so it needs permission
                }

                @Override
                public void permissionRefused() {
                    finish();
                }
            });
        }

        EasyImage.configuration(this)
                .setImagesFolderName("My app images")
                .setAllowMultiplePickInGallery(true);
        mActivity = this;

        init();

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
        switch (item.getItemId()) {
            case R.id.action_favorite:
                PostFilesTask fileTask = new PostFilesTask(cardamUrl, photos, mActivity);

                int count = 0;
                int maxTries = 5;
                String resultMessage = null;
                while(true) {
                    try {
                        fileTask.execute();
                        if(fileTask.getStatuss().equals("Executed")){
                            resultMessage = getResources().getString(R.string.photos_success_upload);
                            break;
                        }
                    } catch (Exception e) {
                        // handle exception
                        if (++count == maxTries) {
                            resultMessage = getResources().getString(R.string.photos_nonsuccess_upload);
                            break;
                        };
                    }
                }

                Snackbar snackbar = Snackbar.make(mActivity.findViewById(R.id.constraintLayout3), R.string.photos_success_upload, Snackbar.LENGTH_LONG);
                snackbar.show();
                photos.clear();
                reloadPhotos();
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

    public void init(){
        photosSpanId = 0;
        reloadPhotos();
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

    @OnClick(R.id.camera_button)
    protected void onTakePhotoClicked() {
        EasyImage.openCamera(this, 0);

    }

    //@OnClick(R.id.documents_button)
    protected void onPickFromDocumentsClicked() {
        /** Some devices such as Samsungs which have their own gallery app require write permission. Testing is advised! */

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openDocuments(this, 0);
        } else {
            Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    EasyImage.openDocuments(PhotoActivity.this, 0);
                }

                @Override
                public void permissionRefused() {

                }
            });
        }
    }



    @OnClick(R.id.chooser_button2)
    protected void onChooserWithGalleryClicked() {
        EasyImage.openChooserWithGallery(this, "Pick source", 0);

    }

    /*@OnClick(R.id.sendImagesBt)
    protected void onSendPicsToServer() {

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                //Handle the images
                onPhotosReturned(imagesFiles);
            }
        });
    }

    private void onPhotosReturned(List<File> returnedPhotos) {
        photos.addAll(returnedPhotos);
        imagesAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(photos.size() - 1);
        //menu.getItem(i).setVisible(false);
        invalidateOptionsMenu();
        reloadPhotos();
    }

    @Override
    protected void onDestroy() {
        // Clear any configuration that was done!
        EasyImage.clearConfiguration(this);
        super.onDestroy();
    }

    public void reloadPhotos() {
        db = new DBHelper(this);
        int uqId;
        ImageView targetImageView = null;
        grLayout = (GridLayout) findViewById(R.id.gridLayout1);

        if(photos.size() > 0 ) {
            //photosSpanTextView.setVisibility(View.INVISIBLE);
            for (int i = 0; i < 8/*photos.size()*/; i++) {
                File imgFile = new File(photos.get(i).toString());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    targetImageView = new ImageView(this);
                    //uqId = db.addUniqueId(db.dbMyFitness);
                    //targetImageView.setId(uqId);
                    //targetImageView.setImageBitmap(myBitmap);
                    switch (i) {
                        case 0:
                            targetImageView = (ImageView) findViewById(R.id.imageView11);
                            break;
                        case 1:
                            targetImageView = (ImageView) findViewById(R.id.imageView12);
                            break;
                        case 2:
                            targetImageView = (ImageView) findViewById(R.id.imageView13);
                            break;
                        case 3:
                            targetImageView = (ImageView) findViewById(R.id.imageView14);
                            break;
                        case 4:
                            targetImageView = (ImageView) findViewById(R.id.imageView15);
                            break;
                        case 5:
                            targetImageView = (ImageView) findViewById(R.id.imageView16);
                            break;
                        case 6:
                            targetImageView = (ImageView) findViewById(R.id.imageView17);
                            break;
                        case 7:
                            targetImageView = (ImageView) findViewById(R.id.imageView18);
                            break;
                        case 8:
                            targetImageView = (ImageView) findViewById(R.id.imageView19);
                            break;
                    }
                    targetImageView.setImageBitmap(myBitmap);
                    targetImageView.invalidate();
                }
                //grLayout.getLayoutParams().width = 1;
                //grLayout.addView(targetImageView);
            }
        }
        else{
            targetImageView = (ImageView) findViewById(R.id.imageView11);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) findViewById(R.id.imageView12);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) findViewById(R.id.imageView13);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) findViewById(R.id.imageView14);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) findViewById(R.id.imageView15);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) findViewById(R.id.imageView16);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) findViewById(R.id.imageView17);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) findViewById(R.id.imageView17);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) findViewById(R.id.imageView18);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
        }
    }

}
