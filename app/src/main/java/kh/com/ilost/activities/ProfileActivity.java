package kh.com.ilost.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import kh.com.ilost.R;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewProfile;
    private TextView textViewUserName;
    private EditText editTextEmail;
    private EditText editTextPhoneNo;
    private EditText editTextAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("User Profile");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageViewProfile = findViewById(R.id.profile_img_user_profile);
        textViewUserName = findViewById(R.id.profile_txt_user_name);
        editTextEmail = findViewById(R.id.profile_edt_email);
        editTextPhoneNo = findViewById(R.id.profile_edt_phone_number);
        editTextAddress = findViewById(R.id.profile_edt_address);
        Button buttonSignOut = findViewById(R.id.profile_btn_sign_out);
        buttonSignOut.setOnClickListener(this);

        loadUserProfile();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("Authentication");
            builder.setMessage("Please Sign Up Or Sign In First");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });
            builder.show();
        }
    }

    private void loadUserProfile() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Glide.with(getApplicationContext()).load(firebaseUser.getPhotoUrl()).into(imageViewProfile);
            textViewUserName.setText(firebaseUser.getDisplayName());
            editTextEmail.setText(firebaseUser.getEmail());
            editTextPhoneNo.setText(firebaseUser.getPhoneNumber());
        }
        editInfo();
    }

    private void editInfo() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            if (firebaseUser.getEmail() != null) {
                editTextEmail.setEnabled(false);
                editTextEmail.setTextColor(getResources().getColor(R.color.colorBlack));
            } else if (firebaseUser.getPhoneNumber() != null) {
                editTextPhoneNo.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        if (clickId == R.id.profile_btn_sign_out) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Signed Out", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }
}
