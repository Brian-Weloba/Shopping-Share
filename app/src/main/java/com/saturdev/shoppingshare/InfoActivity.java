package com.saturdev.shoppingshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.buttonSignIn)
    Button mSignIn;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.editTextName)
    EditText mName;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.editTextEmail)
    EditText mEmail;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
                startActivity(new Intent(InfoActivity.this, HomeActivity.class));
            }
        });
    }

    private void updateUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        String username = mName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();
            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) Log.d(TAG, "User profile updated.");
                        }
                    });

            firebaseUser.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email address updated.");
                            }
                        }
                    });

            System.out.println("Successfully updated user: " + firebaseUser.getUid());

        } else {
            finish();
        }
    }
}