package com.saturdev.shoppingshare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
@BindView(R.id.phoneNmb)
    TextView mPhoneNum;
@BindView(R.id.buttonlogOut)
    Button mLogOut;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUserStatus();
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String phone = firebaseUser.getPhoneNumber();
            String username = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            mPhoneNum.setText(String.format("%s, %s, %s", username, email, phone));
        } else {
            finish();
        }
    }
}