package kh.com.ilost.fragments;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import kh.com.ilost.R;

import kh.com.ilost.activities.LoginActivity;
import kh.com.ilost.activities.SettingsActivity;
import kh.com.ilost.setting.AccountActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {



    private FirebaseAuth fAuth;
    String TAG;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    TextView txt_username;


    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        CardView cardView = (CardView)view.findViewById(R.id.c_notifi);
        CardView c_account =(CardView)view.findViewById(R.id.c_account);
        CardView home_btn_logout = (CardView)view.findViewById(R.id.home_btn_logout);
        txt_username =(TextView)view.findViewById(R.id.txt_username);



        home_btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             logout();
            }

        });
        fAuth = FirebaseAuth.getInstance();

//https://theengineerscafe.com/save-and-retrieve-data-firebase-android/

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(getActivity(),SettingsActivity.class);
                startActivity(i);
            }
        });

        c_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent account = new Intent(getActivity(),AccountActivity.class);
                startActivity(account);




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
