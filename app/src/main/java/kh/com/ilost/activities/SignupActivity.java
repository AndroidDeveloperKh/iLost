package kh.com.ilost.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import kh.com.ilost.R;
import kh.com.ilost.models.User;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnSignup;
    private TextView txtGotoSignin;

    private EditText edtUsername, edtUserEmail, edtuserPassword, edtComPassword;

    private FirebaseAuth fAuth;
    private DatabaseReference reDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //call to reference
        btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);

        txtGotoSignin = findViewById(R.id.txtGotoSignin);
        txtGotoSignin.setOnClickListener(this);

        reDatabase = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        edtUsername = findViewById(R.id.user_name);
        edtUserEmail = findViewById(R.id.user_email);
        edtuserPassword = findViewById(R.id.user_password);
        edtComPassword = findViewById(R.id.com_password);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnSignup:
                userSignup();
                break;

            case R.id.txtGotoSignin:
                Intent signupIntent = new Intent(this,LoginActivity.class);
                startActivity(signupIntent);
                break;

            default:
                break;
        }
    }

    private void userSignup() {

        final String name = edtUsername.getText().toString().trim();
        String email = edtUserEmail.getText().toString().trim();
        String password = edtuserPassword.getText().toString().trim();
        final String comfirmPassword = edtComPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(comfirmPassword)) {

            if (password.contentEquals(comfirmPassword)){

                progressDialog.setMessage("Singing up...");
                progressDialog.show();

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            FirebaseUser user = fAuth.getCurrentUser();

                            writeUserToDatabase(user, name);

                            Intent singupIntent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(singupIntent);

                            Toast.makeText(getApplicationContext(),"User Created",Toast.LENGTH_LONG).show();

                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(SignupActivity.this,"Error while creating acc",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(SignupActivity.this,"Password not matched",Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(SignupActivity.this,"Fields cannot empty",Toast.LENGTH_LONG).show();
        }

    }

    public void writeUser(FirebaseUser user){
        String uid = user.getUid();

        // account
        Map<String, Object> account = new HashMap<>();
        account.put("uid", uid);
        account.put("name", user.getDisplayName());
        account.put("email", user.getEmail());
        account.put("created_date", System.currentTimeMillis());

        reDatabase.child("users").child(uid).setValue(account);
        Toast.makeText(getApplicationContext(),"Added user to database", Toast.LENGTH_LONG).show();
    }


    public void writeUserToDatabase(final FirebaseUser user, String name){

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            writeUser(user);
                            Log.d("app", "added user to db");
                        }
                    }
                });
    }
}
