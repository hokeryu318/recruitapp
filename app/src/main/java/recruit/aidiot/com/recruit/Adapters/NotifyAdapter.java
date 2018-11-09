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

import recruit.aidiot.com.recruit.Fragments.Company.FragmentCompanyDetail;
import recruit.aidiot.com.recruit.Fragments.Session.FragmentSessionMessage;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.CompanyMsgModel;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/9/2018.
 */

public class NotifyAdapter extends ArrayAdapter<CompanyMsgModel> implements View.OnClickListener {

    private ArrayList<CompanyMsgModel> dataSet;
    Activity mContext;

    private static class ViewHolder{
        TextView txtMessage;
    }

    public NotifyAdapter(ArrayList<CompanyMsgModel> data, Activity context){
        super(context, R.layout.listitem_notification_on_profile, data);
        mContext = context;
        dataSet = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CompanyMsgModel companyMsgModel = getItem(position);
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

        viewHolder.txtMessage.setText(companyMsgModel.message);

        View item = convertView.findViewById(R.id.rel_item);
        item.setOnClickListener(this);
        item.setTag(position);

        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rel_item:
                int messageId = (Integer) v.getTag();
                CompanyMsgModel companyMsgModel = getItem(messageId);
                GD.g_selectedSession = companyMsgModel.session;
                FragmentTransaction transaction = mContext.getFragmentManager().beginTransaction();
                FragmentSessionMessage fragmentSessionMessage = new FragmentSessionMessage();
                transaction.addToBackStack("transaction");
                transaction.replace(R.id.lay_main, fragmentSessionMessage);
                transaction.commit();
                break;
        }
    }
}
