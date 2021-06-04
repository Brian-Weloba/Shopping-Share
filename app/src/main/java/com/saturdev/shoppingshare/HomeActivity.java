package com.saturdev.shoppingshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.leanback.widget.HorizontalGridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.helloUser)
    TextView mHelloUser;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.buttonlogOut)
    Button mLogOut;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.buttonCart)
    ImageView mCart;

    ArrayList<Carts> cartsArrayList = new ArrayList<>();

    DatabaseReference dbCart;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        dbCart = FirebaseDatabase.getInstance().getReference("Carts");

        HorizontalGridView horizontalGridView = (HorizontalGridView) findViewById(R.id.cartsView);
        HorizontalGridView horizontalGridView2 = (HorizontalGridView) findViewById(R.id.cartsView2);
        HorizontalGridView horizontalGridView3 = (HorizontalGridView) findViewById(R.id.cartsView3);


        GridElementAdapter adapter = new GridElementAdapter(HomeActivity.this, cartsArrayList);

        horizontalGridView.setAdapter(adapter);
        horizontalGridView2.setAdapter(adapter);
        horizontalGridView3.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        SharedPreferences sharedPref = getSharedPreferences("data", MODE_PRIVATE);

        dbCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fetchData(snapshot);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mLogOut.setOnClickListener(v -> {
            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.putInt("isLogged", 0);
            prefEditor.apply();

            firebaseAuth.signOut();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);

        });

        mCart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BasketActivity.class);
            startActivity(intent);
        });

    }

    @SuppressLint("SetTextI18n")
    private void checkUserStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
//            String phone = firebaseUser.getPhoneNumber();
            String username = firebaseUser.getDisplayName();
//            String email = firebaseUser.getEmail();
            mHelloUser.setText("Hello " + username + ",");
        } else {
            finish();
        }
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            cartsArrayList.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Map<String, String> td = (HashMap<String, String>) ds.getValue();
                assert td != null;
                Carts cart = new Carts(td.get("cart_name"), td.get("cart_items"), td.get("cart_description"), td.get("cart_price"));
                cartsArrayList.add(cart);
            }
        }
    }
}