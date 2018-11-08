package recruit.aidiot.com.recruit.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.Constants;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.TimeModel;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener{
    private ACProgressFlower dialog;
    EditText mEmail, mPassword;
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Log in...")
                .build();

        SharedPreferences pref = getSharedPreferences(GD.PREF, MODE_PRIVATE);
        String login_type = pref.getString("login_type", "");
        if(login_type.equals("line")){
            String line_id = pref.getString("line_id", "");
            String line_token = pref.getString("line_token", "");
            String name = pref.getString("name", "");
            String profile_img = pref.getString("profile_img", "");
            linelogin(line_id, line_token, name, profile_img);
        } else if(login_type.equals("normal")){
            String email = pref.getString("email", "");
            String password = pref.getString("password", "");
            login(email, password);
        }

        setContentView(R.layout.activity_login);

        findViewById(R.id.tv_login).setOnClickListener(this);
        findViewById(R.id.frmLine).setOnClickListener(this);

        mEmail = findViewById(R.id.edt_email);
        mPassword = findViewById(R.id.edt_password);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CODE) {
            Log.e("ERROR", "Unsupported Request");
            return;
        }
        LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
        switch (result.getResponseCode()) {

            case SUCCESS:
                // Login successful

                String accessToken = result.getLineCredential().getAccessToken().getAccessToken();
                LineProfile profile = result.getLineProfile();
                LineCredential credential = result.getLineCredential();
                linelogin(profile.getUserId(), accessToken, profile.getDisplayName(), profile.getPictureUrl().toString());
                break;

            case CANCEL:
                // Login canceled by user
                Log.e("ERROR", "LINE Login Canceled by user!!");
                break;

            default:
                // Login canceled due to other error
                Log.e("ERROR", "Login FAILED!");
                Log.e("ERROR", result.getErrorData().toString());
        }
    }

    private boolean linelogin(String line_id, String line_token, String name, String profile_img){
        try{
            JSONObject jo = new JSONObject();
            jo.put("type", "line");
            jo.put("line_id", line_id);
            jo.put("line_token", line_token);
            jo.put("name", name);
            jo.put("profile_img", profile_img);

            String url = GD.BASE_URL + "login";
            new LineLogInApi().execute(url, jo.toString());
        } catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean login(String email, String password){
        try {
            JSONObject jo = new JSONObject();
            jo.put("type", "email");
            jo.put("email", email);
            jo.put("password", password);

            String url = GD.BASE_URL + "login";
            new LogInApi().execute(url, jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_login:
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(email.isEmpty()){
                    CF.ToastShow(this, "Please input your email");
                    return;
                }
                if(password.isEmpty()){
                    CF.ToastShow(this, "Please input your password");
                    return;
                }
                login(email, password);
                break;
            case R.id.frmLine:
                Intent loginIntent = LineLoginApi.getLoginIntent(v.getContext(), Constants.CHANNEL_ID);
                startActivityForResult(loginIntent, REQUEST_CODE);
                break;
        }
    }

    public class LogInApi extends AsyncTask<String, String, String> {
        String email, password;
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }
        protected String doInBackground(String... param) {
            String url = param[0];
            String data = param[1];
            try {
                JSONObject joParam = new JSONObject(data);
                email = joParam.getString("email");
                password = joParam.getString("password");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String response = CF.HttpPostRequest(url, data, "POST");
            publishProgress(response);
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            String response = progress[0];
            try {
                JSONObject joresult = new JSONObject(response);
                boolean success = joresult.getBoolean("success");
                if(success){
                    JSONObject data = joresult.getJSONObject("data");
                    GD.g_token = data.getString("token");
                    JSONObject ret = data.getJSONObject("ret");
                    //fill profile
                    GD.g_profile.id = ret.getInt("id");
                    GD.g_profile.name = ret.getString("name");
                    GD.g_profile.nickname = ret.getString("nickname");
                    GD.g_profile.tel = ret.getString("tel");
                    GD.g_profile.email = ret.getString("email");
                    GD.g_profile.line_token = ret.getString("line_token");
                    GD.g_profile.profile_img = ret.getString("profile_img");
                    GD.g_profile.univ = ret.getString("univ");
                    GD.g_profile.hobby = ret.getString("hobby");
                    GD.g_profile.graduate_year = ret.getInt("graduate_year");
                    GD.g_profile.gender = ret.getInt("gender");
                    GD.g_profile.major_id = ret.getInt("major_id");
                    GD.g_profile.gakubu = ret.getString("gakubu");
                    GD.g_profile.area = ret.getString("area");
                    GD.g_profile.job_hunting = ret.getInt("job_hunting");
                    GD.g_profile.intern = ret.getInt("intern");
                    GD.g_profile.valid_flag = ret.getInt("valid_flag");
                    GD.g_profile.exam_status = ret.getString("exam_status");
                    GD.g_profile.decline_comment = ret.getString("decline_comment");
                    GD.g_profile.auth01 = ret.getString("auth01");
                    GD.g_profile.auth02 = ret.getString("auth02");
                    GD.g_profile.count = ret.getInt("count");
                    GD.g_profile.gender_name = ret.getString("gender_name");
                    GD.g_profile.favorite = ret.getInt("favorite");
                    GD.g_profile.user_image = ret.getString("user_image");
                    GD.g_profile.graduate_at = ret.getString("graduate_at");

                    JSONArray tag_names = ret.getJSONArray("tag_names");
                    GD.g_profile.tag_names = new String[tag_names.length()];

                    JSONArray schedules = ret.getJSONArray("schedules");
                    GD.g_profile.timeModels = new ArrayList<>();
                    for(int i = 0; i < schedules.length(); i++){
                        JSONObject item = schedules.getJSONObject(i);
                        TimeModel timeModel = new TimeModel();
                        timeModel.id = item.getInt("id");
                        timeModel.due_at = item.getString("due_at");
                        timeModel.end_at = item.getString("end_at");
                        timeModel.due_at_date = item.getString("due_at_date");
                        timeModel.due_at_weekday = item.getString("due_at_weekday");
                        timeModel.due_at_time = item.getString("due_at_time");
                        timeModel.end_at_time = item.getString("end_at_time");
                        timeModel.datetimestring = item.getString("datetimestring");
                        timeModel.due_at_full_date = item.getString("due_at_full_date");
                        GD.g_profile.timeModels.add(timeModel);
                    }

                    SharedPreferences pref = getSharedPreferences(GD.PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("login_type", "normal");
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.commit();

                    for(int i = 0; i < tag_names.length(); i++){
                        GD.g_profile.tag_names[i] = tag_names.getString(i);
                    }

                    Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
                String message = joresult.getString("message");
                CF.ToastShow(LoginActivity.this, message);
            } catch (JSONException e) {
                CF.ToastShow(LoginActivity.this, "Data parsing error!");
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    public class LineLogInApi extends AsyncTask<String, String, String> {
        String line_id, line_token, name, profile_img;
        @Override
        protected void onPreExecute() {
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            super.onPreExecute();
        }
        protected String doInBackground(String... param) {
            String url = param[0];
            String data = param[1];
            try {
                JSONObject joParam = new JSONObject(data);
                line_id = joParam.getString("line_id");
                line_token = joParam.getString("line_token");
                name = joParam.getString("name");
                profile_img = joParam.getString("profile_img");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String response = CF.HttpPostRequest(url, data, "POST");
            publishProgress(response);
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            String response = progress[0];
            try {
                JSONObject joresult = new JSONObject(response);
                boolean success = joresult.getBoolean("success");
                if(success){
                    JSONObject data = joresult.getJSONObject("data");
                    GD.g_token = data.getString("token");
                    JSONObject ret = data.getJSONObject("ret");
                    //fill profile
                    GD.g_profile.id = ret.getInt("id");
                    GD.g_profile.name = ret.getString("name");
                    GD.g_profile.nickname = ret.getString("nickname");
                    GD.g_profile.tel = ret.getString("tel");
                    GD.g_profile.email = ret.getString("email");
                    GD.g_profile.line_token = ret.getString("line_token");
                    GD.g_profile.profile_img = ret.getString("profile_img");
                    GD.g_profile.univ = ret.getString("univ");
                    GD.g_profile.hobby = ret.getString("hobby");
                    GD.g_profile.graduate_year = ret.getInt("graduate_year");
                    GD.g_profile.gender = ret.getInt("gender");
                    GD.g_profile.major_id = ret.getInt("major_id");
                    GD.g_profile.gakubu = ret.getString("gakubu");
                    GD.g_profile.area = ret.getString("area");
                    GD.g_profile.job_hunting = ret.getInt("job_hunting");
                    GD.g_profile.intern = ret.getInt("intern");
                    GD.g_profile.valid_flag = ret.getInt("valid_flag");
                    GD.g_profile.exam_status = ret.getString("exam_status");
                    GD.g_profile.decline_comment = ret.getString("decline_comment");
                    GD.g_profile.auth01 = ret.getString("auth01");
                    GD.g_profile.auth02 = ret.getString("auth02");
                    GD.g_profile.count = ret.getInt("count");
                    GD.g_profile.gender_name = ret.getString("gender_name");
                    GD.g_profile.favorite = ret.getInt("favorite");
                    GD.g_profile.user_image = ret.getString("user_image");
                    GD.g_profile.graduate_at = ret.getString("graduate_at");

                    JSONArray tag_names = ret.getJSONArray("tag_names");
                    GD.g_profile.tag_names = new String[tag_names.length()];
                    for(int i = 0; i < tag_names.length(); i++){
                        GD.g_profile.tag_names[i] = tag_names.getString(i);
                    }

                    JSONArray schedules = ret.getJSONArray("schedules");
                    GD.g_profile.timeModels = new ArrayList<>();
                    for(int i = 0; i < schedules.length(); i++){
                        JSONObject item = schedules.getJSONObject(i);
                        TimeModel timeModel = new TimeModel();
                        timeModel.id = item.getInt("id");
                        timeModel.due_at = item.getString("due_at");
                        timeModel.end_at = item.getString("end_at");
                        timeModel.due_at_date = item.getString("due_at_date");
                        timeModel.due_at_weekday = item.getString("due_at_weekday");
                        timeModel.due_at_time = item.getString("due_at_time");
                        timeModel.end_at_time = item.getString("end_at_time");
                        timeModel.datetimestring = item.getString("datetimestring");
                        timeModel.due_at_full_date = item.getString("due_at_full_date");
                        GD.g_profile.timeModels.add(timeModel);
                    }

                    SharedPreferences pref = getSharedPreferences(GD.PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("login_Type", "Line");
                    editor.putString("line_id", line_id);
                    editor.putString("line_token", line_token);
                    editor.putString("name", name);
                    editor.putString("profile_img", profile_img);
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                }
                String message = joresult.getString("message");
                CF.ToastShow(LoginActivity.this, message);
            } catch (JSONException e) {
                CF.ToastShow(LoginActivity.this, "Data parsing error!");
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
