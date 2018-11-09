package recruit.aidiot.com.recruit.Adapters;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import recruit.aidiot.com.recruit.Fragments.Session.FragmentSessionMessage;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.AdminMsgModel;
import recruit.aidiot.com.recruit.Models.CompanyMsgModel;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/9/2018.
 */

public class NotifyAdminAdapter extends ArrayAdapter<Object> implements View.OnClickListener {

    private ArrayList<Object> dataSet;
    Activity mContext;

    private static class ViewHolder{
        TextView txtMessage;
    }

    public NotifyAdminAdapter(ArrayList<Object> data, Activity context){
        super(context, R.layout.listitem_notification_on_profile, data);
        mContext = context;
        dataSet = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Object msgModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listitem_notification_on_profile, parent, false);
            viewHolder.txtMessage = convertView.findViewById(R.id.tv_message);
            result = convertView;
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        if(msgModel instanceof AdminMsgModel){
            viewHolder.txtMessage.setText(((AdminMsgModel)msgModel).message);
        } else if(msgModel instanceof CompanyMsgModel){
            viewHolder.txtMessage.setText(((CompanyMsgModel)msgModel).message);
        }

        View item = convertView.findViewById(R.id.rel_item);
        item.setBackgroundColor(mContext.getResources().getColor(R.color.notification_list_2, null));
        item.setOnClickListener(this);
        item.setTag(position);

        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rel_item:
                int messageId = (Integer) v.getTag();
                Object msgModel = getItem(messageId);
                if(msgModel instanceof CompanyMsgModel) {
                    GD.g_selectedSession = ((CompanyMsgModel)msgModel).session;
                    FragmentTransaction transaction = mContext.getFragmentManager().beginTransaction();
                    FragmentSessionMessage fragmentSessionMessage = new FragmentSessionMessage();
                    transaction.addToBackStack("transaction");
                    transaction.replace(R.id.lay_main, fragmentSessionMessage);
                    transaction.commit();
                }
                break;
        }
    }
}
