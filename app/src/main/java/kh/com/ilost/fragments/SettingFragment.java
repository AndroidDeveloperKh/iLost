package kh.com.ilost.fragments;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;


import com.google.firebase.auth.FirebaseAuth;

import kh.com.ilost.R;

import kh.com.ilost.activities.AccountSetting;
import kh.com.ilost.activities.LoginActivity;
import kh.com.ilost.activities.NotificationActivity;
import kh.com.ilost.activities.TestActivity;
import kh.com.ilost.setting.AccountActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {



    private FirebaseAuth fAuth;
    String TAG;
   private CardView c_addAccount ,c_notification,c_account,home_btn_logout,c_save;


    TextView txt_username;


    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

         c_notification = (CardView)view.findViewById(R.id.c_notifi);
        c_account =(CardView)view.findViewById(R.id.c_account);
         home_btn_logout = (CardView)view.findViewById(R.id.home_btn_logout);
        txt_username =(TextView)view.findViewById(R.id.txt_username);
        c_addAccount=(CardView)view.findViewById(R.id.c_addAccount);
        c_save=(CardView)view.findViewById(R.id.c_save);




        home_btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             logout();
            }

        });
        fAuth = FirebaseAuth.getInstance();

//https://theengineerscafe.com/save-and-retrieve-data-firebase-android/


        c_addAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(),TestActivity.class);
                startActivity(i);
            }
        });



        c_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent account_setting = new Intent(getActivity(),AccountSetting.class);
                startActivity(account_setting);
            }
        });



        c_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent account = new Intent(getActivity(),AccountActivity.class);
                startActivity(account);


            }
        });

        c_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),NotificationActivity.class);
                startActivity(i);
            }
        });


        return view;
    }



    private void logout() {
        fAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(getContext(), "Logged out", Toast.LENGTH_LONG).show();
        Intent googleIntent = new Intent(getContext(), LoginActivity.class);
        Log.d(TAG," sign out successful");
        startActivity(googleIntent);

    }



}
