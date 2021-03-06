package com.saturdev.shoppingshare;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerifyPhoneActivity extends AppCompatActivity {
    private static final String TAG = "MAIN_TAG";
    private static AlertDialog.Builder spd;
    //if code sending fails, will be used to resend OTP
    public PhoneAuthProvider.ForceResendingToken forceResendingToken;
    @BindView(R.id.buttonVerify)
    Button mVerify;
    @BindView(R.id.textResendCode)
    TextView mResend;
    @BindView(R.id.editTextCode)
    EditText mCode;
    @BindView(R.id.textInstructions)
    TextView mInstructions;
    private String mVerificationId;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        ButterKnife.bind(this);

        String phoneNumber = getIntent().getStringExtra("phonenumber");

        firebaseAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play clients can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(VerifyPhoneActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                Log.d(TAG, "onCodeSent: " + verificationId);

                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                Toast.makeText(VerifyPhoneActivity.this, "Verification code sent... \nto: " + phoneNumber + ".", Toast.LENGTH_SHORT).show();
                mInstructions.setText("Please type the verification code we sent \nto: " + phoneNumber);

            }


        };

        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(VerifyPhoneActivity.this, "Please Enter verification code", Toast.LENGTH_SHORT).show();
                } else {
                    verifyPhoneNumberWithCode(mVerificationId, code);

                }
            }
        });

        mResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phoneNumber, forceResendingToken);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String phoneNumber = getIntent().getStringExtra("phonenumber");
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(VerifyPhoneActivity.this, "Please Enter verification code", Toast.LENGTH_SHORT).show();
        } else {
            startPhoneNumberVerification(phoneNumber);
        }
    }

    public void startPhoneNumberVerification(String phone) {

        pd.setMessage("Verifying Number");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending code");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pd.setMessage("Verifying code");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging in");

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pd.dismiss();
                        String phone = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();
                        Toast.makeText(VerifyPhoneActivity.this, phone + " verified!", Toast.LENGTH_SHORT).show();
                        //start home activity
                        if (firebaseAuth.getCurrentUser().getDisplayName() == null) {
                            Intent intent = new Intent(VerifyPhoneActivity.this, InfoActivity.class);
                            intent.putExtra("phonenumber", getIntent().getStringExtra("phonenumber"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(VerifyPhoneActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(VerifyPhoneActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}