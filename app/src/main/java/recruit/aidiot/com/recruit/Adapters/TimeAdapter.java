package recruit.aidiot.com.recruit.Adapters;

import android.app.*;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import recruit.aidiot.com.recruit.Fragments.FragmentTime;
import recruit.aidiot.com.recruit.Fragments.Session.FragmentSession;
import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.MessageModel;
import recruit.aidiot.com.recruit.Models.SessionModel;
import recruit.aidiot.com.recruit.Models.TimeModel;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/7/2018.
 */

public class TimeAdapter extends ArrayAdapter<TimeModel> implements View.OnClickListener,TimePickerDialog.OnTimeSetListener, View.OnLongClickListener {
    private ArrayList<TimeModel> dataSet;
    Activity mContext;
    String selectedDate = "", fromTime = "", toTime = "";
    private ACProgressFlower dialog;
    FragmentTime parent;

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.rel_item:
                int selectedTime = (Integer) v.getTag();
                if(GD.g_selectedTime == dataSet.size() - 1){ //long click on add

                } else { //long click existing time
//                    TimeModel timeModel = getItem(selectedTime);
//                    postTime(GD.g_profile.id, selectedDate + " " + fromTime, selectedDate + " " + toTime, timeModel.id, "remove");
                }
                break;
        }
        return true;
    }

    private static class ViewHolder{
        TextView txtDay;
        TextView txtFrom;
        TextView txtTo;
    }

    public TimeAdapter(ArrayList<TimeModel> data, FragmentTime context){
        super(context.getActivity(), R.layout.listitem_schedule, data);
        dataSet = data;
        mContext = context.getActivity();
        parent = context;

        dialog = new ACProgressFlower.Builder(mContext)
                .themeColor(mContext.getResources().getColor(R.color.colorLinebase))
                .text("Loading...")
                .build();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TimeModel timeModel = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.listitem_schedule, parent, false);
            viewHolder.txtDay = (TextView) convertView.findViewById(R.id.tv_schedule_month);
            viewHolder.txtFrom = (TextView) convertView.findViewById(R.id.tv_schedule_date_from);
            viewHolder.txtTo = (TextView) convertView.findViewById(R.id.tv_schedule_date_to);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        String topText = timeModel.due_at_date;
        if(!timeModel.due_at_weekday.isEmpty()){
            topText += "(" + timeModel.due_at_weekday + ")";
        }
        viewHolder.txtDay.setText(topText);
        viewHolder.txtFrom.setText(timeModel.due_at_time);
        viewHolder.txtTo.setText(timeModel.end_at_time);

        View item = convertView.findViewById(R.id.rel_item);
        if(position == dataSet.size() - 1){
            item.setBackgroundResource(R.mipmap.plus_timeline);
            convertView.findViewById(R.id.tv_schedule_symbol).setVisibility(View.GONE);
        }
        item.setOnClickListener(this);
        item.setOnLongClickListener(this);
        item.setTag(position);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rel_item:
                GD.g_selectedTime = (Integer) v.getTag();
                if(GD.g_selectedTime == dataSet.size() - 1){ //click on add
                    PopupDateDialog("");
                } else { //click existing time
                    GD.g_selectedSchedule = getItem(GD.g_selectedTime).id;
                    loadSession();
                }
                break;
        }
    }

    private void PopupDateDialog(String strDate){
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Calendar cd = Calendar.getInstance();
            if(!strDate.isEmpty()) {
                Date inputTime = dateFormater.parse(strDate);
                cd.setTime(inputTime);
            }
            new DatePickerDialog(mContext, dateSetListener, cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), cd.get(Calendar.DAY_OF_MONTH)).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar cd = Calendar.getInstance();
            cd.set(Calendar.YEAR, year);
            cd.set(Calendar.MONTH, month);
            cd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            selectedDate = dateFormater.format(cd.getTime());
            TimePickerDialog tpd = TimePickerDialog.newInstance(TimeAdapter.this, cd.get(Calendar.HOUR_OF_DAY), 0, true);
            tpd.show(mContext.getFragmentManager(), "TimePickerDialog");
        }
    };

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss", Locale.US);
        Calendar cd = Calendar.getInstance();
        cd.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cd.set(Calendar.MINUTE, minute);
        fromTime = timeFormatter.format(cd.getTime());

        cd.set(Calendar.HOUR_OF_DAY, hourOfDayEnd);
        cd.set(Calendar.MINUTE, minuteEnd);
        toTime = timeFormatter.format(cd.getTime());

        postTime(GD.g_profile.id, selectedDate + " " + fromTime, selectedDate + " " + toTime, 0, "");
    }

    public void postTime(int user_id, String due_at, String end_at, int id, String type){
        JSONObject jo = new JSONObject();
        try {
            jo.put("user_id", user_id);
            jo.put("due_at", due_at);
            jo.put("end_at", end_at);
            jo.put("id", 0);
            if(!type.isEmpty()){
                jo.put("type", type);
            }

            String url = GD.BASE_URL + "user/times";
            new SaveTimeApi().execute(url, jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class SaveTimeApi extends AsyncTask<String, String, String> {
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
                    parent.loadTimes();
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

    public void loadSession(){
        GD.g_sessionList.clear();
        String url = GD.BASE_URL + "session/list?schedule_id=" + GD.g_selectedSchedule + "&user_id=" + GD.g_profile.id + "&start=0&limit=10&is_request=1";
//        String url = GD.BASE_URL + "session/list?schedule_id=56&user_id=705&start=0&limit=10";
        new GetSessionsApi().execute(url, "");
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
                        SessionModel sessionModel = new SessionModel();
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
//                        sessionModel.messages = new ArrayList<>();
//                        JSONArray jomsg = item.getJSONArray("messages");
//                        for(int j = 0; j < jomsg.length(); j++){
//                            MessageModel messageModel = new MessageModel();
//                            JSONObject jtem = jomsg.getJSONObject(j);
//                            messageModel.id = jtem.getInt("id");
//                            messageModel.session_id = jtem.getInt("session_id");
//                            messageModel.from_to = jtem.getInt("from_to");
//                            messageModel.message = jtem.getString("message");
//                            messageModel.status = jtem.getInt("status");
//                            messageModel.user_image = jtem.getString("user_image");
//                            sessionModel.messages.add(messageModel);
//                        }
                        sessionModel.schedule_string = item.getString("schedule_string");
                        sessionModel.due_at_date = item.getString("due_at_date");
                        sessionModel.due_at_full_date = item.getString("due_at_full_date");
                        sessionModel.due_at_weekday = item.getString("due_at_weekday");
                        sessionModel.due_at_time = item.getString("due_at_time");
                        sessionModel.end_at_time = item.getString("end_at_time");
                        sessionModel.datetimestring = item.getString("datetimestring");
                        GD.g_sessionList.add(sessionModel);
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
            if(GD.g_sessionList.size() == 0){
                CF.ToastShow(mContext, "There is no session belongs to this time");
                return;
            }
            FragmentTransaction transaction = mContext.getFragmentManager().beginTransaction();
            FragmentSession fragmentSession = new FragmentSession();
            transaction.addToBackStack("transaction");
            transaction.replace(R.id.lay_main, fragmentSession);
            transaction.commit();
        }
    }
}
