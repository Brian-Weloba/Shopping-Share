package com.saturdev.shoppingshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUp extends AppCompatActivity {
    @BindView(R.id.textLogInOpt)
    TextView mTextLoginOpt;
    @BindView(R.id.spinnerCountries)
    Spinner mSpinner;
    @BindView(R.id.editTextPhone)
    EditText mPhoneNumber;
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
                intent.putExtra("phonenumber",phoneNum);
                startActivity(intent);
            }
        });

        mTextLoginOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}