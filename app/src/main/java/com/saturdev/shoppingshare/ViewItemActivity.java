package com.saturdev.shoppingshare;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewItemActivity extends AppCompatActivity {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cartName)
    TextView cartNameTV;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.itemPrice)
    TextView priceTV;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.itemsInCart)
    TextView itemsTV;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cartDescription)
    TextView descTV;


    DatabaseReference db;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        ButterKnife.bind(this);
        db = FirebaseDatabase.getInstance().getReference();

        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String items = getIntent().getStringExtra("items");
        String price = getIntent().getStringExtra("price");

        cartNameTV.setText(name);
        priceTV.setText("KSH "+ price);
        descTV.setText(description);
        itemsTV.setText(items);
    }
}