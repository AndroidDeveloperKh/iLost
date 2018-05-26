package kh.com.ilost.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtUsername, edtEmail, edtPassword;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button btnSignUp = findViewById(R.id.sign_up_btn_sign_up);
        TextView txtGotoSignIn = findViewById(R.id.sign_up_txt_goto_sign_in);
        edtUsername = findViewById(R.id.sign_up_edt_username);
        edtEmail = findViewById(R.id.sign_up_edt_email);
        edtPassword = findViewById(R.id.sign_up_edt_password);

        txtGotoSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getApplicationContext());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up_btn_sign_up:
                signUp();
                break;
            case R.id.sign_up_txt_goto_sign_in:
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                break;
            default:
                break;
        }
    }

    private void signUp() {
        // show loading dialog
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        // validate form
        if (!validForm()) {
            return;
        }
        // create user
        final String name = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUser(user, name);
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this,
                                "Error While Creating Account", Toast.LENGTH_LONG).show();
                        Log.e("app", e.getMessage());
                        progressDialog.dismiss();
                    }
                });
    }

    // update username in firebase authentication
    public void updateUser(final FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            writeUserToDb(user);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("app", e.getMessage());
                    }
                });
    }

    // write user to real-time database
    public void writeUserToDb(FirebaseUser firebaseUser) {
        String key = firebaseUser.getUid();
        Map<String, Object> user = new HashMap<>();
        user.put("uid", key);
        user.put("name", firebaseUser.getDisplayName());
        user.put("email", firebaseUser.getEmail());
        user.put("provider", firebaseUser.getProviderId());
        //noinspection ConstantConditions
        user.put("createdAt", firebaseUser.getMetadata().getCreationTimestamp());
        databaseReference.child("users").child(key).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("app", e.getMessage());
                    }
                });
    }

    private boolean validForm() {
        boolean valid = false;
        String name = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (name.isEmpty()) {
            edtUsername.setError("Required");
        } else {
            edtUsername.setError(null);
            valid = true;
        }
        if (email.isEmpty()) {
            edtEmail.setError("Required");
        } else {
            edtEmail.setError(null);
            valid = true;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Required");
        } else {
            edtPassword.setError(null);
            valid = true;
        }
        return valid;
    }


}
