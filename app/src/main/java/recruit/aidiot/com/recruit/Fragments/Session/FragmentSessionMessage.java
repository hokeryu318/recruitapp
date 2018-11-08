package recruit.aidiot.com.recruit.Fragments.Session;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shrikanthravi.chatview.data.Message;
import com.shrikanthravi.chatview.widget.ChatView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import recruit.aidiot.com.recruit.Fragments.FragmentParent;
import recruit.aidiot.com.recruit.Global.CF;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.MessageModel;
import recruit.aidiot.com.recruit.Models.SessionModel;
import recruit.aidiot.com.recruit.ProgressBar.ACProgressFlower;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/7/2018.
 */

public class FragmentSessionMessage extends FragmentParent {
    View rootView;
    private ACProgressFlower dialog;
    TextView btnAction;
    ChatView chatView;
    PopupWindow declinePopup;

    final Handler handler = new Handler();

    ArrayList<MessageModel> messageModels = new ArrayList<>();
    ArrayList<MessageModel> messageNews;
    public int next_id = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message_session, container, false);
        dialog = new ACProgressFlower.Builder(getActivity())
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Sending...")
                .build();

        chatView = (ChatView) rootView.findViewById(R.id.chatView);
        chatView.setOnClickSendButtonListener(onSend);

        btnAction = rootView.findViewById(R.id.iv_settings);

        switch (GD.g_selectedSession.status){
            case 2:
                btnAction.setOnClickListener(onAccept);
                btnAction.setText("確定");
                break;
            case 3:
            case 4:
                btnAction.setOnClickListener(onDecline);
                btnAction.setText("キャンセル");
                break;
        }
        runTimer();
        return rootView;
    }

    public void runTimer(){
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                loadNewMessage();
            }
        }, 2000);
    }

    public void loadNewMessage(){
        int next_id = 0;
        if(messageModels.size() > 0){
            next_id = messageModels.get(messageModels.size() - 1).id;
        }
        String url = GD.BASE_URL + "message?session_id=" + GD.g_selectedSession.id + "&next_id=" + next_id + "&user_id" + GD.g_profile.id;
        new RefreshSessionApi().execute(url);
    }

    ChatView.OnClickSendButtonListener onSend = new ChatView.OnClickSendButtonListener() {
        @Override
        public void onSendButtonClick(String s) {
            sendMessage(s);
        }
    };

    View.OnClickListener onAccept = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            acceptAction();
        }
    };

    View.OnClickListener onDecline = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            declineAction();
        }
    };

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

//    public void declineAction(){
//        SessionModel sessionModel = GD.g_selectedSession;
//        String url = GD.BASE_URL + "session/cancel";
//        try {
//            JSONObject jo = new JSONObject();
//            jo.put("user_id", GD.g_profile.id);
//            jo.put("session_id", sessionModel.id);
//            jo.put("reason", "");
//            new DeclineApi().execute(url, jo.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
    public void declineAction(){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_comment_decline, (ViewGroup)rootView.findViewById(R.id.layout_root));
        EditText reason = layout.findViewById(R.id.ed_decline);
        //Get ScreenSize
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        declinePopup = new PopupWindow(layout, width, height, true);
        declinePopup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        declinePopup.setFocusable(true);

        layout.findViewById(R.id.btn_decline_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = GD.BASE_URL + "session/cancel";
                try {
                    JSONObject jo = new JSONObject();
                    jo.put("user_id", GD.g_profile.id);
                    jo.put("session_id", GD.g_selectedSession.id);
                    jo.put("reason", reason.getText().toString());
                    new DeclineApi().execute(url, jo.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMessage(String msg){
        try {
            String url = GD.BASE_URL + "message/add";
            JSONObject jo = new JSONObject();
            jo.put("session_id", GD.g_selectedSession.id);
            jo.put("message", msg);
            new SendMessageApi().execute(url, jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addMessage(MessageModel messageModel){
        if(messageModel.from_to == 1){
            Message message = new Message();
            message.setType(Message.LeftSimpleMessage);
            message.setBody(messageModel.message);
            message.setUserName(GD.g_selectedSession.name);
            chatView.addMessage(message);
        } else if(messageModel.from_to == 2){
            Message message = new Message();
            message.setType(Message.RightSimpleMessage);
            message.setBody(messageModel.message);
            message.setUserName(GD.g_profile.name);
            chatView.addMessage(message);
        }
    }

    public void processStatus(int status, int action){
        switch (status){
            case 3:
            case 4:
                btnAction.setText("キャンセル");
                btnAction.setOnClickListener(onDecline);
                break;
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            processStatus(GD.g_selectedSession.status, GD.g_selectedSession.action);
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            processStatus(GD.g_selectedSession.status, GD.g_selectedSession.action);
        }
    }

    public class SendMessageApi extends AsyncTask<String, String, String> {
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
                    processStatus(status, action);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            processStatus(GD.g_selectedSession.status, GD.g_selectedSession.action);
        }
    }

    public class RefreshSessionApi extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
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
                    JSONObject session = data.getJSONObject("session");
                    GD.g_selectedSession = GD.sessionFromJson(session);
                    messageNews = new ArrayList<>();
                    for(int i = 0; i < ret.length(); i++){
                        MessageModel messageModel = new MessageModel();
                        JSONObject jtem = ret.getJSONObject(i);
                        messageModel.id = jtem.getInt("id");
                        messageModel.session_id = jtem.getInt("session_id");
                        messageModel.from_to = jtem.getInt("from_to");
                        messageModel.message = jtem.getString("message");
                        messageModel.status = jtem.getInt("status");
                        messageModel.user_image = jtem.getString("user_image");
                        messageModels.add(messageModel);
                        messageNews.add(messageModel);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result) {
//            dialog.dismiss();
            super.onPostExecute(result);
            for(int i = 0; i < messageNews.size(); i++){
                addMessage(messageNews.get(i));
            }
            runTimer();
            processStatus(GD.g_selectedSession.status, GD.g_selectedSession.action);
        }
    }
}
