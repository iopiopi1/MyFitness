package cardam2.cardam2;
import android.app.Activity;
import 	android.app.ListFragment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by yaros on 2/11/2018.
 */

public class PhotosmadeFragment extends ListFragment {
    onReloadPhotosListener mCallback;

    // Container Activity must implement this interface
    public interface onReloadPhotosListener {
        public void onReloadPhotos(ArrayList<File> photos);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (onReloadPhotosListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }



}