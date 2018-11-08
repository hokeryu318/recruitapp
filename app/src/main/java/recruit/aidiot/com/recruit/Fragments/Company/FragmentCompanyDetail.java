package recruit.aidiot.com.recruit.Fragments.Company;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import recruit.aidiot.com.recruit.Fragments.FragmentParent;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.Models.CompanyModel;
import recruit.aidiot.com.recruit.R;

/**
 * Created by E on 11/7/2018.
 */

public class FragmentCompanyDetail extends FragmentParent implements View.OnClickListener  {
    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_company_details, container, false);

        CompanyModel companyModel = GD.g_companyList.get(GD.g_selectedCompany);

        ImageView imgProfile = (ImageView)rootView.findViewById(R.id.iv_company_image);
        Picasso.with(getActivity()).load(companyModel.profile_image).into(imgProfile);
        TextView txtName = (TextView)rootView.findViewById(R.id.tv_company_name);
        txtName.setText(companyModel.name);
        TextView txtStation = (TextView)rootView.findViewById(R.id.tv_company_train_station);
        txtStation.setText(companyModel.near_station);
        TextView txtLocation = (TextView)rootView.findViewById(R.id.tv_company_location);
        txtLocation.setText(companyModel.area);
        TextView txtTel = (TextView)rootView.findViewById(R.id.tv_company_telephone);
        txtTel.setText(companyModel.tel);
        TextView txtSite = (TextView)rootView.findViewById(R.id.tv_company_website);
        txtSite.setText(companyModel.site);
        TextView txtDesc = (TextView)rootView.findViewById(R.id.tv_company_description);
        txtDesc.setText(companyModel.description);
        TextView txtInterDetail = (TextView)rootView.findViewById(R.id.tv_company_interview_details);
        txtInterDetail.setText(companyModel.interview_request);

        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}
