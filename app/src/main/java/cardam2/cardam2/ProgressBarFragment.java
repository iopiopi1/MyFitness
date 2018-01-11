package cardam2.cardam2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yaros on 1/10/2018.
 */

public class ProgressBarFragment extends Fragment {

    private Activity mActivity;
    private Fragment mFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = (PhotoActivity)this.getActivity();
        mFragment = this;
        return inflater.inflate(R.layout.progspinner, container, false);
    }

}
