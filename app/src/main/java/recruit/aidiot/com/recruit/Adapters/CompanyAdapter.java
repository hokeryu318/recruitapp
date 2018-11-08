package recruit.aidiot.com.recruit.Adapters;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import recruit.aidiot.com.recruit.Fragments.Company.FragmentCompanyDetail;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.CompanyModel;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/7/2018.
 */

public class CompanyAdapter extends ArrayAdapter<CompanyModel> implements View.OnClickListener, View.OnFocusChangeListener{

    private ArrayList<CompanyModel> dataSet;
    Activity mContext;

    private static class ViewHolder{
        ImageView imgProfile;
        TextView txtTitle;
        TextView txtCategory;
        TextView txtStation;
        TextView txtUrl;
    }

    public CompanyAdapter(ArrayList<CompanyModel> data, Activity context){
        super(context, R.layout.listitem_company, data);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CompanyModel companyModel = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listitem_company, parent, false);
            viewHolder.imgProfile = (ImageView) convertView.findViewById(R.id.iv_company_image);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.tv_company_title);
            viewHolder.txtCategory = (TextView) convertView.findViewById(R.id.tv_category);
            viewHolder.txtStation = (TextView) convertView.findViewById(R.id.tv_train_station);
            viewHolder.txtUrl = (TextView) convertView.findViewById(R.id.tv_company_url);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Picasso.with(mContext).load(companyModel.profile_image).into(viewHolder.imgProfile);
        viewHolder.txtTitle.setText(companyModel.name);
//        viewHolder.txtCategory.setText(companyModel.);
        viewHolder.txtStation.setText(companyModel.near_station);
        viewHolder.txtUrl.setText(companyModel.site);

        View item = convertView.findViewById(R.id.rel_item);
        item.setOnClickListener(this);
        item.setTag(position);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rel_item:
                GD.g_selectedCompany = (Integer) v.getTag();
                FragmentTransaction transaction = mContext.getFragmentManager().beginTransaction();
                FragmentCompanyDetail fragmentCompanyDetail = new FragmentCompanyDetail();
                transaction.addToBackStack("transaction");
                transaction.replace(R.id.lay_main, fragmentCompanyDetail);
                transaction.commit();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}
