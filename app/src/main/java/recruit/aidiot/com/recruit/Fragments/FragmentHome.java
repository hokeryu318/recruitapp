package recruit.aidiot.com.recruit.Fragments;

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import recruit.aidiot.com.recruit.Fragments.Company.FragmentCompany;
import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

public class FragmentHome extends FragmentParent implements View.OnClickListener  {
    View rootView;
    private ACProgressFlower dialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        dialog = new ACProgressFlower.Builder(getActivity())
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Log in...")
                .build();

        ImageView img_profile = rootView.findViewById(R.id.iv_profile_photo);
        Picasso.with(getActivity()).load(GD.g_profile.user_image).into(img_profile);

        TextView txt_name = rootView.findViewById(R.id.tv_profile_name);
        txt_name.setText(GD.g_profile.name);

        TextView txt_description = rootView.findViewById(R.id.tv_profile_description);
//        txt_description.setText(GD.g_profile.);

        rootView.findViewById(R.id.iv_favourite).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_favourite:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                FragmentCompany fragmentCompany = new FragmentCompany();
                transaction.addToBackStack("transaction");
                transaction.replace(R.id.lay_main, fragmentCompany);
                transaction.commit();
                break;
        }
    }

    public class GetSessionsApi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }
        protected String doInBackground(String... param) {
            String url = param[0];
            String data = param[1];
            String response = CF.HttpPostRequest(url, data, "POST");
            publishProgress(response);
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            String response = progress[0];
            try {
                JSONObject joresult = new JSONObject(response);
                boolean success = joresult.getBoolean("success");
                if(success) {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    public class EmptyApi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }
        protected String doInBackground(String... param) {
            String url = param[0];
            String data = param[1];
            String response = CF.HttpPostRequest(url, data, "POST");
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
                    JSONObject ret = data.getJSONObject("ret");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

//    boolean doubleBackToExitPressedOnce = false;
//    @Override
//    public void onBack(){
//        if (doubleBackToExitPressedOnce) {
//            getActivity().finish();
//        }
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(getActivity(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce = false;
//            }
//        }, 2000);
//    }
}
