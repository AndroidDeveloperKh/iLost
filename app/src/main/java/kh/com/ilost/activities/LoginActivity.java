package kh.com.ilost.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import kh.com.ilost.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean isFirstStart;

    private Button btnSignin;
    private TextView txtGotoSignup;

    private ImageView imgGoogle, imgFacebook, imgTwitter, imgPhone;

    private EditText edtEmail, edtPassword;
    private ProgressDialog progressDialog;

    private DatabaseReference reDatabase;
    private FirebaseAuth fAuth;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    //facebook
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Call to Reference
        btnSignin = (Button)findViewById(R.id.btnSignin);
        btnSignin.setOnClickListener(this);

        txtGotoSignup = (TextView)findViewById(R.id.txtGotoSignup);
        txtGotoSignup.setOnClickListener(this);

        imgGoogle = (ImageView)findViewById(R.id.google_login);
        imgGoogle.setOnClickListener(this);

        imgFacebook = (ImageView)findViewById(R.id.fb_login);
        imgFacebook.setOnClickListener(this);

        imgTwitter = (ImageView)findViewById(R.id.twitter_login);
        imgTwitter.setOnClickListener(this);

        imgPhone = (ImageView)findViewById(R.id.phone_login);
        imgPhone.setOnClickListener(this);

        edtEmail = (EditText)findViewById(R.id.user_email);
        edtPassword = (EditText)findViewById(R.id.user_password);

        progressDialog = new ProgressDialog(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        fAuth = FirebaseAuth.getInstance();
        reDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnSignin:
                userSingin();
                break;

            case R.id.txtGotoSignup:
                Intent signupIntent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(signupIntent);
                break;

            case R.id.google_login:
                googleSignup();
                break;

            case R.id.fb_login:
                facebookSignup();
                break;

            case R.id.twitter_login:
                twitterSignup();
                break;

            case R.id.phone_login:
                phoneSignup();
                break;

            default:
                break;
        }

    }

    private void phoneSignup() {

        Intent googleIntent = new Intent(this,PhoneSignupActivity.class);
        startActivity(googleIntent);

    }

    private void twitterSignup() {

    }

    private void facebookSignup() {

        imgFacebook.setEnabled(false);

        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FaceLog", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FaceLog", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FaceLog", "facebook:onError", error);
                // ...
            }
        });

    }

    private void googleSignup() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void userSingin() {

        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Fields cannot empty", Toast.LENGTH_LONG).show();
        } else {

            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,"Email or password is incorrect.",Toast.LENGTH_LONG).show();

                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI();
                // [END_EXCLUDE]
            }
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        progressDialog.show();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = fAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI();
                        }

                        progressDialog.show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = fAuth.getCurrentUser();

        if (currentUser != null){

            updateUI();
        }
    }

    private void updateUI() {

        Toast.makeText(LoginActivity.this,"User created!",Toast.LENGTH_LONG).show();

        Intent googleIntent = new Intent(this,MainActivity.class);
        startActivity(googleIntent);

        finish();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = fAuth.getCurrentUser();

                            imgFacebook.setEnabled(true);
//                            updateUI();
                            Intent fbIntent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(fbIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            imgFacebook.setEnabled(true);
//                            updateUI();
                        }

                    }
                });
    }

    private void appIntro() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Intro App Initialize SharedPreferences
                SharedPreferences getSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                //  Create a new boolean and preference and set it to true
                isFirstStart = getSharedPreferences.getBoolean("firstStart", true);

                //  Check either activity or app is open very first time or not and do action
                if (isFirstStart) {
                    //  Launch application introduction screen
                    Intent i = new Intent(getApplicationContext(), MyIntro.class);
                    startActivity(i);
                    SharedPreferences.Editor e = getSharedPreferences.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();
    }


}
