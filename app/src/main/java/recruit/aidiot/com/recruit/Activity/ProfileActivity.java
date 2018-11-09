package recruit.aidiot.com.recruit.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.cunoraz.tagview.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

public class ProfileActivity extends AppCompatActivity {
    private ACProgressFlower dialog;
    EditText ed_name, ed_initial, ed_school_name, ed_phone_number, ed_email_address, ed_password, ed_hobby;
    Spinner sp_graduate_year, sp_major, sp_gender, sp_job_hunting;
    TextView btn_add_tag;
    TagContainerLayout tagPersonal, tagInterest;
    PopupWindow popupTags;
    TextView txtAreas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        dialog = new ACProgressFlower.Builder(this)
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Log in...")
                .build();

        ed_name = findViewById(R.id.ed_name);
        ed_initial = findViewById(R.id.ed_initial);
        ed_school_name = findViewById(R.id.ed_school_name);
        ed_phone_number = findViewById(R.id.ed_phone_number);
        ed_email_address = findViewById(R.id.ed_email_address);
        ed_password = findViewById(R.id.ed_password);
        ed_hobby = findViewById(R.id.ed_hobby);

        sp_graduate_year = findViewById(R.id.sp_graduate_year);
        sp_major = findViewById(R.id.sp_major);
        sp_gender = findViewById(R.id.sp_gender);
//        sp_job_hunting = findViewById(R.id.sp_job_hunting);

        btn_add_tag = findViewById(R.id.btn_add_tag);
        btn_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupTag();
            }
        });

        loadProfile();

        tagPersonal = findViewById(R.id.tag_view_personal);
        tagPersonal.setBackgroundColor(getResources().getColor(R.color.White, null));
        tagInterest = findViewById(R.id.tag_view_interest);
        tagInterest.setBackgroundColor(getResources().getColor(R.color.White, null));

        txtAreas = findViewById(R.id.tag_view_area);
        txtAreas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupArea();
            }
        });

        TextView btn_next_term_of_use = findViewById(R.id.btn_next_term_of_use);
        btn_next_term_of_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    public void saveProfile(){
        String url = GD.BASE_URL + "profile";
        JSONObject jo = new JSONObject();
        try {
            jo.put("id", GD.g_profile.id);
            jo.put("name", ed_name.getText().toString());
            jo.put("nickname", ed_initial.getText().toString());
            jo.put("univ", ed_school_name.getText().toString());
            jo.put("email", ed_email_address.getText().toString());
            jo.put("password", ed_password.getText().toString());
            jo.put("hobby", ed_hobby.getText().toString());
            jo.put("tel", ed_phone_number.getText().toString());

            String str_graduate_year = (String)sp_graduate_year.getSelectedItem();
            jo.put("graduate_year", Integer.parseInt(graduate_years.get(str_graduate_year)));
            String str_gender = (String)sp_gender.getSelectedItem();
            jo.put("gender", Integer.parseInt(gender.get(str_gender)));
            String str_major = (String)sp_major.getSelectedItem();
            jo.put("major_id", Integer.parseInt(major.get(str_major)));

            JSONArray ja_tags = new JSONArray(tag_selected.toArray());
            jo.put("tags", ja_tags);
            JSONArray ja_areas = new JSONArray(area_selected.toArray());
            jo.put("areas", ja_areas);

            new ProfileSaveApi().execute(url, jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void popupArea(){
        PopupWindow popupArea;
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_profile_area, (ViewGroup)findViewById(R.id.layout_root));
        //Get ScreenSize
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        popupArea = new PopupWindow(layout, width , height, true);
        popupArea.showAtLocation(layout, Gravity.CENTER, 0, 0);
        popupArea.setFocusable(true);

        TagContainerLayout pTagArea = layout.findViewById(R.id.tag_group_area);
        pTagArea.setBackgroundColor(getResources().getColor(R.color.White, null));
        pTagArea.setTagTextColor(getResources().getColor(R.color.White, null));

        TextView tag_view_area = findViewById(R.id.tag_view_area);
        layout.findViewById(R.id.btn_set_tags).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                area_selected.clear();
                area_selected.addAll(area_selected_popup);
                //updateActivityArea
                tag_view_area.setText("");
                Iterator<String> iTag = areas.keySet().iterator();
                String area_result = "";
                while (iTag.hasNext()){
                    String sTag = iTag.next();
                    int iVal = Integer.parseInt(areas.get(sTag));
                    if(area_selected.contains(iVal)){
                        area_result += sTag + ",";
                    }
                }
                if(area_result.length() > 25){
                    area_result = area_result.substring(0, 25) + "...";
                }
                tag_view_area.setText(area_result);
                popupArea.dismiss();
            }
        });

        //updateAreaTagPopup
        area_selected_popup.clear();
        area_selected_popup.addAll(area_selected);
        Iterator<String> iTag = areas.keySet().iterator();
        while (iTag.hasNext()){
            String sTag = iTag.next();
            int iVal = Integer.parseInt(areas.get(sTag));
            if(area_selected_popup.contains(iVal)){
                pTagArea.addTag(sTag);
                TagView view = pTagArea.getTagView(pTagArea.getChildCount() - 1);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_pink, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_pink, null));
                view.setTagTextColor(getResources().getColor(R.color.White, null));
            } else {
                pTagArea.addTag(sTag);
                TagView view = pTagArea.getTagView(pTagArea.getChildCount() - 1);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_disable, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_disable, null));
                view.setTagTextColor(getResources().getColor(R.color.White, null));
            }
        }
        //onItemClick
        pTagArea.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                TagView view = pTagArea.getTagView(position);
                int id = Integer.parseInt(areas.get(view.getText()));
                if(area_selected_popup.contains(id)){
                    area_selected_popup.remove(id);
                    view.setTagBackgroundColor(getResources().getColor(R.color.tag_disable, null));
                    view.setTagBorderColor(getResources().getColor(R.color.tag_disable, null));
                } else {
                    area_selected_popup.add(id);
                    view.setTagBackgroundColor(getResources().getColor(R.color.tag_pink, null));
                    view.setTagBorderColor(getResources().getColor(R.color.tag_pink, null));
                }
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
    }

    TagContainerLayout pTagPersonal, pTagInterest;
    public void popupTag(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_profile_tag, (ViewGroup)findViewById(R.id.layout_root));
        //Get ScreenSize
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        popupTags = new PopupWindow(layout, width , height, true);
        popupTags.showAtLocation(layout, Gravity.CENTER, 0, 0);
        popupTags.setFocusable(true);

        pTagPersonal = layout.findViewById(R.id.tag_group_personal);
        pTagPersonal.setBackgroundColor(getResources().getColor(R.color.White, null));
        pTagPersonal.setTagTextColor(getResources().getColor(R.color.White, null));
        pTagInterest = layout.findViewById(R.id.tag_group_interest);
        pTagInterest.setBackgroundColor(getResources().getColor(R.color.White, null));
        pTagInterest.setTagTextColor(getResources().getColor(R.color.White, null));

        layout.findViewById(R.id.btn_set_tags).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag_selected.clear();
                tag_selected.addAll(tag_selected_popup);
                updateActivityTag();
                popupTags.dismiss();
            }
        });

        tag_selected_popup.clear();
        tag_selected_popup.addAll(tag_selected);

        updateTagPopup();

        pTagPersonal.setOnTagClickListener(onPopupPersonalTag);
        pTagInterest.setOnTagClickListener(onPopupInterestTag);
    }

    private void updateTagPopup(){
        Iterator<String> iTag = personal_tags.keySet().iterator();
        while (iTag.hasNext()){
            String sTag = iTag.next();
            int iVal = Integer.parseInt(personal_tags.get(sTag));
            if(tag_selected_popup.contains(iVal)){
                pTagPersonal.addTag(sTag);
                TagView view = pTagPersonal.getTagView(pTagPersonal.getChildCount() - 1);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_pink, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_pink, null));
                view.setTagTextColor(getResources().getColor(R.color.White, null));
            } else {
                pTagPersonal.addTag(sTag);
                TagView view = pTagPersonal.getTagView(pTagPersonal.getChildCount() - 1);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_disable, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_disable, null));
                view.setTagTextColor(getResources().getColor(R.color.White, null));
            }
        }
        iTag = interest_tags.keySet().iterator();
        while (iTag.hasNext()){
            String sTag = iTag.next();
            int iVal = Integer.parseInt(interest_tags.get(sTag));
            if(tag_selected_popup.contains(iVal)){
                pTagInterest.addTag(sTag);
                TagView view = pTagInterest.getTagView(pTagInterest.getChildCount() - 1);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_blue, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_blue, null));
                view.setTagTextColor(getResources().getColor(R.color.White, null));
            } else {
                pTagInterest.addTag(sTag);
                TagView view = pTagInterest.getTagView(pTagInterest.getChildCount() - 1);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_disable, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_disable, null));
                view.setTagTextColor(getResources().getColor(R.color.White, null));
            }
        }
    }

    TagView.OnTagClickListener onPopupPersonalTag = new TagView.OnTagClickListener() {
        @Override
        public void onTagClick(int position, String text) {
            TagView view = pTagPersonal.getTagView(position);
            int id = Integer.parseInt(personal_tags.get(view.getText()));
            if(tag_selected_popup.contains(id)){
                tag_selected_popup.remove(id);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_disable, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_disable, null));
            } else {
                tag_selected_popup.add(id);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_pink, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_pink, null));
            }
        }

        @Override
        public void onTagLongClick(int position, String text) {

        }

        @Override
        public void onTagCrossClick(int position) {

        }
    };

    TagView.OnTagClickListener onPopupInterestTag = new TagView.OnTagClickListener() {

        @Override
        public void onTagClick(int position, String text) {
            TagView view = pTagInterest.getTagView(position);
            int id = Integer.parseInt(interest_tags.get(view.getText()));
            if(tag_selected_popup.contains(id)){
                tag_selected_popup.remove(id);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_disable, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_disable, null));
            } else {
                tag_selected_popup.add(id);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_blue, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_blue, null));
            }
        }

        @Override
        public void onTagLongClick(int position, String text) {

        }

        @Override
        public void onTagCrossClick(int position) {

        }
    };

    HashSet<Integer> tag_selected = new HashSet<Integer>();
    HashSet<Integer> tag_selected_popup = new HashSet<Integer>();

    HashSet<Integer> area_selected = new HashSet<Integer>();
    HashSet<Integer> area_selected_popup = new HashSet<Integer>();

    public void loadProfile(){
        String url = GD.BASE_URL + "profile?user_id=" + GD.g_profile.id;
        new LoadProfileApi().execute(url);
    }

    public void updateUi(){
        ed_name.setText(GD.g_profile.name);
        ed_initial.setText(GD.g_profile.nickname);
        ed_school_name.setText(GD.g_profile.univ);
        ed_phone_number.setText(GD.g_profile.tel);
        ed_email_address.setText(GD.g_profile.email);
        ed_hobby.setText(GD.g_profile.hobby);

        ArrayList list_graudate = new ArrayList<String>();
        list_graudate.addAll(graduate_years.keySet());
        ArrayAdapter graduateAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, list_graudate);
        sp_graduate_year.setAdapter(graduateAdapter);

        int selection = 0;
        for(int i = 0; i < list_graudate.size(); i++){
            if(list_graudate.get(i).equals(graduate_key)){
                selection = i;
                break;
            }
        }
        sp_graduate_year.setSelection(selection);

        ArrayList list_gender = new ArrayList<String>();
        list_gender.addAll(gender.keySet());
        ArrayAdapter genderAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, list_gender);
        sp_gender.setAdapter(genderAdapter);

        selection = 0;
        for(int i = 0; i < list_gender.size(); i++){
            if(list_gender.get(i).equals(gender_key)){
                selection = i;
                break;
            }
        }
        sp_gender.setSelection(selection);

        ArrayList list_major = new ArrayList<String>();
        list_major.addAll(major.keySet());
        ArrayAdapter majorAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, list_major);
        sp_major.setAdapter(majorAdapter);

        selection = 0;
        for(int i = 0; i < list_major.size(); i++){
            if(list_major.get(i).equals(major_key)){
                selection = i;
                break;
            }
        }
        sp_major.setSelection(selection);

        updateActivityTag();
    }

    public void updateActivityTag(){
        tagPersonal.removeAllTags();
        tagInterest.removeAllTags();
        Iterator<String> iTag = personal_tags.keySet().iterator();
        while (iTag.hasNext()){
            String sTag = iTag.next();
            int iVal = Integer.parseInt(personal_tags.get(sTag));
            if(tag_selected.contains(iVal)){
                tagPersonal.addTag(sTag);
                TagView view = tagPersonal.getTagView(tagPersonal.getChildCount() - 1);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_pink, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_pink, null));
                view.setTagTextColor(getResources().getColor(R.color.White, null));
            }
        }

        iTag = interest_tags.keySet().iterator();
        while (iTag.hasNext()){
            String sTag = iTag.next();
            int iVal = Integer.parseInt(interest_tags.get(sTag));
            if(tag_selected.contains(iVal)){
                tagInterest.addTag(sTag);
                TagView view = tagInterest.getTagView(tagInterest.getChildCount() - 1);
                view.setTagBackgroundColor(getResources().getColor(R.color.tag_blue, null));
                view.setTagBorderColor(getResources().getColor(R.color.tag_blue, null));
                view.setTagTextColor(getResources().getColor(R.color.White, null));
            }
        }

        TextView tag_view_area = findViewById(R.id.tag_view_area);
        tag_view_area.setText("");
        iTag = areas.keySet().iterator();
        String area_result = "";
        while (iTag.hasNext()){
            String sTag = iTag.next();
            int iVal = Integer.parseInt(areas.get(sTag));
            if(area_selected.contains(iVal)){
                area_result += sTag + ",";
            }
        }
        tag_view_area.setText(area_result);
    }

    public class LoadProfileApi extends AsyncTask<String, String, String> {
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
                    JSONObject ret = data.getJSONObject("user");
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

                    JSONObject codes = data.getJSONObject("codes");
                    JSONObject jgraduate_years = codes.getJSONObject("graduate_years");
                    Iterator<String> keys = jgraduate_years.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        if(Integer.parseInt(key) == GD.g_profile.graduate_year){
                            graduate_key = jgraduate_years.getString(key);
                        }
                        graduate_years.put(jgraduate_years.getString(key), key);
                    }

                    JSONObject jgender = codes.getJSONObject("gender");
                    keys = jgender.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        if(Integer.parseInt(key) == GD.g_profile.gender){
                            gender_key = jgender.getString(key);
                        }
                        gender.put(jgender.getString(key), key);
                    }

                    JSONObject jmajor = codes.getJSONObject("major");
                    keys = jmajor.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        if(Integer.parseInt(key) == GD.g_profile.major_id){
                            major_key = jmajor.getString(key);
                        }
                        major.put(jmajor.getString(key), key);
                    }

                    JSONObject jjob_hunting = codes.getJSONObject("job_hunting");
                    keys = jjob_hunting.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        job_hunting.put(jjob_hunting.getString(key), key);
                    }

                    JSONObject jintern = codes.getJSONObject("intern");
                    keys = jintern.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        intern.put(jintern.getString(key), key);
                    }

                    JSONObject jinterest_tags = codes.getJSONObject("interest_tags");
                    keys = jinterest_tags.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        interest_tags.put(jinterest_tags.getString(key), key);
                    }

                    JSONObject jpersonal_tags = codes.getJSONObject("personal_tags");
                    keys = jpersonal_tags.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        personal_tags.put(jpersonal_tags.getString(key), key);
                    }

                    JSONObject jareas = codes.getJSONObject("areas");
                    keys = jareas.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        areas.put(jareas.getString(key), key);
                    }

                    JSONArray jTags = ret.getJSONArray("tags");
                    for (int i = 0; i < jTags.length(); i++){
                        tagsList.add(jTags.getInt(i));
                        tag_selected.add(jTags.getInt(i));
                    }

                    JSONArray jareasl = ret.getJSONArray("areas");
                    for (int i = 0; i < jareasl.length(); i++){
                        area_selected.add(jareasl.getInt(i));
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
            updateUi();
        }
    }

    public class ProfileSaveApi extends AsyncTask<String, String, String> {
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

    HashMap<String, String> gender = new HashMap<>();
    HashMap<String, String> graduate_years = new HashMap<>();
    HashMap<String, String> major = new HashMap<>();
    HashMap<String, String> job_hunting = new HashMap<>();
    HashMap<String, String> intern = new HashMap<>();
    HashMap<String, String> interest_tags = new HashMap<>();
    HashMap<String, String> personal_tags = new HashMap<>();
    HashMap<String, String> areas = new HashMap<>();

    ArrayList<Integer> tagsList = new ArrayList<>();
    String gender_key, graduate_key, major_key;
}
