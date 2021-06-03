package com.saturdev.shoppingshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

        SharedPreferences sharedPref = getSharedPreferences("data", MODE_PRIVATE);
        int number = sharedPref.getInt("isLogged", 0);

        mSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        if (number == 0) {
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });


            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.putInt("isLogged", 1);
            prefEditor.apply();
        } else {
            Intent intent = new Intent(SignUp.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

    }
}