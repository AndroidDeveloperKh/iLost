package kh.com.ilost.setting;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kh.com.ilost.R;
import kh.com.ilost.activities.UpdateProfile;
import kh.com.ilost.fragments.FragmentHome;
import kh.com.ilost.fragments.FragmentMessage;
import kh.com.ilost.fragments.SettingFragment;
import kh.com.ilost.models.User;

public class AccountActivity extends AppCompatActivity {

    private TextView txt_Username,txt_Password,txt_Email,txt_Adress;
    private Button btn_Edit;
    private String TAG=AccountActivity.class.getName();

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String UserId;



    private FirebaseAuth.AuthStateListener mAuthListerner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        txt_Username =(TextView)findViewById(R.id.txt_username);
        txt_Password =(TextView)findViewById(R.id.txt_password);
        txt_Email  = (TextView)findViewById(R.id.txt_email);
        txt_Adress = (TextView)findViewById(R.id.txt_address);
        btn_Edit = (Button)findViewById(R.id.btn_edit);




       firebaseAuth = FirebaseAuth.getInstance();

// Get a reference to our posts
        databaseReference = FirebaseDatabase.getInstance().getReference();

//        get user id
         FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
         String Uid =fuser.getUid();

        databaseReference.child("users").child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);
                txt_Username.setText(user.getName());
                txt_Email.setText(user.getEmail());
                Log.d("app", "name: " + user.getName() + ", Email " +user.getEmail());

//                   Log.d("app", dataSnapshot.toString());
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




       btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent update = new Intent(AccountActivity.this, UpdateProfile.class);
                startActivity(update);


            }
        });


    }
//    https://www.youtube.com/watch?v=2duc77R4Hqw

}
