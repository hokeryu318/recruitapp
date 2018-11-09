package recruit.aidiot.com.recruit.Activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import recruit.aidiot.com.recruit.Fragments.FragmentHome;
import recruit.aidiot.com.recruit.Fragments.FragmentNotify;
import recruit.aidiot.com.recruit.Fragments.FragmentTime;
import recruit.aidiot.com.recruit.Fragments.Session.FragmentCalendar;
import recruit.aidiot.com.recruit.Global.GD;
import recruit.aidiot.com.recruit.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if(GD.g_profile.valid_flag == 0){ //non approved
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            FragmentHome fragmentHome = new FragmentHome() ;
            transaction.addToBackStack("transaction");
            transaction.replace(R.id.lay_main, fragmentHome);
            transaction.commit();

//            findViewById(R.id.lin_time).setEnabled(false);
//            findViewById(R.id.lin_session).setEnabled(false);
//            findViewById(R.id.lin_message).setEnabled(false);
//            findViewById(R.id.lin_event).setEnabled(false);
//        }

        findViewById(R.id.lin_time).setOnClickListener(this);
        findViewById(R.id.lin_session).setOnClickListener(this);
        findViewById(R.id.lin_event).setOnClickListener(this);
        findViewById(R.id.lin_message).setOnClickListener(this);
        findViewById(R.id.lin_profile).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        switch (v.getId()){
            case R.id.lin_time:
                FragmentTime fragmentTime = new FragmentTime() ;
                transaction.addToBackStack("transaction");
                transaction.replace(R.id.lay_main, fragmentTime);
                transaction.commit();
                break;
            case R.id.lin_session:
                FragmentCalendar fragmentCalendar = new FragmentCalendar() ;
                transaction.addToBackStack("transaction");
                transaction.replace(R.id.lay_main, fragmentCalendar);
                transaction.commit();
                break;
            case R.id.lin_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.lin_message:
                FragmentNotify fragmentNotify = new FragmentNotify() ;
                transaction.addToBackStack("transaction");
                transaction.replace(R.id.lay_main, fragmentNotify);
                transaction.commit();
                break;
        }
    }
}
