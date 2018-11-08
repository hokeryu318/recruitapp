package recruit.aidiot.com.recruit.Fragments.Company;

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

import recruit.aidiot.com.recruit.Adapters.CompanyAdapter;
import recruit.aidiot.com.recruit.Fragments.FragmentParent;
import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.CompanyModel;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/6/2018.
 */

public class FragmentCompany extends FragmentParent implements View.OnClickListener  {
    View rootView;
    private ACProgressFlower dialog;
    ListView mListView;
    int mStart = 0;
    private CompanyAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_company, container, false);

        mListView = rootView.findViewById(R.id.list_company);
        dialog = new ACProgressFlower.Builder(getActivity())
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Loading...")
                .build();

        GD.g_companyList = new ArrayList<>();

        loadCompanies();

        return rootView;
    }

    private void loadCompanies(){
        String url = GD.BASE_URL + "company/list?user_id=" + GD.g_profile.id + "&is_all=1&start=" + Integer.toString(mStart) + "&limit=10";
        new GetFavCompaniesApi().execute(url, "");
    }

    @Override
    public void onClick(View v) {

    }

    public class GetFavCompaniesApi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }
        protected String doInBackground(String... param) {
            String url = param[0];
            String data = param[1];
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
                    for(int i = 0; i < ret.length(); i++){
                        JSONObject item = ret.getJSONObject(i);
                        CompanyModel companyModel = new CompanyModel();
                        companyModel.id = item.getInt("id");
                        companyModel.name = item.getString("name");
                        companyModel.manager_name = item.getString("manager_name");
                        companyModel.email = item.getString("email");
                        companyModel.tel = item.getString("tel");
                        companyModel.area = item.getString("area");
                        companyModel.near_station = item.getString("near_station");
                        companyModel.site = item.getString("site");
                        companyModel.description = item.getString("description");
                        companyModel.interview_content = item.getString("interview_content");
                        companyModel.ticket = item.getInt("ticket");
                        companyModel.profile_img = item.getString("profile_img");
                        companyModel.valid_flag = item.getInt("valid_flag");
                        companyModel.exam_status = item.getString("exam_status");
                        companyModel.interview_request = item.getString("interview_request");
                        companyModel.profile_image = item.getString("profile_image");
                        companyModel.favorite = item.getString("favorite");
                        GD.g_companyList.add(companyModel);
                    }
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            mAdapter = new CompanyAdapter(GD.g_companyList, getActivity());
            mListView.setAdapter(mAdapter);
        }
    }
}
