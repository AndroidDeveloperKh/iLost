package kh.com.ilost.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import kh.com.ilost.R;
import kh.com.ilost.activities.LoginActivity;

public class FragmentHome extends Fragment {

    private FirebaseAuth fAuth;
    private TextView txUemail;
    private ProgressDialog progressDialog;

    public FragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Button btnLogout = root.findViewById(R.id.home_btn_logout);
        txUemail = root.findViewById(R.id.home_txt_user_email);

        progressDialog = new ProgressDialog(getActivity());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Logging out...");
                progressDialog.show();
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
            //get user name from user login
            txUemail.setText(firebaseUser.getDisplayName());
            Log.d("facebook log:", "User details : " + firebaseUser.getDisplayName() + firebaseUser.getEmail() + "\n" + firebaseUser.getPhotoUrl() + "\n"
                    + firebaseUser.getUid() + "\n" + firebaseUser.getToken(true) + "\n" + firebaseUser.getProviderId());

        }
    }

    // test

}
