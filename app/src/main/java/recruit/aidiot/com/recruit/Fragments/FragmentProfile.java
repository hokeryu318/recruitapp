package recruit.aidiot.com.recruit.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/7/2018.
 */

public class FragmentProfile extends FragmentParent implements View.OnClickListener {

    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_profile_setting, container, false);
        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}
