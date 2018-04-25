package kh.com.ilost.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import kh.com.ilost.R;

public class PhoneSignupActivity extends AppCompatActivity {

    private static final String TAG = "PhoneAuthActivity";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private boolean mVerificationInProgress = false;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth fAuth;
    private CountryCodePicker ccp;
    private AppCompatEditText phoneNumber;
    private LinearLayout verifyLayout;
    private LinearLayout inputCodeLayout;
    private TextView timer;
    private Button resendCode;
    private Pinview smsCode;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_signup);

        getWindow().setBackgroundDrawableResource(R.drawable.phone_login_bg);

        inputCodeLayout = findViewById(R.id.phone_signup_input_code_layout);
        verifyLayout = findViewById(R.id.phone_signup_verify_layout);
        ccp = findViewById(R.id.phone_signup_ccp);
        Button loginButton = findViewById(R.id.phone_signup_login_button);
        phoneNumber = findViewById(R.id.phone_signup_phone_number);
        timer = findViewById(R.id.phone_signup_timer);
        resendCode = findViewById(R.id.phone_signup_resend_code);
        smsCode = findViewById(R.id.phone_signup_sms_code);

        showView(verifyLayout);
        hideView(inputCodeLayout);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //triggered when the login button is clicked
                attemptLogin();
            }

        });
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //triggered when the resend code button is pressed
                retryVerify();
            }
        });

        fAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                //sign in user to new Activity here
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                //  invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                verificationId = verificationId;
                resendToken = token;
            }
        };
        smsCode.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean b) {

                //trigger this when the OTP code has finished typing
                final String verifyCode = smsCode.getValue();
                verifyPhoneNumberWithCode(verificationId,verifyCode);
            }
        });
    }

    private void retryVerify() {
        resendVerificationCode(phone,resendToken);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        hideView(verifyLayout);
        hideView(inputCodeLayout);

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


    private void attemptLogin() {

        //reset any erros
        phoneNumber.setError(null);

        //get values from phone edit text and pass to countryPicker
        ccp.registerPhoneNumberTextView(phoneNumber);
        phone = ccp.getFullNumber();

        boolean cancel= false;
        View focusView = null;

        //check if phone number is valid: I would just check the length
        if(!isPhoneValid(phone)){

            focusView=phoneNumber;
            cancel=true;
        }

        if (cancel){
            //there was an error in the length of phone
            focusView.requestFocus();
        }else{

            //show loading screen
            hideView(verifyLayout);
            showView(inputCodeLayout);

            //go ahead and verify number
            startPhoneNumberVerification(phone);
            //time to show retry button
            new CountDownTimer(45000, 1000) {
                @Override
                public void onTick(long l) {
                    timer.setText("0:" + l / 1000 + " s");
                    resendCode.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFinish() {
                    timer.setText(0 + " s");
                    resendCode.startAnimation(AnimationUtils.loadAnimation(PhoneSignupActivity.this, R.anim.slide_from_right));
                    resendCode.setVisibility(View.VISIBLE);
                }
            }.start();
            //timer ends here
        }
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() > 7;
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        mVerificationInProgress = true;
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Intent i = new Intent(PhoneSignupActivity.this,MainActivity.class);
                            startActivity(i);

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneSignupActivity.this,"Invalid Verification Code",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
    private void showView (View... views){
        for(View v: views){
            v.setVisibility(View.VISIBLE);

        }

    }
    private void hideView (View... views){
        for(View v: views){
            v.setVisibility(View.INVISIBLE);

        }

    }
}
