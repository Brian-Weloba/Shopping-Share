package com.saturdev.shoppingshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

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
    @BindView(R.id.increaseCart)
    ImageView increaseIV;
    @BindView(R.id.reduceCart)
    ImageView decreaseIV;
    @BindView(R.id.cartItemCounter)
    TextView quantityTV;
    @BindView(R.id.addToBasket)
    Button addToCartBTN;


    DatabaseReference db;
    FirebaseAuth firebaseAuth;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        ButterKnife.bind(this);
        db = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String items = getIntent().getStringExtra("items");
        String price = getIntent().getStringExtra("price");

        cartNameTV.setText(name);
        priceTV.setText("KSH " + price);
        descTV.setText(description);
        itemsTV.setText(items);

        increaseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = quantityTV.getText().toString();
                int quantityInt = Integer.parseInt(quantityString);
                quantityInt++;
                quantityTV.setText(Integer.toString(quantityInt));
            }
        });

        decreaseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = quantityTV.getText().toString();
                int quantityInt = Integer.parseInt(quantityString);
                if (quantityInt > 1) {
                    quantityInt--;
                } else {
                    quantityInt = 1;
                }
                quantityTV.setText(Integer.toString(quantityInt));
            }
        });

        addToCartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartList();
            }
        });

    }

    private void addToCartList() {
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("name", cartNameTV.getText().toString());
        cartMap.put("price", getIntent().getStringExtra("price"));
        cartMap.put("description", descTV.getText().toString());
        cartMap.put("items", itemsTV.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", quantityTV.getText().toString());

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        cartListRef.child("User View").child(firebaseUser.getPhoneNumber())
                .child("Products")
                .child(cartNameTV.getText().toString())
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            cartListRef.child("Admin View").child(firebaseUser.getPhoneNumber())
                                    .child("Products")
                                    .child(cartNameTV.getText().toString())
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ViewItemActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ViewItemActivity.this, HomeActivity.class));
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}