package recruit.aidiot.com.recruit.Fragments.Session;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shrikanthravi.chatview.data.Message;
import com.shrikanthravi.chatview.widget.ChatView;

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

public class FragmentSessionMessage extends FragmentParent implements View.OnClickListener {
    View rootView;
    private ACProgressFlower dialog;
    TextView btnAction;
    ChatView chatView;

    final Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message_session, container, false);
        dialog = new ACProgressFlower.Builder(getActivity())
                .themeColor(getResources().getColor(R.color.colorLinebase))
                .text("Refreshing...")
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

        loadPreviousMessage();

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
//        if(GD.g_selectedSession.messages.size() > 0){
//            next_id = GD.g_selectedSession.messages.get(GD.g_selectedSession.messages.size() - 1).id;
//        }
        String url = GD.BASE_URL + "message?session_id" + GD.g_selectedSession.id + "&next_id=" + next_id + "&user_id" + GD.g_profile.id;
        new RefreshSessionApi().execute(url);
    }

    @Override
    public void onClick(View v) {

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

    public void declineAction(){
        SessionModel sessionModel = GD.g_selectedSession;
        String url = GD.BASE_URL + "session/cancel";
        try {
            JSONObject jo = new JSONObject();
            jo.put("user_id", GD.g_profile.id);
            jo.put("session_id", sessionModel.id);
            jo.put("reason", "");
            new DeclineApi().execute(url, jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void loadPreviousMessage(){
//        ArrayList<MessageModel> messageModels = GD.g_selectedSession.messages;
//        for(int i = 0; i < messageModels.size(); i++){
//            MessageModel messageModel = messageModels.get(i);
//            addMessage(messageModel);
//        }
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
        }
    }

    public class RefreshSessionApi extends AsyncTask<String, String, String> {
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
