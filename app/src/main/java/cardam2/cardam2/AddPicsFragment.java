package cardam2.cardam2;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.support.v7.widget.LinearLayoutManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ImageButton;

/**
 * Created by yaros on 1/10/2018.
 */

public class AddPicsFragment extends Fragment  {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private TabItem photoTI;
    private TabItem galleryTI;
    private TabHost tab;
    private Activity mActivity;
    private Fragment mFragment;
    private int photosSpanId;
    private TextView photosSpanTextView;
    private Menu menu;
    private MenuItem itemForward;
    private static final String PHOTOS_KEY = "easy_image_photos_list";
    private ImageView targetImageView;
    public DBHelper db;
    @Bind(R.id.recycler_view)
    protected RecyclerView recyclerView;

    private ImagesAdapter imagesAdapter;

    public ArrayList<File> photos = new ArrayList<>();
    private GridLayout grLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = (PhotoActivity)this.getActivity();
        mFragment = this;
        db = new DBHelper(mActivity);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.addpics, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        ButterKnife.bind(mActivity);
        Nammu.init(mActivity);

        if (savedInstanceState != null) {
            photos = (ArrayList<File>) savedInstanceState.getSerializable(PHOTOS_KEY);
        }

        imagesAdapter = new ImagesAdapter(mActivity, photos);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
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
            Nammu.askForPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionCallback() {
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

        ImageButton bt1 = (ImageButton) getView().findViewById(R.id.camera_button);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean canUpload = db.canUploadNew(db.dbMyFitness);
                if(canUpload) {
                    EasyImage.openCamera(mFragment, 0);
                }
                else{
                    String msg = getView().getResources().getString(R.string.photos_enough_upload);
                    String hours = db.uploadTimeNext(db.dbMyFitness);
                    msg = msg  + " " +  hours  + " " +  "часа(ов)";
                    Snackbar snackbar = Snackbar.make(getView().findViewById(R.id.constraintLayout3), msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

        });

        ImageButton bt2 = (ImageButton) getView().findViewById(R.id.chooser_button2);
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean canUpload = db.canUploadNew(db.dbMyFitness);
                if(canUpload) {
                    EasyImage.openGallery(mFragment, 0);
                }
                else{
                    String msg = getView().getResources().getString(R.string.photos_enough_upload);
                    String hours = db.uploadTimeNext(db.dbMyFitness);
                    msg = msg + " " + hours + " " + "часа(ов)";
                    Snackbar snackbar = Snackbar.make(getView().findViewById(R.id.constraintLayout3), msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
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

    @Override
    public void onDestroy() {
        // Clear any configuration that was done!
        EasyImage.clearConfiguration(mActivity);
        super.onDestroy();
    }

    public void reloadPhotos() {
        int uqId;
        targetImageView = null;
        grLayout = (GridLayout) getView().findViewById(R.id.gridLayout1);

        if(photos.size() > 0 ) {
            PhotoActivity pActivity = (PhotoActivity)getActivity();
            pActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            pActivity.invalidateOptionsMenu();
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
                            targetImageView = (ImageView) getView().findViewById(R.id.imageView11);
                            break;
                        case 1:
                            targetImageView = (ImageView) getView().findViewById(R.id.imageView12);
                            break;
                        case 2:
                            targetImageView = (ImageView) getView().findViewById(R.id.imageView13);
                            break;
                        case 3:
                            targetImageView = (ImageView) getView().findViewById(R.id.imageView14);
                            break;
                        case 4:
                            targetImageView = (ImageView) getView().findViewById(R.id.imageView15);
                            break;
                        case 5:
                            targetImageView = (ImageView) getView().findViewById(R.id.imageView16);
                            break;
                    }
                    targetImageView.setImageBitmap(myBitmap);
                    targetImageView.invalidate();
                }
            }
            pActivity.transaction.commit();
        }
        else{
            targetImageView = (ImageView) getView().findViewById(R.id.imageView11);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) getView().findViewById(R.id.imageView12);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) getView().findViewById(R.id.imageView13);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) getView().findViewById(R.id.imageView14);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) getView().findViewById(R.id.imageView15);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
            targetImageView = (ImageView) getView().findViewById(R.id.imageView16);
            targetImageView.setImageResource(0);
            targetImageView.invalidate();
        }
    }

    public void turnOnProgressBar(int progress){
        View view = getView();
        if(view != null) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
            ConstraintLayout cs = (ConstraintLayout) view.findViewById(R.id.photoMainCL);
            GridLayout gr1 = (GridLayout) view.findViewById(R.id.gridLayout1);
            gr1.setAlpha((float) 0.3);
            gr1.invalidate();
            ConstraintLayout cs5 = (ConstraintLayout) view.findViewById(R.id.constraintLayout5);
            cs5.setAlpha((float) 0.3);
            cs5.invalidate();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);
            progressBar.bringToFront();
            progressBar.invalidate();
            cs.invalidate();
        }
    }

    public void turnOffProgressBar(){
        View view = getView();
        if(view != null) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
            ConstraintLayout cs = (ConstraintLayout) view.findViewById(R.id.photoMainCL);
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.invalidate();
            GridLayout gr1 = (GridLayout) view.findViewById(R.id.gridLayout1);
            gr1.setAlpha((float) 1);
            gr1.invalidate();
            ConstraintLayout cs5 = (ConstraintLayout) view.findViewById(R.id.constraintLayout5);
            cs5.setAlpha((float) 1);
            cs5.invalidate();
            cs.invalidate();
        }
    }

}
