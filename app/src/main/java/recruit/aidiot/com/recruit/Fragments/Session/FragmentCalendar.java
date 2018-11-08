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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import recruit.aidiot.com.recruit.Fragments.FragmentParent;
import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.SessionModel;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/7/2018.
 */

public class FragmentCalendar extends FragmentParent implements View.OnClickListener {
    View rootView;
    CalendarView calendarView;
    private ACProgressFlower dialog;
    PopupWindow selectPopup;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        dialog = new ACProgressFlower.Builder(getActivity())
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Loading...")
                .build();

        calendarView = (CalendarView) rootView.findViewById(R.id.calendarView);
        calendarView.setOnDayClickListener(onDay);

        loadDays();

        return rootView;
    }

    OnDayClickListener onDay = new OnDayClickListener() {
        @Override
        public void onDayClick(EventDay eventDay) {
            Calendar selectedCalendar = eventDay.getCalendar();
            Iterator<String> days = GD.g_calendarSession.keySet().iterator();
            while(days.hasNext()){
                try {
                    String day = days.next();
                    Date dt = dateFormater.parse(day);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dt);
                    if(calendar.equals(selectedCalendar)){
                        showSessionPopup(day);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onClick(View v) {

    }

    View.OnClickListener onSessionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GD.g_selectedSession = (SessionModel)v.getTag();
            FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            FragmentSessionDetail fragmentSession = new FragmentSessionDetail();
            transaction.addToBackStack("transaction");
            transaction.replace(R.id.lay_main, fragmentSession);
            transaction.commit();
            selectPopup.dismiss();
        }
    };

    public void showSessionPopup(String day){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_day_list, (ViewGroup)rootView.findViewById(R.id.layout_root));
        //Get ScreenSize
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        selectPopup = new PopupWindow(layout, width , height, true);
        selectPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        selectPopup.setFocusable(true);

        LinearLayout linSessions = layout.findViewById(R.id.lin_sessions);
        ArrayList<SessionModel> sessionModels = GD.g_calendarSession.get(day);
        for (int i = 0; i < sessionModels.size(); i++) {
            SessionModel sessionModel = sessionModels.get(i);
            FrameLayout txtSession = (FrameLayout) inflater.inflate(R.layout.session_preview_item, null);
            txtSession.setTag(sessionModel);
            txtSession.setOnClickListener(onSessionClick);
            ((TextView)txtSession.findViewById(R.id.txt_display)).setText(sessionModel.datetimestring);
            linSessions.addView(txtSession);
        }
    }

    private void loadDays(){
        GD.g_calendarSession.clear();
        String url = GD.BASE_URL + "session/calendar?user_id=" + GD.g_profile.id;
        new LoadDaysApi().execute(url);
    }
    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private void addEvents(){
        List<EventDay> events = new ArrayList<>();
        Iterator<String> days = GD.g_calendarSession.keySet().iterator();
        ArrayList<Calendar> selectedDates = new ArrayList<>();
        while(days.hasNext()){
            try {
                String day = days.next();
                Date dt = dateFormater.parse(day);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dt);
                events.add(new EventDay(calendar, R.drawable.green_rounded_button));
                selectedDates.add(calendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        calendarView.setEvents(events);
        calendarView.setSelectedDates(selectedDates);
    }

    public class LoadDaysApi extends AsyncTask<String, String, String> {
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
                    JSONObject ret = data.getJSONObject("ret");
                    Iterator<String> iter = ret.keys();
                    while(iter.hasNext()){
                        String day = iter.next();
                        JSONArray josessions = ret.getJSONArray(day);
                        ArrayList<SessionModel> sessionModels = new ArrayList<>();
                        for (int i = 0; i < josessions.length(); i++){
                            SessionModel model = GD.sessionFromJson(josessions.getJSONObject(i));
                            sessionModels.add(model);
                        }

                        GD.g_calendarSession.put(day, sessionModels);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            addEvents();
            super.onPostExecute(result);
        }
    }
}
