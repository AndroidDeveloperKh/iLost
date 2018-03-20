package kh.com.ilost.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kh.com.ilost.R;
import kh.com.ilost.activities.LoginActivity;


public class FragmentHome extends Fragment {


    private FirebaseAuth fAuth;
    private TextView txUemail;


    public FragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Button btnLogout = root.findViewById(R.id.home_btn_logout);
        txUemail = root.findViewById(R.id.home_txt_user_email);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                updateUI(null);
            }
        });
        fAuth = FirebaseAuth.getInstance();

        return root;
    }


    private void logout() {
        fAuth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(getContext(), "Logged out", Toast.LENGTH_LONG).show();
        Intent googleIntent = new Intent(getContext(), LoginActivity.class);
        startActivity(googleIntent);
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null){
            updateUI(currentUser);
        }

    }


    private void updateUI(FirebaseUser firebaseUser) {
        if(firebaseUser != null) {
            txUemail.setText(firebaseUser.getEmail());
        }
    }


}
