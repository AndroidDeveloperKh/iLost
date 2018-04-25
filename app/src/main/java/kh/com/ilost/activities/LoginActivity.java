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
import java.util.Arrays;
import kh.com.ilost.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean isFirstStart;
    private ImageView imgFacebook;
    private EditText edtEmail, edtPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient clientGoogleSignIn;
    //facebook
    private CallbackManager fbCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Call to Reference
        Button btnSignIn = findViewById(R.id.login_btn_signin);
        btnSignIn.setOnClickListener(this);

        TextView txtGotoSignUp = findViewById(R.id.login_txt_goto_sign_up);
        txtGotoSignUp.setOnClickListener(this);

        ImageView imgGoogle = findViewById(R.id.login_img_google_login);
        imgGoogle.setOnClickListener(this);

        imgFacebook = findViewById(R.id.login_img_fb_login);
        imgFacebook.setOnClickListener(this);

        ImageView imgPhone = findViewById(R.id.login_img_phone_login);
        imgPhone.setOnClickListener(this);

        edtEmail = findViewById(R.id.login_edt_user_email);
        edtPassword = findViewById(R.id.login_edt_user_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        clientGoogleSignIn = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize Facebook Login button
        fbCallbackManager = CallbackManager.Factory.create();

        appIntro();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.login_btn_signin:
                userSingIn();
                break;

            case R.id.login_txt_goto_sign_up:
                Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUpIntent);
                break;

            case R.id.login_img_google_login:
                googleSignUp();
                break;

            case R.id.login_img_fb_login:
                facebookSignUp();
                break;

            case R.id.login_img_phone_login:
                phoneSignUp();
                break;

            default:
                break;
        }

    }

    //sign up via phone number
    private void phoneSignUp() {
        Intent googleIntent = new Intent(this, PhoneSignupActivity.class);
        startActivity(googleIntent);
    }

    //sign up via facebook
    private void facebookSignUp() {
        imgFacebook.setEnabled(false);

        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog.setMessage("Logging in...");
                progressDialog.show();

                Log.d("FaceLog", "facebook:onSuccess: " + loginResult.getAccessToken().toString());
                Log.d("kk","kk" + loginResult.getAccessToken().getUserId());
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FaceLog", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FaceLog", "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        imgFacebook.setEnabled(true);
                        Intent fbIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(fbIntent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                        imgFacebook.setEnabled(true);
                    }

                }
            });
    }

    //sign up via google account
    private void googleSignUp() {
        Intent signInIntent = clientGoogleSignIn.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    updateUI();
                }
            }
        }
        // Pass the activity result back to the Facebook SDK
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
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

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {

            updateUI();
        }
    }

    private void updateUI() {
        Intent googleIntent = new Intent(this, MainActivity.class);
        startActivity(googleIntent);

        finish();
    }

    //sign up via email address
    private void userSingIn() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Fields cannot empty", Toast.LENGTH_LONG).show();
        } else {

            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Email or password is incorrect.", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    //app introduction
    private void appIntro() {
        Thread thread = new Thread(new Runnable() {
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
        thread.start();
    }


}
