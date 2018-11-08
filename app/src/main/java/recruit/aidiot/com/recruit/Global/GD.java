package recruit.aidiot.com.recruit.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import recruit.aidiot.com.recruit.Models.CompanyModel;
import recruit.aidiot.com.recruit.Models.MessageModel;
import recruit.aidiot.com.recruit.Models.ProfileModel;
import recruit.aidiot.com.recruit.Models.SessionModel;
import recruit.aidiot.com.recruit.Models.TimeModel;

public class GD {
    public static String BASE_URL = "http://session.aidiot.xyz/api/";

    public static String PREF = "RECRUIT";

    public static String g_token;

    public static ProfileModel g_profile = new ProfileModel();

    public static ArrayList<CompanyModel> g_companyList = new ArrayList<>();
    public static int g_selectedCompany;


    public static ArrayList<SessionModel> g_sessionList = new ArrayList<>();
    public static SessionModel g_selectedSession;

    public static int g_selectedSchedule;

    public static ArrayList<TimeModel> g_timeList = new ArrayList<>();
    public static int g_selectedTime;

    public static HashMap<String, ArrayList<SessionModel>> g_calendarSession = new HashMap<>();

    public static SessionModel sessionFromJson(JSONObject item){
        SessionModel sessionModel = new SessionModel();
        try {
            sessionModel.id = item.getInt("id");
            sessionModel.company_id = item.getInt("company_id");
            sessionModel.user_id = item.getInt("user_id");
            sessionModel.schedule_id = item.getInt("schedule_id");
            sessionModel.type = item.getInt("type");
            sessionModel.status = item.getInt("status");
            sessionModel.action = item.getInt("action");
            sessionModel.reason = item.getString("reason");
            sessionModel.review = item.getString("review");
            sessionModel.report = item.getString("report");
            sessionModel.place = item.getString("place");
            sessionModel.content = item.getString("content");
            sessionModel.name = item.getString("name");
            sessionModel.near_station = item.getString("near_station");
            sessionModel.site = item.getString("site");
            sessionModel.profile_image = item.getString("profile_image");
            sessionModel.description = item.getString("description");
            sessionModel.status_message = item.getString("status_message");
            sessionModel.status_detail_message = item.getString("status_detail_message");
//            sessionModel.messages = new ArrayList<>();
            sessionModel.schedule_string = item.getString("schedule_string");
            sessionModel.due_at_date = item.getString("due_at_date");
            sessionModel.due_at_full_date = item.getString("due_at_full_date");
            sessionModel.due_at_weekday = item.getString("due_at_weekday");
            sessionModel.due_at_time = item.getString("due_at_time");
            sessionModel.end_at_time = item.getString("end_at_time");
            sessionModel.datetimestring = item.getString("datetimestring");
//            JSONArray jomsg = item.getJSONArray("messages");
//            for (int j = 0; j < jomsg.length(); j++) {
//                MessageModel messageModel = new MessageModel();
//                JSONObject jtem = jomsg.getJSONObject(j);
//                messageModel.id = jtem.getInt("id");
//                messageModel.session_id = jtem.getInt("session_id");
//                messageModel.from_to = jtem.getInt("from_to");
//                messageModel.message = jtem.getString("message");
//                messageModel.status = jtem.getInt("status");
//                messageModel.user_image = jtem.getString("user_image");
//                sessionModel.messages.add(messageModel);
//            }
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return sessionModel;
    }
}

