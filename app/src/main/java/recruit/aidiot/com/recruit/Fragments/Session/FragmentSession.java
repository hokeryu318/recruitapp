package recruit.aidiot.com.recruit.Fragments.Session;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import recruit.aidiot.com.recruit.Adapters.SessionAdapter;
import recruit.aidiot.com.recruit.Fragments.FragmentParent;
import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.MessageModel;
import recruit.aidiot.com.recruit.Models.SessionModel;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/7/2018.
 */

public class FragmentSession extends FragmentParent implements View.OnClickListener{
    View rootView;
    private ACProgressFlower dialog;
    ListView mListView;
    public int mStart = 0;
    SessionAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sessions, container, false);
        dialog = new ACProgressFlower.Builder(getActivity())
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Loading...")
                .build();
        mListView = rootView.findViewById(R.id.list_sesssion);
        mAdapter = new SessionAdapter(GD.g_sessionList, getActivity());
        mListView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}
