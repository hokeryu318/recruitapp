package recruit.aidiot.com.recruit.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import recruit.aidiot.com.recruit.Adapters.TimeAdapter;
import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.TimeModel;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/7/2018.
 */

public class FragmentTime extends FragmentParent implements View.OnClickListener  {
    View rootView;
    private ACProgressFlower dialog;
    TimeAdapter mAdapter;
    GridView mGridView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        dialog = new ACProgressFlower.Builder(getActivity())
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Loading...")
                .build();

        loadTimes();

        mGridView = rootView.findViewById(R.id.grid_times);

        return rootView;
    }

    public void loadTimes(){
        GD.g_timeList = new ArrayList<>();
        String url = GD.BASE_URL + "user/times?user_id=" + GD.g_profile.id;
        new GetTimesApi().execute(url);
    }

    @Override
    public void onClick(View v) {

    }

    public class GetTimesApi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }
        protected String doInBackground(String... param) {
            String url = param[0];
            String response = CF.HttpGetRequest(url);
            publishProgress(response);
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            String response = progress[0];
            try {
                JSONObject joresult = new JSONObject(response);
                boolean success = joresult.getBoolean("success");
                if(success) {
                    JSONObject data = joresult.getJSONObject("data");
                    JSONArray ret = data.getJSONArray("ret");
                    for (int i = 0; i < ret.length(); i++){
                        JSONObject item = ret.getJSONObject(i);
                        TimeModel timeModel = new TimeModel();
                        timeModel.id = item.getInt("id");
                        timeModel.due_at = item.getString("due_at");
                        timeModel.end_at = item.getString("end_at");
                        timeModel.due_at_date = item.getString("due_at_date");
                        timeModel.due_at_weekday = item.getString("due_at_weekday");
                        timeModel.due_at_time = item.getString("due_at_time");
                        timeModel.end_at_time = item.getString("end_at_time");
                        timeModel.datetimestring = item.getString("datetimestring");
                        timeModel.due_at_full_date = item.getString("datetimestring");
                        GD.g_timeList.add(timeModel);
                    }
                    TimeModel newItem = new TimeModel();
                    GD.g_timeList.add(newItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            mAdapter = new TimeAdapter(GD.g_timeList, FragmentTime.this);
            mGridView.setAdapter(mAdapter);
        }
    }
}
