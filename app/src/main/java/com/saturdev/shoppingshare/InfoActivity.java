package com.saturdev.shoppingshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saturdev.shoppingshare.Models.Users;

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

    Users user = new Users();
    int maxId;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    maxId = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor mEditor = mSharedPreferences.edit();


        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            String username = mName.getText().toString().trim();
                            String email = mEmail.getText().toString().trim();
                            String phone = getIntent().getStringExtra("phonenumber");
                            saveUserToFirebase(username, email, phone);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                updateUser();

                Intent intent = new Intent(InfoActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


    private void updateUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        String username = mName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String phone = getIntent().getStringExtra("phonenumber");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            //Add user to authenticated list
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

    //Add user to firebase database
    private void saveUserToFirebase(String username, String email, String phone) {
        user.setName(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setUserId(maxId + 1);
        databaseReference.child(String.valueOf(maxId + 1)).setValue(user);
    }

}