package kh.com.ilost.setting;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kh.com.ilost.R;
import kh.com.ilost.models.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {



    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    private String Uid;
    TextView txt_username;



    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        CardView cardView = (CardView)view.findViewById(R.id.c_notifi);
        CardView c_account =(CardView)view.findViewById(R.id.c_account);
        txt_username =(TextView)getView().findViewById(R.id.txt_username);




// Get a reference to our posts
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

//        get user id
        FirebaseUser fuser= firebaseAuth.getInstance().getCurrentUser();
         Uid =fuser.getUid();






        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getActivity(),SettingsActivity.class);
//                startActivity(i);
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

}
