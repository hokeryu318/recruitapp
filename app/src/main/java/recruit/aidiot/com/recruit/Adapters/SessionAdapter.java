package recruit.aidiot.com.recruit.Adapters;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import recruit.aidiot.com.recruit.Fragments.Session.FragmentSessionDetail;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.SessionModel;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/7/2018.
 */

public class SessionAdapter extends ArrayAdapter<SessionModel> implements View.OnClickListener {

    private ArrayList<SessionModel> dataSet;
    Activity mContext;

    private static class ViewHolder{
        ImageView imgProfile;
        TextView txtTime;
        TextView txtTitle;
        TextView txtCategory;
        TextView txtStation;
        TextView txtUrl;
    }

    public SessionAdapter(ArrayList<SessionModel> data, Activity context){
        super(context, R.layout.listitem_sessions, data);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SessionModel sessionModel = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listitem_sessions, parent, false);
            viewHolder.imgProfile = (ImageView) convertView.findViewById(R.id.iv_session_image);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.tv_session_title);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.tv_session_date);
            viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.tv_category);
            viewHolder.txtStation = (TextView) convertView.findViewById(R.id.tv_train_station);
            viewHolder.txtUrl = (TextView) convertView.findViewById(R.id.tv_session_url);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Picasso.with(mContext).load(sessionModel.profile_image).into(viewHolder.imgProfile);
        viewHolder.txtTime.setText(sessionModel.schedule_string);
        viewHolder.txtTitle.setText(sessionModel.name);
//        viewHolder.txtCategory.setText(companyModel.);
        viewHolder.txtStation.setText(sessionModel.near_station);
        viewHolder.txtUrl.setText(sessionModel.site);

        View item = convertView.findViewById(R.id.rel_item);
        item.setOnClickListener(this);
        item.setTag(position);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rel_item:
                int sessionId = (Integer) v.getTag();
                GD.g_selectedSession = GD.g_sessionList.get(sessionId);
                FragmentTransaction transaction = mContext.getFragmentManager().beginTransaction();
                FragmentSessionDetail fragmentSession = new FragmentSessionDetail();
                transaction.addToBackStack("transaction");
                transaction.replace(R.id.lay_main, fragmentSession);
                transaction.commit();
                break;
        }
    }
}
