package recruit.aidiot.com.recruit.Fragments.Session;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import recruit.aidiot.com.recruit.Fragments.FragmentParent;
import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.SessionModel;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/7/2018.
 */

public class FragmentSessionDetail extends FragmentParent implements View.OnClickListener  {
    View rootView;
    private ACProgressFlower dialog;
    PopupWindow selectPopup, declinePopup;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_company_details, container, false);
        dialog = new ACProgressFlower.Builder(getActivity())
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Refreshing...")
                .build();

        SessionModel sessionModel = GD.g_selectedSession;

        ImageView imgProfile = (ImageView)rootView.findViewById(R.id.iv_company_image);
        Picasso.with(getActivity()).load(sessionModel.profile_image).into(imgProfile);
        TextView txtName = (TextView)rootView.findViewById(R.id.tv_company_name);
        txtName.setText(sessionModel.name);
        TextView txtStation = (TextView)rootView.findViewById(R.id.tv_company_train_station);
        txtStation.setText(sessionModel.near_station);
        TextView txtLocation = (TextView)rootView.findViewById(R.id.tv_company_location);
        txtLocation.setText(sessionModel.place);
        TextView txtTel = (TextView)rootView.findViewById(R.id.tv_company_telephone);
//        txtTel.setText(sessionModel.tel);
        TextView txtSite = (TextView)rootView.findViewById(R.id.tv_company_website);
        txtSite.setText(sessionModel.site);
        TextView txtDesc = (TextView)rootView.findViewById(R.id.tv_company_description);
        txtDesc.setText(sessionModel.description);
        TextView txtInterDetail = (TextView)rootView.findViewById(R.id.tv_company_interview_details);
        txtInterDetail.setText(sessionModel.content);

        rootView.findViewById(R.id.btn_next_term_of_use).setOnClickListener(this);

        int status = GD.g_selectedSession.status;
        if(!(status == 1 || status == 2 || status == 3 || status == 4 || status == 9)) //canceled
        {
            rootView.findViewById(R.id.btn_next_term_of_use).setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next_term_of_use:
                showActionPopup();
                break;
            case R.id.btn_change_schedule:
                selectPopup.dismiss();
                break;
            case R.id.btn_decline_submit:
                declinePopup.dismiss();
                declineAction();
                break;
        }
    }

    View.OnClickListener onAccept = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            acceptAction();
        }
    };

    public String popupMode = "Decline";

    View.OnClickListener onDecline = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectPopup.dismiss();
            popupMode = "Decline";
            showDeclinePopup();
        }
    };

    View.OnClickListener onMeet = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectPopup.dismiss();
            popupMode = "Meet";
            showDeclinePopup();
        }
    };

    View.OnClickListener onReport = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectPopup.dismiss();
            popupMode = "Report";
            showDeclinePopup();
        }
    };

    View.OnClickListener onMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            FragmentSessionMessage fragmentSessionMessage = new FragmentSessionMessage();
            transaction.addToBackStack("transaction");
            transaction.replace(R.id.lay_main, fragmentSessionMessage);
            transaction.commit();
            selectPopup.dismiss();
        }
    };


    public void showActionPopup(){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_sessions_deciding, (ViewGroup)rootView.findViewById(R.id.layout_root));
        //Get ScreenSize
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        selectPopup = new PopupWindow(layout, width , height, true);
        selectPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        selectPopup.setFocusable(true);

        TextView btn1 = layout.findViewById(R.id.btn_accept);
        TextView btn2 = layout.findViewById(R.id.btn_change_schedule);
        TextView btn3 = layout.findViewById(R.id.btn_decline);
        switch (GD.g_selectedSession.status){
            case 1:
                btn1.setOnClickListener(onAccept);
//                layout.findViewById(R.id.btn_change_schedule).setOnClickListener(this);
                btn3.setOnClickListener(onDecline);
                break;
            case 2:
            case 3:
                btn1.setText("Message");
                btn1.setOnClickListener(onMessage);
                btn2.setVisibility(View.GONE);
                btn3.setOnClickListener(onDecline);
                break;
            case 4:
                btn1.setText("Message"); btn1.setOnClickListener(onMessage);
                btn2.setText("Meet"); btn2.setOnClickListener(onMeet);
                btn3.setText("Report"); btn3.setOnClickListener(onReport);
                break;
        }
    }

    public void showDeclinePopup(){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_comment_decline, (ViewGroup)rootView.findViewById(R.id.layout_root));
        reason = layout.findViewById(R.id.ed_decline);
        //Get ScreenSize
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        declinePopup = new PopupWindow(layout, width, height, true);
        declinePopup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        declinePopup.setFocusable(true);

        layout.findViewById(R.id.btn_decline_submit).setOnClickListener(this);
    }
    EditText reason;

    public void acceptAction(){
        SessionModel sessionModel = GD.g_selectedSession;
        String url = GD.BASE_URL + "session/confirm";
        try {
            JSONObject jo = new JSONObject();
            jo.put("user_id", GD.g_profile.id);
            jo.put("session_id", sessionModel.id);
            new AcceptApi().execute(url, jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void declineAction(){
        SessionModel sessionModel = GD.g_selectedSession;
        if(popupMode.equals("Decline")) {
            String url = GD.BASE_URL + "session/cancel";
            try {
                JSONObject jo = new JSONObject();
                jo.put("user_id", GD.g_profile.id);
                jo.put("session_id", sessionModel.id);
                jo.put("reason", reason.getText().toString());
                new DeclineApi().execute(url, jo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(popupMode.equals("Meet")){
            String url = GD.BASE_URL + "session/confirm";
            try {
                JSONObject jo = new JSONObject();
                jo.put("user_id", GD.g_profile.id);
                jo.put("session_id", sessionModel.id);
                jo.put("review", reason.getText().toString());
                jo.put("type", 1);
                new AcceptApi().execute(url, jo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(popupMode.equals("Report")){
            String url = GD.BASE_URL + "session/confirm";
            try {
                JSONObject jo = new JSONObject();
                jo.put("user_id", GD.g_profile.id);
                jo.put("session_id", sessionModel.id);
                jo.put("report", reason.getText().toString());
                jo.put("type", 2);
                new AcceptApi().execute(url, jo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void processStauts(int status, int action){
        if(!(status == 2 || status == 3 || status == 4 || status == 9)) //canceled
        {
            rootView.findViewById(R.id.btn_next_term_of_use).setVisibility(View.GONE);
        }
    }

    public class AcceptApi extends AsyncTask<String, String, String> {
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
                    GD.g_selectedSession = GD.sessionFromJson(ret);
                    int status = ret.getInt("status");
                    int action = ret.getInt("action");
                    processStauts(status, action);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            if(popupMode.equals("Meet") || popupMode.equals("Report")){
                return;
            }
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            FragmentSessionMessage fragmentSessionMessage = new FragmentSessionMessage();
            transaction.addToBackStack("transaction");
            transaction.replace(R.id.lay_main, fragmentSessionMessage);
            transaction.commit();
            selectPopup.dismiss();
        }
    }

    public class DeclineApi extends AsyncTask<String, String, String> {
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
                    GD.g_selectedSession = GD.sessionFromJson(ret);
                    int status = ret.getInt("status");
                    int action = ret.getInt("action");
                    processStauts(status, action);
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
}
