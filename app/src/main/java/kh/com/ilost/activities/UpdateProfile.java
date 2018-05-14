package kh.com.ilost.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kh.com.ilost.R;
import kh.com.ilost.models.User;

public class UpdateProfile extends AppCompatActivity {

    private EditText edit_Username,edit_Email;
    private Button btn_save;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;




    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        btn_save =(Button)findViewById(R.id.btn_save);
        edit_Username =(EditText)findViewById(R.id.edit_username);
        edit_Email =(EditText)findViewById(R.id.edit_email);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference();
       FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();

       final String uid=firebaseUser.getUid();

       databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               User user=dataSnapshot.getValue(User.class);

               edit_Username.setText(user.getName());
               edit_Email.setText(user.getEmail());

               Log.d("name"+user.getName(),"email"+user.getEmail());

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                use to update database

             User user = new User();
             user.setName(edit_Username.getText().toString());
             user.setEmail(edit_Email.getText().toString());

             databaseReference.child("users").child(uid).setValue(user);

             finish();


            }
        });
    }
}
