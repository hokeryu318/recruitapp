package recruit.aidiot.com.recruit.Fragments;

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

import java.lang.reflect.Array;
import java.util.ArrayList;

import recruit.aidiot.com.recruit.Adapters.NotifyAdapter;
import recruit.aidiot.com.recruit.Adapters.NotifyAdminAdapter;
import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.AdminMsgModel;
import recruit.aidiot.com.recruit.Models.CompanyMsgModel;
import recruit.aidiot.com.recruit.Models.SessionModel;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/9/2018.
 */

public class FragmentNotify extends FragmentParent implements View.OnClickListener {
    View rootView;
    private ACProgressFlower dialog;
    ListView mListView;
    NotifyAdapter mAdapter;
    NotifyAdminAdapter mAdminAdapter;

    public static int MODE_COMPANY = 1;
    public static int MODE_ADMIN = 2;

    ArrayList<CompanyMsgModel> companyMsgModelArrayList = new ArrayList<>();
    ArrayList<Object> adminMsgs = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, container, false);
        dialog = new ACProgressFlower.Builder(getActivity())
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Refresh...")
                .build();
        mListView = rootView.findViewById(R.id.lv_message);

        loadMessage(1);

        rootView.findViewById(R.id.tab_message_from_company).setOnClickListener(this);
        rootView.findViewById(R.id.tab_message_from_management).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab_message_from_company:
                loadMessage(1);
                break;
            case R.id.tab_message_from_management:
                loadMessage(2);
                break;
        }
    }

    public void loadMessage(int mode){
        if(mode == MODE_COMPANY){ //load company message
            companyMsgModelArrayList.clear();
            String url = GD.BASE_URL + "notification?user_id=" + GD.g_profile.id + "&type=company";
            new LoadMessageFromCompanyApi().execute(url);
        } else if(mode == MODE_ADMIN){
            adminMsgs.clear();
            String url = GD.BASE_URL + "notification?user_id=" + GD.g_profile.id + "&type=admin";
            new LoadMessageFromAdminApi().execute(url);
        }
    }

    public void updateUi(int mode){
        if(mode == MODE_COMPANY){
            mAdapter = new NotifyAdapter(companyMsgModelArrayList, getActivity());
            mListView.setAdapter(mAdapter);
        } else if(mode == MODE_ADMIN){
            mAdminAdapter = new NotifyAdminAdapter(adminMsgs, getActivity());
            mListView.setAdapter(mAdminAdapter);
        }
    }

    public class LoadMessageFromCompanyApi extends AsyncTask<String, String, String> {
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
                        CompanyMsgModel companyMsgModel = new CompanyMsgModel();
                        JSONObject item = ret.getJSONObject(i);
                        companyMsgModel.id = item.getInt("id");
                        companyMsgModel.session_id = Integer.parseInt(item.getString("session_id"));
                        companyMsgModel.from_to = item.getString("from_to");
                        companyMsgModel.message = item.getString("message");
                        companyMsgModel.status = item.getString("status");
                        JSONObject session = item.getJSONObject("session");
                        SessionModel sessionModel = GD.sessionFromJson(session);
                        companyMsgModel.session = sessionModel;
                        companyMsgModelArrayList.add(companyMsgModel);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            updateUi(1);
        }
    }

    public class LoadMessageFromAdminApi extends AsyncTask<String, String, String> {
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
                        if(item.has("type") && item.getString("type").equals("admin")){
                            AdminMsgModel model = new AdminMsgModel();
                            model.id = item.getInt("id");
                            model.message = item.getString("message");
                            model.type = "admin";
                            adminMsgs.add(model);
                        } else {
                            CompanyMsgModel companyMsgModel = new CompanyMsgModel();
                            companyMsgModel.id = item.getInt("id");
                            companyMsgModel.session_id = Integer.parseInt(item.getString("session_id"));
                            companyMsgModel.from_to = item.getString("from_to");
                            companyMsgModel.message = item.getString("message");
                            companyMsgModel.status = item.getString("status");
                            JSONObject session = item.getJSONObject("session");
                            SessionModel sessionModel = GD.sessionFromJson(session);
                            companyMsgModel.session = sessionModel;
                            adminMsgs.add(companyMsgModel);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            updateUi(2);
        }
    }
}
