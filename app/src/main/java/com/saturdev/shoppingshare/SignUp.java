package com.saturdev.shoppingshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUp extends AppCompatActivity {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.spinnerCountries)
    Spinner mSpinner;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.editTextPhone)
    EditText mPhoneNumber;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.buttonSignUp)
    Button mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[mSpinner.getSelectedItemPosition()];
                String number = mPhoneNumber.getText().toString().trim();

                if (number.isEmpty() || number.length() < 9) {
                    mPhoneNumber.setError("Valid number is required");
                    mPhoneNumber.requestFocus();
                    return;
                }

                String phoneNum = "+" + code + number;

                Intent intent = new Intent(SignUp.this, VerifyPhoneActivity.class);
                intent.putExtra("phonenumber", phoneNum);
                startActivity(intent);
            }
        });

    }
}